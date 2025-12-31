package com.hesapgunlugu.app.core.backup

import androidx.annotation.StringRes

/**
 * Backup password strength validator.
 * Follows NIST/OWASP guidance for minimum length and diversity checks.
 */
object BackupPasswordValidator {
    private const val MIN_LENGTH = 12
    private const val RECOMMENDED_LENGTH = 16

    // Common weak passwords (subset).
    private val COMMON_PASSWORDS =
        setOf(
            "password", "123456", "12345678", "qwerty", "abc123",
            "monkey", "1234567", "letmein", "trustno1", "dragon",
            "baseball", "iloveyou", "master", "sunshine", "ashley",
            "bailey", "passw0rd", "shadow", "123123", "654321",
            "superman", "qazwsx", "michael", "football", "password1",
        )

    // Keyboard patterns.
    private val KEYBOARD_PATTERNS =
        listOf(
            "qwerty",
            "asdfgh",
            "zxcvbn",
            "123456",
            "098765",
            "qwertz",
            "azerty",
            "dvorak",
        )

    data class ValidationMessage(
        @StringRes val resId: Int,
        val args: List<Any> = emptyList(),
    )

    data class PasswordStrength(
        val isValid: Boolean,
        // 0-100
        val score: Int,
        val errors: List<ValidationMessage>,
        val suggestions: List<ValidationMessage>,
    )

    /**
     * Validates backup password strength.
     */
    fun validate(password: String): PasswordStrength {
        val errors = mutableListOf<ValidationMessage>()
        val suggestions = mutableListOf<ValidationMessage>()
        var score = 0

        // Length check.
        when {
            password.length < MIN_LENGTH -> {
                errors.add(ValidationMessage(R.string.backup_password_min_length_error, listOf(MIN_LENGTH)))
                score += (password.length * 100 / MIN_LENGTH).coerceAtMost(20)
            }
            password.length < RECOMMENDED_LENGTH -> {
                suggestions.add(
                    ValidationMessage(R.string.backup_password_recommended_length, listOf(RECOMMENDED_LENGTH)),
                )
                score += 20
            }
            else -> {
                score += 30
            }
        }

        // Character diversity.
        val hasLowercase = password.any { it.isLowerCase() }
        val hasUppercase = password.any { it.isUpperCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecial = password.any { !it.isLetterOrDigit() }

        val diversityCount = listOf(hasLowercase, hasUppercase, hasDigit, hasSpecial).count { it }

        when (diversityCount) {
            0, 1 -> {
                errors.add(ValidationMessage(R.string.backup_password_diversity_required))
                score += 5
            }
            2 -> {
                errors.add(ValidationMessage(R.string.backup_password_diversity_min_types))
                score += 15
            }
            3 -> {
                suggestions.add(ValidationMessage(R.string.backup_password_add_symbol))
                score += 25
            }
            4 -> {
                score += 35
            }
        }

        // Common password check.
        if (COMMON_PASSWORDS.contains(password.lowercase())) {
            errors.add(ValidationMessage(R.string.backup_password_common_error))
            score = (score * 0.3).toInt()
        }

        // Keyboard pattern check.
        val lowercasePassword = password.lowercase()
        if (KEYBOARD_PATTERNS.any { lowercasePassword.contains(it) }) {
            errors.add(ValidationMessage(R.string.backup_password_keyboard_pattern_error))
            score -= 20
        }

        // Repetition check.
        if (hasRepetitiveCharacters(password)) {
            errors.add(ValidationMessage(R.string.backup_password_repetition_error))
            score -= 10
        }

        // Sequential characters check.
        if (hasSequentialCharacters(password)) {
            suggestions.add(ValidationMessage(R.string.backup_password_sequence_suggestion))
            score -= 5
        }

        // Bonus for length.
        if (password.length >= RECOMMENDED_LENGTH) {
            score += (password.length - RECOMMENDED_LENGTH) * 2
        }

        score = score.coerceIn(0, 100)

        // Strength-based suggestions.
        when {
            score < 40 -> suggestions.add(ValidationMessage(R.string.backup_password_strength_advice_very_weak))
            score < 60 -> suggestions.add(ValidationMessage(R.string.backup_password_strength_advice_weak))
            score < 80 -> suggestions.add(ValidationMessage(R.string.backup_password_strength_advice_medium))
            else -> suggestions.add(ValidationMessage(R.string.backup_password_strength_advice_strong))
        }

        return PasswordStrength(
            isValid = errors.isEmpty(),
            score = score,
            errors = errors,
            suggestions = suggestions,
        )
    }

    private fun hasRepetitiveCharacters(password: String): Boolean {
        return password.windowed(3).any { window ->
            window[0] == window[1] && window[1] == window[2]
        }
    }

    private fun hasSequentialCharacters(password: String): Boolean {
        return password.windowed(3).any { window ->
            val chars = window.map { it.code }
            (chars[1] == chars[0] + 1 && chars[2] == chars[1] + 1) ||
                (chars[1] == chars[0] - 1 && chars[2] == chars[1] - 1)
        }
    }

    @StringRes
    fun getStrengthMeterText(score: Int): Int {
        return when {
            score < 40 -> R.string.backup_password_strength_very_weak
            score < 60 -> R.string.backup_password_strength_weak
            score < 80 -> R.string.backup_password_strength_medium
            score < 90 -> R.string.backup_password_strength_strong
            else -> R.string.backup_password_strength_very_strong
        }
    }

    fun getStrengthColor(score: Int): Long {
        return when {
            score < 40 -> 0xFFFF0000 // Red
            score < 60 -> 0xFFFF6600 // Orange
            score < 80 -> 0xFFFFCC00 // Yellow
            else -> 0xFF00CC00 // Green
        }
    }
}
