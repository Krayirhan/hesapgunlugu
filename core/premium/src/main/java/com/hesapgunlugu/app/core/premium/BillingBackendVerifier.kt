package com.hesapgunlugu.app.core.premium

/**
 * Backend billing verification interface
 * Production'da gerçek backend endpoint'e bağlanmalı
 */
interface BillingBackendVerifier {
    /**
     * Purchase token'ı backend'de doğrula
     * @param purchaseToken Google Play'den gelen token
     * @param productId Satın alınan ürün ID
     * @return Backend onayı (true = geçerli, false = sahte)
     */
    suspend fun verifyPurchase(
        purchaseToken: String,
        productId: String,
    ): BackendVerificationResult

    /**
     * Subscription durumunu backend'den kontrol et
     * @param userId Kullanıcı ID
     * @return Aktif subscription bilgisi
     */
    suspend fun checkSubscriptionStatus(userId: String): SubscriptionStatus
}

data class BackendVerificationResult(
    val isValid: Boolean,
    val userId: String? = null,
    val expiryTimeMillis: Long? = null,
    val errorMessage: String? = null,
)

data class SubscriptionStatus(
    val isActive: Boolean,
    val productId: String?,
    val expiryDate: Long?,
    val autoRenewing: Boolean,
)
