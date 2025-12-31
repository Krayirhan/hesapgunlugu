package com.hesapgunlugu.app.core.backup

import com.hesapgunlugu.app.core.backup.serialization.BackupSerializer
import com.hesapgunlugu.app.core.common.time.TimeProvider
import com.hesapgunlugu.app.core.domain.model.ScheduledPayment
import com.hesapgunlugu.app.core.domain.model.Transaction
import com.hesapgunlugu.app.core.domain.model.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Date

class BackupSerializerTest {
    private class FixedTimeProvider(private val fixedMillis: Long) : TimeProvider {
        override fun nowMillis(): Long = fixedMillis
    }

    @Test
    fun transaction_roundTripPreservesFields() {
        val serializer = BackupSerializer(FixedTimeProvider(1_700_000_000_000))
        val transaction =
            Transaction(
                id = 7,
                title = "Salary",
                amount = 2500.0,
                timestamp = 1_700_000_000_000,
                type = TransactionType.INCOME,
                category = "Income",
                emoji = ":)",
            )

        val backup = serializer.toBackup(transaction)
        val restored = serializer.fromBackup(backup)

        assertEquals(transaction.id, restored.id)
        assertEquals(transaction.title, restored.title)
        assertEquals(transaction.amount, restored.amount, 0.0)
        assertEquals(transaction.timestamp, restored.timestamp)
        assertEquals(transaction.type, restored.type)
    }

    @Test
    fun scheduledPayment_roundTripPreservesFields() {
        val serializer = BackupSerializer(FixedTimeProvider(1_700_000_000_000))
        val payment =
            ScheduledPayment(
                id = 5,
                title = "Rent",
                amount = 1200.0,
                isIncome = false,
                isRecurring = true,
                frequency = "Monthly",
                dueDate = Date(1_700_000_100_000),
                emoji = ":)",
                isPaid = false,
                category = "Bills",
                createdAt = Date(1_700_000_050_000),
            )

        val backup = serializer.toBackup(payment)
        val restored = serializer.fromBackup(backup)

        assertEquals(payment.id, restored.id)
        assertEquals(payment.title, restored.title)
        assertEquals(payment.amount, restored.amount, 0.0)
        assertEquals(payment.dueDate.time, restored.dueDate.time)
    }
}
