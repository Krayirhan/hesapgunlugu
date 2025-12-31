package com.hesapgunlugu.app.feature.history

import com.hesapgunlugu.app.core.common.StringProvider
import com.hesapgunlugu.app.core.domain.model.Transaction
import com.hesapgunlugu.app.core.domain.model.TransactionType
import com.hesapgunlugu.app.core.domain.repository.TransactionRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.Calendar
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryViewModelTest {
    private lateinit var viewModel: HistoryViewModel
    private lateinit var repository: TransactionRepository
    private lateinit var stringProvider: StringProvider
    private val testDispatcher = StandardTestDispatcher()

    private val testTransactions =
        listOf(
            Transaction(
                id = 1,
                title = "Market",
                amount = 150.0,
                date = Date(),
                type = TransactionType.EXPENSE,
                category = "Alışveriş",
            ),
            Transaction(
                id = 2,
                title = "Maaş",
                amount = 5000.0,
                date = Date(),
                type = TransactionType.INCOME,
                category = "Gelir",
            ),
            Transaction(
                id = 3,
                title = "Elektrik",
                amount = 200.0,
                date = Date(),
                type = TransactionType.EXPENSE,
                category = "Fatura",
            ),
        )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        stringProvider = mockk()

        coEvery { repository.getAllTransactions() } returns flowOf(testTransactions)

        // Mock string provider methods
        every { stringProvider.transactionAdded() } returns "İşlem eklendi"
        every { stringProvider.transactionUpdated() } returns "İşlem güncellendi"
        every { stringProvider.transactionDeleted() } returns "İşlem silindi"
        every { stringProvider.errorGeneric() } returns "Bir hata oluştu"
        every { stringProvider.errorLoading() } returns "Yüklenirken hata oluştu"
        every { stringProvider.errorSaving() } returns "Kaydedilirken hata oluştu"
        every { stringProvider.errorDeleting() } returns "Silinirken hata oluştu"
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has correct default values`() =
        runTest {
            viewModel = HistoryViewModel(repository, stringProvider)
            testDispatcher.scheduler.advanceUntilIdle()

            val state = viewModel.state.value
            assertFalse(state.isLoading)
            assertNull(state.error)
        }

    @Test
    fun `transactions are loaded on init`() =
        runTest {
            viewModel = HistoryViewModel(repository, stringProvider)
            testDispatcher.scheduler.advanceUntilIdle()

            val state = viewModel.state.value
            assertEquals(3, state.totalCount)
        }

    @Test
    fun `filter by INCOME shows only income transactions`() =
        runTest {
            viewModel = HistoryViewModel(repository, stringProvider)
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.setFilter(TransactionFilter.INCOME)
            testDispatcher.scheduler.advanceUntilIdle()

            val state = viewModel.state.value
            assertEquals(1, state.transactions.size)
            assertTrue(state.transactions.all { it.type == TransactionType.INCOME })
        }

    @Test
    fun `filter by EXPENSE shows only expense transactions`() =
        runTest {
            viewModel = HistoryViewModel(repository, stringProvider)
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.setFilter(TransactionFilter.EXPENSE)
            testDispatcher.scheduler.advanceUntilIdle()

            val state = viewModel.state.value
            assertEquals(2, state.transactions.size)
            assertTrue(state.transactions.all { it.type == TransactionType.EXPENSE })
        }

    @Test
    fun `search filters transactions by title`() =
        runTest {
            viewModel = HistoryViewModel(repository, stringProvider)
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onSearchQueryChange("Market")
            testDispatcher.scheduler.advanceUntilIdle()

            val state = viewModel.state.value
            assertEquals(1, state.transactions.size)
            assertEquals("Market", state.transactions.first().title)
        }

    @Test
    fun `search is case insensitive`() =
        runTest {
            viewModel = HistoryViewModel(repository, stringProvider)
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onSearchQueryChange("market")
            testDispatcher.scheduler.advanceUntilIdle()

            val state = viewModel.state.value
            assertEquals(1, state.transactions.size)
        }

    @Test
    fun `sort by amount descending works correctly`() =
        runTest {
            viewModel = HistoryViewModel(repository, stringProvider)
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.setSort(TransactionSort.AMOUNT_DESC)
            testDispatcher.scheduler.advanceUntilIdle()

            val state = viewModel.state.value
            val amounts = state.transactions.map { it.amount }
            assertEquals(amounts.sortedDescending(), amounts)
        }

    @Test
    fun `sort by amount ascending works correctly`() =
        runTest {
            viewModel = HistoryViewModel(repository, stringProvider)
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.setSort(TransactionSort.AMOUNT_ASC)
            testDispatcher.scheduler.advanceUntilIdle()

            val state = viewModel.state.value
            val amounts = state.transactions.map { it.amount }
            assertEquals(amounts.sorted(), amounts)
        }

    @Test
    fun `previousMonth decrements month`() =
        runTest {
            viewModel = HistoryViewModel(repository, stringProvider)
            val initialMonth = viewModel.selectedMonth.value.get(Calendar.MONTH)

            viewModel.previousMonth()

            val newMonth = viewModel.selectedMonth.value.get(Calendar.MONTH)
            val expectedMonth = if (initialMonth == 0) 11 else initialMonth - 1
            assertEquals(expectedMonth, newMonth)
        }

    @Test
    fun `nextMonth increments month`() =
        runTest {
            viewModel = HistoryViewModel(repository, stringProvider)
            val initialMonth = viewModel.selectedMonth.value.get(Calendar.MONTH)

            viewModel.nextMonth()

            val newMonth = viewModel.selectedMonth.value.get(Calendar.MONTH)
            val expectedMonth = if (initialMonth == 11) 0 else initialMonth + 1
            assertEquals(expectedMonth, newMonth)
        }

    @Test
    fun `monthly income and expense are calculated correctly`() =
        runTest {
            viewModel = HistoryViewModel(repository, stringProvider)
            testDispatcher.scheduler.advanceUntilIdle()

            val state = viewModel.state.value
            assertEquals(5000.0, state.monthlyIncome, 0.01)
            assertEquals(350.0, state.monthlyExpense, 0.01)
        }

    @Test
    fun `clearError clears error state`() =
        runTest {
            viewModel = HistoryViewModel(repository, stringProvider)
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.clearError()

            assertNull(viewModel.state.value.error)
        }

    @Test
    fun `deleteTransaction calls repository`() =
        runTest {
            coEvery { repository.deleteTransaction(any()) } returns Result.success(Unit)
            viewModel = HistoryViewModel(repository, stringProvider)
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.deleteTransaction(testTransactions.first())
            testDispatcher.scheduler.advanceUntilIdle()

            // Verify no error
            assertNull(viewModel.state.value.error)
        }

    @Test
    fun `updateTransaction calls repository`() =
        runTest {
            coEvery { repository.updateTransaction(any()) } returns Result.success(Unit)
            viewModel = HistoryViewModel(repository, stringProvider)
            testDispatcher.scheduler.advanceUntilIdle()

            val updatedTransaction = testTransactions.first().copy(title = "Updated")
            viewModel.updateTransaction(updatedTransaction)
            testDispatcher.scheduler.advanceUntilIdle()

            assertNull(viewModel.state.value.error)
        }
}
