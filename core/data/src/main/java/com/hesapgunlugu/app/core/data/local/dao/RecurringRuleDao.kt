package com.hesapgunlugu.app.core.data.local.dao

import androidx.room.*
import com.hesapgunlugu.app.core.data.model.RecurringRule
import kotlinx.coroutines.flow.Flow

/**
 * DAO for RecurringRule operations
 */
@Dao
interface RecurringRuleDao {
    @Query("SELECT * FROM recurring_rules WHERE id = :id")
    fun getById(id: Long): Flow<RecurringRule?>

    @Query("SELECT * FROM recurring_rules WHERE scheduledPaymentId = :scheduledPaymentId")
    fun getByScheduledPaymentId(scheduledPaymentId: Long): Flow<List<RecurringRule>>

    @Query("SELECT * FROM recurring_rules WHERE isActive = 1")
    fun getAllActive(): Flow<List<RecurringRule>>

    @Query("SELECT * FROM recurring_rules ORDER BY createdAt DESC")
    fun getAll(): Flow<List<RecurringRule>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rule: RecurringRule): Long

    @Update
    suspend fun update(rule: RecurringRule)

    @Delete
    suspend fun delete(rule: RecurringRule)

    @Query("DELETE FROM recurring_rules WHERE scheduledPaymentId = :scheduledPaymentId")
    suspend fun deleteByScheduledPaymentId(scheduledPaymentId: Long)

    @Query("UPDATE recurring_rules SET isActive = :isActive WHERE id = :id")
    suspend fun updateActive(
        id: Long,
        isActive: Boolean,
    )
}
