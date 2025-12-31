package com.hesapgunlugu.app.core.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.hesapgunlugu.app.core.data.local.ScheduledPaymentDao
import com.hesapgunlugu.app.core.data.local.TransactionDao
import com.hesapgunlugu.app.core.data.local.TransactionEntity
import com.hesapgunlugu.app.core.data.local.dao.RecurringRuleDao
import com.hesapgunlugu.app.core.data.model.RecurringRule
import com.hesapgunlugu.app.core.data.model.getNextOccurrence
import com.hesapgunlugu.app.core.data.model.isValid
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.Calendar
import java.util.Date

@HiltWorker
class RecurringPaymentWorker
    @AssistedInject
    constructor(
        @Assisted appContext: Context,
        @Assisted workerParams: WorkerParameters,
        private val recurringRuleDao: RecurringRuleDao,
        private val scheduledPaymentDao: ScheduledPaymentDao,
        private val transactionDao: TransactionDao,
    ) : CoroutineWorker(appContext, workerParams) {
        override suspend fun doWork(): Result {
            return try {
                processRecurringPayments()
                Result.success()
            } catch (e: Exception) {
                Result.retry()
            }
        }

        private suspend fun processRecurringPayments() {
            val activeRules = recurringRuleDao.getAllActive().first()
            val today =
                Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.time

            activeRules.forEach { rule ->
                if (rule.isValid()) {
                    processRule(rule, today)
                } else {
                    recurringRuleDao.update(rule.copy(isActive = false))
                }
            }
        }

        private suspend fun processRule(
            rule: RecurringRule,
            today: Date,
        ) {
            val scheduledPayment = scheduledPaymentDao.getById(rule.scheduledPaymentId) ?: return
            val nextDate = rule.getNextOccurrence(fromDate = Date(scheduledPayment.dueDate)) ?: return

            if (!nextDate.after(today)) {
                val existingTransaction =
                    transactionDao.findByScheduledPaymentAndDate(
                        scheduledPaymentId = scheduledPayment.id,
                        date = nextDate.time,
                    )

                if (existingTransaction != null) {
                    recurringRuleDao.update(
                        rule.copy(
                            currentOccurrences = rule.currentOccurrences + 1,
                            lastGenerated = nextDate,
                            updatedAt = Date(),
                        ),
                    )
                    return
                }

                val transaction =
                    TransactionEntity(
                        title = scheduledPayment.title,
                        amount = scheduledPayment.amount,
                        type = if (scheduledPayment.isIncome) "INCOME" else "EXPENSE",
                        category = scheduledPayment.category,
                        date = nextDate.time,
                        emoji = scheduledPayment.emoji,
                        scheduledPaymentId = scheduledPayment.id,
                    )

                transactionDao.insertTransaction(transaction)

                recurringRuleDao.update(
                    rule.copy(
                        currentOccurrences = rule.currentOccurrences + 1,
                        lastGenerated = nextDate,
                        updatedAt = Date(),
                    ),
                )
            }
        }
    }
