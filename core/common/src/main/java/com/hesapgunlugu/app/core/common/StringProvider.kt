package com.hesapgunlugu.app.core.common

/**
 * Provides localized strings for ViewModel layer
 * This enables string resources usage in ViewModels without Context dependency
 */
interface StringProvider {
    // Success messages
    fun transactionAdded(): String

    fun transactionUpdated(): String

    fun transactionDeleted(): String

    fun scheduledAdded(): String

    fun scheduledDeleted(): String

    fun paymentCompleted(): String

    fun paymentsCompleted(count: Int): String

    fun somePaymentsFailed(count: Int): String

    fun settingsSaved(): String

    fun categoryBudgetUpdated(): String

    fun categoryBudgetAdded(): String

    // Error messages
    fun errorGeneric(): String

    fun errorLoading(): String

    fun errorSaving(): String

    fun errorDeleting(): String

    fun errorNetwork(): String

    fun errorEmptyTitle(): String

    fun errorInvalidAmount(): String

    fun errorSelectCategory(): String

    // Greeting messages
    fun getGreeting(hour: Int): String

    // Budget warnings
    fun budgetWarningExceededTitle(): String

    fun budgetWarningExceededMessage(limitFormatted: String): String

    fun budgetWarningCriticalTitle(): String

    fun budgetWarningCriticalMessage(percent: Int): String

    fun budgetWarningWarningTitle(): String

    fun budgetWarningWarningMessage(percent: Int): String
}
