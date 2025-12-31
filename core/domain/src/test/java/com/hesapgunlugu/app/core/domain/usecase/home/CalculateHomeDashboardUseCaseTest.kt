package com.hesapgunlugu.app.core.domain.usecase.home

import com.hesapgunlugu.app.core.domain.model.Transaction
import com.hesapgunlugu.app.core.domain.model.TransactionType
import com.hesapgunlugu.app.core.domain.model.UserSettings
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit tests for CalculateHomeDashboardUseCase
 *
 * Tests all dashboard calculation logic in isolation
 */
class CalculateHomeDashboardUseCaseTest {
    private lateinit var useCase: CalculateHomeDashboardUseCase

    @Before
    fun setup() {
        useCase = CalculateHomeDashboardUseCase()
    }

    @Test
    fun `empty transactions returns zero dashboard data`() {
        val result =
            useCase(
                transactions = emptyList(),
                settings = UserSettings(),
            )

        assertEquals(0.0, result.totalBalance)
        assertEquals(0.0, result.totalIncome)
        assertEquals(0.0, result.totalExpense)
        assertTrue(result.categoryExpenses.isEmpty())
        assertEquals(25, result.financialHealthScore)
    }

    @Test
    fun `calculates balance correctly with income and expenses`() {
        val transactions =
            listOf(
                Transaction(
                    id = 1,
                    title = "Salary",
                    amount = 10000.0,
                    type = TransactionType.INCOME,
                    category = "Salary",
                    timestamp = System.currentTimeMillis(),
                    emoji = ":)",
                ),
                Transaction(
                    id = 2,
                    title = "Rent",
                    amount = 3000.0,
                    type = TransactionType.EXPENSE,
                    category = "Housing",
                    timestamp = System.currentTimeMillis(),
                    emoji = ":)",
                ),
                Transaction(
                    id = 3,
                    title = "Groceries",
                    amount = 1000.0,
                    type = TransactionType.EXPENSE,
                    category = "Food",
                    timestamp = System.currentTimeMillis(),
                    emoji = ":)",
                ),
            )

        val result = useCase(transactions, UserSettings())

        assertEquals(10000.0, result.totalIncome)
        assertEquals(4000.0, result.totalExpense)
        assertEquals(6000.0, result.totalBalance)
    }

    @Test
    fun `groups expenses by category correctly`() {
        val transactions =
            listOf(
                Transaction(
                    id = 1,
                    title = "Groceries 1",
                    amount = 500.0,
                    type = TransactionType.EXPENSE,
                    category = "Food",
                    timestamp = System.currentTimeMillis(),
                    emoji = ":)",
                ),
                Transaction(
                    id = 2,
                    title = "Groceries 2",
                    amount = 300.0,
                    type = TransactionType.EXPENSE,
                    category = "Food",
                    timestamp = System.currentTimeMillis(),
                    emoji = ":)",
                ),
                Transaction(
                    id = 3,
                    title = "Electricity",
                    amount = 200.0,
                    type = TransactionType.EXPENSE,
                    category = "Bills",
                    timestamp = System.currentTimeMillis(),
                    emoji = ":)",
                ),
            )

        val result = useCase(transactions, UserSettings())

        assertEquals(800.0, result.categoryExpenses["Food"])
        assertEquals(200.0, result.categoryExpenses["Bills"])
        assertEquals(2, result.categoryExpenses.size)
    }

    @Test
    fun `calculates financial health score - excellent scenario`() {
        val transactions =
            listOf(
                Transaction(
                    id = 1,
                    title = "Income",
                    amount = 10000.0,
                    type = TransactionType.INCOME,
                    category = "Salary",
                    timestamp = System.currentTimeMillis(),
                    emoji = ":)",
                ),
                Transaction(
                    id = 2,
                    title = "Expense",
                    amount = 3000.0,
                    type = TransactionType.EXPENSE,
                    category = "Living",
                    timestamp = System.currentTimeMillis(),
                    emoji = ":)",
                ),
            )

        val settings = UserSettings(monthlyLimit = 10000.0)
        val result = useCase(transactions, settings)

        // High savings (70%), under budget (30%), positive balance
        assertTrue(result.financialHealthScore >= 80, "Expected high score, got ${result.financialHealthScore}")
    }

    @Test
    fun `calculates financial health score - poor scenario`() {
        val transactions =
            listOf(
                Transaction(
                    id = 1,
                    title = "Income",
                    amount = 5000.0,
                    type = TransactionType.INCOME,
                    category = "Salary",
                    timestamp = System.currentTimeMillis(),
                    emoji = ":)",
                ),
                Transaction(
                    id = 2,
                    title = "Expense",
                    amount = 6000.0,
                    type = TransactionType.EXPENSE,
                    category = "Living",
                    timestamp = System.currentTimeMillis(),
                    emoji = ":)",
                ),
            )

        val settings = UserSettings(monthlyLimit = 5000.0)
        val result = useCase(transactions, settings)

        // Negative balance, over budget, low savings
        assertTrue(result.financialHealthScore < 50, "Expected low score, got ${result.financialHealthScore}")
    }

    @Test
    fun `calculates savings rate correctly`() {
        val transactions =
            listOf(
                Transaction(
                    id = 1,
                    title = "Income",
                    amount = 10000.0,
                    type = TransactionType.INCOME,
                    category = "Salary",
                    timestamp = System.currentTimeMillis(),
                    emoji = ":)",
                ),
                Transaction(
                    id = 2,
                    title = "Expense",
                    amount = 3000.0,
                    type = TransactionType.EXPENSE,
                    category = "Living",
                    timestamp = System.currentTimeMillis(),
                    emoji = ":)",
                ),
            )

        val result = useCase(transactions, UserSettings())

        // Savings = 7000, Income = 10000 → 70%
        assertEquals(70f, result.savingsRate, 0.1f)
    }

    @Test
    fun `savings rate is clamped between 0 and 100`() {
        val transactions =
            listOf(
                Transaction(
                    id = 1,
                    title = "Income",
                    amount = 1000.0,
                    type = TransactionType.INCOME,
                    category = "Salary",
                    timestamp = System.currentTimeMillis(),
                    emoji = ":)",
                ),
                Transaction(
                    id = 2,
                    title = "Expense",
                    amount = 2000.0,
                    type = TransactionType.EXPENSE,
                    category = "Living",
                    timestamp = System.currentTimeMillis(),
                    emoji = ":)",
                ),
            )

        val result = useCase(transactions, UserSettings())

        // Should be clamped to 0 (negative savings)
        assertTrue(result.savingsRate >= 0f)
        assertTrue(result.savingsRate <= 100f)
    }

    @Test
    fun `identifies top spending category`() {
        val transactions =
            listOf(
                Transaction(
                    id = 1,
                    title = "Rent",
                    amount = 3000.0,
                    type = TransactionType.EXPENSE,
                    category = "Housing",
                    timestamp = System.currentTimeMillis(),
                    emoji = ":)",
                ),
                Transaction(
                    id = 2,
                    title = "Food",
                    amount = 500.0,
                    type = TransactionType.EXPENSE,
                    category = "Food",
                    timestamp = System.currentTimeMillis(),
                    emoji = ":)",
                ),
                Transaction(
                    id = 3,
                    title = "Transport",
                    amount = 200.0,
                    type = TransactionType.EXPENSE,
                    category = "Transport",
                    timestamp = System.currentTimeMillis(),
                    emoji = ":)",
                ),
            )

        val result = useCase(transactions, UserSettings())

        assertEquals("Housing", result.topSpendingCategory)
    }

    @Test
    fun `calculates average daily spending`() {
        val transactions =
            listOf(
                Transaction(
                    id = 1,
                    title = "Expense",
                    amount = 3000.0,
                    type = TransactionType.EXPENSE,
                    category = "Living",
                    timestamp = System.currentTimeMillis(),
                    emoji = ":)",
                ),
            )

        val result = useCase(transactions, UserSettings())

        // 3000 / 30 = 100
        assertEquals(100.0, result.averageDailySpending, 0.01)
    }

    @Test
    fun `monthly trend is up when spending increases`() {
        val now = System.currentTimeMillis()
        val dayMs = 24 * 60 * 60 * 1000L

        val transactions =
            buildList {
                // Low spending first 3 days
                repeat(3) { day ->
                    add(
                        Transaction(
                            id = day,
                            title = "Expense",
                            amount = 100.0,
                            type = TransactionType.EXPENSE,
                            category = "Test",
                            timestamp = now - ((6 - day) * dayMs),
                            emoji = ":)",
                        ),
                    )
                }
                // High spending last 4 days
                repeat(4) { day ->
                    add(
                        Transaction(
                            id = day + 3,
                            title = "Expense",
                            amount = 500.0,
                            type = TransactionType.EXPENSE,
                            category = "Test",
                            timestamp = now - ((3 - day) * dayMs),
                            emoji = ":)",
                        ),
                    )
                }
            }

        val result = useCase(transactions, UserSettings())

        assertEquals("up", result.monthlyTrend)
    }

    @Test
    fun `weekly spending returns 7 days of data`() {
        val result = useCase(emptyList(), UserSettings())

        assertEquals(7, result.weeklySpending.size)
    }
}
