package com.hesapgunlugu.app.core.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hesapgunlugu.app.core.ui.R
import com.hesapgunlugu.app.core.ui.theme.ExpenseRed
import com.hesapgunlugu.app.core.ui.theme.IncomeGreen
import java.text.NumberFormat
import java.util.Locale

/**
 * Spending Limit Card Component
 */
@Composable
fun SpendingLimitCard(
    currentExpense: Double,
    limit: Double,
    modifier: Modifier = Modifier,
) {
    val progress = if (limit > 0) (currentExpense / limit).toFloat().coerceIn(0f, 1f) else 0f
    val isOverLimit = currentExpense > limit
    val color =
        when {
            progress > 0.9f -> ExpenseRed
            progress > 0.7f -> MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
            else -> IncomeGreen
        }
    val formatter = NumberFormat.getNumberInstance(Locale.getDefault()).apply { maximumFractionDigits = 0 }
    val currencySymbol = stringResource(R.string.currency_symbol)
    val currentFormatted =
        stringResource(
            R.string.amount_with_currency,
            formatter.format(currentExpense),
            currencySymbol,
        )
    val limitFormatted =
        stringResource(
            R.string.amount_with_currency,
            formatter.format(limit),
            currencySymbol,
        )

    Card(
        modifier = modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(R.string.monthly_spending_title),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = stringResource(R.string.amount_pair, currentFormatted, limitFormatted),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = color,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                color = color,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )

            if (isOverLimit) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.limit_exceeded),
                    fontSize = 12.sp,
                    color = ExpenseRed,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}
