package com.hesapgunlugu.app.testutil

import com.hesapgunlugu.app.core.domain.model.ScheduledPayment
import com.hesapgunlugu.app.core.domain.model.Transaction
import com.hesapgunlugu.app.core.domain.model.TransactionType
import java.util.Date

/**
 * Test Fixtures
 *
 * Reusable test data factory for consistent test data across all tests.
 */
object TestFixtures {
    private val fixedNow = Date(1_700_000_000_000)

    // ==================== TRANSACTIONS ====================

    @Suppress("LongParameterList")
    fun createTransaction(
        id: Int = 1,
        title: String = "Test Transaction",
        amount: Double = 100.0,
        type: TransactionType = TransactionType.EXPENSE,
        category: String = "Test Category",
        emoji: String = ":)",
        date: Date = fixedNow,
    ) = Transaction(
        id = id,
        title = title,
        amount = amount,
        type = type,
        category = category,
        emoji = emoji,
        date = date,
    )

    fun createIncomeTransaction(
        id: Int = 1,
        title: String = "Salary",
        amount: Double = 5000.0,
        category: String = "Maas",
    ) = createTransaction(
        id = id,
        title = title,
        amount = amount,
        type = TransactionType.INCOME,
        category = category,
        emoji = ":)",
    )

    fun createExpenseTransaction(
        id: Int = 2,
        title: String = "Market",
        amount: Double = 250.0,
        category: String = "Market",
    ) = createTransaction(
        id = id,
        title = title,
        amount = amount,
        type = TransactionType.EXPENSE,
        category = category,
        emoji = ":)",
    )

    fun createTransactionList(count: Int = 10): List<Transaction> {
        return (1..count).map { index ->
            createTransaction(
                id = index,
                title = "Transaction $index",
                amount = (index * 100).toDouble(),
                type = if (index % 2 == 0) TransactionType.INCOME else TransactionType.EXPENSE,
                category = if (index % 2 == 0) "Maas" else "Market",
            )
        }
    }

    fun createMixedTransactions(): List<Transaction> =
        listOf(
            createIncomeTransaction(1, "Maas", 10000.0),
            createExpenseTransaction(2, "Kira", 3000.0),
            createExpenseTransaction(3, "Market", 1500.0),
            createIncomeTransaction(4, "Freelance", 2500.0),
            createExpenseTransaction(5, "Fatura", 500.0),
            createExpenseTransaction(6, "Ulasim", 800.0),
        )

    // ==================== SCHEDULED PAYMENTS ====================

    @Suppress("LongParameterList")
    fun createScheduledPayment(
        id: Long = 1,
        title: String = "Netflix",
        amount: Double = 99.99,
        isIncome: Boolean = false,
        isRecurring: Boolean = true,
        frequency: String = "Aylik",
        dueDate: Date = fixedNow,
        emoji: String = ":)",
        isPaid: Boolean = false,
        category: String = "Abonelik",
        createdAt: Date = fixedNow,
    ) = ScheduledPayment(
        id = id,
        title = title,
        amount = amount,
        isIncome = isIncome,
        isRecurring = isRecurring,
        frequency = frequency,
        dueDate = dueDate,
        emoji = emoji,
        isPaid = isPaid,
        category = category,
        createdAt = createdAt,
    )

    fun createScheduledPaymentList(count: Int = 5): List<ScheduledPayment> {
        return (1..count).map { index ->
            createScheduledPayment(
                id = index.toLong(),
                title = "Payment $index",
                amount = (index * 50).toDouble(),
            )
        }
    }

    // ==================== DATE UTILITIES ====================

    fun createDateDaysAgo(days: Int): Date {
        val calendar = java.util.Calendar.getInstance()
        calendar.add(java.util.Calendar.DAY_OF_YEAR, -days)
        return calendar.time
    }

    fun createDateDaysFromNow(days: Int): Date {
        val calendar = java.util.Calendar.getInstance()
        calendar.add(java.util.Calendar.DAY_OF_YEAR, days)
        return calendar.time
    }

    fun createStartOfMonth(): Date {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.DAY_OF_MONTH, 1)
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.time
    }

    fun createEndOfMonth(): Date {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(
            java.util.Calendar.DAY_OF_MONTH,
            calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH),
        )
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 23)
        calendar.set(java.util.Calendar.MINUTE, 59)
        calendar.set(java.util.Calendar.SECOND, 59)
        calendar.set(java.util.Calendar.MILLISECOND, 999)
        return calendar.time
    }
}
