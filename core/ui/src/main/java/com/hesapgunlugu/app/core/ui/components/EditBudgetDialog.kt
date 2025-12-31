package com.hesapgunlugu.app.core.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.hesapgunlugu.app.core.ui.R
import com.hesapgunlugu.app.core.ui.theme.PrimaryBlue

/**
 * Edit Budget Dialog Component
 */
@Composable
fun EditBudgetDialog(
    category: String,
    currentLimit: Double,
    onDismiss: () -> Unit,
    onSave: (Double) -> Unit,
) {
    var limitText by remember { mutableStateOf(currentLimit.toInt().toString()) }
    val isValid = limitText.toDoubleOrNull()?.let { it > 0 } ?: false

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = stringResource(R.string.edit_budget),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Text(
                    text = stringResource(R.string.category_with_value, category),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                OutlinedTextField(
                    value = limitText,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() }) {
                            limitText = input
                        }
                    },
                    label = { Text(stringResource(R.string.limit_amount)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    trailingIcon = {
                        Text(
                            stringResource(R.string.currency_symbol),
                            modifier = Modifier.padding(end = 12.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(stringResource(R.string.cancel))
                    }

                    Button(
                        onClick = {
                            limitText.toDoubleOrNull()?.let { limit ->
                                onSave(limit)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = isValid,
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = PrimaryBlue,
                            ),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text(stringResource(R.string.save))
                    }
                }
            }
        }
    }
}
