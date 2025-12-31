package com.hesapgunlugu.app.core.backup.import

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import com.hesapgunlugu.app.core.backup.BackupEncryption
import com.hesapgunlugu.app.core.backup.BackupImportSummary
import com.hesapgunlugu.app.core.backup.BackupResult
import com.hesapgunlugu.app.core.backup.R
import com.hesapgunlugu.app.core.backup.serialization.BackupData
import com.hesapgunlugu.app.core.backup.serialization.BackupSerializer
import com.hesapgunlugu.app.core.domain.repository.BackupStorageRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Backup import operations.
 */
@Singleton
class BackupImporter
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val storageRepository: BackupStorageRepository,
        private val serializer: BackupSerializer,
    ) {
        private val gson = Gson()

        suspend fun importPlain(
            uri: Uri,
            replaceExisting: Boolean = false,
        ): BackupResult =
            withContext(Dispatchers.IO) {
                try {
                    val json =
                        readFromUri(uri)
                            ?: return@withContext BackupResult.Error(R.string.backup_file_read_error)

                    val backupData =
                        parseBackupData(json) ?: return@withContext BackupResult.Error(
                            R.string.backup_import_error,
                            listOf("Invalid JSON"),
                        )

                    performImport(backupData, replaceExisting)
                } catch (e: Exception) {
                    Timber.e(e, "Import failed")
                    BackupResult.Error(R.string.backup_import_error, listOf(e.message ?: ""))
                }
            }

        suspend fun importEncrypted(
            uri: Uri,
            password: String,
            replaceExisting: Boolean = false,
        ): BackupResult =
            withContext(Dispatchers.IO) {
                try {
                    val encryptedData =
                        readFromUri(uri)
                            ?: return@withContext BackupResult.Error(R.string.backup_file_read_error)

                    val json =
                        BackupEncryption.decrypt(encryptedData, password)
                            ?: return@withContext BackupResult.Error(R.string.backup_decrypt_error)

                    val backupData =
                        parseBackupData(json) ?: return@withContext BackupResult.Error(
                            R.string.backup_import_encrypted_error,
                            listOf("Invalid JSON"),
                        )

                    performImport(backupData, replaceExisting)
                } catch (e: Exception) {
                    Timber.e(e, "Encrypted import failed")
                    BackupResult.Error(R.string.backup_import_encrypted_error, listOf(e.message ?: ""))
                }
            }

        suspend fun importFromJson(
            json: String,
            replaceExisting: Boolean = false,
        ): BackupResult =
            withContext(Dispatchers.IO) {
                try {
                    val backupData =
                        parseBackupData(json) ?: return@withContext BackupResult.Error(
                            R.string.backup_import_error,
                            listOf("Invalid JSON"),
                        )

                    performImport(backupData, replaceExisting)
                } catch (e: Exception) {
                    Timber.e(e, "Import failed")
                    BackupResult.Error(R.string.backup_import_error, listOf(e.message ?: ""))
                }
            }

        suspend fun importFromJsonSummary(
            json: String,
            replaceExisting: Boolean = false,
        ): Result<BackupImportSummary> =
            withContext(Dispatchers.IO) {
                val backupData =
                    parseBackupData(json) ?: return@withContext Result.failure(
                        IllegalStateException("Invalid JSON"),
                    )

                val report = buildReport(backupData)
                if (!report.isVersionSupported) {
                    return@withContext Result.failure(IllegalStateException("Unsupported backup version"))
                }

                val invalidTotal = report.invalidTransactions + report.invalidScheduledPayments
                if (invalidTotal > 0) {
                    return@withContext Result.failure(IllegalStateException("Invalid records: $invalidTotal"))
                }

                val transactions = serializer.transactionsFromBackup(backupData.transactions)
                val scheduledPayments = serializer.scheduledPaymentsFromBackup(backupData.scheduledPayments)

                if (replaceExisting) {
                    storageRepository.replaceAll(transactions, scheduledPayments)
                } else {
                    storageRepository.addAll(transactions, scheduledPayments)
                }

                Result.success(
                    BackupImportSummary(
                        transactionCount = transactions.size,
                        scheduledPaymentCount = scheduledPayments.size,
                    ),
                )
            }

        suspend fun dryRunPlain(uri: Uri): Result<BackupImportReport> =
            withContext(Dispatchers.IO) {
                val json =
                    readFromUri(uri) ?: return@withContext Result.failure(
                        IllegalStateException("Backup file could not be read"),
                    )
                dryRunFromJson(json)
            }

        suspend fun dryRunEncrypted(
            uri: Uri,
            password: String,
        ): Result<BackupImportReport> =
            withContext(Dispatchers.IO) {
                val encryptedData =
                    readFromUri(uri) ?: return@withContext Result.failure(
                        IllegalStateException("Backup file could not be read"),
                    )

                val json =
                    BackupEncryption.decrypt(encryptedData, password)
                        ?: return@withContext Result.failure(
                            IllegalStateException("Decryption failed"),
                        )

                dryRunFromJson(json)
            }

        fun dryRunFromJson(json: String): Result<BackupImportReport> {
            val backupData =
                parseBackupData(json) ?: return Result.failure(
                    IllegalStateException("Invalid JSON"),
                )
            val report = buildReport(backupData)
            return Result.success(report)
        }

        suspend fun isBackupEncrypted(uri: Uri): Boolean =
            withContext(Dispatchers.IO) {
                try {
                    val content = readFromUri(uri) ?: return@withContext false

                    if (content.trim().startsWith("{")) {
                        return@withContext false
                    }

                    return@withContext BackupEncryption.isEncrypted(content)
                } catch (e: Exception) {
                    Timber.e(e, "Failed to check if backup is encrypted")
                    false
                }
            }

        private fun readFromUri(uri: Uri): String? {
            return context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    reader.readText()
                }
            }
        }

        private fun parseBackupData(json: String): BackupData? {
            return runCatching { gson.fromJson(json, BackupData::class.java) }.getOrNull()
        }

        private fun buildReport(backupData: BackupData): BackupImportReport {
            val errors = mutableListOf<String>()

            val version = backupData.version
            if (version < BackupSerializer.MIN_SUPPORTED_VERSION || version > BackupSerializer.CURRENT_FORMAT_VERSION) {
                errors.add("Unsupported backup version: $version")
            }

            var invalidTransactions = 0
            backupData.transactions.forEachIndexed { index, backup ->
                val error = validateTransaction(backup)
                if (error != null) {
                    invalidTransactions++
                    errors.add("transactions[$index]: $error")
                }
            }

            var invalidScheduled = 0
            backupData.scheduledPayments.forEachIndexed { index, backup ->
                val error = validateScheduledPayment(backup)
                if (error != null) {
                    invalidScheduled++
                    errors.add("scheduledPayments[$index]: $error")
                }
            }

            return BackupImportReport(
                version = version,
                transactionCount = backupData.transactions.size,
                scheduledPaymentCount = backupData.scheduledPayments.size,
                invalidTransactions = invalidTransactions,
                invalidScheduledPayments = invalidScheduled,
                errors = errors,
            )
        }

        private fun validateTransaction(backup: com.hesapgunlugu.app.core.backup.serialization.TransactionBackup): String? {
            if (backup.title.isBlank()) return "title is blank"
            if (backup.amount < 0.0) return "amount is negative"
            if (backup.category.isBlank()) return "category is blank"
            if (serializer.parseDateOrNull(backup.date) == null) return "date is invalid"
            return runCatching {
                com.hesapgunlugu.app.core.domain.model.TransactionType.valueOf(backup.type)
            }.exceptionOrNull()?.let { "type is invalid" }
        }

        private fun validateScheduledPayment(backup: com.hesapgunlugu.app.core.backup.serialization.ScheduledPaymentBackup): String? {
            if (backup.title.isBlank()) return "title is blank"
            if (backup.amount < 0.0) return "amount is negative"
            if (backup.category.isBlank()) return "category is blank"
            if (backup.frequency.isBlank()) return "frequency is blank"
            if (serializer.parseDateOrNull(backup.dueDate) == null) return "dueDate is invalid"
            return null
        }

        private suspend fun performImport(
            backupData: BackupData,
            replaceExisting: Boolean,
        ): BackupResult {
            val report = buildReport(backupData)
            if (!report.isVersionSupported) {
                return BackupResult.Error(
                    R.string.backup_version_unsupported,
                    listOf(report.version),
                )
            }

            val invalidTotal = report.invalidTransactions + report.invalidScheduledPayments
            if (invalidTotal > 0) {
                return BackupResult.Error(
                    R.string.backup_import_validation_error,
                    listOf(invalidTotal),
                )
            }

            val transactions = serializer.transactionsFromBackup(backupData.transactions)
            val scheduledPayments = serializer.scheduledPaymentsFromBackup(backupData.scheduledPayments)

            if (replaceExisting) {
                storageRepository.replaceAll(transactions, scheduledPayments)
            } else {
                storageRepository.addAll(transactions, scheduledPayments)
            }

            Timber.d(
                "Import successful: ${transactions.size} transactions, ${scheduledPayments.size} scheduled payments",
            )
            return BackupResult.Success(
                R.string.backup_import_success,
                listOf(transactions.size, scheduledPayments.size),
            )
        }
    }
