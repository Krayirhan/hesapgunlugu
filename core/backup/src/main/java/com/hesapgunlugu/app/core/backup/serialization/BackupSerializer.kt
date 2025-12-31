package com.hesapgunlugu.app.core.backup.serialization

import com.hesapgunlugu.app.core.common.time.TimeProvider
import com.hesapgunlugu.app.core.domain.model.ScheduledPayment
import com.hesapgunlugu.app.core.domain.model.Transaction
import com.hesapgunlugu.app.core.domain.model.TransactionType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Backup verileri iÃ§in data class'lar
 */
data class BackupData(
    val version: Int = BackupSerializer.CURRENT_FORMAT_VERSION,
    val createdAt: String,
    val encrypted: Boolean = false,
    val transactions: List<TransactionBackup>,
    val scheduledPayments: List<ScheduledPaymentBackup>,
)

data class TransactionBackup(
    val id: Int,
    val title: String,
    val amount: Double,
    val date: String,
    val type: String,
    val category: String,
)

data class ScheduledPaymentBackup(
    val id: Long,
    val title: String,
    val amount: Double,
    val isIncome: Boolean,
    val isRecurring: Boolean,
    val frequency: String,
    val dueDate: String,
    val emoji: String,
    val isPaid: Boolean,
    val category: String,
)

/**
 * Transaction ve ScheduledPayment modelleri ile Backup modelleri arasÄ±nda dÃ¶nÃ¼ÅŸÃ¼m.
 * Single Responsibility: Sadece model dÃ¶nÃ¼ÅŸÃ¼mÃ¼.
 */
@Singleton
class BackupSerializer
    @Inject
    constructor(
        private val timeProvider: TimeProvider,
    ) {
        companion object {
            const val CURRENT_FORMAT_VERSION = 3
            const val MIN_SUPPORTED_VERSION = 1
        }

        private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

        fun parseDateOrNull(value: String): Date? {
            return runCatching { dateFormat.parse(value) }.getOrNull()
        }

        /**
         * Transaction'Ä± backup formatÄ±na dÃ¶nÃ¼ÅŸtÃ¼r.
         */
        fun toBackup(transaction: Transaction): TransactionBackup {
            return TransactionBackup(
                id = transaction.id,
                title = transaction.title,
                amount = transaction.amount,
                date = dateFormat.format(transaction.date),
                type = transaction.type.name,
                category = transaction.category,
            )
        }

        /**
         * Backup formatÄ±ndan Transaction'a dÃ¶nÃ¼ÅŸtÃ¼r.
         */
        fun fromBackup(backup: TransactionBackup): Transaction {
            return Transaction(
                // New ID will be generated on restore.
                id = backup.id,
                title = backup.title,
                amount = backup.amount,
                date = dateFormat.parse(backup.date) ?: timeProvider.nowDate(),
                type = TransactionType.valueOf(backup.type),
                category = backup.category,
            )
        }

        /**
         * ScheduledPayment'Ä± backup formatÄ±na dÃ¶nÃ¼ÅŸtÃ¼r.
         */
        fun toBackup(scheduledPayment: ScheduledPayment): ScheduledPaymentBackup {
            return ScheduledPaymentBackup(
                id = scheduledPayment.id,
                title = scheduledPayment.title,
                amount = scheduledPayment.amount,
                isIncome = scheduledPayment.isIncome,
                isRecurring = scheduledPayment.isRecurring,
                frequency = scheduledPayment.frequency,
                dueDate = dateFormat.format(scheduledPayment.dueDate),
                emoji = scheduledPayment.emoji,
                isPaid = scheduledPayment.isPaid,
                category = scheduledPayment.category,
            )
        }

        /**
         * Backup formatÄ±ndan ScheduledPayment'a dÃ¶nÃ¼ÅŸtÃ¼r.
         */
        fun fromBackup(backup: ScheduledPaymentBackup): ScheduledPayment {
            return ScheduledPayment(
                // New ID will be generated on restore.
                id = backup.id,
                title = backup.title,
                amount = backup.amount,
                isIncome = backup.isIncome,
                isRecurring = backup.isRecurring,
                frequency = backup.frequency,
                dueDate = dateFormat.parse(backup.dueDate) ?: timeProvider.nowDate(),
                emoji = backup.emoji,
                isPaid = backup.isPaid,
                category = backup.category,
            )
        }

        /**
         * Transaction listesini backup listesine dÃ¶nÃ¼ÅŸtÃ¼r.
         */
        fun transactionsToBackup(transactions: List<Transaction>): List<TransactionBackup> {
            return transactions.map { toBackup(it) }
        }

        /**
         * Backup listesinden Transaction listesi oluÅŸtur.
         */
        fun transactionsFromBackup(backups: List<TransactionBackup>): List<Transaction> {
            return backups.map { fromBackup(it) }
        }

        /**
         * ScheduledPayment listesini backup listesine dÃ¶nÃ¼ÅŸtÃ¼r.
         */
        fun scheduledPaymentsToBackup(payments: List<ScheduledPayment>): List<ScheduledPaymentBackup> {
            return payments.map { toBackup(it) }
        }

        /**
         * Backup listesinden ScheduledPayment listesi oluÅŸtur.
         */
        fun scheduledPaymentsFromBackup(backups: List<ScheduledPaymentBackup>): List<ScheduledPayment> {
            return backups.map { fromBackup(it) }
        }

        /**
         * BackupData oluÅŸtur.
         */
        fun createBackupData(
            transactions: List<Transaction>,
            scheduledPayments: List<ScheduledPayment>,
            encrypted: Boolean = false,
        ): BackupData {
            return BackupData(
                version = CURRENT_FORMAT_VERSION,
                createdAt = dateFormat.format(timeProvider.nowDate()),
                encrypted = encrypted,
                transactions = transactionsToBackup(transactions),
                scheduledPayments = scheduledPaymentsToBackup(scheduledPayments),
            )
        }
    }
