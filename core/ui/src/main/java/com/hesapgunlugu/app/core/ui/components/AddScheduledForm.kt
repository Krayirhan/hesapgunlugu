package com.hesapgunlugu.app.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hesapgunlugu.app.core.domain.model.ScheduledPayment
import com.hesapgunlugu.app.core.domain.schedule.ScheduleFrequency
import com.hesapgunlugu.app.core.domain.schedule.parseScheduleFrequency
import com.hesapgunlugu.app.core.ui.R
import com.hesapgunlugu.app.core.ui.theme.ExpenseRed
import com.hesapgunlugu.app.core.ui.theme.IncomeGreen
import com.hesapgunlugu.app.core.ui.theme.PrimaryBlue
import java.util.Calendar
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScheduledForm(onSaveClick: (ScheduledPayment) -> Unit) {
    var title by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var isIncome by remember { mutableStateOf(false) }
    val weeklyLabel = stringResource(R.string.frequency_weekly)
    val monthlyLabel = stringResource(R.string.frequency_monthly)
    val yearlyLabel = stringResource(R.string.frequency_yearly)
    var selectedFrequency by remember(monthlyLabel) { mutableStateOf(monthlyLabel) }
    var selectedDay by remember { mutableStateOf(1) }
    val weekDayLabels = stringArrayResource(R.array.weekday_short_labels)
    var selectedWeekDay by remember { mutableStateOf(currentWeekDayIndex()) }
    var selectedCategoryChip by remember { mutableStateOf("") }
    var customCategoryInput by remember { mutableStateOf("") }

    val frequencies = listOf(weeklyLabel, monthlyLabel, yearlyLabel)
    val paymentDays = listOf(1, 5, 10, 15, 20, 25)
    val otherCategory = stringResource(R.string.category_other)
    val expenseCategories =
        listOf(
            stringResource(R.string.category_bills),
            stringResource(R.string.category_subscription),
            stringResource(R.string.category_rent),
            stringResource(R.string.category_loan),
            stringResource(R.string.category_insurance),
            otherCategory,
        )
    val incomeCategories =
        listOf(
            stringResource(R.string.category_salary),
            stringResource(R.string.category_rent_income),
            stringResource(R.string.category_investment),
            stringResource(R.string.category_side_job),
            otherCategory,
        )
    val currentCategoryList = if (isIncome) incomeCategories else expenseCategories

    LaunchedEffect(isIncome) {
        selectedCategoryChip = ""
        customCategoryInput = ""
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
            text = stringResource(R.string.add_scheduled_transaction),
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
                isSelected = isIncome,
                activeColor = IncomeGreen,
                onClick = { isIncome = true },
            )
            TransactionTypeToggle(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.expense),
                icon = Icons.AutoMirrored.Filled.TrendingDown,
                isSelected = !isIncome,
                activeColor = ExpenseRed,
                onClick = { isIncome = false },
            )
        }

        Column {
            Text(
                text = stringResource(R.string.recurrence_frequency),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                frequencies.forEach { freq ->
                    val isSelected = selectedFrequency == freq
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFrequency = freq },
                        label = { Text(freq) },
                        modifier = Modifier.weight(1f),
                        colors =
                            FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryBlue,
                                selectedLabelColor = Color.White,
                            ),
                    )
                }
            }
        }

        AnimatedVisibility(visible = selectedFrequency == monthlyLabel) {
            Column {
                Text(
                    text = stringResource(R.string.payment_day_of_month),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
                )

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(paymentDays) { day ->
                        val isSelected = selectedDay == day
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedDay = day },
                            label = { Text(stringResource(R.string.day_number_label, day)) },
                            colors =
                                FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PrimaryBlue,
                                    selectedLabelColor = Color.White,
                                ),
                        )
                    }
                }
            }
        }

        AnimatedVisibility(visible = selectedFrequency == weeklyLabel) {
            Column {
                Text(
                    text = stringResource(R.string.payment_day_of_week),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
                )

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(weekDayLabels.size) { index ->
                        val dayLabel = weekDayLabels[index]
                        val dayIndex = index + 1
                        val isSelected = selectedWeekDay == dayIndex
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedWeekDay = dayIndex },
                            label = { Text(dayLabel) },
                            colors =
                                FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PrimaryBlue,
                                    selectedLabelColor = Color.White,
                                ),
                        )
                    }
                }
            }
        }

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
                                selectedContainerColor = if (isIncome) IncomeGreen else ExpenseRed,
                                selectedLabelColor = Color.White,
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
                )
            },
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val parsedAmount = amountText.toDoubleOrNull() ?: 0.0
                val finalCategory =
                    if (selectedCategoryChip == otherCategory) {
                        customCategoryInput.trim()
                    } else {
                        selectedCategoryChip
                    }
                val frequencyCode =
                    frequencyCodeForLabel(
                        selectedFrequency = selectedFrequency,
                        weeklyLabel = weeklyLabel,
                        monthlyLabel = monthlyLabel,
                        yearlyLabel = yearlyLabel,
                    )
                val dueDate =
                    calculateDueDate(
                        frequencyCode = frequencyCode,
                        selectedDayOfMonth = selectedDay,
                        selectedWeekDay = selectedWeekDay,
                    )

                onSaveClick(
                    ScheduledPayment(
                        id = 0,
                        title = title.trim(),
                        amount = parsedAmount,
                        isIncome = isIncome,
                        isRecurring = true,
                        frequency = frequencyCode,
                        dueDate = dueDate,
                        category = finalCategory,
                    ),
                )

                title = ""
                amountText = ""
                selectedCategoryChip = ""
                customCategoryInput = ""
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
                stringResource(R.string.save),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

private fun frequencyCodeForLabel(
    selectedFrequency: String,
    weeklyLabel: String,
    monthlyLabel: String,
    yearlyLabel: String,
): String {
    return when (selectedFrequency) {
        weeklyLabel -> ScheduleFrequency.WEEKLY.name
        yearlyLabel -> ScheduleFrequency.YEARLY.name
        else -> ScheduleFrequency.MONTHLY.name
    }
}

private fun calculateDueDate(
    frequencyCode: String,
    selectedDayOfMonth: Int,
    selectedWeekDay: Int,
): Date {
    val now = Calendar.getInstance()
    val calendar =
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

    return when (parseScheduleFrequency(frequencyCode)) {
        ScheduleFrequency.WEEKLY -> {
            val todayIndex = calendarDayOfWeekToIndex(now.get(Calendar.DAY_OF_WEEK))
            var diff = selectedWeekDay - todayIndex
            if (diff < 0) diff += 7
            calendar.add(Calendar.DAY_OF_MONTH, diff)
            calendar.time
        }
        ScheduleFrequency.YEARLY -> {
            if (calendar.time.before(now.time)) {
                calendar.add(Calendar.YEAR, 1)
            }
            calendar.time
        }
        ScheduleFrequency.DAILY -> {
            calendar.time
        }
        ScheduleFrequency.MONTHLY -> {
            calendar.set(Calendar.DAY_OF_MONTH, selectedDayOfMonth.coerceIn(1, 31))
            if (calendar.time.before(now.time)) {
                calendar.add(Calendar.MONTH, 1)
                calendar.set(
                    Calendar.DAY_OF_MONTH,
                    selectedDayOfMonth.coerceIn(1, calendar.getActualMaximum(Calendar.DAY_OF_MONTH)),
                )
            }
            calendar.time
        }
    }
}

private fun currentWeekDayIndex(): Int {
    val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
    return calendarDayOfWeekToIndex(today)
}

private fun calendarDayOfWeekToIndex(dayOfWeek: Int): Int {
    return when (dayOfWeek) {
        Calendar.MONDAY -> 1
        Calendar.TUESDAY -> 2
        Calendar.WEDNESDAY -> 3
        Calendar.THURSDAY -> 4
        Calendar.FRIDAY -> 5
        Calendar.SATURDAY -> 6
        Calendar.SUNDAY -> 7
        else -> 1
    }
}
