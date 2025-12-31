package com.hesapgunlugu.app.core.security

/**
 * Åžifre gÃ¼Ã§lÃ¼lÃ¼k kontrol sÄ±nÄ±fÄ±
 * Backup ÅŸifresi ve diÄŸer ÅŸifreler iÃ§in gÃ¼venlik kontrolÃ¼ saÄŸlar
 */
object PasswordStrengthChecker {
    enum class PasswordStrength {
        WEAK, // ZayÄ±f: 6 karakterden az
        FAIR, // Orta: 6+ karakter ama karÄ±ÅŸÄ±k deÄŸil
        GOOD, // Ä°yi: 8+ karakter, harf ve rakam
        STRONG, // GÃ¼Ã§lÃ¼: 10+ karakter, bÃ¼yÃ¼k/kÃ¼Ã§Ã¼k harf, rakam, Ã¶zel karakter
    }

    data class PasswordStrengthResult(
        val strength: PasswordStrength,
        // 0-100`n        val score: Int,
        val suggestions: List<String>,
    )

    /**
     * Åžifre gÃ¼Ã§lÃ¼lÃ¼ÄŸÃ¼nÃ¼ analiz et
     */
    fun analyze(password: String): PasswordStrengthResult {
        if (password.isEmpty()) {
            return PasswordStrengthResult(
                strength = PasswordStrength.WEAK,
                score = 0,
                suggestions = listOf("Åžifre boÅŸ olamaz"),
            )
        }

        var score = 0
        val suggestions = mutableListOf<String>()

        // Uzunluk kontrolÃ¼
        when {
            password.length >= 12 -> score += 30
            password.length >= 10 -> score += 25
            password.length >= 8 -> score += 20
            password.length >= 6 -> score += 10
            else -> suggestions.add("En az 6 karakter kullanÄ±n")
        }

        // KÃ¼Ã§Ã¼k harf kontrolÃ¼
        if (password.any { it.isLowerCase() }) {
            score += 15
        } else {
            suggestions.add("KÃ¼Ã§Ã¼k harf ekleyin")
        }

        // BÃ¼yÃ¼k harf kontrolÃ¼
        if (password.any { it.isUpperCase() }) {
            score += 15
        } else {
            suggestions.add("BÃ¼yÃ¼k harf ekleyin")
        }

        // Rakam kontrolÃ¼
        if (password.any { it.isDigit() }) {
            score += 20
        } else {
            suggestions.add("Rakam ekleyin")
        }

        // Ã–zel karakter kontrolÃ¼
        val specialChars = "!@#\$%^&*()_+-=[]{}|;':\",./<>?"
        if (password.any { it in specialChars }) {
            score += 20
        } else {
            suggestions.add("Ã–zel karakter ekleyin (!@#\$%...)")
        }

        // YaygÄ±n ÅŸifre kontrolÃ¼
        val commonPasswords =
            listOf(
                "123456", "password", "12345678", "qwerty", "123456789",
                "1234567", "ÅŸifre", "sifre", "parola", "111111",
            )
        if (password.lowercase() in commonPasswords) {
            score = (score * 0.3).toInt() // %70 ceza
            suggestions.add("YaygÄ±n ÅŸifre kullanmayÄ±n")
        }

        // ArdÄ±ÅŸÄ±k karakter kontrolÃ¼
        if (hasSequentialChars(password)) {
            score = (score * 0.8).toInt() // %20 ceza
            suggestions.add("ArdÄ±ÅŸÄ±k karakter kullanmaktan kaÃ§Ä±nÄ±n")
        }

        val strength =
            when {
                score >= 80 -> PasswordStrength.STRONG
                score >= 60 -> PasswordStrength.GOOD
                score >= 40 -> PasswordStrength.FAIR
                else -> PasswordStrength.WEAK
            }

        return PasswordStrengthResult(
            strength = strength,
            score = score.coerceIn(0, 100),
            suggestions = suggestions,
        )
    }

    /**
     * Åžifrenin minimum gereksinimleri karÅŸÄ±layÄ±p karÅŸÄ±lamadÄ±ÄŸÄ±nÄ± kontrol et
     */
    fun meetsMinimumRequirements(password: String): Boolean {
        return password.length >= 6
    }

    /**
     * Backup ÅŸifresi iÃ§in Ã¶nerilen minimum gereksinimleri kontrol et
     */
    fun meetsBackupRequirements(password: String): Boolean {
        val result = analyze(password)
        return result.strength >= PasswordStrength.GOOD
    }

    /**
     * ArdÄ±ÅŸÄ±k karakterleri kontrol et (abc, 123 gibi)
     */
    private fun hasSequentialChars(password: String): Boolean {
        if (password.length < 3) return false

        for (i in 0 until password.length - 2) {
            val c1 = password[i].code
            val c2 = password[i + 1].code
            val c3 = password[i + 2].code

            // Artan sÄ±ra (abc, 123)
            if (c2 == c1 + 1 && c3 == c2 + 1) return true
            // Azalan sÄ±ra (cba, 321)
            if (c2 == c1 - 1 && c3 == c2 - 1) return true
            // AynÄ± karakter tekrarÄ± (aaa, 111)
            if (c1 == c2 && c2 == c3) return true
        }
        return false
    }

    /**
     * Åžifre gÃ¼cÃ¼nÃ¼ gÃ¶rsel renk olarak dÃ¶ndÃ¼r
     */
    fun getStrengthColor(strength: PasswordStrength): Long {
        return when (strength) {
            PasswordStrength.WEAK -> 0xFFE53935 // KÄ±rmÄ±zÄ±
            PasswordStrength.FAIR -> 0xFFFFA726 // Turuncu
            PasswordStrength.GOOD -> 0xFF43A047 // YeÅŸil
            PasswordStrength.STRONG -> 0xFF1E88E5 // Mavi
        }
    }

    /**
     * Åžifre gÃ¼cÃ¼nÃ¼ metin olarak dÃ¶ndÃ¼r
     */
    fun getStrengthText(strength: PasswordStrength): String {
        return when (strength) {
            PasswordStrength.WEAK -> "ZayÄ±f"
            PasswordStrength.FAIR -> "Orta"
            PasswordStrength.GOOD -> "Ä°yi"
            PasswordStrength.STRONG -> "GÃ¼Ã§lÃ¼"
        }
    }
}
