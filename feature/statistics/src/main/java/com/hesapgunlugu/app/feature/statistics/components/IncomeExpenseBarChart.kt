package com.hesapgunlugu.app.feature.statistics.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hesapgunlugu.app.core.ui.theme.ExpenseRed
import com.hesapgunlugu.app.core.ui.theme.IncomeGreen
import com.hesapgunlugu.app.feature.statistics.R

@Composable
fun IncomeExpenseBarChart(
    incomeData: List<Float>,
    expenseData: List<Float>,
    labels: List<String>,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = stringResource(R.string.income_vs_expense),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = stringResource(R.string.weekly_comparison),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Legend
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier =
                            Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(IncomeGreen),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(stringResource(R.string.income), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier =
                            Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(ExpenseRed),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(stringResource(R.string.expense), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bar Chart
            val maxValue = (incomeData + expenseData).maxOrNull() ?: 1f

            Canvas(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(160.dp),
            ) {
                val barWidth = size.width / (labels.size * 3)
                val spacing = barWidth / 2

                labels.forEachIndexed { index, _ ->
                    val x = index * (barWidth * 2 + spacing * 2) + spacing

                    // Gelir bar
                    val incomeHeight = if (maxValue > 0) (incomeData.getOrElse(index) { 0f } / maxValue) * size.height * 0.8f else 0f
                    drawRoundRect(
                        color = IncomeGreen,
                        topLeft = Offset(x, size.height - incomeHeight),
                        size = Size(barWidth, incomeHeight),
                        cornerRadius = CornerRadius(4.dp.toPx()),
                    )

                    // Gider bar
                    val expenseHeight = if (maxValue > 0) (expenseData.getOrElse(index) { 0f } / maxValue) * size.height * 0.8f else 0f
                    drawRoundRect(
                        color = ExpenseRed,
                        topLeft = Offset(x + barWidth + 4.dp.toPx(), size.height - expenseHeight),
                        size = Size(barWidth, expenseHeight),
                        cornerRadius = CornerRadius(4.dp.toPx()),
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
                labels.forEach { label ->
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}
