package com.hesapgunlugu.app.core.premium

import com.hesapgunlugu.app.core.premium.api.BillingApiService
import com.hesapgunlugu.app.core.premium.api.BillingRetrofitClient
import com.hesapgunlugu.app.core.premium.api.VerifyPurchaseRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.net.URI
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Production-ready backend billing verification with Retrofit + HTTPS pinning
 */
@Singleton
class BillingBackendVerifierImpl
    @Inject
    constructor(
        private val retrofitClient: BillingRetrofitClient,
    ) : BillingBackendVerifier {
        companion object {
            private const val PACKAGE_NAME = "com.hesapgunlugu.app"
        }

        private val apiService: BillingApiService? by lazy {
            try {
                val baseUrl = BuildConfig.BILLING_BACKEND_URL
                if (baseUrl.isBlank() || !baseUrl.startsWith("https://")) {
                    Timber.w("Invalid billing backend URL: $baseUrl")
                    null
                } else {
                    val pinnedHosts = resolvePinnedHosts(baseUrl)
                    if (pinnedHosts.isEmpty()) {
                        val host = extractHost(baseUrl)
                        val isLocalDebug = BuildConfig.DEBUG && isLocalHost(host)
                        if (!isLocalDebug) {
                            Timber.e("Billing backend pinning is required for non-local hosts")
                            return@lazy null
                        }
                    }
                    retrofitClient.createService<BillingApiService>(
                        baseUrl = baseUrl,
                        isDebug = BuildConfig.DEBUG,
                        pinnedHosts = pinnedHosts,
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to create billing API service")
                null
            }
        }

        override suspend fun verifyPurchase(
            purchaseToken: String,
            productId: String,
        ): BackendVerificationResult =
            withContext(Dispatchers.IO) {
                try {
                    val service = apiService
                    if (service == null) {
                        Timber.e("Billing API service not configured")
                        return@withContext BackendVerificationResult(
                            isValid = false,
                            errorMessage = "Billing backend not configured",
                        )
                    }

                    val request =
                        VerifyPurchaseRequest(
                            purchaseToken = purchaseToken,
                            productId = productId,
                            packageName = PACKAGE_NAME,
                        )

                    val response = service.verifyPurchase(request)
                    Timber.d("Backend verification response: valid=${response.isValid}, userId=${response.userId}")

                    BackendVerificationResult(
                        isValid = response.isValid,
                        userId = response.userId,
                        expiryTimeMillis = response.expiryTimeMillis,
                        errorMessage = response.errorMessage,
                    )
                } catch (e: Exception) {
                    Timber.e(e, "Backend verification failed")
                    BackendVerificationResult(
                        isValid = false,
                        errorMessage = "Network error: ${e.message}",
                    )
                }
            }

        override suspend fun checkSubscriptionStatus(userId: String): SubscriptionStatus =
            withContext(Dispatchers.IO) {
                try {
                    val service = apiService
                    if (service == null) {
                        Timber.w("Billing API service not configured")
                        return@withContext SubscriptionStatus(
                            isActive = false,
                            productId = null,
                            expiryDate = null,
                            autoRenewing = false,
                        )
                    }

                    val response = service.getSubscriptionStatus(userId)
                    Timber.d("Subscription status: active=${response.isActive}, productId=${response.productId}")

                    SubscriptionStatus(
                        isActive = response.isActive,
                        productId = response.productId,
                        expiryDate = response.expiryDate?.toLongOrNull(),
                        autoRenewing = response.autoRenewing,
                    )
                } catch (e: Exception) {
                    Timber.e(e, "Failed to check subscription status")
                    SubscriptionStatus(
                        isActive = false,
                        productId = null,
                        expiryDate = null,
                        autoRenewing = false,
                    )
                }
            }

        private fun resolvePinnedHosts(baseUrl: String): Map<String, List<String>> {
            val pins =
                BuildConfig.BILLING_BACKEND_PINS
                    .split(",")
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }

            if (pins.isEmpty()) return emptyMap()

            val host =
                BuildConfig.BILLING_BACKEND_HOST.ifBlank {
                    runCatching { URI(baseUrl).host }.getOrNull().orEmpty()
                }

            return if (host.isNotBlank()) {
                mapOf(host to pins)
            } else {
                Timber.w("Billing backend host not resolved for pinning")
                emptyMap()
            }
        }

        private fun extractHost(baseUrl: String): String = runCatching { URI(baseUrl).host }.getOrNull().orEmpty()

        private fun isLocalHost(host: String): Boolean {
            val normalized = host.lowercase()
            return normalized == "localhost" || normalized == "127.0.0.1" || normalized == "10.0.2.2"
        }
    }
