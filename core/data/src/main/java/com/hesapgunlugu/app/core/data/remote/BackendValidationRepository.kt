package com.hesapgunlugu.app.core.data.remote

import com.hesapgunlugu.app.core.data.remote.dto.PurchaseVerificationRequest
import com.hesapgunlugu.app.core.data.remote.dto.PurchaseVerificationResponse
import com.hesapgunlugu.app.core.data.remote.dto.ValidationStatus
import com.hesapgunlugu.app.core.util.analytics.CrashlyticsHelper
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for backend validation
 *
 * Handles communication with backend API for purchase verification
 * and subscription status synchronization.
 *
 */
@Singleton
class BackendValidationRepository
    @Inject
    constructor(
        private val api: BillingBackendApi,
        private val config: BackendConfig,
        private val tokenProvider: BackendTokenProvider,
        private val crashlyticsHelper: CrashlyticsHelper,
    ) {
        /**
         * Verify purchase with backend
         *
         * @return Pair of (isValid, errorMessage?)
         */
        suspend fun verifyPurchase(
            purchaseToken: String,
            productId: String,
            packageName: String,
            userId: String?,
        ): Result<PurchaseVerificationResponse> {
            return try {
                if (config.baseUrl.isBlank()) {
                    return Result.failure(IllegalStateException("Backend not configured"))
                }

                val request =
                    PurchaseVerificationRequest(
                        purchaseToken = purchaseToken,
                        productId = productId,
                        packageName = packageName,
                        userId = userId,
                    )

                val authToken =
                    tokenProvider.getToken().getOrElse { error ->
                        return Result.failure(error)
                    }
                val response = api.verifyPurchase("Bearer $authToken", request)

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    Timber.d("Backend validation: ${body.status}")

                    crashlyticsHelper.log("Purchase verification: ${body.status}")
                    crashlyticsHelper.setCustomKey("last_verification_status", body.status)

                    Result.success(body)
                } else {
                    val error = "Backend validation failed: ${response.code()}"
                    Timber.e(error)
                    if (response.code() == 401 || response.code() == 403) {
                        tokenProvider.clear()
                    }
                    crashlyticsHelper.recordException(Exception(error))
                    Result.failure(Exception(error))
                }
            } catch (e: Exception) {
                Timber.e(e, "Backend validation error")
                crashlyticsHelper.recordException(e)
                Result.failure(e)
            }
        }

        /**
         * Sync subscription status with backend
         */
        suspend fun syncSubscriptionStatus(userId: String?): Result<PurchaseVerificationResponse> {
            return try {
                if (config.baseUrl.isBlank()) {
                    return Result.failure(IllegalStateException("Backend not configured"))
                }

                val authToken =
                    tokenProvider.getToken().getOrElse { error ->
                        return Result.failure(error)
                    }
                val response = api.syncSubscriptionStatus("Bearer $authToken")

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    Timber.d("Subscription sync: ${body.status}")
                    Result.success(body)
                } else {
                    val error = "Subscription sync failed: ${response.code()}"
                    Timber.e(error)
                    if (response.code() == 401 || response.code() == 403) {
                        tokenProvider.clear()
                    }
                    Result.failure(Exception(error))
                }
            } catch (e: Exception) {
                Timber.e(e, "Subscription sync error")
                crashlyticsHelper.recordException(e)
                Result.failure(e)
            }
        }

        /**
         * Report billing issue to backend
         */
        suspend fun reportBillingIssue(
            userId: String?,
            issueDescription: String,
            errorCode: String?,
        ): Result<Unit> {
            return try {
                if (config.baseUrl.isBlank()) {
                    return Result.failure(IllegalStateException("Backend not configured"))
                }

                val authToken =
                    tokenProvider.getToken().getOrElse { error ->
                        return Result.failure(error)
                    }
                val issue =
                    mapOf(
                        "user_id" to (userId ?: "unknown"),
                        "description" to issueDescription,
                        "error_code" to (errorCode ?: "unknown"),
                        "timestamp" to System.currentTimeMillis().toString(),
                    )

                val response = api.reportBillingIssue("Bearer $authToken", issue)

                if (response.isSuccessful) {
                    Timber.d("Billing issue reported successfully")
                    Result.success(Unit)
                } else {
                    val error = "Failed to report billing issue: ${response.code()}"
                    Timber.e(error)
                    if (response.code() == 401 || response.code() == 403) {
                        tokenProvider.clear()
                    }
                    Result.failure(Exception(error))
                }
            } catch (e: Exception) {
                Timber.e(e, "Error reporting billing issue")
                crashlyticsHelper.recordException(e)
                Result.failure(e)
            }
        }

        /**
         * Validate response status
         */
        private fun isValidStatus(status: String): Boolean {
            return try {
                val validationStatus = ValidationStatus.valueOf(status.uppercase())
                validationStatus == ValidationStatus.VALID
            } catch (e: Exception) {
                false
            }
        }
    }
