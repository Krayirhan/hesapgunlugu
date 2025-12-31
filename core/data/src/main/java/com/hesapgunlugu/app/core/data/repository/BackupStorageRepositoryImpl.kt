package com.hesapgunlugu.app.core.data.repository

import androidx.room.withTransaction
import com.hesapgunlugu.app.core.data.local.AppDatabase
import com.hesapgunlugu.app.core.data.local.ScheduledPaymentDao
import com.hesapgunlugu.app.core.data.local.TransactionDao
import com.hesapgunlugu.app.core.data.mapper.ScheduledPaymentMapper
import com.hesapgunlugu.app.core.data.mapper.TransactionMapper
import com.hesapgunlugu.app.core.domain.model.ScheduledPayment
import com.hesapgunlugu.app.core.domain.model.Transaction
import com.hesapgunlugu.app.core.domain.repository.BackupStorageRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupStorageRepositoryImpl
    @Inject
    constructor(
        private val database: AppDatabase,
        private val transactionDao: TransactionDao,
        private val scheduledPaymentDao: ScheduledPaymentDao,
    ) : BackupStorageRepository {
        override suspend fun replaceAll(
            transactions: List<Transaction>,
            scheduledPayments: List<ScheduledPayment>,
        ) {
            database.withTransaction {
                transactionDao.deleteAllTransactions()
                scheduledPaymentDao.deleteAllScheduledPayments()
                transactionDao.insertTransactions(TransactionMapper.toEntityList(transactions))
                scheduledPaymentDao.insertPayments(ScheduledPaymentMapper.toEntityList(scheduledPayments))
            }
        }

        override suspend fun addAll(
            transactions: List<Transaction>,
            scheduledPayments: List<ScheduledPayment>,
        ) {
            database.withTransaction {
                transactionDao.insertTransactions(TransactionMapper.toEntityList(transactions))
                scheduledPaymentDao.insertPayments(ScheduledPaymentMapper.toEntityList(scheduledPayments))
            }
        }
    }
