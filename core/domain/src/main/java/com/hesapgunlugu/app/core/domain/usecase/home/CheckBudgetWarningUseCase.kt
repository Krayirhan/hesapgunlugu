package com.hesapgunlugu.app.core.domain.usecase.home

import com.hesapgunlugu.app.core.common.StringProvider
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale
import javax.inject.Inject

/**
 * Use case for checking budget status and generating appropriate warnings.
 *
 * Encapsulates budget threshold logic for better testability
 * and consistent budget notifications across the app.
 */
class CheckBudgetWarningUseCase
    @Inject
    constructor(
        private val stringProvider: StringProvider,
    ) {
        /**
         * Check budget status and return appropriate warning if needed.
         *
         * @param monthlyLimit User's configured monthly budget limit
         * @param currentExpense Current total expense for the month
         * @return BudgetWarning if threshold exceeded, null otherwise
         */
        operator fun invoke(
            monthlyLimit: Double,
            currentExpense: Double,
        ): BudgetWarning? {
            if (monthlyLimit <= 0) return null

            val ratio = currentExpense / monthlyLimit
            val percentUsed = (ratio * 100).toInt()
            val limitFormatted = formatCurrency(monthlyLimit)

            return when {
                ratio >= 1.0 ->
                    BudgetWarning(
                        title = stringProvider.budgetWarningExceededTitle(),
                        message = stringProvider.budgetWarningExceededMessage(limitFormatted),
                        severity = BudgetSeverity.EXCEEDED,
                    )
                ratio >= 0.9 ->
                    BudgetWarning(
                        title = stringProvider.budgetWarningCriticalTitle(),
                        message = stringProvider.budgetWarningCriticalMessage(percentUsed),
                        severity = BudgetSeverity.CRITICAL,
                    )
                ratio >= 0.8 ->
                    BudgetWarning(
                        title = stringProvider.budgetWarningWarningTitle(),
                        message = stringProvider.budgetWarningWarningMessage(percentUsed),
                        severity = BudgetSeverity.WARNING,
                    )
                else -> null
            }
        }

        /**
         * Calculate remaining budget and days
         */
        fun getRemainingBudget(
            monthlyLimit: Double,
            currentExpense: Double,
        ): RemainingBudget {
            val remaining = (monthlyLimit - currentExpense).coerceAtLeast(0.0)
            val percentUsed = if (monthlyLimit > 0) (currentExpense / monthlyLimit * 100).toFloat() else 0f

            return RemainingBudget(
                amount = remaining,
                percentUsed = percentUsed.coerceIn(0f, 100f),
            )
        }

        private fun formatCurrency(amount: Double): String {
            val format = NumberFormat.getCurrencyInstance(Locale.getDefault())
            format.currency = Currency.getInstance("TRY")
            format.maximumFractionDigits = 0
            return format.format(amount)
        }
    }

data class BudgetWarning(
    val title: String,
    val message: String,
    val severity: BudgetSeverity,
)

enum class BudgetSeverity {
    WARNING, // 80-89%
    CRITICAL, // 90-99%
    EXCEEDED, // 100%+
}

data class RemainingBudget(
    val amount: Double,
    val percentUsed: Float,
)
