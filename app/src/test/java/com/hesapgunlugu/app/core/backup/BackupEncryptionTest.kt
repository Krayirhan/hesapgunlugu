package com.hesapgunlugu.app.core.backup

import org.junit.Assert.*
import org.junit.Ignore
import org.junit.Test

/**
 * BackupEncryption için unit testler
 * NOT: Bu testler android.util.Base64 kullandığı için JVM'de çalışmaz.
 * Instrumented test olarak çalıştırılmalıdır.
 */
@Ignore("Android API (Base64) gerektiriyor - instrumented test olarak çalıştırın")
class BackupEncryptionTest {
    @Test
    fun `encrypt and decrypt returns original text`() {
        // Given
        val originalText = "This is test data for encryption"
        val password = "testPassword123"

        // When
        val encrypted = BackupEncryption.encrypt(originalText, password)
        assertNotNull("Encryption should not return null", encrypted)

        val decrypted = BackupEncryption.decrypt(encrypted!!, password)

        // Then
        assertEquals("Decrypted text should match original", originalText, decrypted)
    }

    @Test
    fun `encrypt with empty text returns valid encrypted string`() {
        // Given
        val originalText = ""
        val password = "testPassword123"

        // When
        val encrypted = BackupEncryption.encrypt(originalText, password)
        val decrypted = BackupEncryption.decrypt(encrypted!!, password)

        // Then
        assertEquals("Empty text should be encrypted and decrypted correctly", originalText, decrypted)
    }

    @Test
    fun `encrypt with unicode characters works correctly`() {
        // Given
        val originalText = "Türkçe karakterler: ğüşıöç 日本語 🎉💰"
        val password = "testPassword123"

        // When
        val encrypted = BackupEncryption.encrypt(originalText, password)
        val decrypted = BackupEncryption.decrypt(encrypted!!, password)

        // Then
        assertEquals("Unicode characters should be preserved", originalText, decrypted)
    }

    @Test
    fun `decrypt with wrong password returns null`() {
        // Given
        val originalText = "Secret data"
        val correctPassword = "correctPassword"
        val wrongPassword = "wrongPassword"

        // When
        val encrypted = BackupEncryption.encrypt(originalText, correctPassword)
        val decrypted = BackupEncryption.decrypt(encrypted!!, wrongPassword)

        // Then
        assertNull("Decryption with wrong password should return null", decrypted)
    }

    @Test
    fun `decrypt with corrupted data returns null`() {
        // Given
        val corruptedData = "notvalidbase64data!@#$%"

        // When
        val decrypted = BackupEncryption.decrypt(corruptedData, "anyPassword")

        // Then
        assertNull("Decryption of corrupted data should return null", decrypted)
    }

    @Test
    fun `each encryption produces different output`() {
        // Given
        val originalText = "Test data"
        val password = "testPassword123"

        // When
        val encrypted1 = BackupEncryption.encrypt(originalText, password)
        val encrypted2 = BackupEncryption.encrypt(originalText, password)

        // Then
        assertNotEquals(
            "Each encryption should produce different output (due to random salt/IV)",
            encrypted1,
            encrypted2,
        )

        // Both should decrypt to original
        assertEquals(originalText, BackupEncryption.decrypt(encrypted1!!, password))
        assertEquals(originalText, BackupEncryption.decrypt(encrypted2!!, password))
    }

    @Test
    fun `isEncrypted returns true for encrypted data`() {
        // Given
        val originalText = "Test data for encryption check"
        val password = "testPassword123"
        val encrypted = BackupEncryption.encrypt(originalText, password)

        // When
        val result = BackupEncryption.isEncrypted(encrypted!!)

        // Then
        assertTrue("Encrypted data should be detected as encrypted", result)
    }

    @Test
    fun `isEncrypted returns false for plain JSON`() {
        // Given
        val plainJson = """{"name": "test", "value": 123}"""

        // When
        val result = BackupEncryption.isEncrypted(plainJson)

        // Then
        assertFalse("Plain JSON should not be detected as encrypted", result)
    }

    @Test
    fun `isEncrypted returns false for short base64 string`() {
        // Given
        val shortBase64 = "dGVzdA==" // "test" in base64

        // When
        val result = BackupEncryption.isEncrypted(shortBase64)

        // Then
        assertFalse("Short base64 should not be detected as encrypted", result)
    }

    @Test
    fun `encrypt with special password characters works`() {
        // Given
        val originalText = "Data to encrypt"
        val specialPassword = "p@ssw0rd!#\$%^&*()_+ğüşı"

        // When
        val encrypted = BackupEncryption.encrypt(originalText, specialPassword)
        val decrypted = BackupEncryption.decrypt(encrypted!!, specialPassword)

        // Then
        assertEquals("Special password characters should work", originalText, decrypted)
    }

    @Test
    fun `encrypt large data works correctly`() {
        // Given
        val largeText = "A".repeat(100000) // 100KB of data
        val password = "testPassword123"

        // When
        val encrypted = BackupEncryption.encrypt(largeText, password)
        assertNotNull("Large text encryption should not return null", encrypted)

        val decrypted = BackupEncryption.decrypt(encrypted!!, password)

        // Then
        assertEquals("Large text should be encrypted and decrypted correctly", largeText, decrypted)
    }

    @Test
    fun `encrypt with JSON backup data format works`() {
        // Given
        val backupJson =
            """
            {
                "version": 2,
                "createdAt": "2024-01-15T10:30:00",
                "transactions": [
                    {"id": 1, "title": "Market", "amount": 150.50, "category": "Alışveriş"}
                ],
                "scheduledPayments": []
            }
            """.trimIndent()
        val password = "backupPassword123"

        // When
        val encrypted = BackupEncryption.encrypt(backupJson, password)
        val decrypted = BackupEncryption.decrypt(encrypted!!, password)

        // Then
        assertEquals("JSON backup data should be preserved exactly", backupJson, decrypted)
    }
}
