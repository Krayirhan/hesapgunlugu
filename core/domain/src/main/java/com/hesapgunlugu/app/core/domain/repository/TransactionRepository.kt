package com.hesapgunlugu.app.core.domain.repository

import com.hesapgunlugu.app.core.domain.model.CategoryTotal
import com.hesapgunlugu.app.core.domain.model.Transaction
import com.hesapgunlugu.app.core.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Repository interface for managing financial transactions.
 *
 * Provides CRUD operations, aggregation queries, and paging support
 * for financial transactions. All data operations are reactive using
 * Kotlin Flow for real-time updates.
 *
 * ## Usage
 * ```kotlin
 * class MyViewModel @Inject constructor(
 *     private val repository: TransactionRepository
 * ) {
 *     val transactions = repository.getAllTransactions()
 *         .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
 * }
 * ```
 *
 * ## Thread Safety
 * All suspend functions should be called from a coroutine context.
 * Flow emissions occur on the IO dispatcher.
 *
 * @see Transaction
 */
interface TransactionRepository {
    /**
     * Adds a new transaction to the database.
     *
     * @param transaction The transaction to add
     * @return [Result.success] if added successfully, [Result.failure] with exception otherwise
     * @throws IllegalArgumentException if transaction data is invalid
     */
    suspend fun addTransaction(transaction: Transaction): Result<Unit>

    /**
     * Deletes an existing transaction from the database.
     *
     * @param transaction The transaction to delete (must have valid id)
     * @return [Result.success] if deleted successfully, [Result.failure] otherwise
     */
    suspend fun deleteTransaction(transaction: Transaction): Result<Unit>

    /**
     * Updates an existing transaction in the database.
     *
     * @param transaction The transaction with updated values
     * @return [Result.success] if updated successfully, [Result.failure] otherwise
     */
    suspend fun updateTransaction(transaction: Transaction): Result<Unit>

    /**
     * Retrieves all transactions ordered by date (newest first).
     *
     * @return A Flow emitting the list of all transactions
     */
    fun getAllTransactions(): Flow<List<Transaction>>

    /**
     * Retrieves the total balance (Income - Expense).
     *
     * @return A Flow emitting the current balance
     */
    fun getBalance(): Flow<Double>

    /**
     * Retrieves the total income.
     *
     * @return A Flow emitting the total income
     */
    fun getTotalIncome(): Flow<Double>

    /**
     * Retrieves the total expense.
     *
     * @return A Flow emitting the total expense
     */
    fun getTotalExpense(): Flow<Double>

    /**
     * Retrieves total expenses grouped by category.
     *
     * @param startDate Start of the range (inclusive)
     * @param endDate End of the range (inclusive)
     * @return A Flow emitting a list of [CategoryTotal] objects
     */
    fun getCategoryTotals(
        startDate: Date,
        endDate: Date,
    ): Flow<List<CategoryTotal>>

    /**
     * Retrieves transactions within a specific date range.
     *
     * @param startDate Start of the range (inclusive)
     * @param endDate End of the range (inclusive)
     * @return A Flow emitting the list of matching transactions
     */
    fun getTransactionsByDateRange(
        startDate: Date,
        endDate: Date,
    ): Flow<List<Transaction>>

    /**
     * Finds a transaction created from a scheduled payment on a specific date.
     *
     * @param scheduledPaymentId The source scheduled payment ID
     * @param date The date to match (day-level match expected by caller)
     * @return The transaction if found, null otherwise
     */
    suspend fun findByScheduledPaymentAndDate(
        scheduledPaymentId: Long,
        date: Date,
    ): Transaction?

    /**
     * Retrieves transactions filtered by type (Income/Expense).
     *
     * @param type The transaction type to filter by
     * @return A Flow emitting the list of matching transactions
     */
    fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>>

    /**
     * Retrieves the 5 most recent transactions.
     *
     * @return A Flow emitting the list of recent transactions
     */
    fun getRecentTransactions(): Flow<List<Transaction>>

    /**
     * Deletes all transactions from the database.
     *
     * @return [Result.success] if cleared successfully
     */
    suspend fun clearAllTransactions(): Result<Unit>
}
