package com.hesapgunlugu.app.feature.history

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hesapgunlugu.app.core.domain.model.Transaction
import com.hesapgunlugu.app.core.ui.components.CalendarView
import com.hesapgunlugu.app.core.ui.components.TransactionItem
import com.hesapgunlugu.app.core.ui.theme.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onBackClick: () -> Unit,
    onTransactionClick: (Long) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedMonth by viewModel.selectedMonth.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    val selectedSort by viewModel.selectedSort.collectAsStateWithLifecycle()

    var isSearchActive by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }
    var showCalendar by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val filterSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val groupedTransactions =
        remember(state.transactions) {
            state.transactions.groupBy { transaction ->
                @Suppress("DEPRECATION")
                getDateLabel(transaction.date, context)
            }
        }

    // Ay baÅŸlÄ±ÄŸÄ±nÄ± hesapla
    val monthPattern = stringResource(R.string.month_year_format)
    val monthTitle =
        remember(selectedMonth, monthPattern) {
            val locale = java.util.Locale.getDefault()
            SimpleDateFormat(monthPattern, locale).format(selectedMonth.time)
        }

    fun openTransactionDetail(transaction: Transaction) {
        onTransactionClick(transaction.id.toLong())
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(
                        start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                        top = 0.dp,
                        end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                        bottom = 0.dp,
                    ),
        ) {
            // --- KURUMSAL HEADER (Tek ParÃ§a TasarÄ±m) ---
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.background,
            ) {
                Column(
                    modifier = Modifier.statusBarsPadding(),
                ) {
                    AnimatedContent(targetState = isSearchActive, label = "HeaderAnim") { active ->
                        if (active) {
                            // ARAMA MODU
                            Row(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                TextField(
                                    value = searchQuery,
                                    onValueChange = { viewModel.onSearchQueryChange(it) },
                                    placeholder = { Text(stringResource(R.string.search_transaction), color = MaterialTheme.colorScheme.onSurfaceVariant) },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    colors =
                                        TextFieldDefaults.colors(
                                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                            focusedIndicatorColor = Color.Transparent,
                                            unfocusedIndicatorColor = Color.Transparent,
                                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                            cursorColor = PrimaryBlue,
                                        ),
                                    shape = RoundedCornerShape(12.dp),
                                    leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = stringResource(R.string.search_desc), tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                                    trailingIcon = {
                                        if (searchQuery.isNotEmpty()) {
                                            IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                                Icon(Icons.Default.Close, contentDescription = stringResource(R.string.clear_search), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        }
                                    },
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                TextButton(onClick = {
                                    isSearchActive = false
                                    viewModel.onSearchQueryChange("")
                                }) {
                                    Text(stringResource(R.string.cancel), color = PrimaryBlue, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        } else {
                            // NORMAL MOD
                            Row(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp, vertical = 20.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        stringResource(R.string.account_movements),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                    )
                                    Text(
                                        stringResource(R.string.all_income_expense),
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                                IconButton(
                                    onClick = { isSearchActive = true },
                                    modifier =
                                        Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.surfaceVariant),
                                ) {
                                    Icon(Icons.Outlined.Search, stringResource(R.string.search), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(
                                    onClick = { showFilterSheet = true },
                                    modifier =
                                        Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (selectedFilter != TransactionFilter.ALL || selectedSort != TransactionSort.DATE_DESC) {
                                                    PrimaryBlue.copy(alpha = 0.3f)
                                                } else {
                                                    MaterialTheme.colorScheme.surfaceVariant
                                                },
                                            ),
                                ) {
                                    Icon(Icons.Outlined.FilterList, stringResource(R.string.filter), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }

            // ðŸ“Š AY Ã–ZETÄ° KARTI
            MonthSummaryCard(
                monthlyIncome = state.monthlyIncome,
                monthlyExpense = state.monthlyExpense,
                transactionCount = state.totalCount,
                monthTitle = monthTitle,
            )

            // AY SEÃ‡Ä°CÄ° - Header dÄ±ÅŸÄ±nda
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                IconButton(
                    onClick = { viewModel.previousMonth() },
                    modifier =
                        Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                ) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = stringResource(R.string.previous_month), tint = MaterialTheme.colorScheme.onSurface)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier =
                        Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { showCalendar = !showCalendar }
                            .padding(vertical = 8.dp),
                ) {
                    Icon(
                        Icons.Outlined.CalendarMonth,
                        contentDescription = stringResource(R.string.calendar),
                        tint = if (showCalendar) PrimaryBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = monthTitle,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = if (showCalendar) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp),
                    )
                }

                IconButton(
                    onClick = { viewModel.nextMonth() },
                    modifier =
                        Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                ) {
                    Icon(Icons.Default.ChevronRight, contentDescription = stringResource(R.string.next_month), tint = MaterialTheme.colorScheme.onSurface)
                }
            }

            // Takvim GÃ¶rÃ¼nÃ¼mÃ¼
            AnimatedVisibility(
                visible = showCalendar,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut(),
            ) {
                Card(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        ),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = stringResource(R.string.calendar_view),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            IconButton(onClick = { showCalendar = false }) {
                                Icon(Icons.Default.Close, stringResource(R.string.close), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        CalendarView(
                            selectedDate = LocalDate.now(),
                            actualIncomeDates = state.actualIncomeDates,
                            actualExpenseDates = state.actualExpenseDates,
                            plannedIncomeDates = state.plannedIncomeDates,
                            plannedExpenseDates = state.plannedExpenseDates,
                            onDateSelected = { /* Handle date selection */ },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }

            // --- LÄ°STE ---
            PullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh = { viewModel.refresh() },
                modifier = Modifier.fillMaxSize(),
            ) {
                if (state.transactions.isEmpty()) {
                    val emptyMessage =
                        if (searchQuery.isNotEmpty()) {
                            stringResource(R.string.not_found, searchQuery)
                        } else {
                            stringResource(R.string.no_transactions_in_month, monthTitle)
                        }
                    EmptyHistoryView(message = emptyMessage)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = paddingValues.calculateBottomPadding() + 24.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        groupedTransactions.forEach { (dateLabel, transactions) ->
                            item {
                                Text(
                                    text = dateLabel,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp, top = 16.dp),
                                )
                            }
                            items(items = transactions, key = { it.id }) { transaction ->
                                val dismissState =
                                    rememberSwipeToDismissBoxState(
                                        confirmValueChange = {
                                            if (it == SwipeToDismissBoxValue.EndToStart) {
                                                viewModel.deleteTransaction(transaction)
                                                true
                                            } else {
                                                false
                                            }
                                        },
                                    )

                                SwipeToDismissBox(
                                    state = dismissState,
                                    backgroundContent = {
                                        val color by animateColorAsState(
                                            if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) ExpenseRed else Color.Transparent,
                                            label = "color",
                                        )
                                        val scale by animateFloatAsState(
                                            if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) 1f else 0.75f,
                                            label = "scale",
                                        )
                                        Box(
                                            Modifier
                                                .fillMaxSize()
                                                .clip(RoundedCornerShape(14.dp))
                                                .background(color),
                                            contentAlignment = Alignment.CenterEnd,
                                        ) {
                                            Icon(
                                                Icons.Default.Delete,
                                                stringResource(R.string.delete),
                                                Modifier
                                                    .padding(end = 20.dp)
                                                    .scale(scale),
                                                tint = Color.White,
                                            )
                                        }
                                    },
                                    content = {
                                        TransactionItem(
                                            transaction = transaction,
                                            onDeleteClick = { viewModel.deleteTransaction(transaction) },
                                            onClick = { openTransactionDetail(transaction) },
                                        )
                                    },
                                    enableDismissFromStartToEnd = false,
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // --- FÄ°LTRE BOTTOM SHEET ---
    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            sheetState = filterSheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .navigationBarsPadding(),
            ) {
                Text(
                    text = stringResource(R.string.filter_by_type),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = selectedFilter == TransactionFilter.ALL,
                        onClick = { viewModel.setFilter(TransactionFilter.ALL) },
                        label = { Text(stringResource(R.string.all_types)) },
                    )
                    FilterChip(
                        selected = selectedFilter == TransactionFilter.INCOME,
                        onClick = { viewModel.setFilter(TransactionFilter.INCOME) },
                        label = { Text(stringResource(R.string.income)) },
                        colors =
                            FilterChipDefaults.filterChipColors(
                                selectedContainerColor = IncomeGreen.copy(alpha = 0.2f),
                            ),
                    )
                    FilterChip(
                        selected = selectedFilter == TransactionFilter.EXPENSE,
                        onClick = { viewModel.setFilter(TransactionFilter.EXPENSE) },
                        label = { Text(stringResource(R.string.expense)) },
                        colors =
                            FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ExpenseRed.copy(alpha = 0.2f),
                            ),
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = stringResource(R.string.sort_by),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    SortOption(
                        title = stringResource(R.string.sort_by_date) + " (" + stringResource(R.string.sort_descending) + ")",
                        selected = selectedSort == TransactionSort.DATE_DESC,
                        onClick = { viewModel.setSort(TransactionSort.DATE_DESC) },
                    )
                    SortOption(
                        title = stringResource(R.string.sort_by_date) + " (" + stringResource(R.string.sort_ascending) + ")",
                        selected = selectedSort == TransactionSort.DATE_ASC,
                        onClick = { viewModel.setSort(TransactionSort.DATE_ASC) },
                    )
                    SortOption(
                        title = stringResource(R.string.sort_by_amount) + " (" + stringResource(R.string.sort_descending) + ")",
                        selected = selectedSort == TransactionSort.AMOUNT_DESC,
                        onClick = { viewModel.setSort(TransactionSort.AMOUNT_DESC) },
                    )
                    SortOption(
                        title = stringResource(R.string.sort_by_amount) + " (" + stringResource(R.string.sort_ascending) + ")",
                        selected = selectedSort == TransactionSort.AMOUNT_ASC,
                        onClick = { viewModel.setSort(TransactionSort.AMOUNT_ASC) },
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        viewModel.setFilter(TransactionFilter.ALL)
                        viewModel.setSort(TransactionSort.DATE_DESC)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text(stringResource(R.string.clear_filters), color = MaterialTheme.colorScheme.onSurface)
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun EmptyHistoryView(message: String = stringResource(R.string.no_transactions_yet)) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.empty_inbox_emoji),
                fontSize = 48.sp,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun SortOption(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable { onClick() }
                .background(if (selected) PrimaryBlue.copy(alpha = 0.1f) else Color.Transparent)
                .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = PrimaryBlue),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

private fun getDateLabel(
    date: Date,
    context: android.content.Context,
): String {
    val locale = context.resources.configuration.locales[0]
    val today = Calendar.getInstance()
    val target = Calendar.getInstance().apply { time = date }
    if (isSameDay(today, target)) return context.getString(R.string.today)

    val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
    if (isSameDay(yesterday, target)) return context.getString(R.string.yesterday)

    val formatter = DateFormat.getDateInstance(DateFormat.MEDIUM, locale)
    return formatter.format(date)
}

private fun isSameDay(
    first: Calendar,
    second: Calendar,
): Boolean {
    return first.get(Calendar.YEAR) == second.get(Calendar.YEAR) &&
        first.get(Calendar.DAY_OF_YEAR) == second.get(Calendar.DAY_OF_YEAR)
}

/**
 * Ay Ã–zeti KartÄ± - Gelir, Gider ve Net durumu gÃ¶sterir
 */
@Composable
private fun MonthSummaryCard(
    monthlyIncome: Double,
    monthlyExpense: Double,
    transactionCount: Int,
    monthTitle: String,
) {
    val netAmount = monthlyIncome - monthlyExpense
    val formatter = java.text.NumberFormat.getCurrencyInstance(java.util.Locale.getDefault())

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
        ) {
            // BaÅŸlÄ±k
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.month_summary),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "$transactionCount iÅŸlem",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Gelir ve Gider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                // Gelir
                Column(horizontalAlignment = Alignment.Start) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier =
                                Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(IncomeGreen),
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.income),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "+${formatter.format(monthlyIncome)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = IncomeGreen,
                    )
                }

                // Gider
                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier =
                                Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(ExpenseRed),
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.expense),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "-${formatter.format(monthlyExpense)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ExpenseRed,
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            Spacer(modifier = Modifier.height(12.dp))

            // Net Durum
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.net_balance),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = if (netAmount >= 0) "+${formatter.format(netAmount)}" else formatter.format(netAmount),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (netAmount >= 0) IncomeGreen else ExpenseRed,
                )
            }
        }
    }
}
