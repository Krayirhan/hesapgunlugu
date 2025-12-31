package com.hesapgunlugu.app.core.domain.repository

import com.hesapgunlugu.app.core.domain.model.RecurrenceType
import java.util.Date

/**
 * Repository interface for recurring rules
 * Clean Architecture: Domain layer defines the contract
 */
interface RecurringRuleRepository {
    /**
     * Insert a new recurring rule
     * @return the ID of the inserted rule
     */
    suspend fun insert(
        scheduledPaymentId: Long,
        recurrenceType: RecurrenceType,
        interval: Int = 1,
        dayOfMonth: Int? = null,
        daysOfWeek: List<Int>? = null,
        endDate: Date? = null,
        maxOccurrences: Int? = null,
    ): Long

    /**
     * Get all recurring rules for a scheduled payment
     */
    suspend fun getByScheduledPaymentId(scheduledPaymentId: Long): List<RecurringRuleData>

    /**
     * Delete a recurring rule
     */
    suspend fun delete(id: Long)

    /**
     * Update last generated date
     */
    suspend fun updateLastGenerated(
        id: Long,
        date: Date,
    )
}

/**
 * Domain data class for recurring rule (without Room annotations)
 */
data class RecurringRuleData(
    val id: Long,
    val scheduledPaymentId: Long,
    val recurrenceType: RecurrenceType,
    val interval: Int,
    val dayOfMonth: Int?,
    val daysOfWeek: List<Int>?,
    val endDate: Date?,
    val maxOccurrences: Int?,
    val currentOccurrences: Int,
    val lastGenerated: Date?,
    val isActive: Boolean,
)
