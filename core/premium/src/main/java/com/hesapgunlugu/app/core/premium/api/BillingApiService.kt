package com.hesapgunlugu.app.core.premium.api

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Billing backend REST API service
 * Production-ready Retrofit interface
 */
interface BillingApiService {
    /**
     * Verify purchase with backend
     *
     * @param request Purchase verification request
     * @return Verification result with user info and expiry
     */
    @POST("billing/verify")
    suspend fun verifyPurchase(
        @Body request: VerifyPurchaseRequest,
    ): VerifyPurchaseResponse

    /**
     * Check subscription status for user
     *
     * @param userId User identifier
     * @return Current subscription status
     */
    @GET("billing/status/{userId}")
    suspend fun getSubscriptionStatus(
        @Path("userId") userId: String,
    ): SubscriptionStatusResponse
}

/**
 * Purchase verification request model
 */
data class VerifyPurchaseRequest(
    @SerializedName("purchaseToken")
    val purchaseToken: String,
    @SerializedName("productId")
    val productId: String,
    @SerializedName("packageName")
    val packageName: String,
)

/**
 * Purchase verification response model
 */
data class VerifyPurchaseResponse(
    @SerializedName("valid")
    val isValid: Boolean,
    @SerializedName("userId")
    val userId: String? = null,
    @SerializedName("expiryTimeMillis")
    val expiryTimeMillis: Long? = null,
    @SerializedName("productId")
    val productId: String? = null,
    @SerializedName("autoRenewing")
    val autoRenewing: Boolean = false,
    @SerializedName("errorMessage")
    val errorMessage: String? = null,
)

/**
 * Subscription status response model
 */
data class SubscriptionStatusResponse(
    @SerializedName("active")
    val isActive: Boolean,
    @SerializedName("productId")
    val productId: String? = null,
    @SerializedName("expiryDate")
    val expiryDate: String? = null,
    @SerializedName("autoRenewing")
    val autoRenewing: Boolean = false,
)
