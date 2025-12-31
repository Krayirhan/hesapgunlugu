package com.hesapgunlugu.app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hesapgunlugu.app.core.common.StringProvider
import com.hesapgunlugu.app.core.domain.model.Transaction
import com.hesapgunlugu.app.core.domain.model.TransactionException
import com.hesapgunlugu.app.core.domain.repository.SettingsRepository
import com.hesapgunlugu.app.core.domain.usecase.analytics.CalculateCategoryBudgetsUseCase
import com.hesapgunlugu.app.core.domain.usecase.home.CalculateHomeDashboardUseCase
import com.hesapgunlugu.app.core.domain.usecase.home.CheckBudgetWarningUseCase
import com.hesapgunlugu.app.core.domain.usecase.home.GetGreetingMessageUseCase
import com.hesapgunlugu.app.core.domain.usecase.scheduled.GetScheduledPaymentsUseCase
import com.hesapgunlugu.app.core.domain.usecase.transaction.AddTransactionUseCase
import com.hesapgunlugu.app.core.domain.usecase.transaction.DeleteTransactionUseCase
import com.hesapgunlugu.app.core.domain.usecase.transaction.GetTransactionsUseCase
import com.hesapgunlugu.app.core.domain.usecase.transaction.UpdateTransactionUseCase
import com.hesapgunlugu.app.core.ui.components.UpcomingPaymentPreview
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

sealed class HomeUiEvent {
    data class ShowSuccess(val message: String) : HomeUiEvent()

    data class ShowError(val message: String) : HomeUiEvent()

    data class ShowBudgetWarning(val title: String, val message: String) : HomeUiEvent()
}

/**
 * HomeViewModel - Refactored for Single Responsibility Principle
 *
 * Uses dedicated use cases for:
 * - Dashboard calculations (CalculateHomeDashboardUseCase)
 * - Budget warnings (CheckBudgetWarningUseCase)
 * - Greeting messages (GetGreetingMessageUseCase)
 * - Transaction CRUD operations
 */
@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        private val getTransactionsUseCase: GetTransactionsUseCase,
        private val addTransactionUseCase: AddTransactionUseCase,
        private val updateTransactionUseCase: UpdateTransactionUseCase,
        private val deleteTransactionUseCase: DeleteTransactionUseCase,
        private val getScheduledPaymentsUseCase: GetScheduledPaymentsUseCase,
        private val settingsRepository: SettingsRepository,
        private val stringProvider: StringProvider,
        private val calculateDashboardUseCase: CalculateHomeDashboardUseCase,
        private val calculateCategoryBudgetsUseCase: CalculateCategoryBudgetsUseCase,
        private val checkBudgetWarningUseCase: CheckBudgetWarningUseCase,
        private val getGreetingMessageUseCase: GetGreetingMessageUseCase,
    ) : ViewModel() {
        private val _state = MutableStateFlow(HomeState())
        val state: StateFlow<HomeState> = _state.asStateFlow()

        private val _uiEvent = MutableSharedFlow<HomeUiEvent>()
        val uiEvent: SharedFlow<HomeUiEvent> = _uiEvent.asSharedFlow()

        private var hasNotifiedThisSession = false

        init {
            loadData()
        }

        private fun loadData() {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true, error = null) }

                combine(
                    getTransactionsUseCase(),
                    settingsRepository.settingsFlow,
                    getScheduledPaymentsUseCase(),
                ) { transactions, settings, scheduledPayments ->
                    val dashboardData = calculateDashboardUseCase(transactions, settings)

                    checkAndEmitBudgetWarning(settings.monthlyLimit, dashboardData.totalExpense)

                    val computedBudgets =
                        calculateCategoryBudgetsUseCase(
                            transactions = transactions,
                            categoryBudgets = settings.categoryBudgets,
                        )

                    // Yaklaşan ödemeleri hesapla (7 gün içinde)
                    val now = Calendar.getInstance()
                    val nextWeek = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 7) }

                    val upcomingPayments =
                        scheduledPayments
                            .filter { !it.isIncome && !it.isPaid }
                            .filter { it.dueDate.before(nextWeek.time) }
                            .sortedBy { it.dueDate }
                            .take(3)
                            .map { payment ->
                                UpcomingPaymentPreview(
                                    id = payment.id,
                                    title = payment.title,
                                    amount = payment.amount,
                                    dueDate = payment.dueDate,
                                    emoji = payment.emoji,
                                    isOverdue = payment.dueDate.before(now.time),
                                )
                            }

                    val upcomingTotal = upcomingPayments.sumOf { it.amount }

                    HomeState(
                        totalBalance = dashboardData.totalBalance,
                        totalIncome = dashboardData.totalIncome,
                        totalExpense = dashboardData.totalExpense,
                        greeting = getGreetingMessageUseCase(),
                        recentTransactions = transactions.sortedByDescending { it.date }.take(5),
                        categoryExpenses = dashboardData.categoryExpenses,
                        budgetStatuses = computedBudgets,
                        financialHealthScore = dashboardData.financialHealthScore,
                        savingsRate = dashboardData.savingsRate,
                        weeklySpending = dashboardData.weeklySpending,
                        monthlyTrend = dashboardData.monthlyTrend,
                        topSpendingCategory = dashboardData.topSpendingCategory,
                        averageDailySpending = dashboardData.averageDailySpending,
                        upcomingPayments = upcomingPayments,
                        upcomingPaymentsTotal = upcomingTotal,
                        isLoading = false,
                        error = null,
                    )
                }
                    .catch { e ->
                        _state.update { it.copy(isLoading = false, error = stringProvider.errorLoading()) }
                        _uiEvent.emit(HomeUiEvent.ShowError(e.message ?: stringProvider.errorGeneric()))
                    }
                    .collectLatest { newState ->
                        _state.value = newState
                    }
            }
        }

        private suspend fun checkAndEmitBudgetWarning(
            limit: Double,
            currentExpense: Double,
        ) {
            if (hasNotifiedThisSession) return

            val warning = checkBudgetWarningUseCase(limit, currentExpense)
            if (warning != null) {
                _uiEvent.emit(HomeUiEvent.ShowBudgetWarning(warning.title, warning.message))
                hasNotifiedThisSession = true
            }

            val remainingBudget = checkBudgetWarningUseCase.getRemainingBudget(limit, currentExpense)
            if (remainingBudget.percentUsed < 80f) {
                hasNotifiedThisSession = false
            }
        }

        fun addTransaction(transaction: Transaction) {
            viewModelScope.launch {
                addTransactionUseCase(transaction)
                    .onSuccess {
                        _uiEvent.emit(HomeUiEvent.ShowSuccess(stringProvider.transactionAdded()))
                    }
                    .onFailure { e ->
                        val errorMessage =
                            when (e) {
                                is TransactionException.EmptyTitle -> stringProvider.errorEmptyTitle()
                                is TransactionException.InvalidAmount -> stringProvider.errorInvalidAmount()
                                else -> e.message ?: stringProvider.errorSaving()
                            }
                        _uiEvent.emit(HomeUiEvent.ShowError(errorMessage))
                    }
            }
        }

        fun updateTransaction(transaction: Transaction) {
            viewModelScope.launch {
                updateTransactionUseCase(transaction)
                    .onSuccess {
                        _uiEvent.emit(HomeUiEvent.ShowSuccess(stringProvider.transactionUpdated()))
                    }
                    .onFailure { e ->
                        _uiEvent.emit(HomeUiEvent.ShowError(e.message ?: stringProvider.errorSaving()))
                    }
            }
        }

        fun deleteTransaction(transaction: Transaction) {
            viewModelScope.launch {
                deleteTransactionUseCase(transaction)
                    .onSuccess {
                        _uiEvent.emit(HomeUiEvent.ShowSuccess(stringProvider.transactionDeleted()))
                    }
                    .onFailure { e ->
                        _uiEvent.emit(HomeUiEvent.ShowError(e.message ?: stringProvider.errorDeleting()))
                    }
            }
        }

        fun retry() {
            loadData()
        }

        fun clearError() {
            _state.update { it.copy(error = null) }
        }

        fun updateCategoryBudget(
            category: String,
            limit: Double,
        ) {
            viewModelScope.launch {
                settingsRepository.updateCategoryBudget(category, limit)
                _uiEvent.emit(HomeUiEvent.ShowSuccess(stringProvider.categoryBudgetUpdated()))
            }
        }

        fun addCategoryBudget(
            category: String,
            limit: Double,
        ) {
            viewModelScope.launch {
                settingsRepository.updateCategoryBudget(category, limit)
                _uiEvent.emit(HomeUiEvent.ShowSuccess(stringProvider.categoryBudgetAdded()))
            }
        }
    }
