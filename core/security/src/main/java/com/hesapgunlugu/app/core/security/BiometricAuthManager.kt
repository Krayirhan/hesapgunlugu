package com.hesapgunlugu.app.core.security

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Biometric Authentication Manager
 * Parmak izi ve yüz tanıma desteği sağlar
 */
@Singleton
class BiometricAuthManager
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        private val biometricManager = BiometricManager.from(context)

        /**
         * Cihazda biometric authentication desteklenip desteklenmediğini kontrol eder
         */
        fun canAuthenticate(): BiometricStatus {
            return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
                BiometricManager.BIOMETRIC_SUCCESS -> {
                    Timber.d("Biometric authentication kullanılabilir")
                    BiometricStatus.Available
                }
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                    Timber.w("Cihazda biometric donanımı yok")
                    BiometricStatus.NoHardware
                }
                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                    Timber.w("Biometric donanımı şu an kullanılamıyor")
                    BiometricStatus.HardwareUnavailable
                }
                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                    Timber.w("Kullanıcı biometric kaydetmemiş")
                    BiometricStatus.NoneEnrolled
                }
                else -> {
                    Timber.e("Bilinmeyen biometric durumu")
                    BiometricStatus.Unknown
                }
            }
        }

        /**
         * Biometric prompt gösterir ve sonucu callback ile döner
         */
        fun showBiometricPrompt(
            activity: FragmentActivity,
            title: String = "Kimlik Doğrulama",
            subtitle: String = "Uygulamaya erişmek için doğrulayın",
            negativeButtonText: String = "İptal",
            onSuccess: () -> Unit,
            onError: (errorCode: Int, errorMessage: String) -> Unit,
            onFailed: () -> Unit,
        ) {
            val executor = ContextCompat.getMainExecutor(context)

            val callback =
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        Timber.d("Biometric authentication başarılı")
                        onSuccess()
                    }

                    override fun onAuthenticationError(
                        errorCode: Int,
                        errString: CharSequence,
                    ) {
                        super.onAuthenticationError(errorCode, errString)
                        Timber.e("Biometric authentication hatası: $errorCode - $errString")
                        onError(errorCode, errString.toString())
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        Timber.w("Biometric authentication başarısız")
                        onFailed()
                    }
                }

            val biometricPrompt = BiometricPrompt(activity, executor, callback)

            val promptInfo =
                BiometricPrompt.PromptInfo.Builder()
                    .setTitle(title)
                    .setSubtitle(subtitle)
                    .setNegativeButtonText(negativeButtonText)
                    .build()

            biometricPrompt.authenticate(promptInfo)
        }
    }

/**
 * Biometric durumu
 */
sealed class BiometricStatus {
    object Available : BiometricStatus()

    object NoHardware : BiometricStatus()

    object HardwareUnavailable : BiometricStatus()

    object NoneEnrolled : BiometricStatus()

    object Unknown : BiometricStatus()
}
