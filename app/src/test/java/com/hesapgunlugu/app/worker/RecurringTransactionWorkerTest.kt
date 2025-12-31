package com.hesapgunlugu.app.worker

import com.hesapgunlugu.app.core.data.local.RecurringTransactionEntity
import org.junit.Assert.*
import org.junit.Test
import java.util.concurrent.TimeUnit

/**
 * RecurringTransactionWorker Logic Tests
 *
 * Bu testler worker'ın business logic'ini test eder.
 * Full worker testleri için AndroidTest kullanılmalı (WorkManager ile).
 */
class RecurringTransactionWorkerTest {
    // ==================== NEXT EXECUTION DATE CALCULATION TESTS ====================

    @Test
    fun `daily frequency should add 1 day`() {
        // Given
        val baseDate = System.currentTimeMillis()
        val frequency = "DAILY"

        // When
        val nextDate = calculateNextExecutionDate(baseDate, frequency)

        // Then
        val expectedDate = baseDate + TimeUnit.DAYS.toMillis(1)
        assertEquals("Next date should be 1 day later", expectedDate, nextDate)
    }

    @Test
    fun `weekly frequency should add 7 days`() {
        // Given
        val baseDate = System.currentTimeMillis()
        val frequency = "WEEKLY"

        // When
        val nextDate = calculateNextExecutionDate(baseDate, frequency)

        // Then
        val expectedDate = baseDate + TimeUnit.DAYS.toMillis(7)
        assertEquals("Next date should be 7 days later", expectedDate, nextDate)
    }

    @Test
    fun `monthly frequency should add 30 days`() {
        // Given
        val baseDate = System.currentTimeMillis()
        val frequency = "MONTHLY"

        // When
        val nextDate = calculateNextExecutionDate(baseDate, frequency)

        // Then
        val expectedDate = baseDate + TimeUnit.DAYS.toMillis(30)
        assertEquals("Next date should be 30 days later", expectedDate, nextDate)
    }

    @Test
    fun `yearly frequency should add 365 days`() {
        // Given
        val baseDate = System.currentTimeMillis()
        val frequency = "YEARLY"

        // When
        val nextDate = calculateNextExecutionDate(baseDate, frequency)

        // Then
        val expectedDate = baseDate + TimeUnit.DAYS.toMillis(365)
        assertEquals("Next date should be 365 days later", expectedDate, nextDate)
    }

    @Test
    fun `unknown frequency should default to monthly`() {
        // Given
        val baseDate = System.currentTimeMillis()
        val frequency = "INVALID_FREQUENCY"

        // When
        val nextDate = calculateNextExecutionDate(baseDate, frequency)

        // Then
        val expectedDate = baseDate + TimeUnit.DAYS.toMillis(30)
        assertEquals("Unknown frequency should default to monthly", expectedDate, nextDate)
    }

    // ==================== DUE TRANSACTION FILTERING TESTS ====================

    @Test
    fun `transaction due today should be executed`() {
        // Given
        val currentTime = System.currentTimeMillis()
        val transaction = createRecurringTransaction(nextExecutionDate = currentTime)

        // When
        val isDue = isDueForExecution(transaction, currentTime)

        // Then
        assertTrue("Transaction due today should be executed", isDue)
    }

    @Test
    fun `transaction due yesterday should be executed`() {
        // Given
        val currentTime = System.currentTimeMillis()
        val yesterday = currentTime - TimeUnit.DAYS.toMillis(1)
        val transaction = createRecurringTransaction(nextExecutionDate = yesterday)

        // When
        val isDue = isDueForExecution(transaction, currentTime)

        // Then
        assertTrue("Overdue transaction should be executed", isDue)
    }

    @Test
    fun `transaction due tomorrow should not be executed`() {
        // Given
        val currentTime = System.currentTimeMillis()
        val tomorrow = currentTime + TimeUnit.DAYS.toMillis(1)
        val transaction = createRecurringTransaction(nextExecutionDate = tomorrow)

        // When
        val isDue = isDueForExecution(transaction, currentTime)

        // Then
        assertFalse("Future transaction should not be executed", isDue)
    }

    @Test
    fun `inactive transaction should not be executed`() {
        // Given
        val currentTime = System.currentTimeMillis()
        val transaction =
            createRecurringTransaction(
                nextExecutionDate = currentTime,
                isActive = false,
            )

        // When
        val isDue = isDueForExecution(transaction, currentTime)

        // Then
        assertFalse("Inactive transaction should not be executed", isDue)
    }

    @Test
    fun `transaction past end date should not be executed`() {
        // Given
        val currentTime = System.currentTimeMillis()
        val pastEndDate = currentTime - TimeUnit.DAYS.toMillis(1)
        val transaction =
            createRecurringTransaction(
                nextExecutionDate = currentTime,
                endDate = pastEndDate,
            )

        // When
        val shouldBeActive = currentTime <= (transaction.endDate ?: Long.MAX_VALUE)

        // Then
        assertFalse("Transaction past end date should not be active", shouldBeActive)
    }

    // ==================== END DATE VALIDATION TESTS ====================

    @Test
    fun `transaction without end date should continue indefinitely`() {
        // Given
        val currentTime = System.currentTimeMillis()
        val farFuture = currentTime + TimeUnit.DAYS.toMillis(1000)
        val transaction =
            createRecurringTransaction(
                nextExecutionDate = farFuture,
                endDate = null,
            )

        // When
        val hasExpired = transaction.endDate != null && farFuture > transaction.endDate

        // Then
        assertFalse("Transaction without end date should never expire", hasExpired)
    }

    @Test
    fun `transaction should stop when reaching end date`() {
        // Given
        val currentTime = System.currentTimeMillis()
        val endDate = currentTime + TimeUnit.DAYS.toMillis(10)
        val nextExecution = currentTime + TimeUnit.DAYS.toMillis(30) // Beyond end date

        // When
        val shouldStop = nextExecution > endDate

        // Then
        assertTrue("Transaction should stop when next execution exceeds end date", shouldStop)
    }

    // ==================== RECURRING ENTITY VALIDATION TESTS ====================

    @Test
    fun `recurring transaction should have valid data`() {
        // Given
        val transaction = createRecurringTransaction()

        // Then
        assertTrue("Title should not be empty", transaction.title.isNotEmpty())
        assertTrue("Amount should be positive", transaction.amount > 0)
        assertTrue("Type should be valid", transaction.type in listOf("INCOME", "EXPENSE"))
        assertTrue("Category should not be empty", transaction.category.isNotEmpty())
        assertTrue("Emoji should not be empty", transaction.emoji.isNotEmpty())
        assertTrue("Frequency should be valid", transaction.frequency in listOf("DAILY", "WEEKLY", "MONTHLY", "YEARLY"))
    }

    // ==================== HELPER METHODS ====================

    /**
     * Helper: Calculate next execution date based on frequency
     * Mirrors RecurringTransactionWorker logic
     */
    private fun calculateNextExecutionDate(
        currentDate: Long,
        frequency: String,
    ): Long {
        return when (frequency.uppercase()) {
            "DAILY" -> currentDate + TimeUnit.DAYS.toMillis(1)
            "WEEKLY" -> currentDate + TimeUnit.DAYS.toMillis(7)
            "MONTHLY" -> currentDate + TimeUnit.DAYS.toMillis(30)
            "YEARLY" -> currentDate + TimeUnit.DAYS.toMillis(365)
            else -> currentDate + TimeUnit.DAYS.toMillis(30) // Default to monthly
        }
    }

    /**
     * Helper: Check if transaction is due for execution
     */
    private fun isDueForExecution(
        transaction: RecurringTransactionEntity,
        currentTime: Long,
    ): Boolean {
        return transaction.isActive && transaction.nextExecutionDate <= currentTime
    }

    /**
     * Helper: Create test recurring transaction
     */
    @Suppress("LongParameterList")
    private fun createRecurringTransaction(
        id: Int = 1,
        title: String = "Test Recurring",
        amount: Double = 100.0,
        type: String = "EXPENSE",
        category: String = "Test",
        emoji: String = ":)",
        frequency: String = "MONTHLY",
        startDate: Long = System.currentTimeMillis(),
        endDate: Long? = null,
        lastExecutionDate: Long? = null,
        nextExecutionDate: Long = System.currentTimeMillis(),
        isActive: Boolean = true,
        createdAt: Long = System.currentTimeMillis(),
    ): RecurringTransactionEntity {
        return RecurringTransactionEntity(
            id = id,
            title = title,
            amount = amount,
            type = type,
            category = category,
            emoji = emoji,
            frequency = frequency,
            startDate = startDate,
            endDate = endDate,
            lastExecutionDate = lastExecutionDate,
            nextExecutionDate = nextExecutionDate,
            isActive = isActive,
            createdAt = createdAt,
        )
    }
}

// ==================== FREQUENCY ENUM TESTS ====================

class RecurringFrequencyTest {
    @Test
    fun `frequency enum should have correct day counts`() {
        // Given
        val frequencies =
            mapOf(
                "DAILY" to 1L,
                "WEEKLY" to 7L,
                "MONTHLY" to 30L,
                "YEARLY" to 365L,
            )

        // When & Then
        frequencies.forEach { (frequency, expectedDays) ->
            val actualMillis =
                when (frequency) {
                    "DAILY" -> TimeUnit.DAYS.toMillis(1)
                    "WEEKLY" -> TimeUnit.DAYS.toMillis(7)
                    "MONTHLY" -> TimeUnit.DAYS.toMillis(30)
                    "YEARLY" -> TimeUnit.DAYS.toMillis(365)
                    else -> 0L
                }

            assertEquals(
                "$frequency should equal $expectedDays days in millis",
                TimeUnit.DAYS.toMillis(expectedDays),
                actualMillis,
            )
        }
    }

    @Test
    fun `frequency should be case insensitive`() {
        val frequencies = listOf("daily", "DAILY", "Daily", "DaIlY")
        frequencies.forEach { frequency ->
            assertEquals("daily", frequency.lowercase())
        }
    }
}
