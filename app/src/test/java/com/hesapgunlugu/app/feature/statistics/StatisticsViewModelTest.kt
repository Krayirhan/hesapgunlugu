package com.hesapgunlugu.app.feature.statistics

import com.hesapgunlugu.app.core.domain.usecase.statistics.GetStatisticsUseCase
import com.hesapgunlugu.app.core.domain.usecase.statistics.StatisticsData
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class StatisticsViewModelTest {
    private lateinit var viewModel: StatisticsViewModel
    private lateinit var getStatisticsUseCase: GetStatisticsUseCase

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getStatisticsUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() {
        viewModel = StatisticsViewModel(getStatisticsUseCase)
    }

    private fun createTestData(
        totalIncome: Double = 10000.0,
        totalExpense: Double = 5000.0,
        categoryExpenses: Map<String, Double> = mapOf("Market" to 2000.0, "Fatura" to 1500.0),
    ) = StatisticsData(
        totalIncome = totalIncome,
        totalExpense = totalExpense,
        weeklyIncome = listOf(1000f, 2000f, 1500f, 1000f, 500f, 2000f, 2000f),
        weeklyExpense = listOf(500f, 800f, 600f, 700f, 400f, 1000f, 1000f),
        categoryExpenses = categoryExpenses,
        currentMonthExpense = totalExpense,
        previousMonthExpense = 4000.0,
    )

    @Test
    fun `should load statistics successfully`() =
        runTest {
            // Given
            val data = createTestData()
            every { getStatisticsUseCase() } returns flowOf(data)

            // When
            createViewModel()
            advanceUntilIdle()

            // Then
            val state = viewModel.state.value
            assertFalse(state.isLoading)
            assertNull(state.error)
            assertEquals(10000.0, state.totalIncome, 0.01)
            assertEquals(5000.0, state.totalExpense, 0.01)
        }

    @Test
    fun `should calculate category percentages correctly`() =
        runTest {
            // Given
            val data =
                createTestData(
                    categoryExpenses =
                        mapOf(
                            // 50%
                            "Market" to 500.0,
                            // 30%
                            "Fatura" to 300.0,
                            // 20%
                            "Eglence" to 200.0,
                        ),
                )
            every { getStatisticsUseCase() } returns flowOf(data)

            // When
            createViewModel()
            advanceUntilIdle()

            // Then
            val state = viewModel.state.value
            assertEquals(3, state.categoryExpenses.size)

            // Sıralama büyükten küçüğe
            assertEquals("Market", state.categoryExpenses[0].name)
            assertEquals(50f, state.categoryExpenses[0].percentage, 0.1f)

            assertEquals("Fatura", state.categoryExpenses[1].name)
            assertEquals(30f, state.categoryExpenses[1].percentage, 0.1f)

            assertEquals("Eğlence", state.categoryExpenses[2].name)
            assertEquals(20f, state.categoryExpenses[2].percentage, 0.1f)
        }

    @Test
    fun `should handle empty category expenses`() =
        runTest {
            // Given
            val data = createTestData(categoryExpenses = emptyMap())
            every { getStatisticsUseCase() } returns flowOf(data)

            // When
            createViewModel()
            advanceUntilIdle()

            // Then
            val state = viewModel.state.value
            assertTrue(state.categoryExpenses.isEmpty())
        }

    @Test
    fun `should update period on selection`() =
        runTest {
            // Given
            every { getStatisticsUseCase() } returns flowOf(createTestData())
            createViewModel()
            advanceUntilIdle()

            // When
            viewModel.selectPeriod("Aylık")
            advanceUntilIdle()

            // Then
            assertEquals("Aylık", viewModel.state.value.selectedPeriod)
            verify(atLeast = 2) { getStatisticsUseCase() } // init + selectPeriod
        }

    @Test
    fun `retry should reload statistics`() =
        runTest {
            // Given
            every { getStatisticsUseCase() } returns flowOf(createTestData())
            createViewModel()
            advanceUntilIdle()

            // When
            viewModel.retry()
            advanceUntilIdle()

            // Then
            verify(atLeast = 2) { getStatisticsUseCase() }
        }

    @Test
    fun `clearError should clear error state`() =
        runTest {
            // Given
            every { getStatisticsUseCase() } returns flowOf(createTestData())
            createViewModel()
            advanceUntilIdle()

            // When
            viewModel.clearError()

            // Then
            assertNull(viewModel.state.value.error)
        }

    @Test
    fun `should assign correct emojis to categories`() =
        runTest {
            // Given
            val data =
                createTestData(
                    categoryExpenses =
                        mapOf(
                            "Market" to 1000.0,
                            "Ulaşım" to 500.0,
                            "Fatura" to 300.0,
                            "Kira" to 2000.0,
                        ),
                )
            every { getStatisticsUseCase() } returns flowOf(data)

            // When
            createViewModel()
            advanceUntilIdle()

            // Then
            val state = viewModel.state.value
            val marketCategory = state.categoryExpenses.find { it.name == "Market" }
            val ulasimCategory = state.categoryExpenses.find { it.name == "Ulaşım" }
            val faturaCategory = state.categoryExpenses.find { it.name == "Fatura" }
            val kiraCategory = state.categoryExpenses.find { it.name == "Kira" }

            assertEquals("🛒", marketCategory?.emoji)
            assertEquals("🚗", ulasimCategory?.emoji)
            assertEquals("📄", faturaCategory?.emoji)
            assertEquals("🏠", kiraCategory?.emoji)
        }

    @Test
    fun `should compare current and previous month expenses`() =
        runTest {
            // Given
            val data =
                StatisticsData(
                    totalIncome = 15000.0,
                    totalExpense = 8000.0,
                    weeklyIncome = listOf(0f, 0f, 0f, 0f, 0f, 0f, 0f),
                    weeklyExpense = listOf(0f, 0f, 0f, 0f, 0f, 0f, 0f),
                    categoryExpenses = emptyMap(),
                    currentMonthExpense = 8000.0,
                    previousMonthExpense = 6000.0,
                )
            every { getStatisticsUseCase() } returns flowOf(data)

            // When
            createViewModel()
            advanceUntilIdle()

            // Then
            val state = viewModel.state.value
            assertEquals(8000.0, state.currentMonthExpense, 0.01)
            assertEquals(6000.0, state.previousMonthExpense, 0.01)
            // Bu ay geçen aydan 2000 TL daha fazla harcama yapılmış
            assertTrue(state.currentMonthExpense > state.previousMonthExpense)
        }

    @Test
    fun `should have default week labels in Turkish`() =
        runTest {
            // Given
            every { getStatisticsUseCase() } returns flowOf(createTestData())

            // When
            createViewModel()
            advanceUntilIdle()

            // Then
            val expectedLabels = listOf("Pzt", "Sal", "Çar", "Per", "Cum", "Cmt", "Paz")
            assertEquals(expectedLabels, viewModel.state.value.weekLabels)
        }

    @Test
    fun `should have 7 weekly income values`() =
        runTest {
            // Given
            val data = createTestData()
            every { getStatisticsUseCase() } returns flowOf(data)

            // When
            createViewModel()
            advanceUntilIdle()

            // Then
            assertEquals(7, viewModel.state.value.weeklyIncome.size)
            assertEquals(7, viewModel.state.value.weeklyExpense.size)
        }
}
