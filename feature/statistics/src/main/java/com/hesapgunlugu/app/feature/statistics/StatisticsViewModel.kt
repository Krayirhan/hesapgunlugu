package com.hesapgunlugu.app.feature.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hesapgunlugu.app.core.domain.usecase.statistics.GetStatisticsUseCase
import com.hesapgunlugu.app.core.ui.theme.*
import com.hesapgunlugu.app.core.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

sealed class StatisticsUiEvent {
    data class ShowError(val message: String) : StatisticsUiEvent()
}

data class StatisticsState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val selectedPeriod: String = "Weekly",
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val plannedIncome: Double = 0.0,
    val plannedExpense: Double = 0.0,
    val weeklyIncome: List<Float> = listOf(0f, 0f, 0f, 0f, 0f, 0f, 0f),
    val weeklyExpense: List<Float> = listOf(0f, 0f, 0f, 0f, 0f, 0f, 0f),
    val plannedWeeklyIncome: List<Float> = listOf(0f, 0f, 0f, 0f, 0f, 0f, 0f),
    val plannedWeeklyExpense: List<Float> = listOf(0f, 0f, 0f, 0f, 0f, 0f, 0f),
    val weekLabels: List<String> = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"),
    val categoryExpenses: List<CategoryExpense> = emptyList(),
    val currentMonthExpense: Double = 0.0,
    val previousMonthExpense: Double = 0.0,
    val error: String? = null,
)

@HiltViewModel
class StatisticsViewModel
    @Inject
    constructor(
        private val getStatisticsUseCase: GetStatisticsUseCase,
    ) : ViewModel() {
        private val _state = MutableStateFlow(StatisticsState())
        val state: StateFlow<StatisticsState> = _state.asStateFlow()

        private val _uiEvent = MutableSharedFlow<StatisticsUiEvent>()
        val uiEvent: SharedFlow<StatisticsUiEvent> = _uiEvent.asSharedFlow()

        init {
            loadStatistics()
        }

        fun selectPeriod(period: String) {
            _state.update { it.copy(selectedPeriod = period) }
            loadStatistics()
        }

        fun retry() {
            loadStatistics()
        }

        fun clearError() {
            _state.update { it.copy(error = null) }
        }

        fun refresh() {
            viewModelScope.launch {
                _state.update { it.copy(isRefreshing = true) }
                loadStatistics()
                kotlinx.coroutines.delay(500)
                _state.update { it.copy(isRefreshing = false) }
            }
        }

        private fun loadStatistics() {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true, error = null) }

                try {
                    getStatisticsUseCase(_state.value.selectedPeriod).collect { data ->
                        Timber.d("Statistics loaded: income=${data.totalIncome}, expense=${data.totalExpense}, period=${_state.value.selectedPeriod}")

                        val categoryColors = CategoryPalette

                        val totalCategoryExpense = data.categoryExpenses.values.sum()

                        val categoryExpenses =
                            data.categoryExpenses.entries
                                .sortedByDescending { it.value }
                                .mapIndexed { index, (category, amount) ->
                                    val emoji = getCategoryEmoji(category)
                                    CategoryExpense(
                                        name = category,
                                        emoji = emoji,
                                        amount = amount,
                                        percentage =
                                            if (totalCategoryExpense > 0) {
                                                (amount / totalCategoryExpense * 100).toFloat()
                                            } else {
                                                0f
                                            },
                                        color = categoryColors[index % categoryColors.size],
                                    )
                                }

                        _state.update {
                            it.copy(
                                isLoading = false,
                                totalIncome = data.totalIncome,
                                totalExpense = data.totalExpense,
                                plannedIncome = data.plannedIncome,
                                plannedExpense = data.plannedExpense,
                                weeklyIncome = data.weeklyIncome,
                                weeklyExpense = data.weeklyExpense,
                                plannedWeeklyIncome = data.plannedWeeklyIncome,
                                plannedWeeklyExpense = data.plannedWeeklyExpense,
                                categoryExpenses = categoryExpenses,
                                currentMonthExpense = data.currentMonthExpense,
                                previousMonthExpense = data.previousMonthExpense,
                            )
                        }
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Failed to load statistics")
                    val errorMessage = e.message ?: "Failed to load statistics"
                    _state.update {
                        it.copy(isLoading = false, error = errorMessage)
                    }
                    _uiEvent.emit(StatisticsUiEvent.ShowError(errorMessage))
                }
            }
        }

        private fun getCategoryEmoji(category: String): String {
            return Constants.CATEGORY_EMOJIS[category.lowercase()] ?: "💳"
        }
    }
