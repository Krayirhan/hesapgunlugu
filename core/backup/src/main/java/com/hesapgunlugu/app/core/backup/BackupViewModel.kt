package com.hesapgunlugu.app.core.backup

import android.net.Uri
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class BackupState(
    val isLoading: Boolean = false,
    val showImportConfirmDialog: Boolean = false,
    val pendingImportUri: Uri? = null,
    // CRITICAL SECURITY: Password validation state
    val passwordValidation: BackupPasswordValidator.PasswordStrength? = null,
    val isPasswordValid: Boolean = false,
)

sealed class BackupEvent {
    data class Success(
        @StringRes val messageRes: Int,
        val args: List<Any> = emptyList(),
    ) : BackupEvent()

    data class Error(
        @StringRes val messageRes: Int,
        val args: List<Any> = emptyList(),
    ) : BackupEvent()

    data object RequestExportLocation : BackupEvent()

    data object RequestImportFile : BackupEvent()

    // CRITICAL SECURITY: Password change event
    data class RequestPasswordChange(val currentBackupUri: Uri) : BackupEvent()
}

@HiltViewModel
class BackupViewModel
    @Inject
    constructor(
        private val backupManager: BackupManager,
    ) : ViewModel() {
        private val _state = MutableStateFlow(BackupState())
        val state: StateFlow<BackupState> = _state.asStateFlow()

        private val _event = MutableSharedFlow<BackupEvent>()
        val event: SharedFlow<BackupEvent> = _event.asSharedFlow()

        fun requestExport() {
            viewModelScope.launch {
                _event.emit(BackupEvent.RequestExportLocation)
            }
        }

        fun requestImport() {
            viewModelScope.launch {
                _event.emit(BackupEvent.RequestImportFile)
            }
        }

        fun exportData(
            uri: Uri,
            password: String,
        ) {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true) }

                when (val result = backupManager.exportData(uri, password)) {
                    is BackupResult.Success -> {
                        _event.emit(BackupEvent.Success(result.messageRes, result.args))
                        Timber.d("Export completed successfully")
                    }
                    is BackupResult.Error -> {
                        _event.emit(BackupEvent.Error(result.messageRes, result.args))
                        Timber.e("Export failed")
                    }
                }

                _state.update { it.copy(isLoading = false) }
            }
        }

        fun onImportFileSelected(uri: Uri) {
            _state.update {
                it.copy(
                    showImportConfirmDialog = true,
                    pendingImportUri = uri,
                )
            }
        }

        fun confirmImport(replaceExisting: Boolean) {
            val uri = _state.value.pendingImportUri ?: return

            viewModelScope.launch {
                _state.update { it.copy(isLoading = true, showImportConfirmDialog = false) }

                when (val result = backupManager.importData(uri, replaceExisting)) {
                    is BackupResult.Success -> {
                        _event.emit(BackupEvent.Success(result.messageRes, result.args))
                        Timber.d("Import completed successfully")
                    }
                    is BackupResult.Error -> {
                        _event.emit(BackupEvent.Error(result.messageRes, result.args))
                        Timber.e("Import failed")
                    }
                }

                _state.update { it.copy(isLoading = false, pendingImportUri = null) }
            }
        }

        fun cancelImport() {
            _state.update {
                it.copy(
                    showImportConfirmDialog = false,
                    pendingImportUri = null,
                )
            }
        }

        fun getBackupFileName(): String = backupManager.generateBackupFileName()

        /**
         * CRITICAL SECURITY: Validate backup password before export
         * Shows real-time password strength feedback to user
         */
        fun validateBackupPassword(password: String) {
            viewModelScope.launch {
                val validation = BackupPasswordValidator.validate(password)
                _state.update {
                    it.copy(
                        passwordValidation = validation,
                        isPasswordValid = validation.isValid && validation.score >= 60,
                    )
                }
            }
        }

        /**
         * CRITICAL SECURITY: Change backup password (re-encryption)
         * @param backupUri URI of existing backup file
         * @param oldPassword Current password
         * @param newPassword New password (must be validated)
         */
        fun changeBackupPassword(
            backupUri: Uri,
            oldPassword: String,
            newPassword: String,
        ) {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true) }

                // Validate new password
                val validation = BackupPasswordValidator.validate(newPassword)
                if (!validation.isValid) {
                    _event.emit(BackupEvent.Error(R.string.backup_password_invalid))
                    _state.update { it.copy(isLoading = false) }
                    return@launch
                }

                when (val result = backupManager.changePassword(backupUri, oldPassword, newPassword)) {
                    is BackupResult.Success -> {
                        _event.emit(BackupEvent.Success(R.string.backup_password_change_success))
                        Timber.d("Password changed successfully")
                    }
                    is BackupResult.Error -> {
                        _event.emit(BackupEvent.Error(result.messageRes, result.args))
                        Timber.e("Password change failed")
                    }
                }

                _state.update { it.copy(isLoading = false) }
            }
        }
    }
