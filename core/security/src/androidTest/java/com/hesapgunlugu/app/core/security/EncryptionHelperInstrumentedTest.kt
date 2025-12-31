package com.hesapgunlugu.app.core.security

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/**
 * Instrumented tests for EncryptionHelper
 *
 * These tests verify encryption functionality on an actual Android device/emulator
 * where Android Keystore is available.
 */
@RunWith(AndroidJUnit4::class)
class EncryptionHelperInstrumentedTest {
    private lateinit var context: Context
    private lateinit var encryptionHelper: EncryptionHelper

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        encryptionHelper = EncryptionHelper()

        // Clean up any existing keys
        try {
            encryptionHelper.deleteKey()
        } catch (e: Exception) {
            // Ignore if key doesn't exist
        }
    }

    @Test
    fun generateKey_createsKeystoreEntry() {
        encryptionHelper.generateKey()

        assertTrue(encryptionHelper.hasKey(), "Key should exist after generation")
    }

    @Test
    fun encrypt_decrypt_roundTrip_success() =
        runTest {
            val plaintext = "Sensitive data 123!@#"

            val encrypted = encryptionHelper.encrypt(plaintext)
            val decrypted = encryptionHelper.decrypt(encrypted)

            assertEquals(plaintext, decrypted, "Decrypted text should match original")
        }

    @Test
    fun encrypt_producesDifferentCiphertext_forSamePlaintext() {
        val plaintext = "Test data"

        val encrypted1 = encryptionHelper.encrypt(plaintext)
        val encrypted2 = encryptionHelper.encrypt(plaintext)

        assertNotEquals(encrypted1, encrypted2, "Same plaintext should produce different ciphertext (random IV)")
    }

    @Test
    fun encrypt_ciphertextDiffersFromPlaintext() {
        val plaintext = "Secret message"

        val encrypted = encryptionHelper.encrypt(plaintext)

        assertNotEquals(plaintext, encrypted, "Encrypted text should differ from plaintext")
        assertTrue(encrypted.contains(":"), "Encrypted format should contain IV:CipherText separator")
    }

    @Test
    fun encryptBytes_decryptBytes_roundTrip_success() {
        val data = "Binary test data".toByteArray()

        val encrypted = encryptionHelper.encryptBytes(data)
        val decrypted = encryptionHelper.decryptBytes(encrypted)

        assertEquals(String(data), String(decrypted), "Decrypted bytes should match original")
    }

    @Test
    fun decrypt_withInvalidData_throwsException() {
        var exceptionThrown = false

        try {
            encryptionHelper.decrypt("invalid:format:data")
        } catch (e: EncryptionException) {
            exceptionThrown = true
        }

        assertTrue(exceptionThrown, "Invalid data should throw EncryptionException")
    }

    @Test
    fun deleteKey_removesKeystoreEntry() {
        encryptionHelper.generateKey()
        assertTrue(encryptionHelper.hasKey())

        encryptionHelper.deleteKey()

        assertFalse(encryptionHelper.hasKey(), "Key should not exist after deletion")
    }

    @Test
    fun multipleEncryptDecrypt_withSameKey_success() {
        encryptionHelper.generateKey()

        val testCases =
            listOf(
                "Short",
                "Medium length text with spaces",
                "Very long text ".repeat(100),
                "Special chars: !@#$%^&*()_+-=[]{}|;':\",./<>?",
                "Unicode: 你好世界 🌍 Привет мир",
            )

        testCases.forEach { plaintext ->
            val encrypted = encryptionHelper.encrypt(plaintext)
            val decrypted = encryptionHelper.decrypt(encrypted)
            assertEquals(plaintext, decrypted, "Failed for: $plaintext")
        }
    }

    @Test
    fun hasKey_returnsFalse_whenNoKeyExists() {
        // Ensure no key exists
        try {
            encryptionHelper.deleteKey()
        } catch (e: Exception) {
            // Ignore
        }

        assertFalse(encryptionHelper.hasKey(), "Should return false when no key exists")
    }

    @Test
    fun encrypt_largeData_success() {
        val largeText = "Lorem ipsum ".repeat(1000) // ~12KB

        val encrypted = encryptionHelper.encrypt(largeText)
        val decrypted = encryptionHelper.decrypt(encrypted)

        assertEquals(largeText, decrypted)
    }
}
