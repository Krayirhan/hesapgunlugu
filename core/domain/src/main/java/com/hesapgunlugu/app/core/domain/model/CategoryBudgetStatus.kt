package com.hesapgunlugu.app.core.domain.model

data class CategoryBudgetStatus(
    val categoryName: String,
    val spentAmount: Double,
    val budgetLimit: Double,
    val progress: Float,
    val isOverBudget: Boolean,
)
