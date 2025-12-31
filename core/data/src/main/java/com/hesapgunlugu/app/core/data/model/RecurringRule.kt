package com.hesapgunlugu.app.core.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * Tekrarlama tipi - Data model (Room ile uyumlu)
 */
enum class RecurrenceType {
    DAILY, // Günlük
    WEEKLY, // Haftalık
    MONTHLY, // Aylık
    YEARLY, // Yıllık
}

/**
 * Tekrarlayan ödeme kuralı
 */
@Entity(tableName = "recurring_rules")
data class RecurringRule(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    // İlişkili scheduled payment ID
    val scheduledPaymentId: Long,
    // Tekrar tipi: DAILY, WEEKLY, MONTHLY, YEARLY
    val recurrenceType: RecurrenceType,
    // Tekrar aralığı (örn: her 2 günde bir = 2)
    val interval: Int = 1,
    // Haftanın günleri (WEEKLY için: 1=Pazartesi, 7=Pazar)
    val daysOfWeek: List<Int>? = null,
    // Ayın günü (MONTHLY için: 1-31)
    val dayOfMonth: Int? = null,
    // Bitiş tarihi (null = sınırsız)
    val endDate: Date? = null,
    // Maksimum tekrar sayısı (null = sınırsız)
    val maxOccurrences: Int? = null,
    // Şu ana kadar kaç kez oluşturuldu
    val currentOccurrences: Int = 0,
    // Son oluşturulma tarihi
    val lastGenerated: Date? = null,
    // Aktif mi
    val isActive: Boolean = true,
    // Oluşturulma tarihi
    val createdAt: Date = Date(),
    // Güncellenme tarihi
    val updatedAt: Date = Date(),
)

// RecurrenceType artık core.domain.model'de tanımlı
// import com.hesapgunlugu.app.core.domain.model.RecurrenceType

/**
 * Bir sonraki oluşturulma tarihini hesapla
 */
fun RecurringRule.getNextOccurrence(fromDate: Date = Date()): Date? {
    // Maksimum tekrar kontrolü
    if (maxOccurrences != null && currentOccurrences >= maxOccurrences) {
        return null
    }

    // Bitiş tarihi kontrolü
    if (endDate != null && fromDate.after(endDate)) {
        return null
    }

    val calendar =
        Calendar.getInstance().apply {
            time = lastGenerated ?: fromDate
        }

    return when (recurrenceType) {
        RecurrenceType.DAILY -> {
            calendar.add(Calendar.DAY_OF_MONTH, interval)
            calendar.time
        }

        RecurrenceType.WEEKLY -> {
            calendar.add(Calendar.WEEK_OF_YEAR, interval)
            calendar.time
        }

        RecurrenceType.MONTHLY -> {
            calendar.add(Calendar.MONTH, interval)
            // Ayın belirli bir gününe sabitle
            if (dayOfMonth != null) {
                calendar.set(
                    Calendar.DAY_OF_MONTH,
                    dayOfMonth.coerceIn(1, calendar.getActualMaximum(Calendar.DAY_OF_MONTH)),
                )
            }
            calendar.time
        }

        RecurrenceType.YEARLY -> {
            calendar.add(Calendar.YEAR, interval)
            calendar.time
        }
    }
}

/**
 * Kural hala geçerli mi kontrol et
 */
fun RecurringRule.isValid(): Boolean {
    if (!isActive) return false

    // Maksimum tekrar kontrolü
    if (maxOccurrences != null && currentOccurrences >= maxOccurrences) {
        return false
    }

    // Bitiş tarihi kontrolü
    if (endDate != null && Date().after(endDate)) {
        return false
    }

    return true
}
