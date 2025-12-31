package com.hesapgunlugu.app.core.backup.export

import android.content.Context
import android.net.Uri
import com.google.gson.GsonBuilder
import com.hesapgunlugu.app.core.backup.BackupEncryption
import com.hesapgunlugu.app.core.backup.BackupPasswordValidator
import com.hesapgunlugu.app.core.backup.BackupPayload
import com.hesapgunlugu.app.core.backup.BackupResult
import com.hesapgunlugu.app.core.backup.R
import com.hesapgunlugu.app.core.backup.serialization.BackupSerializer
import com.hesapgunlugu.app.core.domain.repository.ScheduledPaymentRepository
import com.hesapgunlugu.app.core.domain.repository.TransactionRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Backup dışa aktarma işlemlerini yönetir.
 * Single Responsibility: Sadece export işlemleri.
 */
@Singleton
class BackupExporter
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val transactionRepository: TransactionRepository,
        private val scheduledPaymentRepository: ScheduledPaymentRepository,
        private val serializer: BackupSerializer,
    ) {
        private val gson = GsonBuilder().setPrettyPrinting().create()

        /**
         * Tüm verileri JSON formatında dışa aktar.
         */
        suspend fun exportPlain(uri: Uri): BackupResult =
            withContext(Dispatchers.IO) {
                try {
                    val transactions = transactionRepository.getAllTransactions().first()
                    val scheduledPayments = scheduledPaymentRepository.getAllScheduledPayments().first()

                    val backupData =
                        serializer.createBackupData(
                            transactions = transactions,
                            scheduledPayments = scheduledPayments,
                            encrypted = false,
                        )

                    val json = gson.toJson(backupData)
                    writeToUri(uri, json)

                    Timber.d(
                        "Export successful: %d transactions, %d scheduled payments",
                        transactions.size,
                        scheduledPayments.size,
                    )
                    BackupResult.Success(
                        R.string.backup_export_success,
                        listOf(transactions.size, scheduledPayments.size),
                    )
                } catch (e: Exception) {
                    Timber.e(e, "Export failed")
                    BackupResult.Error(R.string.backup_export_error, listOf(e.message ?: ""))
                }
            }

        suspend fun exportPlainToJson(): Result<BackupPayload> =
            withContext(Dispatchers.IO) {
                try {
                    val transactions = transactionRepository.getAllTransactions().first()
                    val scheduledPayments = scheduledPaymentRepository.getAllScheduledPayments().first()

                    val backupData =
                        serializer.createBackupData(
                            transactions = transactions,
                            scheduledPayments = scheduledPayments,
                            encrypted = false,
                        )

                    val json = gson.toJson(backupData)
                    Result.success(
                        BackupPayload(
                            json = json,
                            transactionCount = transactions.size,
                            scheduledPaymentCount = scheduledPayments.size,
                        ),
                    )
                } catch (e: Exception) {
                    Result.failure(e)
                }
            }

        /**
         * Tüm verileri şifreli olarak dışa aktar (AES-256-GCM).
         */
        suspend fun exportEncrypted(
            uri: Uri,
            password: String,
        ): BackupResult =
            withContext(Dispatchers.IO) {
                try {
                    val validation = BackupPasswordValidator.validate(password)
                    if (!validation.isValid || validation.score < 60) {
                        return@withContext BackupResult.Error(R.string.backup_password_invalid)
                    }

                    val transactions = transactionRepository.getAllTransactions().first()
                    val scheduledPayments = scheduledPaymentRepository.getAllScheduledPayments().first()

                    val backupData =
                        serializer.createBackupData(
                            transactions = transactions,
                            scheduledPayments = scheduledPayments,
                            encrypted = true,
                        )

                    val json = gson.toJson(backupData)
                    val encryptedData =
                        BackupEncryption.encrypt(json, password)
                            ?: return@withContext BackupResult.Error(R.string.backup_encrypt_error)

                    writeToUri(uri, encryptedData)

                    Timber.d(
                        "Encrypted export successful: %d transactions, %d scheduled payments",
                        transactions.size,
                        scheduledPayments.size,
                    )
                    BackupResult.Success(
                        R.string.backup_export_encrypted_success,
                        listOf(transactions.size, scheduledPayments.size),
                    )
                } catch (e: Exception) {
                    Timber.e(e, "Encrypted export failed")
                    BackupResult.Error(R.string.backup_export_encrypted_error, listOf(e.message ?: ""))
                }
            }

        /**
         * URI'ye veri yaz.
         */
        private fun writeToUri(
            uri: Uri,
            content: String,
        ) {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                OutputStreamWriter(outputStream).use { writer ->
                    writer.write(content)
                }
            }
        }

        /**
         * Plain backup dosya adı oluştur.
         */
        fun generateFileName(): String {
            val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            return "finans_backup_${dateFormat.format(Date())}.json"
        }

        /**
         * Şifreli backup dosya adı oluştur.
         */
        fun generateEncryptedFileName(): String {
            val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            return "finans_backup_encrypted_${dateFormat.format(Date())}.bak"
        }
    }
