package com.hesapgunlugu.app.feature.scheduled.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hesapgunlugu.app.core.domain.model.RecurrenceType
import com.hesapgunlugu.app.core.ui.theme.PrimaryBlue
import com.hesapgunlugu.app.feature.scheduled.R
import java.util.Locale

/**
 * Create recurring payment rule dialog
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurringRuleDialog(
    onDismiss: () -> Unit,
    onConfirm: (RecurrenceType, Int, Int?, List<Int>?, String?, Int?) -> Unit,
) {
    var selectedType by remember { mutableStateOf(RecurrenceType.MONTHLY) }
    var interval by remember { mutableStateOf("1") }
    var dayOfMonth by remember { mutableStateOf("1") }
    var maxOccurrences by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.recurring_rule_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = stringResource(R.string.repeat_period),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    RecurrenceTypeChip(
                        label = stringResource(R.string.daily),
                        icon = Icons.Default.Today,
                        isSelected = selectedType == RecurrenceType.DAILY,
                        onClick = { selectedType = RecurrenceType.DAILY },
                        modifier = Modifier.weight(1f),
                    )
                    RecurrenceTypeChip(
                        label = stringResource(R.string.weekly_label),
                        icon = Icons.Default.DateRange,
                        isSelected = selectedType == RecurrenceType.WEEKLY,
                        onClick = { selectedType = RecurrenceType.WEEKLY },
                        modifier = Modifier.weight(1f),
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    RecurrenceTypeChip(
                        label = stringResource(R.string.monthly_label),
                        icon = Icons.Default.EventRepeat,
                        isSelected = selectedType == RecurrenceType.MONTHLY,
                        onClick = { selectedType = RecurrenceType.MONTHLY },
                        modifier = Modifier.weight(1f),
                    )
                    RecurrenceTypeChip(
                        label = stringResource(R.string.yearly_label),
                        icon = Icons.Default.CalendarToday,
                        isSelected = selectedType == RecurrenceType.YEARLY,
                        onClick = { selectedType = RecurrenceType.YEARLY },
                        modifier = Modifier.weight(1f),
                    )
                }

                OutlinedTextField(
                    value = interval,
                    onValueChange = { if (it.all { char -> char.isDigit() }) interval = it },
                    label = {
                        Text(
                            stringResource(
                                R.string.recurrence_every,
                                selectedTypeLabel(selectedType).lowercase(Locale.getDefault()),
                            ),
                        )
                    },
                    placeholder = { Text(stringResource(R.string.interval_placeholder)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )

                if (selectedType == RecurrenceType.MONTHLY) {
                    OutlinedTextField(
                        value = dayOfMonth,
                        onValueChange = {
                            if (it.all { char -> char.isDigit() } && (it.toIntOrNull() ?: 0) <= 31) {
                                dayOfMonth = it
                            }
                        },
                        label = { Text(stringResource(R.string.day_of_month_label)) },
                        placeholder = { Text(stringResource(R.string.interval_placeholder)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                }

                OutlinedTextField(
                    value = maxOccurrences,
                    onValueChange = { if (it.all { char -> char.isDigit() }) maxOccurrences = it },
                    label = { Text(stringResource(R.string.repeat_count_label)) },
                    placeholder = { Text(stringResource(R.string.unlimited)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = PrimaryBlue.copy(alpha = 0.1f),
                        ),
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(20.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.auto_transaction_info),
                            fontSize = 12.sp,
                            color = PrimaryBlue,
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val intervalValue = interval.toIntOrNull() ?: 1
                    val dayValue =
                        if (selectedType == RecurrenceType.MONTHLY) {
                            dayOfMonth.toIntOrNull()?.coerceIn(1, 31)
                        } else {
                            null
                        }
                    val maxOcc = maxOccurrences.toIntOrNull()

                    onConfirm(selectedType, intervalValue, dayValue, null, endDate.ifBlank { null }, maxOcc)
                },
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue,
                    ),
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
    )
}

@Composable
private fun RecurrenceTypeChip(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) PrimaryBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(label, fontSize = 13.sp)
            }
        },
        modifier = modifier,
        colors =
            FilterChipDefaults.filterChipColors(
                selectedContainerColor = PrimaryBlue.copy(alpha = 0.2f),
                selectedLabelColor = PrimaryBlue,
            ),
        border =
            FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = isSelected,
                borderColor = if (isSelected) PrimaryBlue else MaterialTheme.colorScheme.outline,
                borderWidth = 1.dp,
            ),
    )
}

@Composable
private fun selectedTypeLabel(type: RecurrenceType): String {
    return when (type) {
        RecurrenceType.DAILY -> stringResource(R.string.daily)
        RecurrenceType.WEEKLY -> stringResource(R.string.weekly_label)
        RecurrenceType.MONTHLY -> stringResource(R.string.monthly_label)
        RecurrenceType.YEARLY -> stringResource(R.string.yearly_label)
    }
}
