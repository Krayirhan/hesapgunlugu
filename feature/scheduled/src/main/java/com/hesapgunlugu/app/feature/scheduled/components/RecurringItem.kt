package com.hesapgunlugu.app.feature.scheduled.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hesapgunlugu.app.core.domain.model.ScheduledPayment
import com.hesapgunlugu.app.core.domain.schedule.ScheduleFrequency
import com.hesapgunlugu.app.core.domain.schedule.parseScheduleFrequency
import com.hesapgunlugu.app.core.ui.theme.IncomeGreen
import com.hesapgunlugu.app.core.ui.theme.PrimaryBlue
import com.hesapgunlugu.app.feature.scheduled.R
import java.text.NumberFormat
import java.util.Locale

@Composable
fun RecurringItem(
    item: ScheduledPayment,
    isIncome: Boolean,
    onDelete: (ScheduledPayment) -> Unit,
    onAddRecurringRule: ((ScheduledPayment) -> Unit)? = null,
) {
    val formatter = NumberFormat.getCurrencyInstance(Locale.getDefault())
    val frequencyLabel =
        when (parseScheduleFrequency(item.frequency)) {
            ScheduleFrequency.WEEKLY -> stringResource(R.string.weekly_label)
            ScheduleFrequency.YEARLY -> stringResource(R.string.yearly_label)
            ScheduleFrequency.DAILY -> stringResource(R.string.daily)
            ScheduleFrequency.MONTHLY -> stringResource(R.string.monthly_label)
        }

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isIncome) {
                                IncomeGreen.copy(alpha = 0.25f)
                            } else {
                                PrimaryBlue.copy(alpha = 0.25f)
                            },
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = item.emoji, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = frequencyLabel,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Text(
                text = formatter.format(item.amount),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isIncome) IncomeGreen else MaterialTheme.colorScheme.onSurface,
            )

            // Recurring rule button (if callback provided)
            if (onAddRecurringRule != null) {
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = { onAddRecurringRule(item) },
                    modifier = Modifier.size(36.dp),
                ) {
                    Icon(
                        Icons.Default.Repeat,
                        contentDescription = stringResource(R.string.recurring_add),
                        tint = PrimaryBlue,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }
    }
}
