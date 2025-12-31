package com.hesapgunlugu.app.feature.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.hesapgunlugu.app.core.domain.model.TransactionType
import com.hesapgunlugu.app.core.ui.theme.ExpenseRed
import com.hesapgunlugu.app.core.ui.theme.IncomeGreen
import java.text.DateFormat
import java.text.NumberFormat
import java.util.Locale
import com.hesapgunlugu.app.core.ui.R as CoreUiR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailScreen(
    viewModel: TransactionDetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onEditClick: (Long) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val lifecycleOwner = LocalLifecycleOwner.current
    var showDeleteDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.event.collect { event ->
                when (event) {
                    TransactionDetailEvent.Updated -> {
                        snackbarHostState.showSnackbar(
                            message = context.getString(R.string.transaction_updated),
                            duration = SnackbarDuration.Short,
                        )
                    }
                    TransactionDetailEvent.Deleted -> {
                        snackbarHostState.showSnackbar(
                            message = context.getString(R.string.transaction_deleted),
                            duration = SnackbarDuration.Short,
                        )
                        onBackClick()
                    }
                    is TransactionDetailEvent.Error -> {
                        snackbarHostState.showSnackbar(
                            message = context.getString(event.messageRes),
                            duration = SnackbarDuration.Long,
                        )
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.transaction_details)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
                actions = {
                    val transaction = state.transaction
                    if (transaction != null) {
                        IconButton(onClick = { onEditClick(transaction.id.toLong()) }) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = stringResource(R.string.edit_transaction),
                            )
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = stringResource(R.string.delete_transaction),
                            )
                        }
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                    ),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        val transaction = state.transaction
        when {
            state.isLoading -> LoadingState(padding)
            transaction == null -> EmptyState(padding, state.errorRes ?: R.string.transaction_not_found)
            else -> TransactionDetailContent(transaction, padding)
        }
    }

    val transactionForDialog = state.transaction
    if (showDeleteDialog && transactionForDialog != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            title = { Text(stringResource(R.string.delete_transaction)) },
            text = { Text(stringResource(R.string.delete_transaction_confirm)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteTransaction(transactionForDialog)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed),
                ) {
                    Text(stringResource(R.string.delete), color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }
}

@Composable
private fun LoadingState(padding: PaddingValues) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyState(
    padding: PaddingValues,
    messageRes: Int,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = stringResource(messageRes))
    }
}

@Composable
private fun TransactionDetailContent(
    transaction: com.hesapgunlugu.app.core.domain.model.Transaction,
    padding: PaddingValues,
) {
    val isIncome = transaction.type == TransactionType.INCOME
    val amountColor = if (isIncome) IncomeGreen else ExpenseRed
    val context = LocalContext.current
    val locale = context.resources.configuration.locales[0]
    val amountText = formatAmount(transaction.amount, isIncome, locale, context)

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = transaction.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = amountText,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = amountColor,
                )
                Divider()
                DetailRow(
                    label = stringResource(R.string.transaction_type),
                    value = stringResource(if (isIncome) R.string.income else R.string.expense),
                    valueColor = amountColor,
                )
                DetailRow(
                    label = stringResource(R.string.amount),
                    value = amountText,
                    valueColor = amountColor,
                )
                DetailRow(
                    label = stringResource(R.string.category),
                    value = transaction.category,
                )
                DetailRow(
                    label = stringResource(R.string.transaction_date),
                    value = formatDate(transaction.date, locale),
                )
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = valueColor,
            fontWeight = FontWeight.Medium,
        )
    }
}

private fun formatAmount(
    amount: Double,
    isIncome: Boolean,
    locale: Locale,
    context: android.content.Context,
): String {
    val format = NumberFormat.getCurrencyInstance(locale)
    format.maximumFractionDigits = 0
    val sign = if (isIncome) "+" else "-"
    return context.getString(CoreUiR.string.transaction_amount_with_sign, sign, format.format(amount))
}

private fun formatDate(
    date: java.util.Date,
    locale: Locale,
): String {
    val formatter = DateFormat.getDateInstance(DateFormat.MEDIUM, locale)
    return formatter.format(date)
}
