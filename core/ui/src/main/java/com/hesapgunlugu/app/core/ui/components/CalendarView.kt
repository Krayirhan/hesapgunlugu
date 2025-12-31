package com.hesapgunlugu.app.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hesapgunlugu.app.core.ui.R
import com.hesapgunlugu.app.core.ui.theme.*
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

/**
 * Takvim Bileşeni
 */
@Composable
fun CalendarView(
    selectedDate: LocalDate = LocalDate.now(),
    onDateSelected: (LocalDate) -> Unit = {},
    actualIncomeDates: Set<LocalDate> = emptySet(),
    actualExpenseDates: Set<LocalDate> = emptySet(),
    plannedIncomeDates: Set<LocalDate> = emptySet(),
    plannedExpenseDates: Set<LocalDate> = emptySet(),
    modifier: Modifier = Modifier,
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
        ) {
            // Header: Month/Year selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        stringResource(R.string.calendar_previous_month),
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }

                Text(
                    text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        stringResource(R.string.calendar_next_month),
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Day headers
            val weekDays = stringArrayResource(R.array.weekday_short_labels)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                weekDays.forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Calendar grid
            val firstDayOfMonth = currentMonth.atDay(1)
            val lastDayOfMonth = currentMonth.atEndOfMonth()
            val daysInMonth = currentMonth.lengthOfMonth()
            val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value // Monday = 1

            val days = mutableListOf<LocalDate?>()

            // Add empty cells for days before month starts
            repeat(firstDayOfWeek - 1) {
                days.add(null)
            }

            // Add all days of the month
            for (day in 1..daysInMonth) {
                days.add(currentMonth.atDay(day))
            }

            // Grid of days
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.heightIn(max = 300.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                items(days) { date ->
                    CalendarDay(
                        date = date,
                        isSelected = date == selectedDate,
                        actualIncome = date?.let { actualIncomeDates.contains(it) } ?: false,
                        actualExpense = date?.let { actualExpenseDates.contains(it) } ?: false,
                        plannedIncome = date?.let { plannedIncomeDates.contains(it) } ?: false,
                        plannedExpense = date?.let { plannedExpenseDates.contains(it) } ?: false,
                        onDateClick = { date?.let { onDateSelected(it) } },
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarDay(
    date: LocalDate?,
    isSelected: Boolean,
    actualIncome: Boolean,
    actualExpense: Boolean,
    plannedIncome: Boolean,
    plannedExpense: Boolean,
    onDateClick: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .aspectRatio(1f)
                .clip(CircleShape)
                .background(
                    when {
                        isSelected -> MaterialTheme.colorScheme.primary
                        else -> Color.Transparent
                    },
                )
                .clickable(enabled = date != null) { onDateClick() },
        contentAlignment = Alignment.Center,
    ) {
        if (date != null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = date.dayOfMonth.toString(),
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color =
                        when {
                            isSelected -> MaterialTheme.colorScheme.onPrimary
                            date == LocalDate.now() -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.onSurface
                        },
                )

                if (!isSelected) {
                    val dots =
                        buildList {
                            if (plannedIncome) add(IncomeGreen.copy(alpha = 0.5f))
                            if (plannedExpense) add(ExpenseRed.copy(alpha = 0.5f))
                            if (actualIncome) add(IncomeGreen)
                            if (actualExpense) add(ExpenseRed)
                        }
                    if (dots.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            dots.take(3).forEach { color ->
                                Box(
                                    modifier =
                                        Modifier
                                            .size(4.dp)
                                            .clip(CircleShape)
                                            .background(color),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
