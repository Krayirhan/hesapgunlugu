package com.hesapgunlugu.app.domain.usecase.statistics

import app.cash.turbine.test
import com.hesapgunlugu.app.testutil.FakeTransactionRepository
import com.hesapgunlugu.app.testutil.MainDispatcherRule
import com.hesapgunlugu.app.testutil.TestFixtures
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Calendar

/**
 * GetStatisticsUseCase Tests
 *
 * Tests for statistics calculation logic.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GetStatisticsUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var useCase: GetStatisticsUseCase
    private lateinit var repository: FakeTransactionRepository

    @Before
    fun setUp() {
        repository = FakeTransactionRepository()
        useCase = GetStatisticsUseCase(repository)
    }

    @Test
    fun `invoke should return zero values when no transactions`() =
        runTest {
            // When & Then
            useCase().test {
                val stats = awaitItem()
                assertEquals(0.0, stats.totalIncome, 0.01)
                assertEquals(0.0, stats.totalExpense, 0.01)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `invoke should calculate correct total income`() =
        runTest {
            // Given - transactions in current month
            val calendar = Calendar.getInstance()
            val transactions =
                listOf(
                    TestFixtures.createIncomeTransaction(id = 1, amount = 5000.0).copy(date = calendar.time),
                    TestFixtures.createIncomeTransaction(id = 2, amount = 2500.0).copy(date = calendar.time),
                    TestFixtures.createExpenseTransaction(id = 3, amount = 1000.0).copy(date = calendar.time),
                )
            repository.setTransactions(transactions)

            // When & Then
            useCase().test {
                val stats = awaitItem()
                assertEquals(7500.0, stats.totalIncome, 0.01)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `invoke should calculate correct total expense`() =
        runTest {
            // Given
            val calendar = Calendar.getInstance()
            val transactions =
                listOf(
                    TestFixtures.createExpenseTransaction(id = 1, amount = 500.0).copy(date = calendar.time),
                    TestFixtures.createExpenseTransaction(id = 2, amount = 1500.0).copy(date = calendar.time),
                    TestFixtures.createIncomeTransaction(id = 3, amount = 3000.0).copy(date = calendar.time),
                )
            repository.setTransactions(transactions)

            // When & Then
            useCase().test {
                val stats = awaitItem()
                assertEquals(2000.0, stats.totalExpense, 0.01)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `invoke should group expenses by category`() =
        runTest {
            // Given
            val calendar = Calendar.getInstance()
            val transactions =
                listOf(
                    TestFixtures.createExpenseTransaction(
                        id = 1,
                        amount = 500.0,
                        category = "Market",
                    ).copy(date = calendar.time),
                    TestFixtures.createExpenseTransaction(
                        id = 2,
                        amount = 300.0,
                        category = "Market",
                    ).copy(date = calendar.time),
                    TestFixtures.createExpenseTransaction(
                        id = 3,
                        amount = 1000.0,
                        category = "Fatura",
                    ).copy(date = calendar.time),
                )
            repository.setTransactions(transactions)

            // When & Then
            useCase().test {
                val stats = awaitItem()
                assertEquals(800.0, stats.categoryExpenses["Market"] ?: 0.0, 0.01)
                assertEquals(1000.0, stats.categoryExpenses["Fatura"] ?: 0.0, 0.01)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `invoke should only include current month transactions`() =
        runTest {
            // Given
            val currentMonth = Calendar.getInstance()
            val lastMonth = Calendar.getInstance().apply { add(Calendar.MONTH, -1) }

            val transactions =
                listOf(
                    TestFixtures.createIncomeTransaction(id = 1, amount = 5000.0).copy(date = currentMonth.time),
                    // Last month
                    TestFixtures.createIncomeTransaction(
                        id = 2,
                        amount = 3000.0,
                    ).copy(date = lastMonth.time),
                )
            repository.setTransactions(transactions)

            // When & Then
            useCase().test {
                val stats = awaitItem()
                // Only current month
                assertEquals(5000.0, stats.totalIncome, 0.01)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `invoke should emit updates when transactions change`() =
        runTest {
            // Given
            val calendar = Calendar.getInstance()

            // When & Then
            useCase().test {
                // Initial - empty
                assertEquals(0.0, awaitItem().totalExpense, 0.01)

                // Add transaction
                repository.addTransactionSync(
                    TestFixtures.createExpenseTransaction(id = 1, amount = 500.0).copy(date = calendar.time),
                )
                assertEquals(500.0, awaitItem().totalExpense, 0.01)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `invoke should calculate weekly data correctly`() =
        runTest {
            // Given
            val calendar = Calendar.getInstance()
            val transactions =
                listOf(
                    TestFixtures.createExpenseTransaction(id = 1, amount = 100.0).copy(date = calendar.time),
                    TestFixtures.createIncomeTransaction(id = 2, amount = 200.0).copy(date = calendar.time),
                )
            repository.setTransactions(transactions)

            // When & Then
            useCase().test {
                val stats = awaitItem()
                // Weekly data should have 7 days
                assertEquals(7, stats.weeklyIncome.size)
                assertEquals(7, stats.weeklyExpense.size)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `invoke should calculate previous month expense`() =
        runTest {
            // Given
            val currentMonth = Calendar.getInstance()
            val lastMonth = Calendar.getInstance().apply { add(Calendar.MONTH, -1) }

            val transactions =
                listOf(
                    TestFixtures.createExpenseTransaction(id = 1, amount = 1000.0).copy(date = currentMonth.time),
                    TestFixtures.createExpenseTransaction(id = 2, amount = 2000.0).copy(date = lastMonth.time),
                )
            repository.setTransactions(transactions)

            // When & Then
            useCase().test {
                val stats = awaitItem()
                assertEquals(1000.0, stats.currentMonthExpense, 0.01)
                assertEquals(2000.0, stats.previousMonthExpense, 0.01)
                cancelAndIgnoreRemainingEvents()
            }
        }
}
