package com.hesapgunlugu.app.core.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Recurring transaction entity.
 * Stores transactions generated on a schedule.
 */
@Entity(tableName = "recurring_transactions")
data class RecurringTransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val amount: Double,
    // INCOME or EXPENSE
    val type: String,
    val category: String,
    val emoji: String,
    // DAILY, WEEKLY, MONTHLY, YEARLY
    val frequency: String,
    // Start time in millis
    val startDate: Long,
    // End time in millis, null means no end
    val endDate: Long? = null,
    // Last time the rule executed
    val lastExecutionDate: Long? = null,
    // Next scheduled execution time
    val nextExecutionDate: Long,
    // Active/inactive flag
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
)
