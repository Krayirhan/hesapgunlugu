package com.hesapgunlugu.app.feature.statistics.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TrendingDown
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hesapgunlugu.app.core.ui.theme.ExpenseRed
import com.hesapgunlugu.app.core.ui.theme.IncomeGreen
import com.hesapgunlugu.app.feature.statistics.R
import java.text.NumberFormat
import java.util.*

/**
 * Para Akışı Kartı
 * Gelir vs Gider karşılaştırması ve net sonuç
 */
@Composable
fun CashFlowCard(
    totalIncome: Double,
    totalExpense: Double,
    previousMonthIncome: Double = 0.0,
    previousMonthExpense: Double = 0.0,
    modifier: Modifier = Modifier,
) {
    val formatter = NumberFormat.getCurrencyInstance(Locale.getDefault())
    val netFlow = totalIncome - totalExpense
    val isPositive = netFlow >= 0

    // Yüzde değişim hesapla
    val previousNet = previousMonthIncome - previousMonthExpense
    val changePercent =
        if (previousNet != 0.0) {
            ((netFlow - previousNet) / kotlin.math.abs(previousNet) * 100).toInt()
        } else {
            0
        }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Text(
                text = stringResource(R.string.cash_flow_title),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Income Bar
            CashFlowBar(
                label = stringResource(R.string.income_label),
                amount = totalIncome,
                maxAmount = maxOf(totalIncome, totalExpense),
                color = IncomeGreen,
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Expense Bar
            CashFlowBar(
                label = stringResource(R.string.expense_label),
                amount = totalExpense,
                maxAmount = maxOf(totalIncome, totalExpense),
                color = ExpenseRed,
            )

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            Spacer(modifier = Modifier.height(12.dp))

            // Net Flow
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
                                .background(if (isPositive) IncomeGreen.copy(alpha = 0.15f) else ExpenseRed.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = if (isPositive) Icons.AutoMirrored.Outlined.TrendingUp else Icons.AutoMirrored.Outlined.TrendingDown,
                            contentDescription = null,
                            tint = if (isPositive) IncomeGreen else ExpenseRed,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = stringResource(R.string.net_flow_label),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = formatter.format(netFlow),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isPositive) IncomeGreen else ExpenseRed,
                    )
                    if (changePercent != 0) {
                        Text(
                            text = if (changePercent > 0) "+$changePercent%" else "$changePercent%",
                            fontSize = 12.sp,
                            color = if (changePercent > 0) IncomeGreen else ExpenseRed,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CashFlowBar(
    label: String,
    amount: Double,
    maxAmount: Double,
    color: androidx.compose.ui.graphics.Color,
) {
    val formatter = NumberFormat.getCurrencyInstance(Locale.getDefault())
    val progress = if (maxAmount > 0) (amount / maxAmount).toFloat() else 0f

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = label,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = formatter.format(amount),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = color,
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}
