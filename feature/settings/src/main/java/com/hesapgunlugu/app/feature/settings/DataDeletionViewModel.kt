package com.hesapgunlugu.app.feature.settings

import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hesapgunlugu.app.core.cloud.GoogleDriveBackupManager
import com.hesapgunlugu.app.core.domain.repository.DatabaseWiperRepository
import com.hesapgunlugu.app.core.domain.repository.SettingsRepository
import com.hesapgunlugu.app.core.security.SecurityManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import javax.inject.Inject

/**
 * GDPR Data Deletion Manager
 * Kullanıcının tüm verilerini kalıcı olarak siler
 */
sealed class DeletionEvent {
    data object Success : DeletionEvent()

    data class Error(val message: String) : DeletionEvent()

    data class RequiresUserAction(val intentSender: android.content.IntentSender) : DeletionEvent()
}

data class DeletionState(
    val isDeleting: Boolean = false,
    val confirmationText: String = "",
    val isConfirmed: Boolean = false,
)

@HiltViewModel
class DataDeletionViewModel
    @Inject
    constructor(
        private val settingsRepository: SettingsRepository,
        private val securityManager: SecurityManager,
        private val databaseWiper: DatabaseWiperRepository,
        private val googleDriveBackupManager: GoogleDriveBackupManager,
        @ApplicationContext private val context: Context,
    ) : ViewModel() {
        companion object {
            private const val DATABASE_NAME = "my_money_db_v2"
        }

        private val _state = MutableStateFlow(DeletionState())
        val state: StateFlow<DeletionState> = _state.asStateFlow()

        private val _event = MutableSharedFlow<DeletionEvent>()
        val event: SharedFlow<DeletionEvent> = _event.asSharedFlow()

        fun onConfirmationTextChanged(text: String) {
            val keywords = context.resources.getStringArray(R.array.delete_confirmation_keywords)
            val isConfirmed = keywords.any { keyword -> keyword.equals(text, ignoreCase = true) }
            _state.value =
                _state.value.copy(
                    confirmationText = text,
                    isConfirmed = isConfirmed,
                )
        }

        /**
         * GDPR Article 17 - Right to Erasure
         * Kullanıcının tüm verilerini kalıcı olarak siler
         */
        fun deleteAllUserData() {
            if (!_state.value.isConfirmed) {
                return
            }

            viewModelScope.launch {
                _state.value = _state.value.copy(isDeleting = true)

                try {
                    withContext(Dispatchers.IO) {
                        Timber.w("GDPR data deletion başlatılıyor...")

                        // 1. Clear database tables (transactions, scheduled, notifications, rules)
                        clearDatabaseTables()

                        // 2. Clear user settings (DataStore)
                        settingsRepository.clearAllSettings()

                        // 3. Clear security data (PIN, biometric, lock state)
                        securityManager.clearAllSecurityData()

                        // 4. SharedPreferences'ı temizle (legacy)
                        clearSharedPreferences()

                        // 5. Veritabanı dosyasını sil
                        deleteDatabaseFiles()

                        // 6. Cache dosyalarını temizle
                        clearCacheFiles()

                        // 7. Export dosyalarını sil
                        deleteExportFiles()

                        // 8. Google Drive yedeklerini sil (varsa)
                        deleteCloudBackups()

                        Timber.w("GDPR data deletion tamamlandı")
                    }

                    _event.emit(DeletionEvent.Success)
                } catch (e: Exception) {
                    Timber.e(e, "GDPR data deletion hatası")
                    _event.emit(DeletionEvent.Error(e.message ?: context.getString(R.string.error_generic_unknown)))
                } finally {
                    _state.value = _state.value.copy(isDeleting = false, confirmationText = "", isConfirmed = false)
                }
            }
        }

        private suspend fun clearDatabaseTables() {
            Timber.d("Database tables cleared")
            databaseWiper.clearAllTables()
        }

        private fun clearSharedPreferences() {
            val prefs = context.getSharedPreferences("finance_prefs", Context.MODE_PRIVATE)
            prefs.edit().clear().apply()
            runCatching { context.deleteSharedPreferences("backend_auth_prefs") }
            runCatching { context.deleteSharedPreferences("encrypted_settings") }
            runCatching { context.deleteSharedPreferences("security_prefs") }
            runCatching { context.deleteSharedPreferences("app_prefs") }
            Timber.d("SharedPreferences temizlendi")
        }

        private fun deleteDatabaseFiles() {
            try {
                databaseWiper.closeDatabase()
            } catch (e: Exception) {
                Timber.w(e, "Database close failed")
            }

            val dbFile = context.getDatabasePath(DATABASE_NAME)
            if (dbFile.exists()) {
                context.deleteDatabase(DATABASE_NAME)
                Timber.d("Veritabanı dosyası silindi: ${dbFile.name}")
            }

            val walFile = File(dbFile.parent, "${dbFile.name}-wal")
            val shmFile = File(dbFile.parent, "${dbFile.name}-shm")

            if (walFile.exists()) walFile.delete()
            if (shmFile.exists()) shmFile.delete()

            Timber.d("Veritabanı yardımcı dosyaları silindi")
        }

        private fun clearCacheFiles() {
            context.cacheDir.deleteRecursively()
            Timber.d("Cache dosyaları temizlendi")
        }

        private suspend fun deleteExportFiles() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val uris = queryExportUris()
                if (uris.isEmpty()) return

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val request = MediaStore.createDeleteRequest(context.contentResolver, uris)
                    _event.emit(DeletionEvent.RequiresUserAction(request.intentSender))
                    Timber.d("MediaStore delete request created for ${uris.size} files")
                } else {
                    uris.forEach { uri ->
                        context.contentResolver.delete(uri, null, null)
                    }
                    Timber.d("MediaStore exports deleted: ${uris.size}")
                }
            } else {
                @Suppress("DEPRECATION")
                val downloadsDir =
                    Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS,
                    )
                deleteLegacyExportFiles(downloadsDir)
            }
        }

        private suspend fun deleteCloudBackups() {
            val backupsResult = googleDriveBackupManager.listBackups()
            if (backupsResult.isSuccess) {
                backupsResult.getOrNull()?.forEach { backup ->
                    googleDriveBackupManager.deleteBackup(backup.id)
                }
            }
        }

        private fun deleteLegacyExportFiles(downloadsDir: File) {
            val prefixes = exportFilePrefixes()
            downloadsDir.listFiles { file ->
                prefixes.any { prefix -> file.name.startsWith(prefix) }
            }?.forEach { file ->
                file.delete()
                Timber.d("Export dosyası silindi: ${file.name}")
            }
        }

        private fun queryExportUris(): List<android.net.Uri> {
            val projection =
                arrayOf(
                    MediaStore.MediaColumns._ID,
                    MediaStore.MediaColumns.DISPLAY_NAME,
                )
            val prefixes = exportFilePrefixes()
            val selection = prefixes.joinToString(" OR ") { "${MediaStore.MediaColumns.DISPLAY_NAME} LIKE ?" }
            val selectionArgs = prefixes.map { "$it%" }.toTypedArray()

            val uris = mutableListOf<android.net.Uri>()
            context.contentResolver.query(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null,
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    uris.add(ContentUris.withAppendedId(MediaStore.Downloads.EXTERNAL_CONTENT_URI, id))
                }
            }
            return uris
        }

        private fun exportFilePrefixes(): List<String> {
            return listOf(
                "transactions_",
                "transactions_export",
                "finans_backup",
                "finans_rapor",
            )
        }
    }
