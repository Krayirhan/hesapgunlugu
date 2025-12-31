package com.hesapgunlugu.app.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hesapgunlugu.app.core.ui.R
import com.hesapgunlugu.app.core.ui.theme.ExpenseRed
import com.hesapgunlugu.app.core.ui.theme.PrimaryBlue
import com.hesapgunlugu.app.core.ui.theme.WarningOrange
import java.text.DateFormat
import java.text.NumberFormat
import java.util.*

/**
 * Yaklaşan Ödemeler Önizleme Kartı
 * Anasayfada en yakın 3 ödemeyi gösterir
 */
data class UpcomingPaymentPreview(
    val id: Long,
    val title: String,
    val amount: Double,
    val dueDate: Date,
    val emoji: String = "📄",
    val isOverdue: Boolean = false,
)

@Composable
fun UpcomingPaymentsCard(
    payments: List<UpcomingPaymentPreview>,
    totalAmount: Double,
    onSeeAllClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val formatter = NumberFormat.getCurrencyInstance(Locale.getDefault())

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier =
                            Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(PrimaryBlue.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Outlined.Schedule,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = stringResource(R.string.upcoming_payments_title),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }

                TextButton(
                    onClick = onSeeAllClick,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                ) {
                    Text(
                        text = stringResource(R.string.see_all),
                        fontSize = 12.sp,
                        color = PrimaryBlue,
                    )
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }

            if (payments.isEmpty()) {
                // Empty State
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.no_upcoming_payments_home),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            } else {
                Spacer(modifier = Modifier.height(12.dp))

                // Payment Items
                payments.take(3).forEach { payment ->
                    UpcomingPaymentRow(payment = payment)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Total
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = stringResource(R.string.this_week_total),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = formatter.format(totalAmount),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ExpenseRed,
                    )
                }
            }
        }
    }
}

@Composable
private fun UpcomingPaymentRow(payment: UpcomingPaymentPreview) {
    val formatter = NumberFormat.getCurrencyInstance(Locale.getDefault())
    val dateFormatter = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())

    val statusColor =
        when {
            payment.isOverdue -> ExpenseRed
            else -> {
                val daysUntil = ((payment.dueDate.time - System.currentTimeMillis()) / (1000 * 60 * 60 * 24)).toInt()
                when {
                    daysUntil <= 1 -> ExpenseRed
                    daysUntil <= 3 -> WarningOrange
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            }
        }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Emoji
        Box(
            modifier =
                Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = payment.emoji,
                fontSize = 16.sp,
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Title & Date
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = payment.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text =
                    if (payment.isOverdue) {
                        stringResource(R.string.overdue)
                    } else {
                        dateFormatter.format(payment.dueDate)
                    },
                fontSize = 12.sp,
                color = statusColor,
            )
        }

        // Amount
        Text(
            text = formatter.format(payment.amount),
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = ExpenseRed,
        )
    }
}
