package com.hesapgunlugu.app.core.common

/**
 * Helper class for showing notifications to the user
 * This interface enables notification usage in feature modules without Android dependencies
 */
interface NotificationHelper {
    /**
     * Show budget alert notification
     */
    fun showBudgetAlert(
        title: String,
        message: String,
    )

    /**
     * Show category budget warning
     */
    fun showCategoryBudgetWarning(
        category: String,
        currentSpending: Double,
        limit: Double,
    )

    /**
     * Show payment reminder notification
     */
    fun showPaymentReminder(
        paymentId: Long,
        title: String,
        message: String,
        dueDate: Long,
    )

    /**
     * Show general notification
     */
    fun showGeneralNotification(
        title: String,
        message: String,
    )
}
