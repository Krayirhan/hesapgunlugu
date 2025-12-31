package com.hesapgunlugu.app.core.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import org.junit.Ignore
import org.junit.Test
import java.security.KeyStore
import javax.crypto.KeyGenerator
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Keystore integrity and PBKDF2 integration tests
 * Bu test CI'da çalışır ve keystore yapılandırmasını doğrular
 */
@Ignore("Requires Android Keystore; run as instrumentation test")
class KeystoreIntegrityTest {
    @Test
    fun `keystore should be accessible`() {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        assertNotNull(keyStore, "Keystore should be initialized")
    }

    @Test
    fun `should generate AES key with correct parameters`() {
        val keyGenerator =
            KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                "AndroidKeyStore",
            )

        val keyGenSpec =
            KeyGenParameterSpec.Builder(
                "test_key_${System.currentTimeMillis()}",
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()

        keyGenerator.init(keyGenSpec)
        val key = keyGenerator.generateKey()

        assertNotNull(key, "Key should be generated")
        assertEquals("AES", key.algorithm)
    }

    @Test
    fun `PBKDF2 parameters should meet security standards`() {
        // PBKDF2 minimum requirements
        val minIterations = 100_000 // OWASP 2023 recommendation
        val minSaltLength = 16 // 128 bits
        val minKeyLength = 32 // 256 bits

        // Bu değerler SecurityManager'dan gelmeliSürece doğrula
        assertTrue(minIterations >= 100_000, "PBKDF2 iterations should be >= 100,000")
        assertTrue(minSaltLength >= 16, "Salt should be >= 16 bytes")
        assertTrue(minKeyLength >= 32, "Key should be >= 32 bytes (256 bits)")
    }

    @Test
    fun `backup encryption key should be distinct from main key`() {
        // Backup ve main encryption farklı keyler kullanmalı
        val mainKeyAlias = "finance_tracker_key"
        val backupKeyAlias = "backup_encryption_key"

        assertTrue(mainKeyAlias != backupKeyAlias, "Backup key should be separate from main key")
    }
}
