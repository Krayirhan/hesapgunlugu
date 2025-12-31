package com.hesapgunlugu.app.feature.scheduled

import app.cash.turbine.test
import com.hesapgunlugu.app.core.common.StringProvider
import com.hesapgunlugu.app.core.domain.model.ScheduledPayment
import com.hesapgunlugu.app.core.domain.model.UserSettings
import com.hesapgunlugu.app.core.domain.repository.SettingsRepository
import com.hesapgunlugu.app.core.domain.schedule.PlannedOccurrence
import com.hesapgunlugu.app.core.domain.usecase.recurring.AddRecurringRuleUseCase
import com.hesapgunlugu.app.core.domain.usecase.scheduled.AddScheduledPaymentUseCase
import com.hesapgunlugu.app.core.domain.usecase.scheduled.DeleteScheduledPaymentUseCase
import com.hesapgunlugu.app.core.domain.usecase.scheduled.GetPlannedOccurrencesUseCase
import com.hesapgunlugu.app.core.domain.usecase.scheduled.GetScheduledPaymentsUseCase
import com.hesapgunlugu.app.core.domain.usecase.scheduled.MarkPaymentAsPaidUseCase
import com.hesapgunlugu.app.core.domain.usecase.transaction.GetTransactionsByDateRangeUseCase
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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Calendar

@OptIn(ExperimentalCoroutinesApi::class)
class ScheduledViewModelTest {
    private lateinit var viewModel: ScheduledViewModel
    private lateinit var getScheduledPaymentsUseCase: GetScheduledPaymentsUseCase
    private lateinit var addScheduledPaymentUseCase: AddScheduledPaymentUseCase
    private lateinit var deleteScheduledPaymentUseCase: DeleteScheduledPaymentUseCase
    private lateinit var markPaymentAsPaidUseCase: MarkPaymentAsPaidUseCase
    private lateinit var addRecurringRuleUseCase: AddRecurringRuleUseCase
    private lateinit var getPlannedOccurrencesUseCase: GetPlannedOccurrencesUseCase
    private lateinit var getTransactionsByDateRangeUseCase: GetTransactionsByDateRangeUseCase
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var stringProvider: StringProvider

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        getScheduledPaymentsUseCase = mockk()
        addScheduledPaymentUseCase = mockk()
        deleteScheduledPaymentUseCase = mockk()
        markPaymentAsPaidUseCase = mockk()
        addRecurringRuleUseCase = mockk()
        getPlannedOccurrencesUseCase = mockk()
        getTransactionsByDateRangeUseCase = mockk()
        settingsRepository = mockk()
        stringProvider = mockk()

        every { settingsRepository.settingsFlow } returns flowOf(UserSettings())
        every { stringProvider.scheduledAdded() } returns "Scheduled payment added"
        every { stringProvider.scheduledDeleted() } returns "Scheduled payment deleted"
        every { stringProvider.paymentCompleted() } returns "Payment completed"
        every { stringProvider.errorGeneric() } returns "Error"
        every { stringProvider.errorLoading() } returns "Load error"
        every { stringProvider.errorSaving() } returns "Save error"
        every { stringProvider.errorDeleting() } returns "Delete error"
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() {
        viewModel =
            ScheduledViewModel(
                getScheduledPaymentsUseCase = getScheduledPaymentsUseCase,
                addScheduledPaymentUseCase = addScheduledPaymentUseCase,
                deleteScheduledPaymentUseCase = deleteScheduledPaymentUseCase,
                markPaymentAsPaidUseCase = markPaymentAsPaidUseCase,
                addRecurringRuleUseCase = addRecurringRuleUseCase,
                getPlannedOccurrencesUseCase = getPlannedOccurrencesUseCase,
                getTransactionsByDateRangeUseCase = getTransactionsByDateRangeUseCase,
                settingsRepository = settingsRepository,
                stringProvider = stringProvider,
            )
    }

    private fun createTestPayment(
        id: Long = 1,
        title: String = "Test",
        amount: Double = 100.0,
        isIncome: Boolean = false,
        isRecurring: Boolean = false,
        daysFromNow: Int = 3,
    ): ScheduledPayment {
        val calendar =
            Calendar.getInstance().apply {
                add(Calendar.DAY_OF_MONTH, daysFromNow)
            }
        return ScheduledPayment(
            id = id,
            title = title,
            amount = amount,
            isIncome = isIncome,
            isRecurring = isRecurring,
            frequency = if (isRecurring) "MONTHLY" else "",
            dueDate = calendar.time,
            emoji = ":)",
            isPaid = false,
            category = "Test",
        )
    }

    @Test
    fun `should load scheduled payments successfully`() =
        runTest {
            val payments =
                listOf(
                    createTestPayment(1, "Netflix", 99.0, isRecurring = true),
                    createTestPayment(2, "Spotify", 39.0, isRecurring = true),
                )
            val occurrences = payments.map { PlannedOccurrence(it, it.dueDate) }

            every { getScheduledPaymentsUseCase() } returns flowOf(payments)
            every { getPlannedOccurrencesUseCase(any(), any()) } returns flowOf(occurrences)
            every { getTransactionsByDateRangeUseCase(any(), any()) } returns flowOf(emptyList())

            createViewModel()
            advanceUntilIdle()

            val state = viewModel.state.value
            assertFalse(state.isLoading)
            assertNull(state.error)
        }

    @Test
    fun `should add scheduled payment successfully`() =
        runTest {
            every { getScheduledPaymentsUseCase() } returns flowOf(emptyList())
            every { getPlannedOccurrencesUseCase(any(), any()) } returns flowOf(emptyList())
            every { getTransactionsByDateRangeUseCase(any(), any()) } returns flowOf(emptyList())
            coEvery { addScheduledPaymentUseCase(any()) } returns Result.success(1L)

            createViewModel()
            advanceUntilIdle()

            val newPayment = createTestPayment(0, "New Payment", 50.0)

            viewModel.uiEvent.test {
                viewModel.addScheduled(newPayment)
                advanceUntilIdle()

                val event = awaitItem()
                assertTrue(event is UiEvent.ShowSuccess)
                assertEquals("Scheduled payment added", (event as UiEvent.ShowSuccess).message)
            }
            io.mockk.coVerify(exactly = 1) { addScheduledPaymentUseCase(newPayment) }
        }

    @Test
    fun `should mark planned occurrence as paid successfully`() =
        runTest {
            val payment = createTestPayment(1, "Test", 100.0)
            val occurrence = PlannedOccurrence(payment, payment.dueDate)

            every { getScheduledPaymentsUseCase() } returns flowOf(listOf(payment))
            every { getPlannedOccurrencesUseCase(any(), any()) } returns flowOf(listOf(occurrence))
            every { getTransactionsByDateRangeUseCase(any(), any()) } returns flowOf(emptyList())
            coEvery { markPaymentAsPaidUseCase(any(), any(), any()) } returns Result.success(Unit)

            createViewModel()
            advanceUntilIdle()

            viewModel.uiEvent.test {
                viewModel.markAsPaid(occurrence)
                advanceUntilIdle()

                val event = awaitItem()
                assertTrue(event is UiEvent.ShowSuccess)
                assertEquals("Payment completed", (event as UiEvent.ShowSuccess).message)
            }
            io.mockk.coVerify(exactly = 1) { markPaymentAsPaidUseCase(payment, occurrence.date, true) }
        }
}
