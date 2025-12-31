package com.hesapgunlugu.app.feature.scheduled.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Payment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hesapgunlugu.app.core.domain.schedule.PlannedOccurrence
import com.hesapgunlugu.app.core.ui.theme.ExpenseRed
import com.hesapgunlugu.app.core.ui.theme.PrimaryBlue
import com.hesapgunlugu.app.feature.scheduled.R
import java.text.NumberFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Gecikmiş Ödemeler Kartı
 * Vadesi geçmiş ama ödenmemiş işlemleri gösterir
 */
@Composable
fun OverduePaymentsCard(
    overduePayments: List<PlannedOccurrence>,
    totalOverdue: Double,
    onPayAll: () -> Unit,
    onPaySingle: (PlannedOccurrence) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (overduePayments.isEmpty()) return

    val formatter = NumberFormat.getCurrencyInstance(Locale.getDefault())

    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = ExpenseRed.copy(alpha = 0.1f),
            ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(ExpenseRed.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = ExpenseRed,
                        modifier = Modifier.size(20.dp),
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.overdue_payments),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ExpenseRed,
                    )
                    Text(
                        text =
                            pluralStringResource(
                                R.plurals.overdue_payments_count,
                                overduePayments.size,
                                overduePayments.size,
                            ),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    text = formatter.format(totalOverdue),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ExpenseRed,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = ExpenseRed.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(12.dp))

            // Overdue items list
            overduePayments.take(3).forEach { occurrence ->
                OverduePaymentItem(
                    occurrence = occurrence,
                    onPay = { onPaySingle(occurrence) },
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Show more indicator if there are more items
            if (overduePayments.size > 3) {
                Text(
                    text = stringResource(R.string.and_more_payments, overduePayments.size - 3),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 44.dp),
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Pay All Button
            Button(
                onClick = onPayAll,
                modifier = Modifier.fillMaxWidth(),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = ExpenseRed,
                    ),
                shape = RoundedCornerShape(12.dp),
            ) {
                Icon(
                    Icons.Outlined.Payment,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.pay_all_overdue),
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun OverduePaymentItem(
    occurrence: PlannedOccurrence,
    onPay: () -> Unit,
) {
    val formatter = NumberFormat.getCurrencyInstance(Locale.getDefault())
    val daysOverdue = getDaysOverdue(occurrence)
    val payment = occurrence.payment

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Emoji
        Box(
            modifier =
                Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = payment.emoji,
                fontSize = 14.sp,
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = payment.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text =
                    pluralStringResource(
                        R.plurals.days_overdue,
                        daysOverdue,
                        daysOverdue,
                    ),
                fontSize = 12.sp,
                color = ExpenseRed,
            )
        }

        Text(
            text = formatter.format(payment.amount),
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = ExpenseRed,
        )

        Spacer(modifier = Modifier.width(8.dp))

        TextButton(
            onClick = onPay,
            colors =
                ButtonDefaults.textButtonColors(
                    contentColor = PrimaryBlue,
                ),
        ) {
            Text(
                text = stringResource(R.string.pay_now),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

private fun getDaysOverdue(occurrence: PlannedOccurrence): Int {
    val now = System.currentTimeMillis()
    val diff = now - occurrence.date.time
    return TimeUnit.MILLISECONDS.toDays(diff).toInt().coerceAtLeast(1)
}
