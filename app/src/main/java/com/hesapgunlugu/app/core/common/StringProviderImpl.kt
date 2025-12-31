package com.hesapgunlugu.app.core.common

import android.content.Context
import com.hesapgunlugu.app.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of StringProvider for Android
 * Provides localized strings from resources
 */
@Singleton
class StringProviderImpl
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : StringProvider {
        override fun transactionAdded(): String = context.getString(R.string.transaction_added)

        override fun transactionUpdated(): String = context.getString(R.string.transaction_updated)

        override fun transactionDeleted(): String = context.getString(R.string.transaction_deleted)

        override fun scheduledAdded(): String = context.getString(R.string.scheduled_added)

        override fun scheduledDeleted(): String = context.getString(R.string.scheduled_deleted)

        override fun paymentCompleted(): String = context.getString(R.string.payment_completed)

        override fun paymentsCompleted(count: Int): String = context.getString(R.string.payments_completed, count)

        override fun somePaymentsFailed(count: Int): String = context.getString(R.string.some_payments_failed, count)

        override fun settingsSaved(): String = context.getString(R.string.settings_saved)

        override fun categoryBudgetUpdated(): String = context.getString(R.string.category_budget_updated)

        override fun categoryBudgetAdded(): String = context.getString(R.string.category_budget_added)

        override fun errorGeneric(): String = context.getString(R.string.error_generic)

        override fun errorLoading(): String = context.getString(R.string.error_loading)

        override fun errorSaving(): String = context.getString(R.string.error_saving)

        override fun errorDeleting(): String = context.getString(R.string.error_deleting)

        override fun errorNetwork(): String = context.getString(R.string.error_network)

        override fun errorEmptyTitle(): String = context.getString(R.string.error_empty_title)

        override fun errorInvalidAmount(): String = context.getString(R.string.error_invalid_amount)

        override fun errorSelectCategory(): String = context.getString(R.string.error_select_category)

        override fun getGreeting(hour: Int): String {
            return when (hour) {
                in 5..11 -> context.getString(R.string.greeting_morning)
                in 12..17 -> context.getString(R.string.greeting_afternoon)
                in 18..22 -> context.getString(R.string.greeting_evening)
                else -> context.getString(R.string.greeting_night)
            }
        }

        override fun budgetWarningExceededTitle(): String = context.getString(R.string.budget_warning_exceeded_title)

        override fun budgetWarningExceededMessage(limitFormatted: String): String = context.getString(R.string.budget_warning_exceeded_message, limitFormatted)

        override fun budgetWarningCriticalTitle(): String = context.getString(R.string.budget_warning_critical_title)

        override fun budgetWarningCriticalMessage(percent: Int): String = context.getString(R.string.budget_warning_critical_message, percent)

        override fun budgetWarningWarningTitle(): String = context.getString(R.string.budget_warning_warning_title)

        override fun budgetWarningWarningMessage(percent: Int): String = context.getString(R.string.budget_warning_warning_message, percent)
    }
