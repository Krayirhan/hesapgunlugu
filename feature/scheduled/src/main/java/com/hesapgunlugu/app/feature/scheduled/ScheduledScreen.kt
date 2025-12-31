package com.hesapgunlugu.app.feature.scheduled

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TrendingDown
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.hesapgunlugu.app.core.ui.components.AddScheduledForm
import com.hesapgunlugu.app.core.ui.components.ErrorCard
import com.hesapgunlugu.app.core.ui.components.ShimmerLoadingList
import com.hesapgunlugu.app.core.ui.theme.PrimaryBlue
import com.hesapgunlugu.app.feature.scheduled.components.*
import kotlinx.coroutines.launch
import com.hesapgunlugu.app.core.ui.R as CoreUiR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduledScreen(viewModel: ScheduledViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showBottomSheet by remember { mutableStateOf(false) }
    var showRecurringDialog by remember { mutableStateOf(false) }
    var selectedScheduledPaymentId by remember { mutableStateOf<Long?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // UI Event'leri dinle
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiEvent.collect { event ->
                when (event) {
                    is UiEvent.ShowSuccess -> {
                        snackbarHostState.showSnackbar(event.message)
                    }
                    is UiEvent.ShowError -> {
                        snackbarHostState.showSnackbar(event.message)
                    }
                }
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0.dp),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (!state.isLoading && state.error == null) {
                val fabDescription = stringResource(CoreUiR.string.add_scheduled_transaction)
                FloatingActionButton(
                    onClick = { showBottomSheet = true },
                    containerColor = PrimaryBlue,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
                    modifier =
                        Modifier.semantics {
                            contentDescription = fabDescription
                            role = Role.Button
                        },
                ) {
                    Icon(
                        Icons.Default.Add,
                        // Parent has description
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                    )
                }
            }
        },
    ) { paddingValues ->

        // Loading State
        if (state.isLoading) {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(bottom = paddingValues.calculateBottomPadding()),
            ) {
                ScheduledHeader()
                Spacer(modifier = Modifier.height(20.dp))
                ShimmerLoadingList(itemCount = 5)
            }
            return@Scaffold
        }

        // Error State
        if (state.error != null) {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(bottom = paddingValues.calculateBottomPadding()),
            ) {
                ScheduledHeader()
                Spacer(modifier = Modifier.height(40.dp))
                ErrorCard(
                    message = state.error!!,
                    onRetry = { viewModel.retry() },
                )
            }
            return@Scaffold
        }

        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier.fillMaxSize(),
        ) {
            LazyColumn(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(bottom = paddingValues.calculateBottomPadding()),
            ) {
                // Header
                item {
                    ScheduledHeader()
                }

                // Overdue Payments Card (if any)
                if (state.overduePayments.isNotEmpty()) {
                    item {
                        OverduePaymentsCard(
                            overduePayments = state.overduePayments,
                            totalOverdue = state.totalOverdue,
                            onPayAll = { viewModel.payAllOverdue() },
                            onPaySingle = { occurrence -> viewModel.markAsPaid(occurrence) },
                        )
                    }
                }

                // Summary Cards
                item {
                    ScheduledSummaryCards(
                        totalUpcoming = state.totalUpcoming,
                        totalRecurring = state.totalRecurring,
                        nextPaymentDays = state.nextPaymentDays,
                    )
                }

                // Monthly Fixed Expenses Card (Budget Integration)
                item {
                    MonthlyFixedExpensesCard(
                        monthlyFixedExpenses = state.monthlyFixedExpenses,
                        monthlyBudget = state.monthlyBudget,
                    )
                }

                // Upcoming Payments
                item {
                    SectionTitle(
                        title = stringResource(R.string.upcoming_payments),
                        subtitle = stringResource(R.string.next_7_days),
                    )
                }

                if (state.upcomingPayments.isEmpty()) {
                    item {
                        EmptyStateCard(
                            icon = Icons.Outlined.EventAvailable,
                            message = stringResource(R.string.no_upcoming_payments),
                        )
                    }
                } else {
                    items(state.upcomingPayments) { occurrence ->
                        ScheduledPaymentItem(
                            occurrence = occurrence,
                            onPayNow = { viewModel.markAsPaid(it) },
                            onDelete = { viewModel.deleteScheduled(it.payment) },
                        )
                    }
                }

                // Recurring Incomes
                item {
                    SectionTitle(
                        title = stringResource(R.string.recurring_incomes),
                        subtitle = stringResource(R.string.your_regular_income_sources),
                    )
                }

                if (state.recurringIncomes.isEmpty()) {
                    item {
                        EmptyStateCard(
                            icon = Icons.AutoMirrored.Outlined.TrendingUp,
                            message = stringResource(R.string.no_recurring_income_added),
                        )
                    }
                } else {
                    items(state.recurringIncomes) { income ->
                        RecurringItem(
                            item = income,
                            isIncome = true,
                            onDelete = { viewModel.deleteScheduled(it) },
                            onAddRecurringRule = {
                                selectedScheduledPaymentId = it.id
                                showRecurringDialog = true
                            },
                        )
                    }
                }

                // Recurring Expenses
                item {
                    SectionTitle(
                        title = stringResource(R.string.recurring_expenses),
                        subtitle = stringResource(R.string.subscriptions_and_regular_payments),
                    )
                }

                if (state.recurringExpenses.isEmpty()) {
                    item {
                        EmptyStateCard(
                            icon = Icons.AutoMirrored.Outlined.TrendingDown,
                            message = stringResource(R.string.no_recurring_expense_added),
                        )
                    }
                } else {
                    items(state.recurringExpenses) { expense ->
                        RecurringItem(
                            item = expense,
                            isIncome = false,
                            onDelete = { viewModel.deleteScheduled(it) },
                            onAddRecurringRule = {
                                selectedScheduledPaymentId = it.id
                                showRecurringDialog = true
                            },
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }

    // ModalBottomSheet
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            dragHandle = { BottomSheetDefaults.DragHandle(color = MaterialTheme.colorScheme.outline) },
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp)
                        .navigationBarsPadding()
                        .imePadding(),
            ) {
                AddScheduledForm(
                    onSaveClick = { scheduled ->
                        viewModel.addScheduled(scheduled)
                        scope.launch {
                            sheetState.hide()
                            showBottomSheet = false
                        }
                    },
                )
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }

    // Recurring Rule Dialog
    if (showRecurringDialog && selectedScheduledPaymentId != null) {
        RecurringRuleDialog(
            onDismiss = {
                showRecurringDialog = false
                selectedScheduledPaymentId = null
            },
            onConfirm = { recurrenceType, interval, dayOfMonth, daysOfWeek, endDate, maxOccurrences ->
                viewModel.addRecurringRule(
                    scheduledPaymentId = selectedScheduledPaymentId!!,
                    recurrenceType = recurrenceType,
                    interval = interval,
                    dayOfMonth = dayOfMonth,
                    daysOfWeek = daysOfWeek,
                    endDate = endDate,
                    maxOccurrences = maxOccurrences,
                )
                showRecurringDialog = false
                selectedScheduledPaymentId = null
            },
        )
    }
}
