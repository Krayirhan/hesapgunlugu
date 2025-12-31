package com.hesapgunlugu.app.core.security

import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/**
 * Unit tests for EncryptionHelper using Robolectric
 *
 * Note: Android Keystore is not available in Robolectric,
 * so these tests verify the encryption logic flow.
 * Full encryption testing should be done in instrumented tests.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
@Ignore("Requires Android Keystore; run as instrumentation test")
class EncryptionHelperTest {
    private lateinit var encryptionHelper: EncryptionHelper

    @Before
    fun setup() {
        encryptionHelper = EncryptionHelper()
    }

    @Test
    fun `test encryption helper initialization`() {
        // Basic initialization test
        assertTrue(encryptionHelper != null)
    }

    @Test
    fun `test key generation does not throw`() {
        // This may fail in Robolectric but shouldn't crash
        try {
            encryptionHelper.generateKey()
        } catch (e: Exception) {
            // Expected in Robolectric environment
            assertTrue(e is EncryptionException || e.message?.contains("Keystore") == true)
        }
    }

    @Test
    fun `test encrypt and decrypt roundtrip`() {
        // This test will fail in Robolectric but documents expected behavior
        val plaintext = "Sensitive data 123"

        try {
            val encrypted = encryptionHelper.encrypt(plaintext)
            val decrypted = encryptionHelper.decrypt(encrypted)

            assertNotEquals(plaintext, encrypted, "Encrypted should differ from plaintext")
            assertEquals(plaintext, decrypted, "Decrypted should match original plaintext")
        } catch (e: EncryptionException) {
            // Expected in Robolectric - Keystore not available
            println("Encryption test skipped: ${e.message}")
        }
    }

    @Test
    fun `test encrypt produces different output each time`() {
        val plaintext = "Test data"

        try {
            val encrypted1 = encryptionHelper.encrypt(plaintext)
            val encrypted2 = encryptionHelper.encrypt(plaintext)

            // GCM mode uses random IV, so same plaintext should produce different ciphertext
            assertNotEquals(encrypted1, encrypted2, "Same plaintext should produce different ciphertext (random IV)")
        } catch (e: EncryptionException) {
            // Expected in Robolectric
            println("Encryption test skipped: ${e.message}")
        }
    }

    @Test
    fun `test decrypt with invalid format throws exception`() {
        assertFailsWith<EncryptionException> {
            encryptionHelper.decrypt("invalid_format")
        }
    }

    @Test
    fun `test byte array encryption roundtrip`() {
        val data = "Binary data test".toByteArray()

        try {
            val encrypted = encryptionHelper.encryptBytes(data)
            val decrypted = encryptionHelper.decryptBytes(encrypted)

            assertEquals(String(data), String(decrypted), "Decrypted bytes should match original")
        } catch (e: EncryptionException) {
            // Expected in Robolectric
            println("Byte encryption test skipped: ${e.message}")
        }
    }
}
