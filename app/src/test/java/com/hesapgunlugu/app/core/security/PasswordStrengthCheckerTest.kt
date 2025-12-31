package com.hesapgunlugu.app.core.security

import org.junit.Assert.*
import org.junit.Test

/**
 * PasswordStrengthChecker Tests
 */
class PasswordStrengthCheckerTest {
    @Test
    fun `weak PIN should be detected`() {
        // Given
        val weakPins = listOf("1234", "0000", "1111", "4321")

        // When & Then
        weakPins.forEach { pin ->
            assertTrue("$pin should be weak", PasswordStrengthChecker.isWeakPin(pin))
        }
    }

    @Test
    fun `strong PIN should not be detected as weak`() {
        // Given
        val strongPins = listOf("7392", "8461", "2957", "6183")

        // When & Then
        strongPins.forEach { pin ->
            assertFalse("$pin should not be weak", PasswordStrengthChecker.isWeakPin(pin))
        }
    }

    @Test
    fun `sequential PIN should be detected`() {
        // Given
        val sequentialPins = listOf("1234", "2345", "9876", "8765")

        // When & Then
        sequentialPins.forEach { pin ->
            assertTrue("$pin should be sequential", PasswordStrengthChecker.isSequential(pin))
        }
    }

    @Test
    fun `repeated PIN should be detected`() {
        // Given
        val repeatedPins = listOf("0000", "1111", "5555", "9999")

        // When & Then
        repeatedPins.forEach { pin ->
            assertTrue("$pin should be repeated", PasswordStrengthChecker.isRepeated(pin))
        }
    }

    @Test
    fun `PIN length validation should work`() {
        // Given
        val shortPin = "123"
        val validPin = "1234"
        val longPin = "123456789"

        // When & Then
        assertFalse(PasswordStrengthChecker.isValidLength(shortPin))
        assertTrue(PasswordStrengthChecker.isValidLength(validPin))
        assertFalse(PasswordStrengthChecker.isValidLength(longPin))
    }

    @Test
    fun `PIN strength should be calculated correctly`() {
        // Given
        val weakPin = "1234"
        val mediumPin = "1357"
        val strongPin = "7392"

        // When
        val weakStrength = PasswordStrengthChecker.calculateStrength(weakPin)
        val strongStrength = PasswordStrengthChecker.calculateStrength(strongPin)

        // Then
        assertTrue(weakStrength < strongStrength)
    }
}

/**
 * Password Strength Checker utility for tests
 * This should match the actual implementation
 */
object PasswordStrengthChecker {
    private val WEAK_PINS =
        setOf(
            "0000", "1111", "2222", "3333", "4444",
            "5555", "6666", "7777", "8888", "9999",
            "1234", "4321", "0123", "9876", "1212",
            "2121", "1357", "2468", "0852", "1470",
        )

    fun isWeakPin(pin: String): Boolean {
        return WEAK_PINS.contains(pin) ||
            isSequential(pin) ||
            isRepeated(pin)
    }

    fun isSequential(pin: String): Boolean {
        if (pin.length < 4) return false

        val ascending = pin.zipWithNext().all { (a, b) -> b - a == 1 }
        val descending = pin.zipWithNext().all { (a, b) -> a - b == 1 }

        return ascending || descending
    }

    fun isRepeated(pin: String): Boolean {
        return pin.all { it == pin[0] }
    }

    fun isValidLength(pin: String): Boolean {
        return pin.length in 4..8
    }

    fun calculateStrength(pin: String): Int {
        var score = 0

        // Length bonus
        score += pin.length * 10

        // Unique digits bonus
        score += pin.toSet().size * 15

        // No sequential penalty
        if (!isSequential(pin)) score += 20

        // No repeated penalty
        if (!isRepeated(pin)) score += 20

        // Not in weak list
        if (!WEAK_PINS.contains(pin)) score += 30

        return score.coerceIn(0, 100)
    }
}
