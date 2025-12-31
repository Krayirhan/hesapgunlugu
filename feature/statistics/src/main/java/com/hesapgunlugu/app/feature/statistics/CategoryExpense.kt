package com.hesapgunlugu.app.feature.statistics

import androidx.compose.ui.graphics.Color

/**
 * Kategori bazlı harcama verisi
 */
data class CategoryExpense(
    val name: String,
    val emoji: String,
    val amount: Double,
    val percentage: Float,
    val color: Color,
)
