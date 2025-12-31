package com.hesapgunlugu.app.core.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Bildirimler için Room Entity
 */
@Entity(
    tableName = "notifications",
    indices = [
        Index(value = ["createdAt"]),
        Index(value = ["isRead"]),
        Index(value = ["type"]),
    ],
)
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val message: String,
    val type: NotificationType,
    val isRead: Boolean = false,
    val createdAt: Date = Date(),
    val relatedTransactionId: Int? = null,
    val relatedScheduledPaymentId: Long? = null,
)

/**
 * Bildirim tipleri
 */
enum class NotificationType {
    BUDGET_WARNING, // Bütçe uyarısı
    BUDGET_EXCEEDED, // Bütçe aşıldı
    SCHEDULED_PAYMENT_DUE, // Planlı ödeme günü geldi
    MONTHLY_SUMMARY, // Aylık özet
    SAVINGS_GOAL, // Tasarruf hedefi
    SYSTEM, // Sistem bildirimi
}
