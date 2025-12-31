package com.hesapgunlugu.app.core.domain.schedule

/**
 * Stable frequency codes for scheduled payments.
 * Stored values should be locale-independent.
 */
enum class ScheduleFrequency {
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY,
}

fun parseScheduleFrequency(raw: String): ScheduleFrequency {
    val normalized = raw.trim().lowercase()
    return when (normalized) {
        "daily", "günlük", "gunluk" -> ScheduleFrequency.DAILY
        "weekly", "haftalık", "haftalik" -> ScheduleFrequency.WEEKLY
        "monthly", "aylık", "aylik" -> ScheduleFrequency.MONTHLY
        "yearly", "yıllık", "yillik" -> ScheduleFrequency.YEARLY
        else -> ScheduleFrequency.MONTHLY
    }
}
