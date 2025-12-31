package com.hesapgunlugu.app.core.domain.usecase.home

import com.hesapgunlugu.app.core.domain.model.Transaction
import com.hesapgunlugu.app.core.domain.model.TransactionType
import com.hesapgunlugu.app.core.domain.model.UserSettings
import javax.inject.Inject

/**
 * Consolidated use case for calculating home screen dashboard data.
 *
 * This use case reduces the number of dependencies in HomeViewModel
 * and encapsulates complex calculation logic in a reusable component.
 *
 * ## Benefits
 * - Single Responsibility: All dashboard calculations in one place
 * - Testability: Easy to unit test without ViewModel complexity
 * - Reusability: Can be used by other features (widgets, notifications)
 * - Maintainability: Centralized business logic
 */
class CalculateHomeDashboardUseCase
    @Inject
    constructor() {
        operator fun invoke(
            transactions: List<Transaction>,
            settings: UserSettings,
        ): DashboardData {
            val income =
                transactions
                    .filter { it.type == TransactionType.INCOME }
                    .sumOf { it.amount }

            val expenseList =
                transactions
                    .filter { it.type == TransactionType.EXPENSE }

            val totalExpense = expenseList.sumOf { it.amount }
            val balance = income - totalExpense

            // Category expenses mapping
            val categoryExpenses =
                expenseList
                    .groupBy { it.category }
                    .mapValues { entry -> entry.value.sumOf { it.amount } }

            // Financial health score (0-100)
            val healthScore =
                calculateHealthScore(
                    balance = balance,
                    income = income,
                    expense = totalExpense,
                    budgetLimit = settings.monthlyLimit,
                )

            // Savings rate
            val savingsRate =
                if (income > 0) {
                    ((income - totalExpense) / income * 100).toFloat().coerceIn(0f, 100f)
                } else {
                    0f
                }

            // Weekly spending (last 7 days)
            val weeklySpending = calculateWeeklySpending(expenseList)

            // Monthly trend
            val monthlyTrend = calculateMonthlyTrend(weeklySpending)

            // Top spending category
            val topCategory = categoryExpenses.maxByOrNull { it.value }?.key ?: ""

            // Average daily spending
            val avgDaily =
                if (expenseList.isNotEmpty()) {
                    totalExpense / 30.0
                } else {
                    0.0
                }

            return DashboardData(
                totalBalance = balance,
                totalIncome = income,
                totalExpense = totalExpense,
                categoryExpenses = categoryExpenses,
                financialHealthScore = healthScore,
                savingsRate = savingsRate,
                weeklySpending = weeklySpending,
                monthlyTrend = monthlyTrend,
                topSpendingCategory = topCategory,
                averageDailySpending = avgDaily,
            )
        }

        private fun calculateHealthScore(
            balance: Double,
            income: Double,
            expense: Double,
            budgetLimit: Double,
        ): Int {
            var score = 0

            // Positive balance: +30 points
            if (balance > 0) score += 30

            // Savings rate (20%+): +30 points
            val savingsRate = if (income > 0) (balance / income) else 0.0
            score +=
                when {
                    savingsRate >= 0.30 -> 30
                    savingsRate >= 0.20 -> 20
                    savingsRate >= 0.10 -> 10
                    else -> 0
                }

            // Budget discipline: +25 points
            if (budgetLimit > 0) {
                val budgetUsage = expense / budgetLimit
                score +=
                    when {
                        budgetUsage <= 0.80 -> 25
                        budgetUsage <= 0.90 -> 15
                        budgetUsage <= 1.0 -> 5
                        else -> 0
                    }
            } else {
                score += 15 // Default partial score
            }

            // Income/expense balance: +15 points
            if (income > 0) {
                val ratio = expense / income
                score +=
                    when {
                        ratio <= 0.50 -> 15
                        ratio <= 0.70 -> 10
                        ratio <= 0.90 -> 5
                        else -> 0
                    }
            }

            return score.coerceIn(0, 100)
        }

        private fun calculateWeeklySpending(expenses: List<Transaction>): List<Double> {
            val now = System.currentTimeMillis()
            val dayInMillis = 24 * 60 * 60 * 1000L

            return (0..6).map { daysAgo ->
                val startOfDay = now - (daysAgo * dayInMillis)
                val endOfDay = startOfDay + dayInMillis

                expenses
                    .filter { it.timestamp in startOfDay until endOfDay }
                    .sumOf { it.amount }
            }.reversed()
        }

        private fun calculateMonthlyTrend(weeklySpending: List<Double>): String {
            if (weeklySpending.size < 2) return "stable"

            val firstHalf = weeklySpending.take(weeklySpending.size / 2).average()
            val secondHalf = weeklySpending.drop(weeklySpending.size / 2).average()

            return when {
                secondHalf > firstHalf * 1.1 -> "up"
                secondHalf < firstHalf * 0.9 -> "down"
                else -> "stable"
            }
        }
    }

/**
 * Consolidated dashboard data
 */
data class DashboardData(
    val totalBalance: Double,
    val totalIncome: Double,
    val totalExpense: Double,
    val categoryExpenses: Map<String, Double>,
    val financialHealthScore: Int,
    val savingsRate: Float,
    val weeklySpending: List<Double>,
    val monthlyTrend: String,
    val topSpendingCategory: String,
    val averageDailySpending: Double,
)
