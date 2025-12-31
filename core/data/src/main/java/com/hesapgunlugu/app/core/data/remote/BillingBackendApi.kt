package com.hesapgunlugu.app.core.data.remote

import com.hesapgunlugu.app.core.data.remote.dto.PurchaseVerificationRequest
import com.hesapgunlugu.app.core.data.remote.dto.PurchaseVerificationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Backend API for billing validation and user management
 *
 * Base URL should be configured in BuildConfig or RemoteConfigwhen backend is ready
 */
interface BillingBackendApi {
    /**
     * Verify purchase with backend
     *
     * @param authorization Bearer token for authentication
     * @param request Purchase verification request containing purchase token and product ID
     * @return Verification response with validation status
     */
    @POST("/api/v1/billing/verify")
    suspend fun verifyPurchase(
        @Header("Authorization") authorization: String,
        @Body request: PurchaseVerificationRequest,
    ): Response<PurchaseVerificationResponse>

    /**
     * Sync user subscription status
     */
    @POST("/api/v1/billing/sync")
    suspend fun syncSubscriptionStatus(
        @Header("Authorization") authorization: String,
    ): Response<PurchaseVerificationResponse>

    /**
     * Report billing issue to backend
     */
    @POST("/api/v1/billing/report-issue")
    suspend fun reportBillingIssue(
        @Header("Authorization") authorization: String,
        @Body issue: Map<String, String>,
    ): Response<Unit>
}
