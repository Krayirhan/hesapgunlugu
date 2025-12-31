package com.hesapgunlugu.app.feature.scheduled

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hesapgunlugu.app.core.common.StringProvider
import com.hesapgunlugu.app.core.domain.model.RecurrenceType
import com.hesapgunlugu.app.core.domain.model.ScheduledPayment
import com.hesapgunlugu.app.core.domain.repository.SettingsRepository
import com.hesapgunlugu.app.core.domain.schedule.PlannedOccurrence
import com.hesapgunlugu.app.core.domain.schedule.ScheduleFrequency
import com.hesapgunlugu.app.core.domain.schedule.parseScheduleFrequency
import com.hesapgunlugu.app.core.domain.usecase.recurring.AddRecurringRuleUseCase
import com.hesapgunlugu.app.core.domain.usecase.scheduled.AddScheduledPaymentUseCase
import com.hesapgunlugu.app.core.domain.usecase.scheduled.DeleteScheduledPaymentUseCase
import com.hesapgunlugu.app.core.domain.usecase.scheduled.GetPlannedOccurrencesUseCase
import com.hesapgunlugu.app.core.domain.usecase.scheduled.GetScheduledPaymentsUseCase
import com.hesapgunlugu.app.core.domain.usecase.scheduled.MarkPaymentAsPaidUseCase
import com.hesapgunlugu.app.core.domain.usecase.transaction.GetTransactionsByDateRangeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

data class ScheduledState(
    val upcomingPayments: List<PlannedOccurrence> = emptyList(),
    val recurringIncomes: List<ScheduledPayment> = emptyList(),
    val recurringExpenses: List<ScheduledPayment> = emptyList(),
    val overduePayments: List<PlannedOccurrence> = emptyList(),
    val totalUpcoming: Double = 0.0,
    val totalRecurring: Double = 0.0,
    val totalOverdue: Double = 0.0,
    val monthlyFixedExpenses: Double = 0.0,
    val monthlyBudget: Double = 20000.0,
    val nextPaymentDays: Int = -1,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: String? = null,
)

sealed class UiEvent {
    data class ShowSuccess(val message: String) : UiEvent()

    data class ShowError(val message: String) : UiEvent()
}

@HiltViewModel
class ScheduledViewModel
    @Inject
    constructor(
        private val getScheduledPaymentsUseCase: GetScheduledPaymentsUseCase,
        private val addScheduledPaymentUseCase: AddScheduledPaymentUseCase,
        private val deleteScheduledPaymentUseCase: DeleteScheduledPaymentUseCase,
        private val markPaymentAsPaidUseCase: MarkPaymentAsPaidUseCase,
        private val addRecurringRuleUseCase: AddRecurringRuleUseCase,
        private val getPlannedOccurrencesUseCase: GetPlannedOccurrencesUseCase,
        private val getTransactionsByDateRangeUseCase: GetTransactionsByDateRangeUseCase,
        private val settingsRepository: SettingsRepository,
        private val stringProvider: StringProvider,
    ) : ViewModel() {
        private val _state = MutableStateFlow(ScheduledState())
        val state: StateFlow<ScheduledState> = _state.asStateFlow()

        private val _uiEvent = MutableSharedFlow<UiEvent>()
        val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

        init {
            loadScheduledPayments()
            observeSettings()
        }

        private fun observeSettings() {
            viewModelScope.launch {
                settingsRepository.settingsFlow.collect { settings ->
                    _state.update { it.copy(monthlyBudget = settings.monthlyLimit) }
                }
            }
        }

        private fun loadScheduledPayments() {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true, error = null) }

                try {
                    val startRange =
                        Calendar.getInstance().apply {
                            add(Calendar.DAY_OF_MONTH, -90)
                            set(Calendar.HOUR_OF_DAY, 0)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                    val endRange =
                        Calendar.getInstance().apply {
                            add(Calendar.DAY_OF_MONTH, 30)
                            set(Calendar.HOUR_OF_DAY, 23)
                            set(Calendar.MINUTE, 59)
                            set(Calendar.SECOND, 59)
                            set(Calendar.MILLISECOND, 999)
                        }

                    combine(
                        getScheduledPaymentsUseCase(),
                        getPlannedOccurrencesUseCase(startRange.time, endRange.time),
                        getTransactionsByDateRangeUseCase(startRange.time, endRange.time),
                    ) { payments, planned, transactions ->
                        Triple(payments, planned, transactions)
                    }
                        .catch { e ->
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    error = stringProvider.errorLoading(),
                                )
                            }
                            _uiEvent.emit(UiEvent.ShowError(e.message ?: stringProvider.errorGeneric()))
                        }
                        .collect { (payments, planned, transactions) ->
                            updateState(payments, planned, transactions)
                        }
                } catch (e: Exception) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = stringProvider.errorLoading(),
                        )
                    }
                    _uiEvent.emit(UiEvent.ShowError(e.message ?: stringProvider.errorGeneric()))
                }
            }
        }

        private fun updateState(
            allScheduled: List<ScheduledPayment>,
            plannedOccurrences: List<PlannedOccurrence>,
            transactions: List<com.hesapgunlugu.app.core.domain.model.Transaction>,
        ) {
            val now = Calendar.getInstance()
            val nextWeek = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 7) }

            val paidOccurrences =
                transactions
                    .filter { it.scheduledPaymentId != null }
                    .map { it.scheduledPaymentId!! to normalizeDate(it.date) }
                    .toSet()

            val unpaidOccurrences =
                plannedOccurrences.filter { occurrence ->
                    val key = occurrence.payment.id to normalizeDate(occurrence.date)
                    !paidOccurrences.contains(key)
                }

            val overdue =
                unpaidOccurrences.filter {
                    !it.payment.isIncome && it.date.before(now.time)
                }.sortedBy { it.date }

            val upcoming =
                unpaidOccurrences.filter {
                    !it.payment.isIncome &&
                        !it.date.before(now.time) && it.date.before(nextWeek.time)
                }.sortedBy { it.date }

            val recurringIncomes = allScheduled.filter { it.isRecurring && it.isIncome }
            val recurringExpenses = allScheduled.filter { it.isRecurring && !it.isIncome }

            val totalUpcoming = upcoming.sumOf { it.payment.amount }
            val totalOverdue = overdue.sumOf { it.payment.amount }
            val totalRecurring = recurringExpenses.sumOf { it.amount }

            val monthlyFixedExpenses =
                recurringExpenses.sumOf { payment ->
                    when (parseScheduleFrequency(payment.frequency)) {
                        ScheduleFrequency.DAILY -> payment.amount * 30
                        ScheduleFrequency.WEEKLY -> payment.amount * 4
                        ScheduleFrequency.YEARLY -> payment.amount / 12
                        else -> payment.amount
                    }
                }

            val nextPaymentDays =
                upcoming.firstOrNull()?.let { payment ->
                    val diff = payment.date.time - now.timeInMillis
                    (diff / (1000 * 60 * 60 * 24)).toInt()
                } ?: -1

            _state.update {
                it.copy(
                    upcomingPayments = upcoming,
                    recurringIncomes = recurringIncomes,
                    recurringExpenses = recurringExpenses,
                    overduePayments = overdue,
                    totalUpcoming = totalUpcoming,
                    totalRecurring = totalRecurring,
                    totalOverdue = totalOverdue,
                    monthlyFixedExpenses = monthlyFixedExpenses,
                    nextPaymentDays = nextPaymentDays,
                    isLoading = false,
                    error = null,
                )
            }
        }

        fun addScheduled(payment: ScheduledPayment) {
            viewModelScope.launch {
                addScheduledPaymentUseCase(payment)
                    .onSuccess {
                        _uiEvent.emit(UiEvent.ShowSuccess(stringProvider.scheduledAdded()))
                    }
                    .onFailure { e ->
                        _uiEvent.emit(UiEvent.ShowError(e.message ?: stringProvider.errorSaving()))
                    }
            }
        }

        fun markAsPaid(occurrence: PlannedOccurrence) {
            viewModelScope.launch {
                markPaymentAsPaidUseCase(
                    payment = occurrence.payment,
                    paidDate = occurrence.date,
                    isPaid = true,
                )
                    .onSuccess {
                        _uiEvent.emit(UiEvent.ShowSuccess(stringProvider.paymentCompleted()))
                    }
                    .onFailure { e ->
                        _uiEvent.emit(UiEvent.ShowError(e.message ?: stringProvider.errorGeneric()))
                    }
            }
        }

        fun deleteScheduled(payment: ScheduledPayment) {
            viewModelScope.launch {
                deleteScheduledPaymentUseCase(payment.id)
                    .onSuccess {
                        _uiEvent.emit(UiEvent.ShowSuccess(stringProvider.scheduledDeleted()))
                    }
                    .onFailure { e ->
                        _uiEvent.emit(UiEvent.ShowError(e.message ?: stringProvider.errorDeleting()))
                    }
            }
        }

        fun retry() {
            loadScheduledPayments()
        }

        fun refresh() {
            viewModelScope.launch {
                _state.update { it.copy(isRefreshing = true) }
                kotlinx.coroutines.delay(500)
                _state.update { it.copy(isRefreshing = false) }
            }
        }

        fun clearError() {
            _state.update { it.copy(error = null) }
        }

        fun payAllOverdue() {
            viewModelScope.launch {
                val overduePayments = _state.value.overduePayments
                var successCount = 0
                var failCount = 0

                overduePayments.forEach { occurrence ->
                    markPaymentAsPaidUseCase(
                        payment = occurrence.payment,
                        paidDate = occurrence.date,
                        isPaid = true,
                    )
                        .onSuccess { successCount++ }
                        .onFailure { failCount++ }
                }

                if (successCount > 0) {
                    _uiEvent.emit(UiEvent.ShowSuccess("$successCount payment completed"))
                }
                if (failCount > 0) {
                    _uiEvent.emit(UiEvent.ShowError("$failCount payment failed"))
                }
            }
        }

        fun getDaysOverdue(payment: ScheduledPayment): Int {
            val now = Calendar.getInstance().timeInMillis
            val diff = now - payment.dueDate.time
            return (diff / (1000 * 60 * 60 * 24)).toInt()
        }

        fun addRecurringRule(
            scheduledPaymentId: Long,
            recurrenceType: RecurrenceType,
            interval: Int,
            dayOfMonth: Int?,
            daysOfWeek: List<Int>?,
            endDate: String?,
            maxOccurrences: Int?,
        ) {
            viewModelScope.launch {
                addRecurringRuleUseCase(
                    scheduledPaymentId = scheduledPaymentId,
                    recurrenceType = recurrenceType,
                    interval = interval,
                    dayOfMonth = dayOfMonth,
                    daysOfWeek = daysOfWeek,
                    endDate = endDate,
                    maxOccurrences = maxOccurrences,
                )
                    .onSuccess {
                        _uiEvent.emit(UiEvent.ShowSuccess("Automatic recurrence rule added"))
                    }
                    .onFailure { e ->
                        _uiEvent.emit(UiEvent.ShowError(e.message ?: "Error occurred while adding rule"))
                    }
            }
        }
    }

private fun normalizeDate(date: Date): Long {
    val calendar =
        Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    return calendar.timeInMillis
}
