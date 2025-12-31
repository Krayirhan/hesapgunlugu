package com.hesapgunlugu.app.feature.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hesapgunlugu.app.core.ui.theme.ExpenseRed
import com.hesapgunlugu.app.core.ui.theme.HesapGunluguTheme
import com.hesapgunlugu.app.core.ui.theme.IncomeGreen
import com.hesapgunlugu.app.core.ui.theme.PrimaryBlue
import com.hesapgunlugu.app.core.ui.theme.WarningOrange
import com.hesapgunlugu.app.feature.settings.R

/**
 * Budget Alert Threshold Dialog
 * Customize budget alert threshold.
 */
@Composable
fun BudgetAlertThresholdDialog(
    currentThreshold: Int,
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit,
) {
    var selectedThreshold by remember { mutableFloatStateOf(currentThreshold.toFloat()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(
                text = stringResource(R.string.budget_alert_threshold),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.budget_alert_question),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = stringResource(R.string.percentage_value, selectedThreshold.toInt()),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color =
                        when {
                            selectedThreshold >= 90 -> ExpenseRed
                            selectedThreshold >= 75 -> WarningOrange
                            else -> IncomeGreen
                        },
                )

                Spacer(modifier = Modifier.height(16.dp))

                Slider(
                    value = selectedThreshold,
                    onValueChange = { selectedThreshold = it },
                    valueRange = 50f..100f,
                    steps = 9,
                    colors =
                        SliderDefaults.colors(
                            thumbColor = PrimaryBlue,
                            activeTrackColor = PrimaryBlue,
                            inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                        ),
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = stringResource(R.string.percentage_value, 50),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = stringResource(R.string.percentage_value, 100),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.quick_select),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    listOf(60, 70, 80, 90).forEach { threshold ->
                        FilterChip(
                            selected = selectedThreshold.toInt() == threshold,
                            onClick = { selectedThreshold = threshold.toFloat() },
                            label = { Text(stringResource(R.string.percentage_value, threshold)) },
                            colors =
                                FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PrimaryBlue.copy(alpha = 0.2f),
                                    selectedLabelColor = PrimaryBlue,
                                ),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                val description =
                    when {
                        selectedThreshold >= 90 -> stringResource(R.string.budget_alert_desc_critical)
                        selectedThreshold >= 80 -> stringResource(R.string.budget_alert_desc_recommended)
                        selectedThreshold >= 70 -> stringResource(R.string.budget_alert_desc_early)
                        else -> stringResource(R.string.budget_alert_desc_very_early)
                    }

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp),
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(selectedThreshold.toInt()) },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
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

@Preview(showBackground = true)
@Composable
private fun BudgetAlertThresholdDialogPreview() {
    HesapGunluguTheme {
        BudgetAlertThresholdDialog(
            currentThreshold = 80,
            onDismiss = {},
            onSave = {},
        )
    }
}
