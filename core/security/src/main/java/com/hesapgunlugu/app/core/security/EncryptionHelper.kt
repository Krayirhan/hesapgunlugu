package com.hesapgunlugu.app.core.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import timber.log.Timber
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Provides AES-256-GCM encryption using Android Keystore for secure data storage.
 *
 * This class manages encryption/decryption of sensitive data using hardware-backed
 * cryptography when available. All encryption keys are stored in Android Keystore
 * and never exposed to the application.
 *
 * ## Security Features
 *
 * ### Key Storage
 * - Keys stored in Android Keystore System
 * - Hardware-backed on supported devices
 * - Keys never leave secure hardware
 * - Protected against extraction even on rooted devices
 *
 * ### Encryption Algorithm
 * - AES-256-GCM (Galois/Counter Mode)
 * - Authenticated encryption (prevents tampering)
 * - Random 12-byte IV for each encryption
 * - 128-bit authentication tag
 *
 * ### Use Cases
 * - Encrypting sensitive user data before storage
 * - Protecting export files
 * - Securing temporary cache data
 * - Encrypting backup files
 *
 * ## Usage
 * ```kotlin
 * // Encrypt data
 * val encrypted = encryptionHelper.encrypt("sensitive data")
 *
 * // Decrypt data
 * val decrypted = encryptionHelper.decrypt(encrypted)
 *
 * // Check if key exists
 * if (!encryptionHelper.hasKey()) {
 *     encryptionHelper.generateKey()
 * }
 * ```
 *
 * ## Architecture
 * Works alongside:
 * - [SecurityManager] for PIN/biometric authentication
 * - EncryptedSharedPreferences for preferences
 * - Room database for structured data
 *
 * @see SecurityManager
 */
@Singleton
class EncryptionHelper
    @Inject
    constructor() {
        companion object {
            private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
            private const val KEY_ALIAS = "HesapGunluguEncryptionKey"
            private const val TRANSFORMATION = "AES/GCM/NoPadding"
            private const val GCM_TAG_LENGTH = 128
            private const val IV_LENGTH = 12 // GCM standard IV length

            // Separator for IV and encrypted data
            private const val SEPARATOR = ":"
        }

        private val keyStore: KeyStore =
            KeyStore.getInstance(KEYSTORE_PROVIDER).apply {
                load(null)
            }

        /**
         * Generate or retrieve encryption key from Android Keystore
         */
        fun generateKey() {
            try {
                if (!keyStore.containsAlias(KEY_ALIAS)) {
                    Timber.d("Generating new encryption key in Keystore")

                    val keyGenerator =
                        KeyGenerator.getInstance(
                            KeyProperties.KEY_ALGORITHM_AES,
                            KEYSTORE_PROVIDER,
                        )

                    val keyGenParameterSpec =
                        KeyGenParameterSpec.Builder(
                            KEY_ALIAS,
                            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
                        ).apply {
                            setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                            setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                            setKeySize(256) // AES-256
                            setRandomizedEncryptionRequired(true) // Force unique IV per encryption
                            // Note: setUserAuthenticationRequired(true) could be added for extra security
                            // but would require biometric/PIN for each decrypt operation
                        }.build()

                    keyGenerator.init(keyGenParameterSpec)
                    keyGenerator.generateKey()

                    Timber.d("Encryption key generated successfully")
                } else {
                    Timber.d("Encryption key already exists in Keystore")
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to generate encryption key")
                throw EncryptionException("Key generation failed", e)
            }
        }

        /**
         * Check if encryption key exists
         */
        fun hasKey(): Boolean {
            return try {
                keyStore.containsAlias(KEY_ALIAS)
            } catch (e: Exception) {
                Timber.e(e, "Failed to check key existence")
                false
            }
        }

        /**
         * Get the secret key from Keystore
         */
        private fun getSecretKey(): SecretKey {
            if (!keyStore.containsAlias(KEY_ALIAS)) {
                generateKey()
            }

            return (keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry)?.secretKey
                ?: throw EncryptionException("Failed to retrieve encryption key")
        }

        /**
         * Encrypt data using AES-256-GCM
         *
         * @param plaintext Data to encrypt
         * @return Base64-encoded string containing IV:CipherText
         * @throws EncryptionException if encryption fails
         */
        fun encrypt(plaintext: String): String {
            try {
                val cipher = Cipher.getInstance(TRANSFORMATION)
                cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())

                // Encrypt the data
                val cipherText = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))

                // Get the IV used for this encryption
                val iv = cipher.iv

                // Combine IV + cipherText and encode as Base64
                // Format: Base64(IV):Base64(CipherText)
                val ivBase64 = Base64.encodeToString(iv, Base64.NO_WRAP)
                val cipherTextBase64 = Base64.encodeToString(cipherText, Base64.NO_WRAP)

                return "$ivBase64$SEPARATOR$cipherTextBase64"
            } catch (e: Exception) {
                Timber.e(e, "Encryption failed")
                throw EncryptionException("Encryption failed", e)
            }
        }

        /**
         * Decrypt data using AES-256-GCM
         *
         * @param encryptedData Base64-encoded string containing IV:CipherText
         * @return Decrypted plaintext
         * @throws EncryptionException if decryption fails
         */
        fun decrypt(encryptedData: String): String {
            try {
                // Split IV and cipherText
                val parts = encryptedData.split(SEPARATOR)
                if (parts.size != 2) {
                    throw EncryptionException("Invalid encrypted data format")
                }

                val iv = Base64.decode(parts[0], Base64.NO_WRAP)
                val cipherText = Base64.decode(parts[1], Base64.NO_WRAP)

                // Initialize cipher with the stored IV
                val cipher = Cipher.getInstance(TRANSFORMATION)
                val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
                cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec)

                // Decrypt
                val plaintext = cipher.doFinal(cipherText)

                return String(plaintext, Charsets.UTF_8)
            } catch (e: Exception) {
                Timber.e(e, "Decryption failed")
                throw EncryptionException("Decryption failed", e)
            }
        }

        /**
         * Encrypt byte array (useful for file encryption)
         *
         * @param data Byte array to encrypt
         * @return Encrypted byte array with IV prepended
         */
        fun encryptBytes(data: ByteArray): ByteArray {
            try {
                val cipher = Cipher.getInstance(TRANSFORMATION)
                cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())

                val cipherText = cipher.doFinal(data)
                val iv = cipher.iv

                // Prepend IV to ciphertext
                return iv + cipherText
            } catch (e: Exception) {
                Timber.e(e, "Byte encryption failed")
                throw EncryptionException("Byte encryption failed", e)
            }
        }

        /**
         * Decrypt byte array
         *
         * @param encryptedData Encrypted byte array with IV prepended
         * @return Decrypted byte array
         */
        fun decryptBytes(encryptedData: ByteArray): ByteArray {
            try {
                // Extract IV (first 12 bytes) and ciphertext
                val iv = encryptedData.sliceArray(0 until IV_LENGTH)
                val cipherText = encryptedData.sliceArray(IV_LENGTH until encryptedData.size)

                val cipher = Cipher.getInstance(TRANSFORMATION)
                val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
                cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec)

                return cipher.doFinal(cipherText)
            } catch (e: Exception) {
                Timber.e(e, "Byte decryption failed")
                throw EncryptionException("Byte decryption failed", e)
            }
        }

        /**
         * Delete the encryption key from Keystore
         *
         * WARNING: This will make all encrypted data unrecoverable!
         */
        fun deleteKey() {
            try {
                if (keyStore.containsAlias(KEY_ALIAS)) {
                    keyStore.deleteEntry(KEY_ALIAS)
                    Timber.w("Encryption key deleted from Keystore")
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to delete encryption key")
                throw EncryptionException("Key deletion failed", e)
            }
        }
    }

/**
 * Custom exception for encryption/decryption errors
 */
class EncryptionException(message: String, cause: Throwable? = null) : Exception(message, cause)
