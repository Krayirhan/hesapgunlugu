package com.hesapgunlugu.app.core.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Purchase verification request
 */
data class PurchaseVerificationRequest(
    @SerializedName("purchase_token")
    val purchaseToken: String,
    @SerializedName("product_id")
    val productId: String,
    @SerializedName("package_name")
    val packageName: String,
    @SerializedName("user_id")
    val userId: String? = null,
)

/**
 * Purchase verification response
 */
data class PurchaseVerificationResponse(
    @SerializedName("is_valid")
    val isValid: Boolean,
    @SerializedName("status")
    val status: String,
    @SerializedName("expiry_time_millis")
    val expiryTimeMillis: Long? = null,
    @SerializedName("auto_renewing")
    val autoRenewing: Boolean? = null,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("error_code")
    val errorCode: String? = null,
)

/**
 * Backend validation status
 */
enum class ValidationStatus {
    VALID,
    INVALID,
    EXPIRED,
    CANCELLED,
    PENDING,
    ERROR,
}
