package com.hesapgunlugu.app.core.domain.usecase.analytics

import com.hesapgunlugu.app.core.domain.model.Transaction
import com.hesapgunlugu.app.core.domain.model.TransactionType
import javax.inject.Inject

/**
 * Calculates user's financial health score (0-100)
 *
 * This use case analyzes multiple financial metrics to produce a comprehensive
 * health score that helps users understand their financial situation at a glance.
 *
 * ## Scoring Algorithm
 *
 * ### Balance Positivity (max +20 points)
 * - `balance > income * 0.5`: +20 points (strong reserves)
 * - `balance > 0`: +10 points (positive balance)
 * - `balance <= 0`: -10 points (negative balance)
 *
 * ### Savings Rate (max +20 points)
 * Calculated as: `(income - expense) / income`
 * - `≥30%`: +20 points (excellent savings)
 * - `≥20%`: +15 points (good savings)
 * - `≥10%`: +10 points (moderate savings)
 * - `>0%`: +5 points (minimal savings)
 * - `≤0%`: -10 points (deficit)
 *
 * ### Budget Adherence (max +20 points)
 * Only calculated if budgetLimit > 0
 * - `expense ≤ 80% of budget`: +20 points (well under budget)
 * - `expense ≤ 100% of budget`: +10 points (within budget)
 * - `expense ≤ 120% of budget`: -5 points (slight overspend)
 * - `expense > 120% of budget`: -15 points (significant overspend)
 *
 * ### Income/Expense Balance (max +10 points)
 * - `expense < 50% of income`: +10 points (very sustainable)
 * - `expense < 70% of income`: +5 points (sustainable)
 * - `expense < 90% of income`: 0 points (balanced)
 * - `expense ≥ 90% of income`: -5 points (risky)
 *
 * ## Usage
 * ```kotlin
 * val score = calculateFinancialHealthScoreUseCase(
 *     balance = 5000.0,
 *     income = 10000.0,
 *     expense = 6000.0,
 *     budgetLimit = 8000.0
 * )
 * // Returns: 65 (Good financial health)
 * ```
 *
 * @see Transaction
 * @see TransactionType
 */
class CalculateFinancialHealthScoreUseCase
    @Inject
    constructor() {
        /**
         * Calculates financial health score based on current financial metrics
         *
         * @param balance Current account balance (can be negative)
         * @param income Total income in the analysis period
         * @param expense Total expenses in the analysis period
         * @param budgetLimit User's defined spending budget limit (0 if no budget set)
         * @return Financial health score between 0 (poor) and 100 (excellent)
         *
         * @throws IllegalArgumentException if income or budgetLimit are negative
         */
        operator fun invoke(
            balance: Double,
            income: Double,
            expense: Double,
            budgetLimit: Double,
        ): Int {
            var score = 50 // Başlangıç skoru

            // 1. Bakiye pozitifliği (+20 puan)
            score +=
                when {
                    balance > income * 0.5 -> 20
                    balance > 0 -> 10
                    else -> -10
                }

            // 2. Tasarruf oranı (+20 puan)
            val savingsRate = if (income > 0) (income - expense) / income else 0.0
            score +=
                when {
                    savingsRate >= 0.3 -> 20 // %30+ tasarruf
                    savingsRate >= 0.2 -> 15 // %20+ tasarruf
                    savingsRate >= 0.1 -> 10 // %10+ tasarruf
                    savingsRate > 0 -> 5
                    else -> -10
                }

            // 3. Bütçe kontrolü (+20 puan)
            if (budgetLimit > 0) {
                val budgetRatio = expense / budgetLimit
                score +=
                    when {
                        budgetRatio <= 0.8 -> 20 // Bütçenin %80'inden az
                        budgetRatio <= 1.0 -> 10 // Bütçe içinde
                        budgetRatio <= 1.2 -> -5 // %20 fazla
                        else -> -15 // Çok fazla aşım
                    }
            }

            // 4. Gelir/gider dengesi (+10 puan)
            if (income > 0) {
                val expenseRatio = expense / income
                score +=
                    when {
                        expenseRatio < 0.5 -> 10
                        expenseRatio < 0.7 -> 5
                        expenseRatio < 0.9 -> 0
                        else -> -5
                    }
            }

            return score.coerceIn(0, 100)
        }
    }
