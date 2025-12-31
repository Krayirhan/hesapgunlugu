package com.hesapgunlugu.app.feature.scheduled.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hesapgunlugu.app.core.domain.model.ScheduledPayment
import com.hesapgunlugu.app.core.domain.schedule.ScheduleFrequency
import com.hesapgunlugu.app.core.domain.schedule.parseScheduleFrequency
import com.hesapgunlugu.app.core.ui.theme.ExpenseRed
import com.hesapgunlugu.app.core.ui.theme.HesapGunluguTheme
import com.hesapgunlugu.app.core.ui.theme.IncomeGreen
import com.hesapgunlugu.app.core.ui.theme.PrimaryBlue
import com.hesapgunlugu.app.feature.scheduled.R
import java.util.Date
import com.hesapgunlugu.app.core.ui.R as CoreUiR

/**
 * Recurring Transaction Edit Dialog
 * Edit scheduled/recurring transactions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScheduledPaymentDialog(
    payment: ScheduledPayment,
    onDismiss: () -> Unit,
    onSave: (ScheduledPayment) -> Unit,
    onDelete: () -> Unit,
) {
    var title by remember { mutableStateOf(payment.title) }
    var amount by remember { mutableStateOf(payment.amount.toString()) }
    val dailyLabel = stringResource(R.string.daily)
    val weeklyLabel = stringResource(R.string.weekly_label)
    val monthlyLabel = stringResource(R.string.monthly_label)
    val yearlyLabel = stringResource(R.string.yearly_label)
    val initialFrequencyLabel =
        when (parseScheduleFrequency(payment.frequency)) {
            ScheduleFrequency.DAILY -> dailyLabel
            ScheduleFrequency.WEEKLY -> weeklyLabel
            ScheduleFrequency.MONTHLY -> monthlyLabel
            ScheduleFrequency.YEARLY -> yearlyLabel
        }
    var selectedFrequency by remember { mutableStateOf(initialFrequencyLabel) }
    var isIncome by remember { mutableStateOf(payment.isIncome) }
    var selectedCategory by remember { mutableStateOf(payment.category) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val frequencies =
        listOf(
            dailyLabel,
            weeklyLabel,
            monthlyLabel,
            yearlyLabel,
        )
    val categories =
        if (isIncome) {
            listOf(
                stringResource(CoreUiR.string.category_salary),
                stringResource(CoreUiR.string.category_side_job),
                stringResource(CoreUiR.string.category_investment),
                stringResource(CoreUiR.string.category_rent_income),
                stringResource(CoreUiR.string.category_other),
            )
        } else {
            listOf(
                stringResource(CoreUiR.string.category_bills),
                stringResource(CoreUiR.string.category_subscription),
                stringResource(CoreUiR.string.category_rent),
                stringResource(CoreUiR.string.category_loan),
                stringResource(CoreUiR.string.category_insurance),
                stringResource(CoreUiR.string.category_other),
            )
        }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth(0.95f),
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.edit_scheduled_payment_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = stringResource(R.string.close),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    FilterChip(
                        selected = isIncome,
                        onClick = { isIncome = true },
                        label = { Text(stringResource(R.string.income)) },
                        colors =
                            FilterChipDefaults.filterChipColors(
                                selectedContainerColor = IncomeGreen.copy(alpha = 0.2f),
                                selectedLabelColor = IncomeGreen,
                            ),
                        modifier = Modifier.weight(1f),
                    )
                    FilterChip(
                        selected = !isIncome,
                        onClick = { isIncome = false },
                        label = { Text(stringResource(R.string.expense)) },
                        colors =
                            FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ExpenseRed.copy(alpha = 0.2f),
                                selectedLabelColor = ExpenseRed,
                            ),
                        modifier = Modifier.weight(1f),
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.transaction_title)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text(stringResource(R.string.amount_currency)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.recurrence_frequency_title),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    frequencies.forEach { freq ->
                        FilterChip(
                            selected = selectedFrequency == freq,
                            onClick = { selectedFrequency = freq },
                            label = { Text(freq, style = MaterialTheme.typography.bodySmall) },
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(CoreUiR.string.category),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    categories.take(3).forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, style = MaterialTheme.typography.bodySmall) },
                        )
                    }
                }
                if (categories.size > 3) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        categories.drop(3).forEach { cat ->
                            FilterChip(
                                selected = selectedCategory == cat,
                                onClick = { selectedCategory = cat },
                                label = { Text(cat, style = MaterialTheme.typography.bodySmall) },
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    OutlinedButton(
                        onClick = { showDeleteConfirm = true },
                        colors =
                            ButtonDefaults.outlinedButtonColors(
                                contentColor = ExpenseRed,
                            ),
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(stringResource(R.string.delete))
                    }

                    Button(
                        onClick = {
                            val frequencyCode =
                                when (selectedFrequency) {
                                    dailyLabel -> ScheduleFrequency.DAILY.name
                                    weeklyLabel -> ScheduleFrequency.WEEKLY.name
                                    yearlyLabel -> ScheduleFrequency.YEARLY.name
                                    else -> ScheduleFrequency.MONTHLY.name
                                }
                            val updatedPayment =
                                payment.copy(
                                    title = title,
                                    amount = amount.toDoubleOrNull() ?: payment.amount,
                                    frequency = frequencyCode,
                                    isIncome = isIncome,
                                    category = selectedCategory,
                                )
                            onSave(updatedPayment)
                        },
                        enabled = title.isNotBlank() && amount.toDoubleOrNull() != null,
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = PrimaryBlue,
                            ),
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(stringResource(R.string.save))
                    }
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text(stringResource(R.string.delete_transaction_title)) },
            text = { Text(stringResource(R.string.delete_transaction_confirm, title)) },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed),
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }
}

// ==================== PREVIEW ====================

@Preview(showBackground = true)
@Composable
private fun EditScheduledPaymentDialogPreview() {
    HesapGunluguTheme {
        EditScheduledPaymentDialog(
            payment =
                ScheduledPayment(
                    id = 1,
                    title = stringResource(R.string.preview_title_netflix),
                    amount = 99.99,
                    isIncome = false,
                    isRecurring = true,
                    frequency = ScheduleFrequency.MONTHLY.name,
                    dueDate = Date(),
                    emoji = "",
                    isPaid = false,
                    category = stringResource(CoreUiR.string.category_subscription),
                    createdAt = Date(),
                ),
            onDismiss = {},
            onSave = {},
            onDelete = {},
        )
    }
}
