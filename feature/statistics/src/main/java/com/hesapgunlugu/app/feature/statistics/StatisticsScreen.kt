package com.hesapgunlugu.app.feature.statistics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hesapgunlugu.app.core.ui.accessibility.*
import com.hesapgunlugu.app.core.ui.components.*
import com.hesapgunlugu.app.core.ui.theme.CategoryEntertainment
import com.hesapgunlugu.app.core.ui.theme.CategoryFood
import com.hesapgunlugu.app.core.ui.theme.CategoryHealth
import com.hesapgunlugu.app.core.ui.theme.CategoryMarket
import com.hesapgunlugu.app.core.ui.theme.CategoryOther
import com.hesapgunlugu.app.core.ui.theme.CategoryTransport
import com.hesapgunlugu.app.core.ui.theme.ExpenseRed
import com.hesapgunlugu.app.core.ui.theme.IncomeGreen
import com.hesapgunlugu.app.core.ui.theme.PrimaryBlue
import com.hesapgunlugu.app.feature.statistics.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(viewModel: StatisticsViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    val hasData = state.totalIncome > 0 || state.totalExpense > 0

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0.dp),
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(bottom = paddingValues.calculateBottomPadding())
                        .verticalScroll(scrollState)
                        .semantics(mergeDescendants = false) {
                            // Accessibility: Announce screen content
                        },
            ) {
                // Header
                StatisticsHeader(modifier = Modifier.semantics { heading() })

                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Spacer(modifier = Modifier.height(20.dp))

                    // Period Selector
                    PeriodSelector(
                        selectedPeriod = state.selectedPeriod,
                        onPeriodSelected = { viewModel.selectPeriod(it) },
                        modifier = Modifier.accessibleDescription(stringResource(R.string.period_selector, state.selectedPeriod)),
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    if (!hasData) {
                        // Empty State
                        EmptyStatisticsCard()
                    } else {
                        // Para Akışı Kartı
                        CashFlowCard(
                            totalIncome = state.totalIncome,
                            totalExpense = state.totalExpense,
                            previousMonthExpense = state.previousMonthExpense,
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // En Çok Harcanan Kategori
                        if (state.categoryExpenses.isNotEmpty()) {
                            val topCategory = state.categoryExpenses.maxByOrNull { it.amount }
                            if (topCategory != null) {
                                val percentage =
                                    if (state.totalExpense > 0) {
                                        (topCategory.amount / state.totalExpense * 100).toFloat()
                                    } else {
                                        0f
                                    }
                                TopSpendingCategoryCard(
                                    categoryName = topCategory.name,
                                    categoryEmoji = topCategory.emoji,
                                    amount = topCategory.amount,
                                    percentage = percentage,
                                    totalExpense = state.totalExpense,
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }

                        // Income/Expense Summary Cards
                        IncomeExpenseSummaryCards(
                            income = state.totalIncome,
                            expense = state.totalExpense,
                            modifier =
                                Modifier.balanceCardAccessibility(
                                    label = stringResource(R.string.income_expense_summary),
                                    amount = state.totalIncome - state.totalExpense,
                                    isPositive = state.totalIncome > state.totalExpense,
                                ),
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Gelir vs Gider Bar Chart
                        IncomeExpenseBarChart(
                            incomeData = state.weeklyIncome,
                            expenseData = state.weeklyExpense,
                            labels = state.weekLabels,
                            modifier =
                                Modifier.chartAccessibility(
                                    chartType = stringResource(R.string.bar_chart),
                                    summary = stringResource(R.string.income_expense_comparison),
                                ),
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        PlannedActualBarChart(
                            title = stringResource(R.string.planned_vs_actual_expense_title),
                            subtitle = stringResource(R.string.planned_vs_actual_subtitle),
                            plannedLabel = stringResource(R.string.planned_label),
                            actualLabel = stringResource(R.string.actual_label),
                            plannedColor = PrimaryBlue,
                            actualColor = ExpenseRed,
                            plannedData = state.plannedWeeklyExpense,
                            actualData = state.weeklyExpense,
                            labels = state.weekLabels,
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        PlannedActualBarChart(
                            title = stringResource(R.string.planned_vs_actual_income_title),
                            subtitle = stringResource(R.string.planned_vs_actual_subtitle),
                            plannedLabel = stringResource(R.string.planned_label),
                            actualLabel = stringResource(R.string.actual_label),
                            plannedColor = PrimaryBlue,
                            actualColor = IncomeGreen,
                            plannedData = state.plannedWeeklyIncome,
                            actualData = state.weeklyIncome,
                            labels = state.weekLabels,
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Category-Based Pie Chart
                        if (state.categoryExpenses.isNotEmpty()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors =
                                    CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    ),
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = stringResource(R.string.category_distribution),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    PieChart(
                                        data =
                                            state.categoryExpenses.map { category ->
                                                PieChartData(
                                                    label = category.name,
                                                    value = category.amount.toFloat(),
                                                    color =
                                                        when (category.name) {
                                                            stringResource(R.string.category_food) -> CategoryFood
                                                            stringResource(R.string.category_transportation) -> CategoryTransport
                                                            stringResource(R.string.category_shopping) -> CategoryMarket
                                                            stringResource(R.string.category_entertainment) -> CategoryEntertainment
                                                            stringResource(R.string.category_health) -> CategoryHealth
                                                            else -> CategoryOther
                                                        },
                                                )
                                            },
                                        modifier =
                                            Modifier
                                                .fillMaxWidth()
                                                .height(250.dp),
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))
                        }

                        // Kategori Bazlı Harcama
                        if (state.categoryExpenses.isNotEmpty()) {
                            CategoryBreakdownCard(
                                categories = state.categoryExpenses,
                            )

                            Spacer(modifier = Modifier.height(24.dp))
                        }

                        // Trend Analizi
                        TrendAnalysisCard(
                            currentMonth = state.currentMonthExpense,
                            previousMonth = state.previousMonthExpense,
                        )
                    }

                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}
