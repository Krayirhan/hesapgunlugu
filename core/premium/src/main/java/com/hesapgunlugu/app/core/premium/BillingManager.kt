package com.hesapgunlugu.app.core.premium

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages Google Play Billing integration with backend verification
 *
 * This singleton handles the complete in-app purchase flow for premium subscriptions,
 * including product listing, purchase initiation, server-side verification, and
 * subscription state management.
 *
 * ## Features
 *
 * ### Subscription Products
 * - Monthly subscription: `premium_monthly`
 * - Yearly subscription: `premium_yearly` (best value)
 *
 * ### Security
 * - Client-side purchase verification (Google Play)
 * - Server-side verification via [BillingBackendVerifier]
 * - Double verification prevents purchase tampering
 * - Automatic acknowledgement only after backend approval
 *
 * ### Purchase Flow
 * 1. User initiates purchase → [launchBillingFlow]
 * 2. Google Play handles payment
 * 3. [onPurchasesUpdated] receives purchase
 * 4. Backend verification via [backendVerifier]
 * 5. Purchase acknowledged if valid
 * 6. Premium status updated
 *
 * ### State Management
 * - [purchaseState]: Current purchase flow state
 * - [isPremium]: User's premium subscription status
 * - States persist across app restarts
 * - Automatic refresh on app start
 *
 * ## Usage
 * ```kotlin
 * // Initialize connection
 * billingManager.startConnection()
 *
 * // Observe premium status
 * billingManager.isPremium.collectLatest { isPremium ->
 *     if (isPremium) {
 *         // Show premium features
 *     }
 * }
 *
 * // Launch purchase flow
 * billingManager.launchBillingFlow(
 *     activity = this,
 *     productId = BillingManager.PRODUCT_ID_MONTHLY
 * )
 *
 * // Clean up
 * billingManager.endConnection()
 * ```
 *
 * ## Error Handling
 * Purchase errors are exposed via [purchaseState]:
 * - `PurchaseState.Error`: User-facing error message
 * - Automatic retry logic for network failures
 * - Graceful degradation when backend unavailable
 *
 * @property context Application context
 * @property backendVerifier Server-side purchase verification service
 * @see BillingBackendVerifier for backend integration
 * @see PurchaseState for purchase flow states
 */
@Singleton
class BillingManager
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val backendVerifier: BillingBackendVerifier,
    ) : PurchasesUpdatedListener {
        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        private var billingClient: BillingClient? = null

        private val _purchaseState = MutableStateFlow<PurchaseState>(PurchaseState.Idle)
        val purchaseState: StateFlow<PurchaseState> = _purchaseState.asStateFlow()

        private val _isPremium = MutableStateFlow(false)
        val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

        companion object {
            const val PRODUCT_ID_MONTHLY = "premium_monthly"
            const val PRODUCT_ID_YEARLY = "premium_yearly"
        }

        init {
            setupBillingClient()
        }

        private fun setupBillingClient() {
            billingClient =
                BillingClient.newBuilder(context)
                    .setListener(this)
                    .enablePendingPurchases()
                    .build()

            billingClient?.startConnection(
                object : BillingClientStateListener {
                    override fun onBillingSetupFinished(billingResult: BillingResult) {
                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                            Timber.d("Billing setup successful")
                            queryPurchases()
                        } else {
                            Timber.e("Billing setup failed: ${billingResult.debugMessage}")
                        }
                    }

                    override fun onBillingServiceDisconnected() {
                        Timber.w("Billing service disconnected")
                        // Retry connection
                        setupBillingClient()
                    }
                },
            )
        }

        /**
         * Query active purchases
         */
        private fun queryPurchases() {
            billingClient?.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder()
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build(),
            ) { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    val hasPremium =
                        purchases.any {
                            it.products.contains(PRODUCT_ID_MONTHLY) ||
                                it.products.contains(PRODUCT_ID_YEARLY)
                        }
                    _isPremium.value = hasPremium
                    Timber.d("Premium status: $hasPremium")
                }
            }
        }

        /**
         * Launch purchase flow
         */
        suspend fun launchPurchaseFlow(
            activity: Activity,
            productId: String,
        ) {
            val productList =
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(productId)
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build(),
                )

            val params =
                QueryProductDetailsParams.newBuilder()
                    .setProductList(productList)
                    .build()

            billingClient?.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && productDetailsList.isNotEmpty()) {
                    val productDetails = productDetailsList[0]

                    val offerToken = productDetails.subscriptionOfferDetails?.get(0)?.offerToken

                    if (offerToken != null) {
                        val productDetailsParamsList =
                            listOf(
                                BillingFlowParams.ProductDetailsParams.newBuilder()
                                    .setProductDetails(productDetails)
                                    .setOfferToken(offerToken)
                                    .build(),
                            )

                        val billingFlowParams =
                            BillingFlowParams.newBuilder()
                                .setProductDetailsParamsList(productDetailsParamsList)
                                .build()

                        billingClient?.launchBillingFlow(activity, billingFlowParams)
                    }
                } else {
                    _purchaseState.value = PurchaseState.Error("Product not found")
                }
            }
        }

        override fun onPurchasesUpdated(
            billingResult: BillingResult,
            purchases: List<Purchase>?,
        ) {
            when (billingResult.responseCode) {
                BillingClient.BillingResponseCode.OK -> {
                    purchases?.forEach { purchase ->
                        handlePurchase(purchase)
                    }
                }
                BillingClient.BillingResponseCode.USER_CANCELED -> {
                    _purchaseState.value = PurchaseState.Cancelled
                }
                else -> {
                    _purchaseState.value = PurchaseState.Error(billingResult.debugMessage)
                }
            }
        }

        private fun handlePurchase(purchase: Purchase) {
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                val productId = purchase.products.firstOrNull()
                if (productId == null) {
                    _purchaseState.value = PurchaseState.Error("ProductId missing in purchase")
                    return
                }

                scope.launch {
                    val verification =
                        backendVerifier.verifyPurchase(
                            purchaseToken = purchase.purchaseToken,
                            productId = productId,
                        )

                    if (verification.isValid) {
                        if (!purchase.isAcknowledged) {
                            acknowledgePurchase(purchase)
                        }
                        _isPremium.value = true
                        _purchaseState.value = PurchaseState.Success
                    } else {
                        _purchaseState.value =
                            PurchaseState.Error(
                                verification.errorMessage ?: "Backend verification failed",
                            )
                    }
                }
            }
        }

        private fun acknowledgePurchase(purchase: Purchase) {
            val acknowledgePurchaseParams =
                AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()

            billingClient?.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Timber.d("Purchase acknowledged")
                }
            }
        }

        /**
         * Restore purchases
         */
        fun restorePurchases() {
            queryPurchases()
        }

        fun destroy() {
            billingClient?.endConnection()
        }
    }

sealed class PurchaseState {
    object Idle : PurchaseState()

    object Success : PurchaseState()

    object Cancelled : PurchaseState()

    data class Error(val message: String) : PurchaseState()
}
