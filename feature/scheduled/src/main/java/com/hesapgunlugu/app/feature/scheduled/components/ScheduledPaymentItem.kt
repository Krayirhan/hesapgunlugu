package com.hesapgunlugu.app.feature.scheduled.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hesapgunlugu.app.core.domain.schedule.PlannedOccurrence
import com.hesapgunlugu.app.core.ui.theme.ExpenseRed
import com.hesapgunlugu.app.core.ui.theme.IncomeGreen
import com.hesapgunlugu.app.core.ui.theme.PrimaryBlue
import com.hesapgunlugu.app.feature.scheduled.R
import java.text.DateFormat
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ScheduledPaymentItem(
    occurrence: PlannedOccurrence,
    onPayNow: (PlannedOccurrence) -> Unit,
    onDelete: (PlannedOccurrence) -> Unit,
) {
    val formatter = NumberFormat.getCurrencyInstance(Locale.getDefault())
    val dateFormatter = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
    val payment = occurrence.payment

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
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Emoji/Icon
            Box(
                modifier =
                    Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (payment.isIncome) {
                                IncomeGreen.copy(alpha = 0.15f)
                            } else {
                                ExpenseRed.copy(alpha = 0.15f)
                            },
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = payment.emoji, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = payment.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = dateFormatter.format(occurrence.date),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatter.format(payment.amount),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (payment.isIncome) IncomeGreen else ExpenseRed,
                )
                TextButton(
                    onClick = { onPayNow(occurrence) },
                    contentPadding = PaddingValues(4.dp),
                ) {
                    Text(
                        text = stringResource(R.string.mark_as_paid_short),
                        fontSize = 11.sp,
                        color = PrimaryBlue,
                    )
                }
            }
        }
    }
}
