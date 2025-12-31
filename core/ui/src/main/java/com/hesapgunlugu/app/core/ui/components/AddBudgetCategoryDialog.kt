package com.hesapgunlugu.app.core.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.hesapgunlugu.app.core.ui.R
import com.hesapgunlugu.app.core.ui.theme.PrimaryBlue

/**
 * Add Budget Category Dialog Component
 */
@Composable
fun AddBudgetCategoryDialog(
    onDismiss: () -> Unit,
    onSave: (String, Double) -> Unit,
) {
    var selectedCategory by remember { mutableStateOf("") }
    var limitText by remember { mutableStateOf("") }

    val expenseCategories =
        listOf(
            stringResource(R.string.category_market),
            stringResource(R.string.category_bills),
            stringResource(R.string.category_transport),
            stringResource(R.string.category_food),
            stringResource(R.string.category_rent),
            stringResource(R.string.category_entertainment),
            stringResource(R.string.category_health),
            stringResource(R.string.category_clothing),
        )

    val isValid =
        selectedCategory.isNotBlank() &&
            (limitText.toDoubleOrNull()?.let { it > 0 } ?: false)

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
                    text = stringResource(R.string.add_category_budget),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Text(
                    text = stringResource(R.string.select_category),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(expenseCategories) { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(category) },
                            colors =
                                FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PrimaryBlue,
                                    selectedLabelColor = Color.White,
                                ),
                        )
                    }
                }

                OutlinedTextField(
                    value = limitText,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() }) {
                            limitText = input
                        }
                    },
                    label = { Text(stringResource(R.string.monthly_limit)) },
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
                                onSave(selectedCategory, limit)
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
