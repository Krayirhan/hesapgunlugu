package com.hesapgunlugu.app.testutil

import com.hesapgunlugu.app.core.domain.model.CategoryTotal
import com.hesapgunlugu.app.core.domain.model.Transaction
import com.hesapgunlugu.app.core.domain.model.TransactionType
import com.hesapgunlugu.app.core.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.util.Date

class FakeTransactionRepository : TransactionRepository {
    private val transactions = MutableStateFlow<List<Transaction>>(emptyList())

    var shouldReturnError = false
    var errorToReturn: Exception = Exception("Test error")

    override suspend fun addTransaction(transaction: Transaction): Result<Unit> {
        return if (shouldReturnError) {
            Result.failure(errorToReturn)
        } else {
            val currentList = transactions.value.toMutableList()
            val newId = (currentList.maxOfOrNull { it.id } ?: 0) + 1
            currentList.add(transaction.copy(id = newId))
            transactions.value = currentList
            Result.success(Unit)
        }
    }

    override suspend fun deleteTransaction(transaction: Transaction): Result<Unit> {
        return if (shouldReturnError) {
            Result.failure(errorToReturn)
        } else {
            transactions.value = transactions.value.filter { it.id != transaction.id }
            Result.success(Unit)
        }
    }

    override suspend fun updateTransaction(transaction: Transaction): Result<Unit> {
        return if (shouldReturnError) {
            Result.failure(errorToReturn)
        } else {
            transactions.value =
                transactions.value.map {
                    if (it.id == transaction.id) transaction else it
                }
            Result.success(Unit)
        }
    }

    override fun getAllTransactions(): Flow<List<Transaction>> = transactions

    override fun getBalance(): Flow<Double> {
        return combine(getTotalIncome(), getTotalExpense()) { income, expense ->
            income - expense
        }
    }

    override fun getTotalIncome(): Flow<Double> =
        transactions.map { list ->
            list.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        }

    override fun getTotalExpense(): Flow<Double> =
        transactions.map { list ->
            list.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        }

    override fun getCategoryTotals(
        startDate: Date,
        endDate: Date,
    ): Flow<List<CategoryTotal>> =
        transactions.map { list ->
            list.filter {
                it.type == TransactionType.EXPENSE &&
                    it.date.time >= startDate.time &&
                    it.date.time <= endDate.time
            }
                .groupBy { it.category }
                .map { (category, items) ->
                    CategoryTotal(
                        category = category,
                        total = items.sumOf { it.amount },
                    )
                }
                .sortedByDescending { it.total }
        }

    override fun getTransactionsByDateRange(
        startDate: Date,
        endDate: Date,
    ): Flow<List<Transaction>> =
        transactions.map { list ->
            list.filter {
                it.date.time >= startDate.time && it.date.time <= endDate.time
            }
        }

    override fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>> =
        transactions.map { list ->
            list.filter { it.type == type }
        }

    override fun getRecentTransactions(): Flow<List<Transaction>> =
        transactions.map { list ->
            list.sortedByDescending { it.date }.take(5)
        }

    override suspend fun clearAllTransactions(): Result<Unit> {
        return if (shouldReturnError) {
            Result.failure(errorToReturn)
        } else {
            transactions.value = emptyList()
            Result.success(Unit)
        }
    }

    override suspend fun findByScheduledPaymentAndDate(
        scheduledPaymentId: Long,
        date: Date,
    ): Transaction? {
        return transactions.value.firstOrNull {
            it.scheduledPaymentId == scheduledPaymentId && it.date.time == date.time
        }
    }

    fun setTransactions(list: List<Transaction>) {
        transactions.value = list
    }

    fun addTransactionSync(transaction: Transaction) {
        val currentList = transactions.value.toMutableList()
        currentList.add(transaction)
        transactions.value = currentList
    }

    fun clear() {
        transactions.value = emptyList()
        shouldReturnError = false
    }

    fun getTransactionCount(): Int = transactions.value.size

    fun findById(id: Int): Transaction? = transactions.value.find { it.id == id }
}
