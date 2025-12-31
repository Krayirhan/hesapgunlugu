package com.hesapgunlugu.app.core.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Planlı İşlemler için Data Access Object
 */
@Dao
interface ScheduledPaymentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(payment: ScheduledPaymentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: ScheduledPaymentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayments(payments: List<ScheduledPaymentEntity>)

    @Update
    suspend fun update(payment: ScheduledPaymentEntity)

    @Delete
    suspend fun delete(payment: ScheduledPaymentEntity)

    @Query("DELETE FROM scheduled_payments WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM scheduled_payments")
    suspend fun deleteAllScheduledPayments()

    @Query("SELECT * FROM scheduled_payments ORDER BY dueDate ASC")
    fun getAllScheduledPayments(): Flow<List<ScheduledPaymentEntity>>

    @Query("SELECT * FROM scheduled_payments WHERE id = :id")
    suspend fun getById(id: Long): ScheduledPaymentEntity?

    @Query("SELECT * FROM scheduled_payments WHERE isRecurring = 1 AND isIncome = 1")
    fun getRecurringIncomes(): Flow<List<ScheduledPaymentEntity>>

    @Query("SELECT * FROM scheduled_payments WHERE isRecurring = 1 AND isIncome = 0")
    fun getRecurringExpenses(): Flow<List<ScheduledPaymentEntity>>

    @Query(
        "SELECT * FROM scheduled_payments WHERE isPaid = 0 AND dueDate BETWEEN :startDate AND :endDate ORDER BY dueDate ASC",
    )
    fun getUpcomingPayments(
        startDate: Long,
        endDate: Long,
    ): Flow<List<ScheduledPaymentEntity>>

    @Query("UPDATE scheduled_payments SET isPaid = :isPaid WHERE id = :id")
    suspend fun updatePaidStatus(
        id: Long,
        isPaid: Boolean,
    )

    /**
     * Marks a payment as paid or unpaid.
     * Alias for updatePaidStatus for better semantic meaning.
     */
    suspend fun markAsPaid(
        id: Long,
        isPaid: Boolean,
    ) = updatePaidStatus(id, isPaid)

    /**
     * Get all scheduled payments for GDPR data export
     * Returns all scheduled payments for compliance reporting
     */
    @Query("SELECT * FROM scheduled_payments ORDER BY dueDate ASC")
    suspend fun getAllScheduledPaymentsForExport(): List<ScheduledPaymentEntity>
}
