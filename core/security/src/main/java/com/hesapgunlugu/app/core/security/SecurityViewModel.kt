package com.hesapgunlugu.app.core.security

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class SecurityState(
    val isLoading: Boolean = true,
    val isAppLockEnabled: Boolean = false,
    val isBiometricEnabled: Boolean = false,
    val hasPinSet: Boolean = false,
    val isAuthenticated: Boolean = false,
    val showPinScreen: Boolean = false,
    val pinError: String? = null,
    val remainingAttempts: Int = 3,
    val lockoutSeconds: Int = 0,
    val isLockedOut: Boolean = false,
)

sealed class SecurityEvent {
    data object AuthenticationSuccess : SecurityEvent()

    data class AuthenticationFailed(val message: String) : SecurityEvent()
}

@HiltViewModel
class SecurityViewModel
    @Inject
    constructor(
        private val securityManager: SecurityManager,
        private val biometricAuthManager: BiometricAuthManager,
    ) : ViewModel() {
        private val _state = MutableStateFlow(SecurityState())
        val state: StateFlow<SecurityState> = _state.asStateFlow()

        private val _event = MutableSharedFlow<SecurityEvent>()
        val event: SharedFlow<SecurityEvent> = _event.asSharedFlow()

        init {
            loadSecurityState()
        }

        private fun loadSecurityState() {
            viewModelScope.launch {
                securityManager.enforceSessionTimeout()
                combine(
                    securityManager.isAppLockEnabled,
                    securityManager.isBiometricEnabled,
                    securityManager.hasPinSet,
                    securityManager.isAuthenticated,
                ) { appLockEnabled, biometricEnabled, hasPinSet, isAuthenticated ->
                    SecurityState(
                        isLoading = false,
                        isAppLockEnabled = appLockEnabled,
                        isBiometricEnabled = biometricEnabled,
                        hasPinSet = hasPinSet,
                        isAuthenticated = isAuthenticated,
                        showPinScreen = appLockEnabled && !isAuthenticated,
                    )
                }.collect { state ->
                    _state.value = state
                    Timber.d("Security state: appLock=${state.isAppLockEnabled}, authenticated=${state.isAuthenticated}")
                }
            }
        }

        fun verifyPin(pin: String) {
            viewModelScope.launch {
                when (val result = securityManager.verifyPin(pin)) {
                    is PinVerificationResult.Success -> {
                        securityManager.setAuthenticated(true)
                        _state.update {
                            it.copy(
                                pinError = null,
                                showPinScreen = false,
                                remainingAttempts = 3,
                                isLockedOut = false,
                            )
                        }
                        _event.emit(SecurityEvent.AuthenticationSuccess)
                    }
                    is PinVerificationResult.NotSet -> {
                        _state.update {
                            it.copy(
                                pinError = "PIN not set",
                                showPinScreen = false,
                            )
                        }
                        Timber.d("PIN verification successful")
                    }
                    is PinVerificationResult.Failed -> {
                        val errorMsg = "Yanlış PIN (${result.remainingAttempts} deneme kaldı)"
                        _state.update {
                            it.copy(
                                pinError = errorMsg,
                                remainingAttempts = result.remainingAttempts,
                            )
                        }
                        _event.emit(SecurityEvent.AuthenticationFailed(errorMsg))
                        Timber.w("PIN verification failed, remaining: ${result.remainingAttempts}")
                    }
                    is PinVerificationResult.LockedOut -> {
                        val errorMsg = "Çok fazla yanlış deneme. ${result.remainingSeconds} saniye bekleyin."
                        _state.update {
                            it.copy(
                                pinError = errorMsg,
                                lockoutSeconds = result.remainingSeconds,
                                isLockedOut = true,
                                remainingAttempts = 0,
                            )
                        }
                        _event.emit(SecurityEvent.AuthenticationFailed(errorMsg))
                        Timber.w("PIN locked out for ${result.remainingSeconds} seconds")

                        // Kilit süresini takip et
                        startLockoutCountdown(result.remainingSeconds)
                    }
                }
            }
        }

        private fun startLockoutCountdown(seconds: Int) {
            viewModelScope.launch {
                var remaining = seconds
                while (remaining > 0) {
                    kotlinx.coroutines.delay(1000)
                    remaining--
                    _state.update { it.copy(lockoutSeconds = remaining) }
                }
                _state.update {
                    it.copy(
                        isLockedOut = false,
                        pinError = null,
                        remainingAttempts = 3,
                    )
                }
            }
        }

        fun authenticateWithBiometric(activity: FragmentActivity) {
            biometricAuthManager.showBiometricPrompt(
                activity = activity,
                title = "Kimlik Doğrulama",
                subtitle = "Parmak izinizi kullanarak giriş yapın",
                onSuccess = {
                    viewModelScope.launch {
                        securityManager.setAuthenticated(true)
                        _state.update { it.copy(showPinScreen = false) }
                        _event.emit(SecurityEvent.AuthenticationSuccess)
                        Timber.d("Biometric authentication successful")
                    }
                },
                onError = { errorCode, errorMessage ->
                    viewModelScope.launch {
                        _event.emit(SecurityEvent.AuthenticationFailed(errorMessage))
                        Timber.e("Biometric authentication error: $errorCode - $errorMessage")
                    }
                },
                onFailed = {
                    viewModelScope.launch {
                        _event.emit(SecurityEvent.AuthenticationFailed("Parmak izi tanınmadı"))
                        Timber.w("Biometric authentication failed")
                    }
                },
            )
        }

        fun canUseBiometric(): Boolean {
            return biometricAuthManager.canAuthenticate() is BiometricStatus.Available
        }

        fun clearPinError() {
            _state.update { it.copy(pinError = null) }
        }

        // Settings için
        fun setAppLockEnabled(enabled: Boolean) {
            viewModelScope.launch {
                securityManager.setAppLockEnabled(enabled)
            }
        }

        fun setBiometricEnabled(enabled: Boolean) {
            viewModelScope.launch {
                securityManager.setBiometricEnabled(enabled)
            }
        }

        fun setPin(pin: String) {
            viewModelScope.launch {
                securityManager.setPin(pin)
            }
        }

        fun removePin() {
            viewModelScope.launch {
                securityManager.removePin()
            }
        }

        fun resetPin() {
            viewModelScope.launch {
                securityManager.removePin()
                _state.update {
                    it.copy(
                        hasPinSet = false,
                        isAppLockEnabled = false,
                        showPinScreen = false,
                        pinError = null,
                    )
                }
            }
        }

        // Uygulama arka plana gittiğinde
        fun onAppBackgrounded() {
            viewModelScope.launch {
                if (_state.value.isAppLockEnabled) {
                    securityManager.setAuthenticated(false)
                    _state.update { it.copy(showPinScreen = true) }
                    Timber.d("App backgrounded, lock activated")
                }
            }
        }
    }
