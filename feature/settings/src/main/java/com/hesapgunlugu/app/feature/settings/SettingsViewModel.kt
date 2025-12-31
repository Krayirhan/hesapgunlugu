package com.hesapgunlugu.app.feature.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hesapgunlugu.app.core.backup.BackupManager
import com.hesapgunlugu.app.core.backup.BackupResult
import com.hesapgunlugu.app.core.domain.model.UserSettings
import com.hesapgunlugu.app.core.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.hesapgunlugu.app.core.backup.R as BackupR

@HiltViewModel
class SettingsViewModel
    @Inject
    constructor(
        private val settingsRepository: SettingsRepository,
        private val backupManager: BackupManager,
    ) : ViewModel() {
        private val _state = MutableStateFlow(UserSettings())
        val state: StateFlow<UserSettings> = _state.asStateFlow()

        private val _backupState = MutableStateFlow(BackupState())
        val backupState: StateFlow<BackupState> = _backupState.asStateFlow()

        init {
            viewModelScope.launch {
                settingsRepository.settingsFlow.collectLatest { savedSettings ->
                    _state.value = savedSettings
                }
            }
        }

        // Backup functions
        fun exportBackup(
            uri: Uri,
            encrypted: Boolean,
            password: String? = null,
        ) {
            viewModelScope.launch {
                _backupState.update { it.copy(isLoading = true, messageRes = null, messageArgs = emptyList()) }

                val result =
                    if (encrypted) {
                        if (password.isNullOrBlank()) {
                            _backupState.update {
                                it.copy(
                                    isLoading = false,
                                    messageRes = BackupR.string.backup_password_invalid,
                                    messageArgs = emptyList(),
                                    isSuccess = false,
                                )
                            }
                            return@launch
                        }
                        backupManager.exportData(uri, password)
                    } else {
                        backupManager.exportDataPlain(uri)
                    }

                _backupState.update {
                    when (result) {
                        is BackupResult.Success -> it.copy(isLoading = false, messageRes = result.messageRes, messageArgs = result.args, isSuccess = true)
                        is BackupResult.Error -> it.copy(isLoading = false, messageRes = result.messageRes, messageArgs = result.args, isSuccess = false)
                    }
                }
            }
        }

        fun importBackup(
            uri: Uri,
            encrypted: Boolean,
            password: String? = null,
            replaceExisting: Boolean = false,
        ) {
            viewModelScope.launch {
                _backupState.update { it.copy(isLoading = true, messageRes = null, messageArgs = emptyList()) }

                val result =
                    if (encrypted && password != null) {
                        backupManager.importDataEncrypted(uri, password, replaceExisting)
                    } else {
                        backupManager.importData(uri, replaceExisting)
                    }

                _backupState.update {
                    when (result) {
                        is BackupResult.Success -> it.copy(isLoading = false, messageRes = result.messageRes, messageArgs = result.args, isSuccess = true)
                        is BackupResult.Error -> it.copy(isLoading = false, messageRes = result.messageRes, messageArgs = result.args, isSuccess = false)
                    }
                }
            }
        }

        suspend fun isBackupEncrypted(uri: Uri): Boolean {
            return backupManager.isBackupEncrypted(uri)
        }

        fun getBackupFileName(encrypted: Boolean): String {
            return if (encrypted) {
                backupManager.generateEncryptedBackupFileName()
            } else {
                backupManager.generatePlainBackupFileName()
            }
        }

        fun clearBackupMessage() {
            _backupState.update { it.copy(messageRes = null, messageArgs = emptyList()) }
        }

        fun addOrUpdateCategory(
            name: String,
            limit: Double,
        ) {
            if (name.isBlank()) return
            viewModelScope.launch {
                val current = _state.value.categoryBudgets.toMutableMap()
                current[name] = limit
                settingsRepository.updateCategoryBudgets(current)
            }
        }

        fun removeCategory(name: String) {
            viewModelScope.launch {
                val current = _state.value.categoryBudgets.toMutableMap()
                current.remove(name)
                settingsRepository.updateCategoryBudgets(current)
            }
        }

        fun updateMonthlyLimit(newLimit: Double) {
            viewModelScope.launch {
                settingsRepository.updateMonthlyLimit(newLimit)
            }
        }

        fun updateUserName(name: String) {
            viewModelScope.launch {
                settingsRepository.updateUserName(name)
            }
        }

        fun updateCurrency(currencyCode: String) {
            viewModelScope.launch {
                settingsRepository.updateCurrency(currencyCode)
            }
        }
    }
