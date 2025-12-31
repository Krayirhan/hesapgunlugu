package com.hesapgunlugu.app.core.backup

import android.content.Context
import android.net.Uri
import com.hesapgunlugu.app.core.backup.export.BackupExporter
import com.hesapgunlugu.app.core.backup.import.BackupImportReport
import com.hesapgunlugu.app.core.backup.import.BackupImporter
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupManager
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val exporter: BackupExporter,
        private val importer: BackupImporter,
    ) {
        suspend fun exportData(
            uri: Uri,
            password: String,
        ): BackupResult {
            return exporter.exportEncrypted(uri, password)
        }

        suspend fun exportDataPlain(uri: Uri): BackupResult {
            return exporter.exportPlain(uri)
        }

        suspend fun exportDataToJson(): Result<BackupPayload> {
            return exporter.exportPlainToJson()
        }

        suspend fun importData(
            uri: Uri,
            replaceExisting: Boolean = false,
        ): BackupResult {
            return importer.importPlain(uri, replaceExisting)
        }

        suspend fun importDataEncrypted(
            uri: Uri,
            password: String,
            replaceExisting: Boolean = false,
        ): BackupResult {
            return importer.importEncrypted(uri, password, replaceExisting)
        }

        suspend fun importDataFromJson(
            json: String,
            replaceExisting: Boolean = false,
        ): Result<BackupImportSummary> {
            return importer.importFromJsonSummary(json, replaceExisting)
        }

        suspend fun dryRunImport(uri: Uri): Result<BackupImportReport> {
            return importer.dryRunPlain(uri)
        }

        suspend fun dryRunImportEncrypted(
            uri: Uri,
            password: String,
        ): Result<BackupImportReport> {
            return importer.dryRunEncrypted(uri, password)
        }

        suspend fun isBackupEncrypted(uri: Uri): Boolean {
            return importer.isBackupEncrypted(uri)
        }

        fun generateBackupFileName(): String {
            return exporter.generateEncryptedFileName()
        }

        fun generatePlainBackupFileName(): String {
            return exporter.generateFileName()
        }

        fun generateEncryptedBackupFileName(): String {
            return exporter.generateEncryptedFileName()
        }

        suspend fun changePassword(
            backupUri: Uri,
            oldPassword: String,
            newPassword: String,
        ): BackupResult =
            withContext(Dispatchers.IO) {
                try {
                    val validation = BackupPasswordValidator.validate(newPassword)
                    if (!validation.isValid || validation.score < 60) {
                        return@withContext BackupResult.Error(R.string.backup_password_invalid)
                    }

                    val encryptedContent =
                        readFromUri(backupUri)
                            ?: return@withContext BackupResult.Error(R.string.backup_file_read_error_detailed)

                    val reEncrypted =
                        BackupEncryption.reEncrypt(
                            encryptedData = encryptedContent.trim(),
                            oldPassword = oldPassword,
                            newPassword = newPassword,
                        ) ?: return@withContext BackupResult.Error(R.string.backup_password_reencrypt_error)

                    writeToUri(backupUri, reEncrypted)
                        ?: return@withContext BackupResult.Error(R.string.backup_file_write_error)

                    Timber.d("Backup password changed successfully (key rotation completed)")
                    BackupResult.Success(R.string.backup_password_change_success)
                } catch (e: Exception) {
                    Timber.e(e, "Failed to change backup password")
                    val message = e.message
                    if (message.isNullOrBlank()) {
                        BackupResult.Error(R.string.backup_password_change_error_generic)
                    } else {
                        BackupResult.Error(R.string.backup_password_change_error, listOf(message))
                    }
                }
            }

        private fun readFromUri(uri: Uri): String? {
            return context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    reader.readText()
                }
            }
        }

        private fun writeToUri(
            uri: Uri,
            content: String,
        ): Unit? {
            return context.contentResolver.openOutputStream(uri, "wt")?.use { output ->
                OutputStreamWriter(output).use { writer ->
                    writer.write(content)
                }
            }
        }
    }
