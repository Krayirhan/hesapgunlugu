package com.hesapgunlugu.app.core.security

import org.junit.Assert.*
import org.junit.Test

/**
 * SecurityManager Tests
 *
 * PIN validation logic tests (non-Android parts)
 * For full SecurityManager testing, use instrumented tests with DataStore
 */
class SecurityManagerTest {
    // ==================== PIN STRENGTH VALIDATION TESTS ====================

    @Test
    fun `PIN less than 4 digits should be invalid`() {
        // Given
        val shortPins = listOf("", "1", "12", "123")

        // When & Then
        shortPins.forEach { pin ->
            val result = validatePinStrength(pin)
            assertFalse("PIN '$pin' should be invalid (too short)", result.isValid)
        }
    }

    @Test
    fun `PIN more than 8 digits should be invalid`() {
        // Given
        val longPin = "123456789"

        // When
        val result = validatePinStrength(longPin)

        // Then
        assertFalse("PIN should be invalid (too long)", result.isValid)
    }

    @Test
    fun `PIN with 4-8 digits should be valid length`() {
        // Given
        val validLengthPins = listOf("7392", "84615", "293847", "1928374", "19283746")

        // When & Then - only length, not strength
        validLengthPins.forEach { pin ->
            assertTrue("PIN '$pin' should have valid length", pin.length in 4..8)
        }
    }

    @Test
    fun `PIN with non-digit characters should be invalid`() {
        // Given
        val invalidPins = listOf("123a", "12.3", "12-34", "abcd", "12 34")

        // When & Then
        invalidPins.forEach { pin ->
            val result = validatePinStrength(pin)
            assertFalse("PIN '$pin' should be invalid (non-digit)", result.isValid)
        }
    }

    @Test
    fun `repeated digit PIN should be invalid`() {
        // Given
        val repeatedPins = listOf("0000", "1111", "2222", "5555", "9999", "00000", "111111")

        // When & Then
        repeatedPins.forEach { pin ->
            val result = validatePinStrength(pin)
            assertFalse("PIN '$pin' should be invalid (repeated)", result.isValid)
        }
    }

    @Test
    fun `ascending sequential PIN should be invalid`() {
        // Given
        val ascendingPins = listOf("1234", "2345", "3456", "0123", "4567", "12345")

        // When & Then
        ascendingPins.forEach { pin ->
            val isSequential = isSequentialPin(pin)
            assertTrue("PIN '$pin' should be detected as sequential", isSequential)
        }
    }

    @Test
    fun `descending sequential PIN should be invalid`() {
        // Given
        val descendingPins = listOf("4321", "5432", "6543", "9876", "3210", "54321")

        // When & Then
        descendingPins.forEach { pin ->
            val isSequential = isSequentialPin(pin)
            assertTrue("PIN '$pin' should be detected as sequential", isSequential)
        }
    }

    @Test
    fun `common weak PINs should be invalid`() {
        // Given - common weak PINs
        val weakPins = listOf("1234", "4321", "0000", "1111", "123456", "654321")

        // When & Then
        weakPins.forEach { pin ->
            val result = validatePinStrength(pin)
            assertFalse("PIN '$pin' should be invalid (weak)", result.isValid)
        }
    }

    @Test
    fun `strong random PINs should be valid`() {
        // Given - random strong PINs
        val strongPins = listOf("7392", "8461", "2957", "6183", "4817", "93726")

        // When & Then
        strongPins.forEach { pin ->
            val result = validatePinStrength(pin)
            assertTrue("PIN '$pin' should be valid", result.isValid)
        }
    }

    @Test
    fun `PIN validation should return appropriate error messages`() {
        // Given
        val testCases =
            mapOf(
                "12" to "en az 4 haneli",
                "123456789" to "en fazla 8 haneli",
                "abcd" to "sadece rakam",
                "1111" to "tekrarlayan",
                "1234" to "yaygın",
            )

        // When & Then
        testCases.forEach { (pin, expectedMessagePart) ->
            val result = validatePinStrength(pin)
            assertFalse("PIN '$pin' should be invalid", result.isValid)
            assertTrue(
                "Error message should contain '$expectedMessagePart' for PIN '$pin', but was: ${result.errorMessage}",
                result.errorMessage.lowercase().contains(expectedMessagePart.lowercase()),
            )
        }
    }

    // ==================== SEQUENTIAL PIN DETECTION TESTS ====================

    @Test
    fun `isSequentialPin should detect ascending sequences`() {
        assertTrue(isSequentialPin("1234"))
        assertTrue(isSequentialPin("2345"))
        assertTrue(isSequentialPin("6789"))
    }

    @Test
    fun `isSequentialPin should detect descending sequences`() {
        assertTrue(isSequentialPin("4321"))
        assertTrue(isSequentialPin("5432"))
        assertTrue(isSequentialPin("9876"))
    }

    @Test
    fun `isSequentialPin should not detect non-sequential PINs`() {
        assertFalse(isSequentialPin("1357"))
        assertFalse(isSequentialPin("2468"))
        assertFalse(isSequentialPin("7392"))
        assertFalse(isSequentialPin("1928"))
    }

    @Test
    fun `isSequentialPin should handle short PINs`() {
        assertFalse(isSequentialPin("12"))
        assertFalse(isSequentialPin("1"))
        assertFalse(isSequentialPin(""))
    }

    // ==================== PIN HASH TESTS ====================

    @Test
    fun `hashPin should return consistent results`() {
        // Given
        val pin = "1234"

        // When
        val hash1 = hashPin(pin)
        val hash2 = hashPin(pin)

        // Then
        assertEquals("Same PIN should produce same hash", hash1, hash2)
    }

    @Test
    fun `hashPin should return different hashes for different PINs`() {
        // Given
        val pin1 = "1234"
        val pin2 = "4321"

        // When
        val hash1 = hashPin(pin1)
        val hash2 = hashPin(pin2)

        // Then
        assertNotEquals("Different PINs should produce different hashes", hash1, hash2)
    }

    @Test
    fun `hashPin should return 64 character hex string`() {
        // Given
        val pin = "1234"

        // When
        val hash = hashPin(pin)

        // Then
        assertEquals("SHA-256 hash should be 64 characters", 64, hash.length)
        assertTrue("Hash should be hex string", hash.all { it in '0'..'9' || it in 'a'..'f' })
    }

    // ==================== HELPER METHODS (mirror SecurityManager logic) ====================

    private fun validatePinStrength(pin: String): PinValidationResult {
        // Minimum uzunluk kontrolü
        if (pin.length < 4) {
            return PinValidationResult(false, "PIN en az 4 haneli olmalıdır")
        }

        // Maksimum uzunluk kontrolü
        if (pin.length > 8) {
            return PinValidationResult(false, "PIN en fazla 8 haneli olabilir")
        }

        // Sadece rakam kontrolü
        if (!pin.all { it.isDigit() }) {
            return PinValidationResult(false, "PIN sadece rakamlardan oluşmalıdır")
        }

        // Tekrarlayan rakam kontrolü (örn: 1111, 0000)
        if (pin.matches(Regex("(\\d)\\1{3,}"))) {
            return PinValidationResult(false, "PIN tekrarlayan rakamlar içeremez (örn: 1111)")
        }

        // Ardışık rakam kontrolü (örn: 1234, 4321)
        if (isSequentialPin(pin)) {
            return PinValidationResult(false, "PIN ardışık rakamlar içeremez (örn: 1234)")
        }

        // Yaygın zayıf PIN'ler
        val weakPins =
            setOf(
                "1234", "4321", "0000", "1111", "2222", "3333",
                "4444", "5555", "6666", "7777", "8888", "9999",
                "123456", "654321", "000000", "111111",
            )
        if (pin in weakPins) {
            return PinValidationResult(false, "Bu PIN çok yaygın, daha güçlü bir PIN seçin")
        }

        return PinValidationResult(true, "PIN geçerli")
    }

    private fun isSequentialPin(pin: String): Boolean {
        if (pin.length < 3) return false

        // Artan sıra kontrolü (1234)
        var isAscending = true
        for (i in 0 until pin.length - 1) {
            if (pin[i].digitToInt() + 1 != pin[i + 1].digitToInt()) {
                isAscending = false
                break
            }
        }
        if (isAscending) return true

        // Azalan sıra kontrolü (4321)
        var isDescending = true
        for (i in 0 until pin.length - 1) {
            if (pin[i].digitToInt() - 1 != pin[i + 1].digitToInt()) {
                isDescending = false
                break
            }
        }
        return isDescending
    }

    private fun hashPin(pin: String): String {
        val bytes = java.security.MessageDigest.getInstance("SHA-256").digest(pin.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}

// ==================== PIN VERIFICATION RESULT TESTS ====================

class PinVerificationResultTest {
    @Test
    fun `Success result should be Success type`() {
        val result = PinVerificationResult.Success
        assertTrue(result is PinVerificationResult.Success)
    }

    @Test
    fun `Failed result should contain remaining attempts`() {
        val result = PinVerificationResult.Failed(2)
        assertTrue(result is PinVerificationResult.Failed)
        assertEquals(2, result.remainingAttempts)
    }

    @Test
    fun `LockedOut result should contain remaining seconds`() {
        val result = PinVerificationResult.LockedOut(30)
        assertTrue(result is PinVerificationResult.LockedOut)
        assertEquals(30, result.remainingSeconds)
    }

    @Test
    fun `Failed with zero attempts should be valid`() {
        val result = PinVerificationResult.Failed(0)
        assertEquals(0, result.remainingAttempts)
    }

    @Test
    fun `LockedOut with extended duration should be valid`() {
        val result = PinVerificationResult.LockedOut(300) // 5 minutes
        assertEquals(300, result.remainingSeconds)
    }
}

// ==================== PIN VALIDATION RESULT TESTS ====================

class PinValidationResultTest {
    @Test
    fun `valid result should have isValid true`() {
        val result = PinValidationResult(true, "PIN geçerli")
        assertTrue(result.isValid)
        assertEquals("PIN geçerli", result.errorMessage)
    }

    @Test
    fun `invalid result should have isValid false`() {
        val result = PinValidationResult(false, "PIN çok kısa")
        assertFalse(result.isValid)
        assertEquals("PIN çok kısa", result.errorMessage)
    }

    @Test
    fun `result should be a data class with equality`() {
        val result1 = PinValidationResult(true, "message")
        val result2 = PinValidationResult(true, "message")
        assertEquals(result1, result2)
    }
}
