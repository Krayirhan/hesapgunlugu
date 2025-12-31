package com.hesapgunlugu.app.core.domain.repository

import com.hesapgunlugu.app.core.domain.model.ScheduledPayment
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Repository interface for managing scheduled and recurring payments.
 *
 * Handles operations for payments that occur on specific dates or
 * repeat on a regular schedule (weekly, monthly, yearly).
 *
 * ## Features
 * - One-time scheduled payments
 * - Recurring income (salary, rent income)
 * - Recurring expenses (bills, subscriptions)
 * - Payment status tracking (paid/unpaid)
 *
 * ## Usage
 * ```kotlin
 * @HiltViewModel
 * class ScheduledViewModel @Inject constructor(
 *     private val repository: ScheduledPaymentRepository
 * ) : ViewModel() {
 *     val upcomingPayments = repository.getUpcomingPayments(today, nextWeek)
 *         .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
 * }
 * ```
 *
 * @see ScheduledPayment
 */
interface ScheduledPaymentRepository {
    /**
     * Gets all scheduled payments as a reactive Flow.
     *
     * @return Flow emitting all scheduled payments, ordered by due date
     */
    fun getAllScheduledPayments(): Flow<List<ScheduledPayment>>

    /**
     * Gets all recurring income payments.
     *
     * Filters to only include payments where [ScheduledPayment.isRecurring] is true
     * and [ScheduledPayment.isIncome] is true.
     *
     * @return Flow emitting list of recurring incomes
     */
    fun getRecurringIncomes(): Flow<List<ScheduledPayment>>

    /**
     * Gets all recurring expense payments.
     *
     * Filters to only include payments where [ScheduledPayment.isRecurring] is true
     * and [ScheduledPayment.isIncome] is false.
     *
     * @return Flow emitting list of recurring expenses
     */
    fun getRecurringExpenses(): Flow<List<ScheduledPayment>>

    /**
     * Gets payments due within a specific date range.
     *
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return Flow emitting list of payments due in the range
     */
    fun getUpcomingPayments(
        startDate: Date,
        endDate: Date,
    ): Flow<List<ScheduledPayment>>

    /**
     * Adds a new scheduled payment.
     *
     * @param payment The payment to add
     * @return ID of the inserted payment
     */
    suspend fun addScheduledPayment(payment: ScheduledPayment): Long

    /**
     * Updates an existing scheduled payment.
     *
     * @param payment The payment with updated values
     */
    suspend fun updateScheduledPayment(payment: ScheduledPayment)

    /**
     * Deletes a scheduled payment.
     *
     * @param payment The payment to delete
     */
    suspend fun deleteScheduledPayment(payment: ScheduledPayment)

    /**
     * Deletes a scheduled payment by ID.
     *
     * @param id The ID of the payment to delete
     */
    suspend fun deleteScheduledPaymentById(id: Long)

    /**
     * Marks a payment as paid or unpaid.
     *
     * @param id The ID of the payment
     * @param isPaid Whether the payment is paid
     */
    suspend fun markAsPaid(
        id: Long,
        isPaid: Boolean = true,
    )

    /**
     * Gets a specific payment by ID.
     *
     * @param id The ID of the payment
     * @return The payment if found, null otherwise
     */
    suspend fun getScheduledPaymentById(id: Long): ScheduledPayment?
}
