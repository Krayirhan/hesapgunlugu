package com.hesapgunlugu.app.core.domain.usecase.analytics

import com.hesapgunlugu.app.core.domain.model.CategoryBudgetStatus
import com.hesapgunlugu.app.core.domain.model.Transaction
import com.hesapgunlugu.app.core.domain.model.TransactionType
import javax.inject.Inject

/**
 * Kategori bazlı bütçe durumlarını hesaplar
 */
class CalculateCategoryBudgetsUseCase
    @Inject
    constructor() {
        operator fun invoke(
            transactions: List<Transaction>,
            categoryBudgets: Map<String, Double>,
        ): List<CategoryBudgetStatus> {
            val expenseTransactions = transactions.filter { it.type == TransactionType.EXPENSE }

            return categoryBudgets.map { (category, limit) ->
                val spent =
                    expenseTransactions
                        .filter { it.category == category }
                        .sumOf { it.amount }

                CategoryBudgetStatus(
                    categoryName = category,
                    spentAmount = spent,
                    budgetLimit = limit,
                    progress = if (limit > 0) (spent / limit).toFloat().coerceIn(0f, 1f) else 0f,
                    isOverBudget = spent > limit,
                )
            }.sortedByDescending { it.progress }
        }
    }
