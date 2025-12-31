package com.hesapgunlugu.app.feature.history

import app.cash.turbine.test
import com.hesapgunlugu.app.core.domain.model.Transaction
import com.hesapgunlugu.app.core.domain.model.TransactionType
import com.hesapgunlugu.app.core.domain.repository.TransactionRepository
import com.hesapgunlugu.app.core.domain.usecase.scheduled.GetPlannedOccurrencesUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: TransactionRepository
    private lateinit var getPlannedOccurrencesUseCase: GetPlannedOccurrencesUseCase
    private lateinit var viewModel: HistoryViewModel

    private val now = System.currentTimeMillis()
    private val transactions =
        listOf(
            Transaction(
                id = 1,
                title = "Salary",
                amount = 1000.0,
                timestamp = now,
                type = TransactionType.INCOME,
                category = "Income",
                emoji = ":)",
            ),
            Transaction(
                id = 2,
                title = "Rent",
                amount = 500.0,
                timestamp = now,
                type = TransactionType.EXPENSE,
                category = "Housing",
                emoji = ":)",
            ),
        )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        getPlannedOccurrencesUseCase = mockk()
        every { repository.getAllTransactions() } returns flowOf(transactions)
        every { getPlannedOccurrencesUseCase(any(), any()) } returns flowOf(emptyList())
        viewModel = HistoryViewModel(repository, getPlannedOccurrencesUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial load shows transactions`() =
        runTest {
            advanceUntilIdle()

            val state = viewModel.state.value
            assertFalse(state.isLoading)
            assertEquals(2, state.transactions.size)
        }

    @Test
    fun `filter income shows only income transactions`() =
        runTest {
            viewModel.setFilter(TransactionFilter.INCOME)
            advanceUntilIdle()

            val state = viewModel.state.value
            assertTrue(state.transactions.all { it.type == TransactionType.INCOME })
        }

    @Test
    fun `search filters by title or category`() =
        runTest {
            viewModel.onSearchQueryChange("rent")
            advanceUntilIdle()

            val state = viewModel.state.value
            assertEquals(1, state.transactions.size)
            assertEquals("Rent", state.transactions.first().title)
        }

    @Test
    fun `deleteTransaction emits success event`() =
        runTest {
            coEvery { repository.deleteTransaction(any()) } returns Result.success(Unit)

            viewModel.uiEvent.test {
                viewModel.deleteTransaction(transactions[0])
                advanceUntilIdle()

                val event = awaitItem()
                assertTrue(event is HistoryUiEvent.ShowSuccess)
                cancelAndIgnoreRemainingEvents()
            }
        }
}
