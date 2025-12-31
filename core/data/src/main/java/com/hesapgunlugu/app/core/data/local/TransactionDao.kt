package com.hesapgunlugu.app.core.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    // Veri ekle (Çakışma olursa eskisiyle değiştir)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<TransactionEntity>)

    // Veri sil
    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    // YENİ: Veri Güncelle
    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    // Tüm harcamaları tarihe göre (yeni en üstte) getir
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    // Toplam tutarı hesapla
    @Query("SELECT SUM(amount) FROM transactions")
    fun getTotalAmount(): Flow<Double?>

    // Son işlemler (default 10)
    @Query("SELECT * FROM transactions ORDER BY date DESC LIMIT 10")
    fun getRecentTransactions(): Flow<List<TransactionEntity>>

    // Tüm işlemleri sil
    @Query("DELETE FROM transactions")
    suspend fun deleteAllTransactions()

    // ===== SQL AGGREGATION QUERIES =====

    // Toplam gelir (type = INCOME)
    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM transactions WHERE type = 'INCOME'")
    fun getTotalIncome(): Flow<Double>

    // Toplam gider (type = EXPENSE)
    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM transactions WHERE type = 'EXPENSE'")
    fun getTotalExpense(): Flow<Double>

    // Belirli tarih aralığında toplam gelir
    @Query(
        "SELECT COALESCE(SUM(amount), 0.0) FROM transactions WHERE type = 'INCOME' AND date >= :startDate AND date <= :endDate",
    )
    fun getTotalIncomeInRange(
        startDate: Long,
        endDate: Long,
    ): Flow<Double>

    // Belirli tarih aralığında toplam gider
    @Query(
        "SELECT COALESCE(SUM(amount), 0.0) FROM transactions WHERE type = 'EXPENSE' AND date >= :startDate AND date <= :endDate",
    )
    fun getTotalExpenseInRange(
        startDate: Long,
        endDate: Long,
    ): Flow<Double>

    // Kategoriye göre toplam harcama
    @Query(
        "SELECT COALESCE(SUM(amount), 0.0) FROM transactions WHERE type = 'EXPENSE' AND category = :category AND date >= :startDate AND date <= :endDate",
    )
    fun getCategoryTotalInRange(
        category: String,
        startDate: Long,
        endDate: Long,
    ): Flow<Double>

    // Tarih aralığındaki işlemler
    @Query("SELECT * FROM transactions WHERE date >= :startDate AND date <= :endDate ORDER BY date DESC")
    fun getTransactionsInRange(
        startDate: Long,
        endDate: Long,
    ): Flow<List<TransactionEntity>>

    // Kategori bazlı toplam gider listesi (istatistikler için)
    @Query(
        """
        SELECT category, COALESCE(SUM(amount), 0.0) as total 
        FROM transactions 
        WHERE type = 'EXPENSE' AND date >= :startDate AND date <= :endDate 
        GROUP BY category
    """,
    )
    fun getCategoryTotals(
        startDate: Long,
        endDate: Long,
    ): Flow<List<DataCategoryTotal>>

    // İşlem sayısı
    @Query("SELECT COUNT(*) FROM transactions WHERE date >= :startDate AND date <= :endDate")
    fun getTransactionCountInRange(
        startDate: Long,
        endDate: Long,
    ): Flow<Int>

    // ===== PAGING 3 QUERIES =====

    /**
     * Get all transactions with pagination support
     * @param limit Number of items per page
     * @param offset Starting position
     */
    @Query("SELECT * FROM transactions ORDER BY date DESC LIMIT :limit OFFSET :offset")
    suspend fun getAllTransactionsPaged(
        limit: Int,
        offset: Int,
    ): List<TransactionEntity>

    /**
     * Search transactions with pagination
     */
    @Query(
        """
        SELECT * FROM transactions 
        WHERE title LIKE :query OR category LIKE :query 
        ORDER BY date DESC 
        LIMIT :limit OFFSET :offset
    """,
    )
    suspend fun searchTransactionsPaged(
        query: String,
        limit: Int,
        offset: Int,
    ): List<TransactionEntity>

    /**
     * Get transactions by type with pagination
     */
    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY date DESC LIMIT :limit OFFSET :offset")
    suspend fun getTransactionsByTypePaged(
        type: String,
        limit: Int,
        offset: Int,
    ): List<TransactionEntity>

    /**
     * Get transactions by date range with pagination
     */
    @Query(
        """
        SELECT * FROM transactions 
        WHERE date >= :startDate AND date <= :endDate 
        ORDER BY date DESC 
        LIMIT :limit OFFSET :offset
    """,
    )
    suspend fun getTransactionsByDateRangePaged(
        startDate: Long,
        endDate: Long,
        limit: Int,
        offset: Int,
    ): List<TransactionEntity>

    /**
     * Search with type filter and pagination
     */
    @Query(
        """
        SELECT * FROM transactions 
        WHERE (title LIKE :query OR category LIKE :query) AND type = :type 
        ORDER BY date DESC 
        LIMIT :limit OFFSET :offset
    """,
    )
    suspend fun searchTransactionsWithTypePaged(
        query: String,
        type: String,
        limit: Int,
        offset: Int,
    ): List<TransactionEntity>

    /**
     * Get transactions by type (Flow version for backwards compatibility)
     */
    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY date DESC")
    fun getTransactionsByType(type: String): Flow<List<TransactionEntity>>

    /**
     * CRITICAL FIX: Idempotency check for recurring payment worker
     * Prevents duplicate transaction creation if worker runs multiple times
     * @param scheduledPaymentId The source scheduled payment ID
     * @param date The transaction date (epoch milliseconds)
     * @return Existing transaction or null
     */
    @Query("SELECT * FROM transactions WHERE scheduledPaymentId = :scheduledPaymentId AND date = :date LIMIT 1")
    suspend fun findByScheduledPaymentAndDate(
        scheduledPaymentId: Long,
        date: Long,
    ): TransactionEntity?

    /**
     * Get all transactions for GDPR data export
     * Returns all transactions ordered by date for compliance reporting
     */
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    suspend fun getAllTransactionsForExport(): List<TransactionEntity>
}

/**
 * Kategori bazlı toplam için data class (Room query result)
 */
data class DataCategoryTotal(
    val category: String,
    val total: Double,
)
