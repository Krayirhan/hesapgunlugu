package com.hesapgunlugu.app.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hesapgunlugu.app.core.domain.model.Transaction
import com.hesapgunlugu.app.core.domain.model.TransactionType
import com.hesapgunlugu.app.core.domain.repository.TransactionRepository
import com.hesapgunlugu.app.core.domain.schedule.PlannedOccurrence
import com.hesapgunlugu.app.core.domain.usecase.scheduled.GetPlannedOccurrencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.util.Calendar
import javax.inject.Inject

enum class TransactionFilter { ALL, INCOME, EXPENSE }

enum class TransactionSort { DATE_DESC, DATE_ASC, AMOUNT_DESC, AMOUNT_ASC }

sealed class HistoryUiEvent {
    data class ShowError(val message: String) : HistoryUiEvent()

    data class ShowSuccess(val message: String) : HistoryUiEvent()
}

@HiltViewModel
class HistoryViewModel
    @Inject
    constructor(
        private val repository: TransactionRepository,
        private val getPlannedOccurrencesUseCase: GetPlannedOccurrencesUseCase,
    ) : ViewModel() {
        private val _state = MutableStateFlow(HistoryState())
        val state: StateFlow<HistoryState> = _state.asStateFlow()

        private val _uiEvent = MutableSharedFlow<HistoryUiEvent>()
        val uiEvent: SharedFlow<HistoryUiEvent> = _uiEvent.asSharedFlow()

        private val _searchQuery = MutableStateFlow("")
        val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

        private val _selectedMonth = MutableStateFlow(Calendar.getInstance())
        val selectedMonth: StateFlow<Calendar> = _selectedMonth.asStateFlow()

        private val _selectedFilter = MutableStateFlow(TransactionFilter.ALL)
        val selectedFilter: StateFlow<TransactionFilter> = _selectedFilter.asStateFlow()

        private val _selectedSort = MutableStateFlow(TransactionSort.DATE_DESC)
        val selectedSort: StateFlow<TransactionSort> = _selectedSort.asStateFlow()

        private val plannedOccurrencesFlow =
            _selectedMonth.flatMapLatest { month ->
                val start = monthStart(month)
                val end = monthEnd(month)
                getPlannedOccurrencesUseCase(start, end)
            }

        init {
            loadTransactions()
        }

        private fun loadTransactions() {
            viewModelScope.launch {
                combine(
                    repository.getAllTransactions(),
                    plannedOccurrencesFlow,
                    _searchQuery,
                    _selectedMonth,
                    _selectedFilter,
                    _selectedSort,
                ) { values ->
                    @Suppress("UNCHECKED_CAST")
                    val transactions = values[0] as List<Transaction>
                    val planned = values[1] as List<PlannedOccurrence>
                    val query = values[2] as String
                    val currentMonth = values[3] as Calendar
                    val filter = values[4] as TransactionFilter
                    val sort = values[5] as TransactionSort
                    processTransactions(transactions, planned, query, currentMonth, filter, sort)
                }
                    .onStart {
                        _state.update { it.copy(isLoading = true, error = null) }
                    }
                    .catch { e ->
                        Timber.e(e, "Failed to load transactions")
                        _state.update { it.copy(isLoading = false, error = e.message ?: "Unknown error") }
                    }
                    .collect { newState ->
                        _state.value = newState
                    }
            }
        }

        private fun processTransactions(
            transactions: List<Transaction>,
            plannedOccurrences: List<PlannedOccurrence>,
            query: String,
            currentMonth: Calendar,
            filter: TransactionFilter,
            sort: TransactionSort,
        ): HistoryState {
            var filtered =
                transactions.filter { t ->
                    val tCal = Calendar.getInstance().apply { time = t.date }
                    tCal.get(Calendar.YEAR) == currentMonth.get(Calendar.YEAR) &&
                        tCal.get(Calendar.MONTH) == currentMonth.get(Calendar.MONTH)
                }

            filtered =
                when (filter) {
                    TransactionFilter.INCOME -> filtered.filter { it.type == TransactionType.INCOME }
                    TransactionFilter.EXPENSE -> filtered.filter { it.type == TransactionType.EXPENSE }
                    TransactionFilter.ALL -> filtered
                }

            if (query.isNotBlank()) {
                filtered =
                    filtered.filter { t ->
                        t.title.contains(query, ignoreCase = true) ||
                            t.category.contains(query, ignoreCase = true)
                    }
            }

            val sortedList =
                when (sort) {
                    TransactionSort.DATE_DESC -> filtered.sortedByDescending { it.date }
                    TransactionSort.DATE_ASC -> filtered.sortedBy { it.date }
                    TransactionSort.AMOUNT_DESC -> filtered.sortedByDescending { it.amount }
                    TransactionSort.AMOUNT_ASC -> filtered.sortedBy { it.amount }
                }

            val monthlyIncome = filtered.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
            val monthlyExpense = filtered.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

            val actualIncomeDates =
                filtered
                    .filter { it.type == TransactionType.INCOME }
                    .map { it.date.toLocalDate() }
                    .toSet()
            val actualExpenseDates =
                filtered
                    .filter { it.type == TransactionType.EXPENSE }
                    .map { it.date.toLocalDate() }
                    .toSet()

            val plannedIncomeDates =
                plannedOccurrences
                    .filter { it.payment.isIncome }
                    .map { it.date.toLocalDate() }
                    .toSet()
            val plannedExpenseDates =
                plannedOccurrences
                    .filter { !it.payment.isIncome }
                    .map { it.date.toLocalDate() }
                    .toSet()

            return HistoryState(
                transactions = sortedList,
                totalCount = sortedList.size,
                monthlyIncome = monthlyIncome,
                monthlyExpense = monthlyExpense,
                actualIncomeDates = actualIncomeDates,
                actualExpenseDates = actualExpenseDates,
                plannedIncomeDates = plannedIncomeDates,
                plannedExpenseDates = plannedExpenseDates,
                isLoading = false,
                error = null,
            )
        }

        fun previousMonth() {
            val current = _selectedMonth.value.clone() as Calendar
            current.add(Calendar.MONTH, -1)
            _selectedMonth.value = current
        }

        fun nextMonth() {
            val current = _selectedMonth.value.clone() as Calendar
            current.add(Calendar.MONTH, 1)
            _selectedMonth.value = current
        }

        fun onSearchQueryChange(newQuery: String) {
            _searchQuery.value = newQuery
        }

        fun setFilter(filter: TransactionFilter) {
            _selectedFilter.value = filter
        }

        fun setSort(sort: TransactionSort) {
            _selectedSort.value = sort
        }

        fun deleteTransaction(transaction: Transaction) {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true) }
                repository.deleteTransaction(transaction)
                    .onSuccess {
                        _uiEvent.emit(HistoryUiEvent.ShowSuccess("Transaction deleted"))
                    }
                    .onFailure { e ->
                        Timber.e(e, "Failed to delete transaction")
                        _uiEvent.emit(HistoryUiEvent.ShowError(e.message ?: "Delete error"))
                    }
                _state.update { it.copy(isLoading = false) }
            }
        }

        fun updateTransaction(transaction: Transaction) {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true) }
                repository.updateTransaction(transaction)
                    .onSuccess {
                        _uiEvent.emit(HistoryUiEvent.ShowSuccess("Transaction updated"))
                    }
                    .onFailure { e ->
                        Timber.e(e, "Failed to update transaction")
                        _uiEvent.emit(HistoryUiEvent.ShowError(e.message ?: "Update error"))
                    }
                _state.update { it.copy(isLoading = false) }
            }
        }

        fun clearError() {
            _state.update { it.copy(error = null) }
        }

        fun retry() {
            loadTransactions()
        }

        fun refresh() {
            viewModelScope.launch {
                _state.update { it.copy(isRefreshing = true) }
                loadTransactions()
                kotlinx.coroutines.delay(500)
                _state.update { it.copy(isRefreshing = false) }
            }
        }
    }

data class HistoryState(
    val transactions: List<Transaction> = emptyList(),
    val totalCount: Int = 0,
    val monthlyIncome: Double = 0.0,
    val monthlyExpense: Double = 0.0,
    val actualIncomeDates: Set<LocalDate> = emptySet(),
    val actualExpenseDates: Set<LocalDate> = emptySet(),
    val plannedIncomeDates: Set<LocalDate> = emptySet(),
    val plannedExpenseDates: Set<LocalDate> = emptySet(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
)

private fun java.util.Date.toLocalDate(): LocalDate {
    val calendar = Calendar.getInstance().apply { time = this@toLocalDate }
    return LocalDate.of(
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH) + 1,
        calendar.get(Calendar.DAY_OF_MONTH),
    )
}

private fun monthStart(month: Calendar): java.util.Date {
    val calendar = month.clone() as Calendar
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.time
}

private fun monthEnd(month: Calendar): java.util.Date {
    val calendar = month.clone() as Calendar
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)
    calendar.set(Calendar.MILLISECOND, 999)
    return calendar.time
}
