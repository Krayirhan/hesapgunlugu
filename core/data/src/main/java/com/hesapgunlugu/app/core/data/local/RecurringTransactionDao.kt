package com.hesapgunlugu.app.core.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Recurring Transaction DAO
 */
@Dao
interface RecurringTransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recurring: RecurringTransactionEntity): Long

    @Update
    suspend fun update(recurring: RecurringTransactionEntity)

    @Delete
    suspend fun delete(recurring: RecurringTransactionEntity)

    @Query("SELECT * FROM recurring_transactions WHERE isActive = 1 ORDER BY nextExecutionDate ASC")
    fun getAllActive(): Flow<List<RecurringTransactionEntity>>

    @Query("SELECT * FROM recurring_transactions WHERE id = :id")
    suspend fun getById(id: Int): RecurringTransactionEntity?

    @Query("SELECT * FROM recurring_transactions WHERE isActive = 1 AND nextExecutionDate <= :currentTime")
    suspend fun getDueRecurringTransactions(currentTime: Long = System.currentTimeMillis()): List<RecurringTransactionEntity>

    @Query(
        "UPDATE recurring_transactions SET lastExecutionDate = :executionDate, nextExecutionDate = :nextDate WHERE id = :id",
    )
    suspend fun updateExecutionDates(
        id: Int,
        executionDate: Long,
        nextDate: Long,
    )

    @Query("UPDATE recurring_transactions SET isActive = :active WHERE id = :id")
    suspend fun setActive(
        id: Int,
        active: Boolean,
    )
}
