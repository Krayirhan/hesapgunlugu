package com.hesapgunlugu.app.core.util

/**
 * Uygulama genelinde kullanılan sabitler.
 */
object Constants {
    const val EXPORT_DATE_FORMAT: String = "yyyyMMdd_HHmmss"
    const val EXPORT_FILENAME_PREFIX: String = "transactions"

    // CSV export içindeki tarih formatı
    const val DATE_FORMAT_FULL: String = "dd.MM.yyyy"

    val CATEGORY_EMOJIS: Map<String, String> =
        mapOf(
            "food" to "🍽️",
            "restaurant" to "🍽️",
            "grocery" to "🛒",
            "shopping" to "🛍️",
            "transport" to "🚗",
            "fuel" to "⛽",
            "rent" to "🏠",
            "bills" to "🧾",
            "utilities" to "💡",
            "health" to "🏥",
            "education" to "🎓",
            "entertainment" to "🎮",
            "salary" to "💼",
            "income" to "💰",
            "other" to "💳",
        )
}
