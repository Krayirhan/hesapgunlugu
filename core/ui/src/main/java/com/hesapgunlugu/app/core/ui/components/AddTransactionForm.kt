package com.hesapgunlugu.app.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hesapgunlugu.app.core.domain.model.Transaction
import com.hesapgunlugu.app.core.domain.model.TransactionType
import com.hesapgunlugu.app.core.ui.R
import com.hesapgunlugu.app.core.ui.theme.ExpenseRed
import com.hesapgunlugu.app.core.ui.theme.IncomeGreen
import com.hesapgunlugu.app.core.ui.theme.PrimaryBlue
import java.text.DateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionForm(
    transactionToEdit: Transaction? = null,
    onSaveClick: (Transaction) -> Unit,
) {
    var title by remember { mutableStateOf(transactionToEdit?.title ?: "") }
    var amountText by remember { mutableStateOf(transactionToEdit?.amount?.toString() ?: "") }
    var selectedType by remember { mutableStateOf(transactionToEdit?.type ?: TransactionType.EXPENSE) }
    var selectedDate by remember { mutableStateOf(transactionToEdit?.date ?: Date()) }

    val otherCategory = stringResource(R.string.category_other)
    val expenseCategories =
        listOf(
            stringResource(R.string.category_market),
            stringResource(R.string.category_bills),
            stringResource(R.string.category_transport),
            stringResource(R.string.category_food),
            stringResource(R.string.category_rent),
            otherCategory,
        )
    val incomeCategories =
        listOf(
            stringResource(R.string.category_salary),
            stringResource(R.string.category_investment),
            stringResource(R.string.category_side_job),
            stringResource(R.string.category_gift),
            stringResource(R.string.category_sales),
            otherCategory,
        )
    val standardCategories = expenseCategories + incomeCategories

    val initialCategory = transactionToEdit?.category ?: ""
    val isStandardCategory = standardCategories.contains(initialCategory)

    var selectedCategoryChip by remember(initialCategory, otherCategory) {
        mutableStateOf(
            if (isStandardCategory) {
                initialCategory
            } else if (initialCategory.isNotEmpty()) {
                otherCategory
            } else {
                ""
            },
        )
    }
    var customCategoryInput by remember(initialCategory, isStandardCategory) {
        mutableStateOf(if (!isStandardCategory) initialCategory else "")
    }

    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormatter =
        remember {
            DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
        }

    val currentCategoryList = if (selectedType == TransactionType.EXPENSE) expenseCategories else incomeCategories

    LaunchedEffect(selectedType) {
        if (transactionToEdit == null || selectedType != transactionToEdit.type) {
            if (selectedCategoryChip != otherCategory) selectedCategoryChip = ""
            customCategoryInput = ""
        }
    }

    val isAmountValid = (amountText.toDoubleOrNull() ?: 0.0) > 0.0
    val isTitleValid = title.isNotBlank()
    val isCategoryValid =
        if (selectedCategoryChip == otherCategory) {
            customCategoryInput.isNotBlank()
        } else {
            selectedCategoryChip.isNotBlank()
        }
    val isFormValid = isAmountValid && isTitleValid && isCategoryValid

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text(
            text =
                if (transactionToEdit != null) {
                    stringResource(R.string.edit_transaction)
                } else {
                    stringResource(R.string.new_transaction)
                },
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                    .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            TransactionTypeToggle(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.income),
                icon = Icons.AutoMirrored.Filled.TrendingUp,
                isSelected = selectedType == TransactionType.INCOME,
                activeColor = IncomeGreen,
                onClick = { selectedType = TransactionType.INCOME },
            )
            TransactionTypeToggle(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.expense),
                icon = Icons.AutoMirrored.Filled.TrendingDown,
                isSelected = selectedType == TransactionType.EXPENSE,
                activeColor = ExpenseRed,
                onClick = { selectedType = TransactionType.EXPENSE },
            )
        }

        OutlinedTextField(
            value = dateFormatter.format(selectedDate),
            onValueChange = {},
            label = { Text(stringResource(R.string.date)) },
            readOnly = true,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
            enabled = false,
            shape = RoundedCornerShape(12.dp),
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = stringResource(R.string.select_date),
                        tint = PrimaryBlue,
                    )
                }
            },
            colors = themedTextFieldColors(),
        )

        Column {
            Text(
                text = stringResource(R.string.category),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(currentCategoryList) { category ->
                    val isSelected = selectedCategoryChip == category
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedCategoryChip = category
                            if (category != otherCategory) customCategoryInput = ""
                        },
                        label = { Text(category) },
                        colors =
                            FilterChipDefaults.filterChipColors(
                                selectedContainerColor = if (selectedType == TransactionType.EXPENSE) ExpenseRed else IncomeGreen,
                                selectedLabelColor = Color.White,
                            ),
                        border =
                            FilterChipDefaults.filterChipBorder(
                                borderColor = MaterialTheme.colorScheme.outline,
                                enabled = true,
                                selected = isSelected,
                            ),
                    )
                }
            }

            AnimatedVisibility(visible = selectedCategoryChip == otherCategory) {
                OutlinedTextField(
                    value = customCategoryInput,
                    onValueChange = { customCategoryInput = it },
                    label = { Text(stringResource(R.string.enter_category_name)) },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = themedTextFieldColors(),
                )
            }
        }

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text(stringResource(R.string.description)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            colors = themedTextFieldColors(),
        )

        OutlinedTextField(
            value = amountText,
            onValueChange = { input ->
                if (input.all { it.isDigit() || it == '.' } && input.count { it == '.' } <= 1) {
                    amountText = input
                }
            },
            label = { Text(stringResource(R.string.amount)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            shape = RoundedCornerShape(12.dp),
            trailingIcon = {
                Text(
                    stringResource(R.string.currency_symbol),
                    modifier = Modifier.padding(end = 12.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            colors = themedTextFieldColors(),
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val parsedAmount = amountText.toDoubleOrNull() ?: 0.0
                val finalCategory = if (selectedCategoryChip == otherCategory) customCategoryInput.trim() else selectedCategoryChip

                onSaveClick(
                    Transaction(
                        id = transactionToEdit?.id ?: 0,
                        title = title.trim(),
                        amount = parsedAmount,
                        type = selectedType,
                        date = selectedDate,
                        category = finalCategory,
                    ),
                )

                if (transactionToEdit == null) {
                    title = ""
                    amountText = ""
                    selectedCategoryChip = ""
                    customCategoryInput = ""
                    selectedDate = Date()
                }
            },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
            enabled = isFormValid,
        ) {
            Icon(Icons.Default.Check, contentDescription = stringResource(R.string.save))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (transactionToEdit != null) stringResource(R.string.update) else stringResource(R.string.save),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
fun TransactionTypeToggle(
    modifier: Modifier = Modifier,
    text: String,
    icon: ImageVector,
    isSelected: Boolean,
    activeColor: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            modifier
                .fillMaxHeight()
                .clip(RoundedCornerShape(10.dp))
                .background(if (isSelected) activeColor else Color.Transparent)
                .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 14.sp,
            )
        }
    }
}

@Composable
fun themedTextFieldColors() =
    OutlinedTextFieldDefaults.colors(
        focusedBorderColor = PrimaryBlue,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        focusedLabelColor = PrimaryBlue,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    )
