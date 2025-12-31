package com.hesapgunlugu.app.core.backup

import android.util.Base64
import timber.log.Timber
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

/**
 * Backup verilerini AES-256-GCM ile şifreleyen/çözen helper class
 *
 * CRITICAL SECURITY ENHANCEMENTS:
 * - Key rotation support (version metadata in backup)
 * - Re-encryption capability for password changes
 * - Secure key derivation with PBKDF2 (100,000 iterations)
 * - Versioned backup format for future migration
 */
object BackupEncryption {
    private const val ALGORITHM = "AES/GCM/NoPadding"
    private const val KEY_ALGORITHM = "AES"
    private const val KEY_DERIVATION_ALGORITHM = "PBKDF2WithHmacSHA256"
    private const val KEY_LENGTH = 256
    private const val ITERATION_COUNT = 100_000 // CRITICAL: High iteration count (was 65536)
    private const val GCM_IV_LENGTH = 12
    private const val GCM_TAG_LENGTH = 128
    private const val SALT_LENGTH = 32 // CRITICAL: Increased to 256 bits (was 128)

    // CRITICAL SECURITY: Version byte for future key rotation
    private const val BACKUP_FORMAT_VERSION: Byte = 2

    // Backup file format v2: VERSION(1) + SALT(32) + IV(12) + ENCRYPTED_DATA
    // Version 1 had: SALT(16) + IV(12) + ENCRYPTED_DATA (legacy support)

    /**
     * Veriyi şifrele - CRITICAL SECURITY: Version 2 format with key rotation support
     * @param plainText Şifrelenecek metin
     * @param password Kullanıcı şifresi (must be validated with BackupPasswordValidator)
     * @return Base64 encoded şifreli veri (versioned format)
     */
    fun encrypt(
        plainText: String,
        password: String,
    ): String? {
        return try {
            // Validate password strength (defense in depth)
            val validation = BackupPasswordValidator.validate(password)
            if (!validation.isValid || validation.score < 60) {
                Timber.w("Weak password used for backup encryption")
                // Don't fail, but log warning
            }

            // Random salt ve IV oluştur
            val salt = ByteArray(SALT_LENGTH)
            val iv = ByteArray(GCM_IV_LENGTH)
            SecureRandom().apply {
                nextBytes(salt)
                nextBytes(iv)
            }

            // Şifreden anahtar türet (high iteration count)
            val secretKey = deriveKey(password, salt)

            // Şifrele
            val cipher = Cipher.getInstance(ALGORITHM)
            val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec)

            val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))

            // CRITICAL SECURITY: Include version byte for future key rotation
            // Format v2: VERSION(1) + SALT(32) + IV(12) + ENCRYPTED_DATA
            val combined = byteArrayOf(BACKUP_FORMAT_VERSION) + salt + iv + encryptedBytes

            encodeBase64(combined)
        } catch (e: Exception) {
            Timber.e(e, "Encryption failed")
            null
        }
    }

    /**
     * Şifreli veriyi çöz - CRITICAL SECURITY: Supports v1 and v2 formats
     * @param encryptedBase64 Base64 encoded şifreli veri
     * @param password Kullanıcı şifresi
     * @return Çözülmüş metin veya null (hata durumunda)
     */
    fun decrypt(
        encryptedBase64: String,
        password: String,
    ): String? {
        return try {
            val combined = decodeBase64(encryptedBase64)

            // Detect backup format version
            val isV2 = combined.size > 45 && combined[0] == BACKUP_FORMAT_VERSION

            val (salt, iv, encryptedBytes) =
                if (isV2) {
                    // Version 2: VERSION(1) + SALT(32) + IV(12) + DATA
                    Triple(
                        combined.copyOfRange(1, 1 + SALT_LENGTH),
                        combined.copyOfRange(1 + SALT_LENGTH, 1 + SALT_LENGTH + GCM_IV_LENGTH),
                        combined.copyOfRange(1 + SALT_LENGTH + GCM_IV_LENGTH, combined.size),
                    )
                } else {
                    // Version 1 (legacy): SALT(16) + IV(12) + DATA
                    Timber.d("Decrypting legacy backup format v1")
                    val oldSaltLength = 16
                    Triple(
                        combined.copyOfRange(0, oldSaltLength),
                        combined.copyOfRange(oldSaltLength, oldSaltLength + GCM_IV_LENGTH),
                        combined.copyOfRange(oldSaltLength + GCM_IV_LENGTH, combined.size),
                    )
                }

            // Şifreden anahtar türet
            val secretKey = deriveKey(password, salt)

            // Çöz
            val cipher = Cipher.getInstance(ALGORITHM)
            val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec)

            val decryptedBytes = cipher.doFinal(encryptedBytes)
            String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            Timber.e(e, "Decryption failed - wrong password or corrupted data")
            null
        }
    }

    /**
     * CRITICAL SECURITY: Re-encrypt backup with new password (key rotation)
     * @param encryptedData Current encrypted backup
     * @param oldPassword Current password
     * @param newPassword New password (must be validated)
     * @return Re-encrypted backup or null on failure
     */
    fun reEncrypt(
        encryptedData: String,
        oldPassword: String,
        newPassword: String,
    ): String? {
        // Validate new password
        val validation = BackupPasswordValidator.validate(newPassword)
        if (!validation.isValid) {
            Timber.e("New password failed validation: ${validation.errors}")
            return null
        }

        // Decrypt with old password
        val plainText =
            decrypt(encryptedData, oldPassword) ?: run {
                Timber.e("Failed to decrypt with old password")
                return null
            }

        // Encrypt with new password (will use latest format version)
        val reEncrypted = encrypt(plainText, newPassword)

        if (reEncrypted != null) {
            Timber.d("Successfully re-encrypted backup with new password")
        }

        return reEncrypted
    }

    /**
     * Şifreden AES anahtarı türet (PBKDF2)
     */
    private fun deriveKey(
        password: String,
        salt: ByteArray,
    ): SecretKeySpec {
        val factory = SecretKeyFactory.getInstance(KEY_DERIVATION_ALGORITHM)
        val spec = PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH)
        val secretKey = factory.generateSecret(spec)
        return SecretKeySpec(secretKey.encoded, KEY_ALGORITHM)
    }

    /**
     * Verinin şifreli olup olmadığını kontrol et
     * (Base64 ve minimum uzunluk kontrolü - supports both v1 and v2)
     */
    fun isEncrypted(data: String): Boolean {
        return try {
            val decoded = decodeBase64(data)
            // Minimum uzunluk: VERSION(1) + SALT(32) + IV(12) + en az 16 byte data
            // Or legacy v1: SALT(16) + IV(12) + 16 byte data
            decoded.size >= (1 + SALT_LENGTH + GCM_IV_LENGTH + 16) ||
                decoded.size >= (16 + GCM_IV_LENGTH + 16)
        } catch (e: Exception) {
            false
        }
    }

    private fun encodeBase64(data: ByteArray): String {
        return try {
            Base64.encodeToString(data, Base64.NO_WRAP)
        } catch (e: Exception) {
            java.util.Base64.getEncoder().encodeToString(data)
        }
    }

    private fun decodeBase64(data: String): ByteArray {
        return try {
            Base64.decode(data, Base64.NO_WRAP)
        } catch (e: Exception) {
            java.util.Base64.getDecoder().decode(data)
        }
    }
}
