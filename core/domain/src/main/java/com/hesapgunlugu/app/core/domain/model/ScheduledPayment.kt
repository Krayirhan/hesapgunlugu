package com.hesapgunlugu.app.core.domain.model

import java.util.Date

/**
 * Scheduled payment domain model.
 */
data class ScheduledPayment(
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val isIncome: Boolean,
    val isRecurring: Boolean,
    // Weekly, monthly, yearly.
    val frequency: String = "",
    val dueDate: Date,
    val emoji: String = ":)",
    val isPaid: Boolean = false,
    val category: String = "",
    val createdAt: Date = Date(),
)
