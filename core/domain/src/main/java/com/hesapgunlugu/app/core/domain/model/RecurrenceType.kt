package com.hesapgunlugu.app.core.domain.model

/**
 * Tekrarlama tipi - Domain model
 * Planlanmış ödemeler ve tekrarlayan işlemler için kullanılır
 */
enum class RecurrenceType {
    DAILY, // Günlük
    WEEKLY, // Haftalık
    MONTHLY, // Aylık
    YEARLY, // Yıllık
}
