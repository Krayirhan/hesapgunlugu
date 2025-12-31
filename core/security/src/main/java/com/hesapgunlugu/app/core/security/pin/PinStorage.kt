package com.hesapgunlugu.app.core.security.pin

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import timber.log.Timber
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * PIN depolama ve doÄŸrulama iÅŸlemleri.
 * Single Responsibility Principle uygulanarak SecurityManager'dan ayrÄ±ldÄ±.
 *
 * ## GÃ¼venlik Ã–zellikleri
 * - PBKDF2-HMAC-SHA256 ile hash (100,000 iteration)
 * - 256-bit cryptographically secure random salt
 * - EncryptedSharedPreferences ile ÅŸifreli depolama (AES256-GCM)
 * - Constant-time karÅŸÄ±laÅŸtÄ±rma (timing attack korumasÄ±)
 */
@Singleton
class PinStorage
    @Inject
    constructor(
        private val context: Context,
    ) {
        companion object {
            private const val KEY_PIN_HASH = "pin_hash"
            private const val KEY_PIN_SALT = "pin_salt"
            private const val PBKDF2_ITERATIONS = 100_000
            private const val SALT_LENGTH = 32 // 256 bits
        }

        private val encryptedPrefs: SharedPreferences by lazy {
            val masterKey =
                MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build()

            EncryptedSharedPreferences.create(
                context,
                "security_encrypted_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
            )
        }

        /**
         * PIN ayarlandÄ± mÄ±
         */
        fun hasPinSet(): Boolean {
            return encryptedPrefs.contains(KEY_PIN_HASH) && encryptedPrefs.contains(KEY_PIN_SALT)
        }

        /**
         * PIN kaydet
         * @throws IllegalArgumentException PIN validasyon hatasÄ±
         */
        fun savePin(pin: String) {
            // Validate PIN strength
            val validationResult = PinValidator.validatePinStrength(pin)
            if (!validationResult.isValid) {
                throw IllegalArgumentException(validationResult.errorMessage)
            }

            // Generate cryptographically secure random salt
            val salt = ByteArray(SALT_LENGTH)
            SecureRandom().nextBytes(salt)
            val saltBase64 = Base64.encodeToString(salt, Base64.NO_WRAP)

            // Hash PIN with PBKDF2 (high iteration count)
            val hash = hashPinSecure(pin, salt)
            val hashBase64 = Base64.encodeToString(hash, Base64.NO_WRAP)

            // Store in EncryptedSharedPreferences (double encryption layer)
            encryptedPrefs.edit()
                .putString(KEY_PIN_HASH, hashBase64)
                .putString(KEY_PIN_SALT, saltBase64)
                .apply()

            Timber.d("PIN securely stored with PBKDF2 + salt")
        }

        /**
         * PIN doÄŸrula
         * @return true if PIN matches, false otherwise
         */
        fun verifyPin(pin: String): Boolean {
            val storedHashBase64 = encryptedPrefs.getString(KEY_PIN_HASH, null)
            val storedSaltBase64 = encryptedPrefs.getString(KEY_PIN_SALT, null)

            if (storedHashBase64 == null || storedSaltBase64 == null) {
                return false
            }

            // Decode stored values
            val storedHash = Base64.decode(storedHashBase64, Base64.NO_WRAP)
            val storedSalt = Base64.decode(storedSaltBase64, Base64.NO_WRAP)

            // Hash input PIN with same salt
            val inputHash = hashPinSecure(pin, storedSalt)

            // CRITICAL SECURITY: Constant-time comparison to prevent timing attacks
            return MessageDigest.isEqual(inputHash, storedHash)
        }

        /**
         * PIN sil
         */
        fun removePin() {
            encryptedPrefs.edit()
                .remove(KEY_PIN_HASH)
                .remove(KEY_PIN_SALT)
                .apply()

            Timber.d("PIN removed from storage")
        }

        /**
         * CRITICAL SECURITY: Hash PIN with PBKDF2-HMAC-SHA256
         */
        private fun hashPinSecure(
            pin: String,
            salt: ByteArray,
        ): ByteArray {
            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            val spec =
                PBEKeySpec(
                    pin.toCharArray(),
                    salt,
                    PBKDF2_ITERATIONS,
                    // Output key length in bits`n                    256,
                )
            return factory.generateSecret(spec).encoded
        }
    }
