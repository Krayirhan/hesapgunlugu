package com.hesapgunlugu.app.core.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Scheduled payment entity.
 * Stores recurring income/expense definitions.
 */
@Entity(
    tableName = "scheduled_payments",
    indices = [
        Index(value = ["dueDate"]),
        Index(value = ["isRecurring"]),
        Index(value = ["isPaid"]),
        Index(value = ["dueDate", "isPaid"]),
    ],
)
data class ScheduledPaymentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val isIncome: Boolean,
    val isRecurring: Boolean,
    // Example values: "Weekly", "Monthly", "Yearly"
    val frequency: String,
    // Epoch milliseconds
    val dueDate: Long,
    val emoji: String,
    val isPaid: Boolean = false,
    val category: String = "",
    val createdAt: Long = System.currentTimeMillis(),
)
