package com.hesapgunlugu.app.feature.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.hesapgunlugu.app.core.navigation.Route
import com.hesapgunlugu.app.core.navigation.navigateTo
import com.hesapgunlugu.app.core.navigation.navigateToTab
import com.hesapgunlugu.app.core.ui.components.*
import com.hesapgunlugu.app.core.ui.components.FinancialInsight
import com.hesapgunlugu.app.core.ui.components.FinancialInsightCard
import com.hesapgunlugu.app.core.ui.components.InsightType
import kotlinx.coroutines.launch
import com.hesapgunlugu.app.core.ui.R as CoreUiR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    navController: NavController,
    modifier: Modifier = Modifier,
    userName: String? = null,
    isSignedIn: Boolean = false,
    onSignIn: () -> Unit = {},
    onSignOut: () -> Unit = {},
    authError: String? = null,
    isAuthLoading: Boolean = false,
    onAuthErrorShown: () -> Unit = {},
) {
    val homeState by homeViewModel.state.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val displayName =
        userName?.takeIf { it.isNotBlank() }
            ?: stringResource(CoreUiR.string.auth_default_user)

    // Show auth error in snackbar
    LaunchedEffect(authError) {
        authError?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short,
            )
            onAuthErrorShown()
        }
    }

    // Pull-to-refresh state
    val pullToRefreshState = rememberPullToRefreshState()
    var isRefreshing by remember { mutableStateOf(false) }

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val openAddTransactionSheet =
        remember<() -> Unit> {
            { showBottomSheet = true }
        }

    // Sadece önemli uyarılar için insight (bütçe aşımı)
    val budgetAlert =
        remember(homeState.totalExpense) {
            if (homeState.totalExpense > 10000) {
                FinancialInsight(
                    context.getString(R.string.insight_budget_exceeded),
                    context.getString(R.string.insight_budget_exceeded_desc, (homeState.totalExpense - 10000).toInt()),
                    InsightType.ALERT,
                )
            } else {
                null
            }
        }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets(0.dp),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { openAddTransactionSheet() },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp),
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp),
                modifier =
                    Modifier.semantics {
                        contentDescription = context.getString(R.string.add_transaction_fab)
                    },
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(28.dp))
            }
        },
    ) { paddingValues ->

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                homeViewModel.retry()
                scope.launch {
                    kotlinx.coroutines.delay(1000)
                    isRefreshing = false
                }
            },
            state = pullToRefreshState,
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(bottom = paddingValues.calculateBottomPadding()),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState),
            ) {
                // Header - Sade
                HomeHeader(
                    userName = displayName,
                    isSignedIn = isSignedIn,
                    isAuthLoading = isAuthLoading,
                    unreadNotificationCount = homeState.unreadNotificationCount,
                    onSignIn = onSignIn,
                    onSignOut = onSignOut,
                    onNotificationClick = { navController.navigate(Route.Notifications) },
                )

                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    // 💰 Ana Dashboard Kartı
                    AdvancedDashboardCard(
                        balance = homeState.totalBalance,
                        totalIncome = homeState.totalIncome,
                        totalExpense = homeState.totalExpense,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // ⚠️ Uyarı Kartı - SADECE bütçe aşımında göster
                    budgetAlert?.let { alert ->
                        FinancialInsightCard(insights = listOf(alert))
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // 📊 Bu Ay Özeti - Mini istatistik kartı
                    MonthSummaryCard(
                        totalIncome = homeState.totalIncome,
                        totalExpense = homeState.totalExpense,
                        transactionCount = homeState.recentTransactions.size,
                        onStatisticsClick = { navController.navigateToTab(Route.Statistics) },
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 📅 Yaklaşan Ödemeler
                    SectionHeaderWithAction(
                        title = stringResource(R.string.upcoming_payments_title),
                        actionText = stringResource(R.string.manage),
                        onActionClick = { navController.navigateToTab(Route.Scheduled) },
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (homeState.upcomingPayments.isNotEmpty()) {
                        UpcomingPaymentsCard(
                            payments = homeState.upcomingPayments,
                            totalAmount = homeState.upcomingPaymentsTotal,
                            onSeeAllClick = { navController.navigateToTab(Route.Scheduled) },
                        )
                    } else {
                        EmptyUpcomingPaymentsCard(
                            onAddClick = { navController.navigateToTab(Route.Scheduled) },
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 📋 Son İşlemler - 3 adet + "Tümünü Gör"
                    SectionHeaderWithAction(
                        title = stringResource(R.string.recent_transactions),
                        actionText = stringResource(R.string.see_all),
                        onActionClick = { navController.navigateToTab(Route.History) },
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (homeState.recentTransactions.isEmpty()) {
                        EmptyTransactionCard()
                    } else {
                        homeState.recentTransactions.take(3).forEach { transaction ->
                            TransactionItem(
                                transaction = transaction,
                                onDeleteClick = { homeViewModel.deleteTransaction(transaction) },
                                onClick = {
                                    navController.navigateTo(
                                        Route.TransactionDetail(transactionId = transaction.id.toLong()),
                                    )
                                },
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }

        // Bottom Sheet - İşlem Ekleme
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    AddTransactionForm(
                        transactionToEdit = null,
                        onSaveClick = { transaction ->
                            homeViewModel.addTransaction(transaction)
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
    }
}

@Composable
private fun SectionHeaderWithAction(
    title: String,
    actionText: String,
    onActionClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Row(
            modifier =
                Modifier
                    .clickable(onClick = onActionClick)
                    .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = actionText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium,
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun EmptyTransactionCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.empty_transaction_emoji),
                style = MaterialTheme.typography.displaySmall,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.no_transaction_yet),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.add_first_transaction),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun MonthSummaryCard(
    totalIncome: Double,
    totalExpense: Double,
    transactionCount: Int,
    onStatisticsClick: () -> Unit,
) {
    val netAmount = totalIncome - totalExpense
    val isPositive = netAmount >= 0

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onStatisticsClick),
        shape = RoundedCornerShape(16.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = stringResource(R.string.this_month_summary),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text =
                            if (transactionCount > 0) {
                                stringResource(R.string.transaction_count_format, transactionCount)
                            } else {
                                stringResource(R.string.no_transactions_this_month)
                            },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.see_statistics),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.SemiBold,
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

@Composable
private fun EmptyUpcomingPaymentsCard(onAddClick: () -> Unit) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onAddClick),
        shape = RoundedCornerShape(16.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.empty_upcoming_payments_emoji),
                style = MaterialTheme.typography.headlineSmall,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.no_upcoming_payments),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(R.string.add_scheduled_payment_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}
