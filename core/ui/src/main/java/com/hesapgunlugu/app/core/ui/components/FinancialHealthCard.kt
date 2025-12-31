package com.hesapgunlugu.app.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.TrendingDown
import androidx.compose.material.icons.outlined.TrendingFlat
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hesapgunlugu.app.core.ui.R
import com.hesapgunlugu.app.core.ui.theme.ExpenseRed
import com.hesapgunlugu.app.core.ui.theme.IncomeGreen
import com.hesapgunlugu.app.core.ui.theme.WarningOrange

/**
 * Finansal Sağlık Skoru Kartı
 * Kullanıcının finansal durumunu tek bakışta gösterir
 */
@Composable
fun FinancialHealthCard(
    score: Int,
    savingsRate: Float,
    monthlyTrend: String,
    modifier: Modifier = Modifier,
) {
    val scoreColor =
        when {
            score >= 80 -> IncomeGreen
            score >= 50 -> WarningOrange
            else -> ExpenseRed
        }

    val scoreMessage =
        when {
            score >= 80 -> stringResource(R.string.health_excellent)
            score >= 60 -> stringResource(R.string.health_good)
            score >= 40 -> stringResource(R.string.health_fair)
            else -> stringResource(R.string.health_needs_attention)
        }

    val trendIcon =
        when (monthlyTrend) {
            "up" -> Icons.Outlined.TrendingUp
            "down" -> Icons.Outlined.TrendingDown
            else -> Icons.Outlined.TrendingFlat
        }

    val trendColor =
        when (monthlyTrend) {
            "up" -> IncomeGreen
            "down" -> ExpenseRed
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
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
            // Score Circle
            Box(
                modifier =
                    Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(scoreColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$score",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = scoreColor,
                    )
                    Text(
                        text = "/100",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.financial_health),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = scoreMessage,
                    fontSize = 12.sp,
                    color = scoreColor,
                )
            }

            // Stats Column
            Column(horizontalAlignment = Alignment.End) {
                // Savings Rate
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.savings_label),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "%${savingsRate.toInt()}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (savingsRate > 0) IncomeGreen else ExpenseRed,
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Trend
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.trend_label),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = trendIcon,
                        contentDescription = null,
                        tint = trendColor,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}
