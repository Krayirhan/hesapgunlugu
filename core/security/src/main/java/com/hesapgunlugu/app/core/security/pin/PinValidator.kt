package com.hesapgunlugu.app.core.security.pin

/**
 * PIN güç kontrolü ve validasyon işlemleri.
 * Single Responsibility Principle uygulanarak SecurityManager'dan ayrıldı.
 */
object PinValidator {
    /**
     * PIN gücünü kontrol et
     * @return Validation result with error message if invalid
     */
    fun validatePinStrength(pin: String): PinValidationResult {
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

    /**
     * Ardışık rakam kontrolü
     */
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
}

/**
 * PIN güç kontrolü sonucu
 */
data class PinValidationResult(
    val isValid: Boolean,
    val errorMessage: String,
)
