package com.hesapgunlugu.app.core.data.repository

import com.hesapgunlugu.app.core.data.local.TransactionDao
import com.hesapgunlugu.app.core.data.mapper.TransactionMapper
import com.hesapgunlugu.app.core.data.remote.FirestoreDataSource
import com.hesapgunlugu.app.core.domain.model.CategoryTotal
import com.hesapgunlugu.app.core.domain.model.Transaction
import com.hesapgunlugu.app.core.domain.model.TransactionType
import com.hesapgunlugu.app.core.domain.repository.TransactionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Date
import javax.inject.Inject

/**
 * Transaction Repository Implementation
 *
 * Tüm veritabanı işlemlerinde try-catch ile hata yönetimi sağlar.
 * Domain ve data layer arasında veri dönüşümü yapar.
 *
 * **Özellikler:**
 * - CRUD operations (Create, Read, Update, Delete)
 * - Firebase Firestore senkronizasyonu (kullanıcı bazlı)
 * - Paging 3 desteği ile büyük veri setlerini yönetir
 * - Flow-based reactive data streams
 * - SQL aggregation queries (SUM, GROUP BY)
 * - Error handling with Result type
 *
 * **Dependency Injection:**
 * Hilt tarafından singleton olarak yönetilir.
 *
 * @param dao Room DAO instance
 * @param firestoreDataSource Firebase Firestore veri kaynağı
 * @see TransactionRepository
 * @see TransactionDao
 */
class TransactionRepositoryImpl
    @Inject
    constructor(
        private val dao: TransactionDao,
        private val firestoreDataSource: FirestoreDataSource,
    ) : TransactionRepository {
        private val syncScope = CoroutineScope(Dispatchers.IO)

        /**
         * Yeni bir transaction ekler ve Firestore'a senkronize eder
         *
         * @param transaction Eklenecek transaction
         * @return Result<Unit> Başarı veya hata
         */
        override suspend fun addTransaction(transaction: Transaction): Result<Unit> {
            return try {
                val entity = TransactionMapper.toEntity(transaction)
                dao.insertTransaction(entity)

                // Firestore'a asenkron olarak senkronize et
                syncScope.launch {
                    firestoreDataSource.saveTransaction(entity)
                }

                Result.success(Unit)
            } catch (e: Exception) {
                Timber.e(e, "İşlem eklenirken hata oluştu")
                Result.failure(e)
            }
        }

        override suspend fun deleteTransaction(transaction: Transaction): Result<Unit> {
            return try {
                dao.deleteTransaction(TransactionMapper.toEntity(transaction))

                // Firestore'dan da sil
                syncScope.launch {
                    firestoreDataSource.deleteTransaction(transaction.id)
                }

                Result.success(Unit)
            } catch (e: Exception) {
                Timber.e(e, "İşlem silinirken hata oluştu")
                Result.failure(e)
            }
        }

        override suspend fun updateTransaction(transaction: Transaction): Result<Unit> {
            return try {
                val entity = TransactionMapper.toEntity(transaction)
                dao.updateTransaction(entity)

                // Firestore'a senkronize et
                syncScope.launch {
                    firestoreDataSource.saveTransaction(entity)
                }

                Result.success(Unit)
            } catch (e: Exception) {
                Timber.e(e, "İşlem güncellenirken hata oluştu")
                Result.failure(e)
            }
        }

        override fun getAllTransactions(): Flow<List<Transaction>> {
            return dao.getAllTransactions().map { entities ->
                TransactionMapper.toDomainList(entities)
            }
        }

        override fun getBalance(): Flow<Double> {
            return combine(getTotalIncome(), getTotalExpense()) { income, expense ->
                income - expense
            }
        }

        override fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>> {
            return dao.getTransactionsByType(type.name).map { entities ->
                TransactionMapper.toDomainList(entities)
            }
        }

        // SQL Aggregation implementations
        override fun getTotalIncome(): Flow<Double> = dao.getTotalIncome()

        override fun getTotalExpense(): Flow<Double> = dao.getTotalExpense()

        override fun getTransactionsByDateRange(
            startDate: Date,
            endDate: Date,
        ): Flow<List<Transaction>> {
            return dao.getTransactionsInRange(startDate.time, endDate.time).map { entities ->
                TransactionMapper.toDomainList(entities)
            }
        }

        override suspend fun findByScheduledPaymentAndDate(
            scheduledPaymentId: Long,
            date: Date,
        ): Transaction? {
            return dao.findByScheduledPaymentAndDate(scheduledPaymentId, date.time)
                ?.let { TransactionMapper.toDomain(it) }
        }

        override fun getCategoryTotals(
            startDate: Date,
            endDate: Date,
        ): Flow<List<CategoryTotal>> =
            dao.getCategoryTotals(startDate.time, endDate.time).map { dataList ->
                dataList.map { TransactionMapper.toDomain(it) }
            }

        override fun getRecentTransactions(): Flow<List<Transaction>> {
            return dao.getRecentTransactions().map { entities ->
                TransactionMapper.toDomainList(entities)
            }
        }

        override suspend fun clearAllTransactions(): Result<Unit> {
            return try {
                dao.deleteAllTransactions()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
