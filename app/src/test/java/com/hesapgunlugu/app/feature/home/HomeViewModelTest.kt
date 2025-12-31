package com.hesapgunlugu.app.feature.home

import app.cash.turbine.test
import com.hesapgunlugu.app.core.domain.model.Transaction
import com.hesapgunlugu.app.core.domain.model.TransactionType
import com.hesapgunlugu.app.core.domain.model.UserSettings
import com.hesapgunlugu.app.core.domain.repository.SettingsRepository
import com.hesapgunlugu.app.core.domain.usecase.transaction.*
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private lateinit var viewModel: HomeViewModel
    private lateinit var getTransactionsUseCase: GetTransactionsUseCase
    private lateinit var addTransactionUseCase: AddTransactionUseCase
    private lateinit var updateTransactionUseCase: UpdateTransactionUseCase
    private lateinit var deleteTransactionUseCase: DeleteTransactionUseCase
    private lateinit var settingsRepository: SettingsRepository

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        getTransactionsUseCase = mockk()
        addTransactionUseCase = mockk()
        updateTransactionUseCase = mockk()
        deleteTransactionUseCase = mockk()
        settingsRepository = mockk()

        // Default mock responses
        every { settingsRepository.settingsFlow } returns
            flowOf(
                UserSettings(
                    userName = "Test User",
                    monthlyLimit = 5000.0,
                    categoryBudgets = emptyMap(),
                    notificationsEnabled = true,
                    currency = "₺",
                    currencyCode = "TRY",
                    languageCode = "tr",
                    budgetAlertThreshold = 80,
                ),
            )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() {
        viewModel =
            HomeViewModel(
                getTransactionsUseCase = getTransactionsUseCase,
                addTransactionUseCase = addTransactionUseCase,
                updateTransactionUseCase = updateTransactionUseCase,
                deleteTransactionUseCase = deleteTransactionUseCase,
                settingsRepository = settingsRepository,
            )
    }

    @Test
    fun `should load transactions successfully`() =
        runTest {
            // Given
            val transactions =
                listOf(
                    Transaction(1, "Maaş", 10000.0, Date(), TransactionType.INCOME, "Maaş"),
                    Transaction(2, "Market", 500.0, Date(), TransactionType.EXPENSE, "Market"),
                )
            every { getTransactionsUseCase() } returns flowOf(transactions)

            // When
            createViewModel()
            advanceUntilIdle()

            // Then
            val state = viewModel.state.value
            assertEquals(10000.0, state.totalIncome, 0.01)
            assertEquals(500.0, state.totalExpense, 0.01)
            assertEquals(9500.0, state.totalBalance, 0.01)
            assertEquals(2, state.recentTransactions.size)
            assertFalse(state.isLoading)
            assertNull(state.error)
        }

    @Test
    fun `should calculate balance correctly with multiple transactions`() =
        runTest {
            // Given
            val transactions =
                listOf(
                    Transaction(1, "Maaş", 15000.0, Date(), TransactionType.INCOME, "Maaş"),
                    Transaction(2, "Kira", 5000.0, Date(), TransactionType.EXPENSE, "Kira"),
                    Transaction(3, "Market", 1000.0, Date(), TransactionType.EXPENSE, "Market"),
                    Transaction(4, "Freelance", 3000.0, Date(), TransactionType.INCOME, "Freelance"),
                )
            every { getTransactionsUseCase() } returns flowOf(transactions)

            // When
            createViewModel()
            advanceUntilIdle()

            // Then
            val state = viewModel.state.value
            assertEquals(18000.0, state.totalIncome, 0.01) // 15000 + 3000
            assertEquals(6000.0, state.totalExpense, 0.01) // 5000 + 1000
            assertEquals(12000.0, state.totalBalance, 0.01) // 18000 - 6000
        }

    @Test
    fun `should handle empty transactions`() =
        runTest {
            // Given
            every { getTransactionsUseCase() } returns flowOf(emptyList())

            // When
            createViewModel()
            advanceUntilIdle()

            // Then
            val state = viewModel.state.value
            assertEquals(0.0, state.totalIncome, 0.01)
            assertEquals(0.0, state.totalExpense, 0.01)
            assertEquals(0.0, state.totalBalance, 0.01)
            assertTrue(state.recentTransactions.isEmpty())
        }

    @Test
    fun `should add transaction successfully`() =
        runTest {
            // Given
            every { getTransactionsUseCase() } returns flowOf(emptyList())
            coEvery { addTransactionUseCase(any()) } returns Result.success(Unit)
            createViewModel()
            advanceUntilIdle()

            val newTransaction =
                Transaction(
                    id = 0,
                    title = "Test",
                    amount = 100.0,
                    date = Date(),
                    type = TransactionType.EXPENSE,
                    category = "Test",
                )

            // When & Then
            viewModel.uiEvent.test {
                viewModel.addTransaction(newTransaction)
                advanceUntilIdle()

                val event = awaitItem()
                assertTrue(event is HomeUiEvent.ShowSuccess)
                assertEquals("İşlem eklendi", (event as HomeUiEvent.ShowSuccess).message)
            }
            coVerify(exactly = 1) { addTransactionUseCase(newTransaction) }
        }

    @Test
    fun `should show error when add transaction fails`() =
        runTest {
            // Given
            every { getTransactionsUseCase() } returns flowOf(emptyList())
            coEvery { addTransactionUseCase(any()) } returns Result.failure(Exception("DB Error"))
            createViewModel()
            advanceUntilIdle()

            val newTransaction =
                Transaction(
                    id = 0,
                    title = "Test",
                    amount = 100.0,
                    date = Date(),
                    type = TransactionType.EXPENSE,
                    category = "Test",
                )

            // When & Then
            viewModel.uiEvent.test {
                viewModel.addTransaction(newTransaction)
                advanceUntilIdle()

                val event = awaitItem()
                assertTrue(event is HomeUiEvent.ShowError)
            }
        }

    @Test
    fun `should delete transaction successfully`() =
        runTest {
            // Given
            val transaction = Transaction(1, "Test", 100.0, Date(), TransactionType.EXPENSE, "Test")
            every { getTransactionsUseCase() } returns flowOf(listOf(transaction))
            coEvery { deleteTransactionUseCase(any()) } returns Result.success(Unit)
            createViewModel()
            advanceUntilIdle()

            // When & Then
            viewModel.uiEvent.test {
                viewModel.deleteTransaction(transaction)
                advanceUntilIdle()

                val event = awaitItem()
                assertTrue(event is HomeUiEvent.ShowSuccess)
                assertEquals("İşlem silindi", (event as HomeUiEvent.ShowSuccess).message)
            }
        }

    @Test
    fun `should update transaction successfully`() =
        runTest {
            // Given
            val transaction = Transaction(1, "Test", 100.0, Date(), TransactionType.EXPENSE, "Test")
            every { getTransactionsUseCase() } returns flowOf(listOf(transaction))
            coEvery { updateTransactionUseCase(any()) } returns Result.success(Unit)
            createViewModel()
            advanceUntilIdle()

            val updatedTransaction = transaction.copy(amount = 200.0)

            // When & Then
            viewModel.uiEvent.test {
                viewModel.updateTransaction(updatedTransaction)
                advanceUntilIdle()

                val event = awaitItem()
                assertTrue(event is HomeUiEvent.ShowSuccess)
                assertEquals("İşlem güncellendi", (event as HomeUiEvent.ShowSuccess).message)
            }
        }

    @Test
    fun `should group expenses by category correctly`() =
        runTest {
            // Given
            val transactions =
                listOf(
                    Transaction(1, "Market1", 200.0, Date(), TransactionType.EXPENSE, "Market"),
                    Transaction(2, "Market2", 300.0, Date(), TransactionType.EXPENSE, "Market"),
                    Transaction(3, "Fatura", 150.0, Date(), TransactionType.EXPENSE, "Fatura"),
                    Transaction(4, "Maaş", 5000.0, Date(), TransactionType.INCOME, "Maaş"),
                )
            every { getTransactionsUseCase() } returns flowOf(transactions)

            // When
            createViewModel()
            advanceUntilIdle()

            // Then
            val state = viewModel.state.value
            assertEquals(500.0, state.categoryExpenses["Market"] ?: 0.0, 0.01)
            assertEquals(150.0, state.categoryExpenses["Fatura"] ?: 0.0, 0.01)
            assertNull(state.categoryExpenses["Maaş"]) // Income should not be in expenses
        }

    @Test
    fun `should take only 5 recent transactions`() =
        runTest {
            // Given
            val transactions =
                (1..10).map { i ->
                    Transaction(i, "Transaction $i", 100.0, Date(), TransactionType.EXPENSE, "Test")
                }
            every { getTransactionsUseCase() } returns flowOf(transactions)

            // When
            createViewModel()
            advanceUntilIdle()

            // Then
            val state = viewModel.state.value
            assertEquals(5, state.recentTransactions.size)
        }
}
