package com.hesapgunlugu.app.feature.settings

import android.content.Intent
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hesapgunlugu.app.core.backup.BackupManager
import com.hesapgunlugu.app.core.cloud.BackupFile
import com.hesapgunlugu.app.core.cloud.GoogleDriveBackupManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CloudBackupState(
    val isSignedIn: Boolean = false,
    val isLoading: Boolean = false,
    val backups: List<BackupFile> = emptyList(),
)

sealed class CloudBackupEvent {
    data class LaunchSignIn(val intent: Intent) : CloudBackupEvent()

    data class Message(
        @StringRes val messageRes: Int,
        val args: List<Any> = emptyList(),
    ) : CloudBackupEvent()
}

@HiltViewModel
class CloudBackupViewModel
    @Inject
    constructor(
        private val googleDriveBackupManager: GoogleDriveBackupManager,
        private val backupManager: BackupManager,
    ) : ViewModel() {
        private val _state =
            MutableStateFlow(
                CloudBackupState(isSignedIn = googleDriveBackupManager.isSignedIn()),
            )
        val state: StateFlow<CloudBackupState> = _state.asStateFlow()

        private val _event = MutableSharedFlow<CloudBackupEvent>()
        val event: SharedFlow<CloudBackupEvent> = _event.asSharedFlow()

        init {
            if (_state.value.isSignedIn) {
                refreshBackups()
            }
        }

        fun requestSignIn() {
            viewModelScope.launch {
                _event.emit(CloudBackupEvent.LaunchSignIn(googleDriveBackupManager.getSignInIntent()))
            }
        }

        fun onSignInResult(data: Intent?) {
            viewModelScope.launch {
                val result = googleDriveBackupManager.handleSignInResult(data)
                if (result.isSuccess) {
                    _state.update { it.copy(isSignedIn = true) }
                    refreshBackupsInternal(showErrors = false)
                    _event.emit(CloudBackupEvent.Message(R.string.cloud_sign_in_success))
                } else {
                    _event.emit(CloudBackupEvent.Message(R.string.cloud_sign_in_error))
                }
            }
        }

        fun signOut() {
            viewModelScope.launch {
                googleDriveBackupManager.signOut()
                _state.update { it.copy(isSignedIn = false, backups = emptyList()) }
                _event.emit(CloudBackupEvent.Message(R.string.cloud_sign_out_success))
            }
        }

        fun refreshBackups() {
            viewModelScope.launch {
                refreshBackupsInternal(showErrors = true)
            }
        }

        fun uploadBackup() {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true) }

                val payloadResult = backupManager.exportDataToJson()
                if (payloadResult.isSuccess) {
                    val payload = payloadResult.getOrThrow()
                    val uploadResult =
                        googleDriveBackupManager.uploadBackup(
                            fileName = backupManager.generatePlainBackupFileName(),
                            content = payload.json,
                        )
                    if (uploadResult.isSuccess) {
                        refreshBackupsInternal(showErrors = false)
                        _event.emit(
                            CloudBackupEvent.Message(
                                R.string.cloud_backup_upload_success,
                                listOf(payload.transactionCount, payload.scheduledPaymentCount),
                            ),
                        )
                    } else {
                        _event.emit(CloudBackupEvent.Message(R.string.cloud_backup_upload_error))
                    }
                } else {
                    _event.emit(CloudBackupEvent.Message(R.string.cloud_backup_export_error))
                }

                _state.update { it.copy(isLoading = false) }
            }
        }

        fun restoreBackup(
            fileId: String,
            replaceExisting: Boolean,
        ) {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true) }

                val downloadResult = googleDriveBackupManager.downloadBackup(fileId)
                if (downloadResult.isSuccess) {
                    val importResult =
                        backupManager.importDataFromJson(
                            json = downloadResult.getOrThrow(),
                            replaceExisting = replaceExisting,
                        )
                    if (importResult.isSuccess) {
                        val summary = importResult.getOrThrow()
                        _event.emit(
                            CloudBackupEvent.Message(
                                R.string.cloud_backup_restore_success,
                                listOf(summary.transactionCount, summary.scheduledPaymentCount),
                            ),
                        )
                    } else {
                        _event.emit(CloudBackupEvent.Message(R.string.cloud_backup_restore_error))
                    }
                } else {
                    _event.emit(CloudBackupEvent.Message(R.string.cloud_backup_download_error))
                }

                _state.update { it.copy(isLoading = false) }
            }
        }

        fun deleteBackup(fileId: String) {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true) }

                val result = googleDriveBackupManager.deleteBackup(fileId)
                if (result.isSuccess) {
                    refreshBackupsInternal(showErrors = false)
                    _event.emit(CloudBackupEvent.Message(R.string.cloud_backup_delete_success))
                } else {
                    _event.emit(CloudBackupEvent.Message(R.string.cloud_backup_delete_error))
                }

                _state.update { it.copy(isLoading = false) }
            }
        }

        private suspend fun refreshBackupsInternal(showErrors: Boolean) {
            _state.update { it.copy(isLoading = true) }
            val result = googleDriveBackupManager.listBackups()
            if (result.isSuccess) {
                val backups = result.getOrThrow().sortedByDescending { it.createdTime }
                _state.update { it.copy(backups = backups) }
            } else if (showErrors) {
                _event.emit(CloudBackupEvent.Message(R.string.cloud_backup_list_error))
            }
            _state.update { it.copy(isLoading = false) }
        }
    }
