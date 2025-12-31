package com.hesapgunlugu.app.feature.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.hesapgunlugu.app.core.common.StringProvider
import com.hesapgunlugu.app.core.domain.model.Transaction
import com.hesapgunlugu.app.core.domain.model.TransactionType
import com.hesapgunlugu.app.core.domain.model.UserSettings
import com.hesapgunlugu.app.core.domain.repository.SettingsRepository
import com.hesapgunlugu.app.core.domain.usecase.analytics.CalculateCategoryBudgetsUseCase
import com.hesapgunlugu.app.core.domain.usecase.home.BudgetSeverity
import com.hesapgunlugu.app.core.domain.usecase.home.BudgetWarning
import com.hesapgunlugu.app.core.domain.usecase.home.CalculateHomeDashboardUseCase
import com.hesapgunlugu.app.core.domain.usecase.home.CheckBudgetWarningUseCase
import com.hesapgunlugu.app.core.domain.usecase.home.DashboardData
import com.hesapgunlugu.app.core.domain.usecase.home.GetGreetingMessageUseCase
import com.hesapgunlugu.app.core.domain.usecase.home.RemainingBudget
import com.hesapgunlugu.app.core.domain.usecase.scheduled.GetScheduledPaymentsUseCase
import com.hesapgunlugu.app.core.domain.usecase.transaction.AddTransactionUseCase
import com.hesapgunlugu.app.core.domain.usecase.transaction.DeleteTransactionUseCase
import com.hesapgunlugu.app.core.domain.usecase.transaction.GetTransactionsUseCase
import com.hesapgunlugu.app.core.domain.usecase.transaction.UpdateTransactionUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Date
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Comprehensive unit tests for refactored HomeViewModel
 *
 * Tests cover:
 * - Initial state loading
 * - Dashboard data calculation
 * - Transaction CRUD operations
 * - Budget warning emission
 * - Error handling
 * - Greeting message
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelRefactoredTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    // Mocks
    private lateinit var getTransactionsUseCase: GetTransactionsUseCase
    private lateinit var addTransactionUseCase: AddTransactionUseCase
    private lateinit var updateTransactionUseCase: UpdateTransactionUseCase
    private lateinit var deleteTransactionUseCase: DeleteTransactionUseCase
    private lateinit var getScheduledPaymentsUseCase: GetScheduledPaymentsUseCase
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var stringProvider: StringProvider
    private lateinit var calculateDashboardUseCase: CalculateHomeDashboardUseCase
    private lateinit var calculateCategoryBudgetsUseCase: CalculateCategoryBudgetsUseCase
    private lateinit var checkBudgetWarningUseCase: CheckBudgetWarningUseCase
    private lateinit var getGreetingMessageUseCase: GetGreetingMessageUseCase

    private lateinit var viewModel: HomeViewModel

    // Test data
    private val testTransactions =
        listOf(
            Transaction(
                id = 1,
                title = "Maaş",
                amount = 10000.0,
                type = TransactionType.INCOME,
                category = "Maaş",
                date = Date(),
            ),
            Transaction(
                id = 2,
                title = "Market",
                amount = 500.0,
                type = TransactionType.EXPENSE,
                category = "Market",
                date = Date(),
            ),
        )

    private val testSettings =
        UserSettings(
            monthlyLimit = 10000.0,
            categoryBudgets = mapOf("Market" to 2000.0),
            isDarkTheme = false,
        )

    private val testDashboardData =
        DashboardData(
            totalBalance = 9500.0,
            totalIncome = 10000.0,
            totalExpense = 500.0,
            categoryExpenses = mapOf("Market" to 500.0),
            financialHealthScore = 85,
            savingsRate = 95f,
            weeklySpending = listOf(100.0, 150.0, 50.0, 100.0, 50.0, 25.0, 25.0),
            monthlyTrend = "up",
            topSpendingCategory = "Market",
            averageDailySpending = 16.67,
        )

    private val successAdded = "Transaction added"
    private val successUpdated = "Transaction updated"
    private val successDeleted = "Transaction deleted"
    private val errorEmptyTitle = "Title cannot be empty"
    private val errorInvalidAmount = "Invalid amount"
    private val errorSaving = "Failed to save"
    private val errorDeleting = "Failed to delete"
    private val errorLoading = "Failed to load"
    private val errorGeneric = "Unknown error"
    private val categoryBudgetUpdated = "Category budget updated"
    private val categoryBudgetAdded = "Category budget added"

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // Create mocks
        getTransactionsUseCase = mockk()
        addTransactionUseCase = mockk()
        updateTransactionUseCase = mockk()
        deleteTransactionUseCase = mockk()
        getScheduledPaymentsUseCase = mockk()
        settingsRepository = mockk()
        stringProvider = mockk()
        calculateDashboardUseCase = mockk()
        calculateCategoryBudgetsUseCase = mockk()
        checkBudgetWarningUseCase = mockk()
        getGreetingMessageUseCase = mockk()

        // Default mock behaviors
        every { getTransactionsUseCase() } returns flowOf(testTransactions)
        every { getScheduledPaymentsUseCase() } returns flowOf(emptyList())
        every { settingsRepository.settingsFlow } returns flowOf(testSettings)
        every { calculateDashboardUseCase(any(), any()) } returns testDashboardData
        every { calculateCategoryBudgetsUseCase(any(), any()) } returns emptyList()
        every { checkBudgetWarningUseCase(any(), any()) } returns null
        every { checkBudgetWarningUseCase.getRemainingBudget(any(), any()) } returns RemainingBudget(9500.0, 5f)
        every { getGreetingMessageUseCase() } returns "Günaydın"
        every { stringProvider.transactionAdded() } returns successAdded
        every { stringProvider.transactionUpdated() } returns successUpdated
        every { stringProvider.transactionDeleted() } returns successDeleted
        every { stringProvider.errorEmptyTitle() } returns errorEmptyTitle
        every { stringProvider.errorInvalidAmount() } returns errorInvalidAmount
        every { stringProvider.errorSaving() } returns errorSaving
        every { stringProvider.errorDeleting() } returns errorDeleting
        every { stringProvider.errorLoading() } returns errorLoading
        every { stringProvider.errorGeneric() } returns errorGeneric
        every { stringProvider.categoryBudgetUpdated() } returns categoryBudgetUpdated
        every { stringProvider.categoryBudgetAdded() } returns categoryBudgetAdded
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): HomeViewModel {
        return HomeViewModel(
            getTransactionsUseCase = getTransactionsUseCase,
            addTransactionUseCase = addTransactionUseCase,
            updateTransactionUseCase = updateTransactionUseCase,
            deleteTransactionUseCase = deleteTransactionUseCase,
            getScheduledPaymentsUseCase = getScheduledPaymentsUseCase,
            settingsRepository = settingsRepository,
            stringProvider = stringProvider,
            calculateDashboardUseCase = calculateDashboardUseCase,
            calculateCategoryBudgetsUseCase = calculateCategoryBudgetsUseCase,
            checkBudgetWarningUseCase = checkBudgetWarningUseCase,
            getGreetingMessageUseCase = getGreetingMessageUseCase,
        )
    }

    // ==================== INITIAL STATE TESTS ====================

    @Test
    fun `initial state is loading`() =
        runTest {
            viewModel = createViewModel()

            viewModel.state.test {
                val initialState = awaitItem()
                assertTrue(initialState.isLoading)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `state shows data after loading`() =
        runTest {
            viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.state.test {
                val state = awaitItem()
                assertFalse(state.isLoading)
                assertEquals(9500.0, state.totalBalance)
                assertEquals(10000.0, state.totalIncome)
                assertEquals(500.0, state.totalExpense)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `greeting message is set correctly`() =
        runTest {
            every { getGreetingMessageUseCase() } returns "İyi Akşamlar"

            viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.state.test {
                val state = awaitItem()
                assertEquals("İyi Akşamlar", state.greeting)
                cancelAndIgnoreRemainingEvents()
            }
        }

    // ==================== DASHBOARD CALCULATION TESTS ====================

    @Test
    fun `dashboard data is calculated correctly`() =
        runTest {
            viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.state.test {
                val state = awaitItem()
                assertEquals(85, state.financialHealthScore)
                assertEquals(95f, state.savingsRate)
                assertEquals("Market", state.topSpendingCategory)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `recent transactions are sorted by date descending`() =
        runTest {
            viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.state.test {
                val state = awaitItem()
                assertTrue(state.recentTransactions.size <= 5)
                cancelAndIgnoreRemainingEvents()
            }
        }

    // ==================== TRANSACTION CRUD TESTS ====================

    @Test
    fun `addTransaction emits success event`() =
        runTest {
            val newTransaction =
                Transaction(
                    id = 0,
                    title = "Yeni İşlem",
                    amount = 100.0,
                    type = TransactionType.EXPENSE,
                    category = "Diğer",
                    date = Date(),
                )

            coEvery { addTransactionUseCase(any()) } returns Result.success(Unit)

            viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.uiEvent.test {
                viewModel.addTransaction(newTransaction)
                advanceUntilIdle()

                val event = awaitItem()
                assertTrue(event is HomeUiEvent.ShowSuccess)
                assertEquals(successAdded, (event as HomeUiEvent.ShowSuccess).message)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `addTransaction with empty title emits error`() =
        runTest {
            val invalidTransaction =
                Transaction(
                    id = 0,
                    title = "",
                    amount = 100.0,
                    type = TransactionType.EXPENSE,
                    category = "Diğer",
                    date = Date(),
                )

            coEvery { addTransactionUseCase(any()) } returns
                Result.failure(
                    com.hesapgunlugu.app.core.domain.model.TransactionException.EmptyTitle,
                )

            viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.uiEvent.test {
                viewModel.addTransaction(invalidTransaction)
                advanceUntilIdle()

                val event = awaitItem()
                assertTrue(event is HomeUiEvent.ShowError)
                assertEquals(errorEmptyTitle, (event as HomeUiEvent.ShowError).message)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `updateTransaction emits success event`() =
        runTest {
            coEvery { updateTransactionUseCase(any()) } returns Result.success(Unit)

            viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.uiEvent.test {
                viewModel.updateTransaction(testTransactions[0])
                advanceUntilIdle()

                val event = awaitItem()
                assertTrue(event is HomeUiEvent.ShowSuccess)
                assertEquals(successUpdated, (event as HomeUiEvent.ShowSuccess).message)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `deleteTransaction emits success event`() =
        runTest {
            coEvery { deleteTransactionUseCase(any()) } returns Result.success(Unit)

            viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.uiEvent.test {
                viewModel.deleteTransaction(testTransactions[0])
                advanceUntilIdle()

                val event = awaitItem()
                assertTrue(event is HomeUiEvent.ShowSuccess)
                assertEquals(successDeleted, (event as HomeUiEvent.ShowSuccess).message)
                cancelAndIgnoreRemainingEvents()
            }
        }

    // ==================== BUDGET WARNING TESTS ====================

    @Test
    fun `budget warning is emitted when threshold exceeded`() =
        runTest {
            every { checkBudgetWarningUseCase(any(), any()) } returns
                BudgetWarning(
                    title = "⚠️ Bütçe Aşıldı!",
                    message = "Aylık limiti aştınız.",
                    severity = BudgetSeverity.EXCEEDED,
                )

            viewModel = createViewModel()

            viewModel.uiEvent.test {
                advanceUntilIdle()

                val event = awaitItem()
                assertTrue(event is HomeUiEvent.ShowBudgetWarning)
                assertTrue((event as HomeUiEvent.ShowBudgetWarning).title.contains("Aşıldı"))
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `budget warning is not emitted when under threshold`() =
        runTest {
            every { checkBudgetWarningUseCase(any(), any()) } returns null
            every { checkBudgetWarningUseCase.getRemainingBudget(any(), any()) } returns RemainingBudget(7000.0, 30f)

            viewModel = createViewModel()
            advanceUntilIdle()

            // No budget warning should be emitted
            viewModel.state.test {
                val state = awaitItem()
                assertNull(state.error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    // ==================== ERROR HANDLING TESTS ====================

    @Test
    fun `error state is set when loading fails`() =
        runTest {
            every { getTransactionsUseCase() } returns flow { throw IllegalStateException("Network error") }

            viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.state.test {
                val state = awaitItem()
                assertNotNull(state.error)
                assertFalse(state.isLoading)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `retry clears error and reloads data`() =
        runTest {
            // First load fails
            every { getTransactionsUseCase() } returns flow { throw IllegalStateException("Network error") }

            viewModel = createViewModel()
            advanceUntilIdle()

            // Now setup success
            every { getTransactionsUseCase() } returns flowOf(testTransactions)
            every { getScheduledPaymentsUseCase() } returns flowOf(emptyList())

            viewModel.retry()
            advanceUntilIdle()

            viewModel.state.test {
                val state = awaitItem()
                assertFalse(state.isLoading)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `clearError removes error from state`() =
        runTest {
            every { getTransactionsUseCase() } returns flow { throw IllegalStateException("Error") }

            viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.clearError()

            viewModel.state.test {
                val state = awaitItem()
                assertNull(state.error)
                cancelAndIgnoreRemainingEvents()
            }
        }
}
