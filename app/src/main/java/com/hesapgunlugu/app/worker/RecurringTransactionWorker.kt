package com.hesapgunlugu.app.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.hesapgunlugu.app.core.data.local.RecurringTransactionDao
import com.hesapgunlugu.app.core.data.local.TransactionDao
import com.hesapgunlugu.app.core.data.local.TransactionEntity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Recurring Transaction Worker
 *
 * WorkManager ile otomatik olarak çalışır ve tekrarlayan transaction'ları execute eder.
 * Günde bir kez kontrol eder ve süresi gelen işlemleri otomatik olarak oluşturur.
 */
@HiltWorker
class RecurringTransactionWorker
    @AssistedInject
    constructor(
        @Assisted context: Context,
        @Assisted params: WorkerParameters,
        private val recurringDao: RecurringTransactionDao,
        private val transactionDao: TransactionDao,
    ) : CoroutineWorker(context, params) {
        override suspend fun doWork(): Result {
            return try {
                Timber.d("RecurringTransactionWorker başlatıldı")

                val currentTime = System.currentTimeMillis()
                val dueTransactions = recurringDao.getDueRecurringTransactions(currentTime)

                Timber.d("${dueTransactions.size} adet süresinde olan recurring transaction bulundu")

                dueTransactions.forEach { recurring ->
                    executeRecurringTransaction(recurring)
                }

                Timber.d("RecurringTransactionWorker başarıyla tamamlandı")
                Result.success()
            } catch (e: Exception) {
                Timber.e(e, "RecurringTransactionWorker hatası")
                Result.retry()
            }
        }

        private suspend fun executeRecurringTransaction(recurring: com.hesapgunlugu.app.core.data.local.RecurringTransactionEntity) {
            try {
                // 1. Yeni transaction oluştur
                val newTransaction =
                    TransactionEntity(
                        // Auto-generate
                        id = 0,
                        title = recurring.title,
                        amount = recurring.amount,
                        date = recurring.nextExecutionDate,
                        type = recurring.type,
                        category = recurring.category,
                        emoji = recurring.emoji,
                    )

                transactionDao.insertTransaction(newTransaction)
                Timber.d("Recurring transaction execute edildi: ${recurring.title}")

                // 2. Bir sonraki execution tarihini hesapla
                val nextExecutionDate =
                    calculateNextExecutionDate(
                        currentDate = recurring.nextExecutionDate,
                        frequency = recurring.frequency,
                    )

                // 3. End date kontrolü
                val endDate = recurring.endDate
                if (endDate != null && nextExecutionDate > endDate) {
                    // Recurring transaction'ı pasif yap
                    recurringDao.setActive(recurring.id, false)
                    Timber.d("Recurring transaction end date'e ulaştı, pasif yapıldı: ${recurring.title}")
                } else {
                    // Execution tarihlerini güncelle
                    recurringDao.updateExecutionDates(
                        id = recurring.id,
                        executionDate = recurring.nextExecutionDate,
                        nextDate = nextExecutionDate,
                    )
                    Timber.d("Next execution date: ${java.util.Date(nextExecutionDate)}")
                }
            } catch (e: Exception) {
                Timber.e(e, "Recurring transaction execute edilemedi: ${recurring.title}")
            }
        }

        private fun calculateNextExecutionDate(
            currentDate: Long,
            frequency: String,
        ): Long {
            val calendar =
                Calendar.getInstance().apply {
                    timeInMillis = currentDate
                }

            when (frequency) {
                "DAILY" -> calendar.add(Calendar.DAY_OF_MONTH, 1)
                "WEEKLY" -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
                "MONTHLY" -> calendar.add(Calendar.MONTH, 1)
                "YEARLY" -> calendar.add(Calendar.YEAR, 1)
                else -> calendar.add(Calendar.MONTH, 1) // Default: Monthly
            }

            return calendar.timeInMillis
        }

        companion object {
            const val WORK_NAME = "recurring_transaction_worker"
            val REPEAT_INTERVAL = TimeUnit.HOURS.toMillis(24) // Günde bir
        }
    }
