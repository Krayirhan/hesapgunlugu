package com.hesapgunlugu.app.feature.home

import androidx.compose.runtime.Stable
import com.hesapgunlugu.app.core.domain.model.CategoryBudgetStatus
import com.hesapgunlugu.app.core.domain.model.Transaction
import com.hesapgunlugu.app.core.ui.components.UpcomingPaymentPreview

@Stable
data class HomeState(
    val totalBalance: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val greeting: String = "",
    val recentTransactions: List<Transaction> = emptyList(),
    val categoryExpenses: Map<String, Double> = emptyMap(),
    val budgetStatuses: List<CategoryBudgetStatus> = emptyList(),
    // New metrics.
    val financialHealthScore: Int = 0,
    val savingsRate: Float = 0f,
    val weeklySpending: List<Double> = emptyList(),
    val monthlyTrend: String = "stable",
    val topSpendingCategory: String = "",
    val averageDailySpending: Double = 0.0,
    // Upcoming payments (scheduled integration).
    val upcomingPayments: List<UpcomingPaymentPreview> = emptyList(),
    val upcomingPaymentsTotal: Double = 0.0,
    // Notification count.
    val unreadNotificationCount: Int = 0,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: String? = null,
)
