package com.hesapgunlugu.app.core.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hesapgunlugu.app.core.ui.R
import com.hesapgunlugu.app.core.ui.theme.*

/**
 * Gelişmiş Gelir/Gider Bar Chart - Custom Canvas Implementation
 */
@Composable
fun AdvancedBarChart(
    incomeData: List<Float>,
    expenseData: List<Float>,
    labels: List<String>,
    modifier: Modifier = Modifier,
) {
    val maxValue = (incomeData + expenseData).maxOrNull() ?: 1f
    val textMeasurer = rememberTextMeasurer()
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.weekly_income_expense),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Canvas(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(200.dp),
            ) {
                val barWidth = size.width / (incomeData.size * 3)
                val chartHeight = size.height - 30.dp.toPx()

                incomeData.forEachIndexed { index, income ->
                    val expense = expenseData.getOrElse(index) { 0f }
                    val x = index * (barWidth * 3) + barWidth / 2

                    // Income bar
                    val incomeHeight = (income / maxValue) * chartHeight
                    drawRoundRect(
                        color = IncomeGreen,
                        topLeft = Offset(x, chartHeight - incomeHeight),
                        size = Size(barWidth, incomeHeight),
                        cornerRadius = CornerRadius(4.dp.toPx()),
                    )

                    // Expense bar
                    val expenseHeight = (expense / maxValue) * chartHeight
                    drawRoundRect(
                        color = ExpenseRed,
                        topLeft = Offset(x + barWidth + 4.dp.toPx(), chartHeight - expenseHeight),
                        size = Size(barWidth, expenseHeight),
                        cornerRadius = CornerRadius(4.dp.toPx()),
                    )

                    // Label
                    if (index < labels.size) {
                        val label = labels[index]
                        val textLayout =
                            textMeasurer.measure(
                                text = label,
                                style = TextStyle(fontSize = 10.sp, color = labelColor),
                            )
                        drawText(
                            textLayoutResult = textLayout,
                            topLeft =
                                Offset(
                                    x + barWidth / 2 - textLayout.size.width / 2,
                                    chartHeight + 8.dp.toPx(),
                                ),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                LegendItem(color = IncomeGreen, label = stringResource(R.string.income))
                Spacer(modifier = Modifier.width(24.dp))
                LegendItem(color = ExpenseRed, label = stringResource(R.string.expense))
            }
        }
    }
}

/**
 * Trend Line Chart - Custom Canvas Implementation
 */
@Composable
fun TrendLineChart(
    data: List<Float>,
    labels: List<String>,
    title: String? = null,
    lineColor: Color = PrimaryBlue,
    modifier: Modifier = Modifier,
) {
    val maxValue = data.maxOrNull() ?: 1f
    val minValue = data.minOrNull() ?: 0f
    val range = maxValue - minValue
    val textMeasurer = rememberTextMeasurer()
    val gridColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant
    val pointInnerColor = MaterialTheme.colorScheme.surface

    val resolvedTitle = title ?: stringResource(R.string.trend_analysis)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = resolvedTitle,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Canvas(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(180.dp),
            ) {
                if (data.isEmpty()) return@Canvas

                val chartHeight = size.height - 30.dp.toPx()
                val stepX = size.width / (data.size - 1).coerceAtLeast(1)

                // Draw grid lines
                for (i in 0..4) {
                    val y = chartHeight * i / 4
                    drawLine(
                        color = gridColor,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1.dp.toPx(),
                    )
                }

                // Draw line path
                val path = Path()
                data.forEachIndexed { index, value ->
                    val x = index * stepX
                    val normalizedValue = if (range > 0) (value - minValue) / range else 0.5f
                    val y = chartHeight - (normalizedValue * chartHeight)

                    if (index == 0) {
                        path.moveTo(x, y)
                    } else {
                        path.lineTo(x, y)
                    }
                }

                drawPath(
                    path = path,
                    color = lineColor,
                    style = Stroke(width = 3.dp.toPx()),
                )

                // Draw points and labels
                data.forEachIndexed { index, value ->
                    val x = index * stepX
                    val normalizedValue = if (range > 0) (value - minValue) / range else 0.5f
                    val y = chartHeight - (normalizedValue * chartHeight)

                    // Point
                    drawCircle(
                        color = lineColor,
                        radius = 5.dp.toPx(),
                        center = Offset(x, y),
                    )
                    drawCircle(
                        color = pointInnerColor,
                        radius = 3.dp.toPx(),
                        center = Offset(x, y),
                    )

                    // Label
                    if (index < labels.size) {
                        val label = labels[index]
                        val textLayout =
                            textMeasurer.measure(
                                text = label,
                                style = TextStyle(fontSize = 10.sp, color = labelColor),
                            )
                        drawText(
                            textLayoutResult = textLayout,
                            topLeft =
                                Offset(
                                    x - textLayout.size.width / 2,
                                    chartHeight + 8.dp.toPx(),
                                ),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendItem(
    color: Color,
    label: String,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            modifier = Modifier.size(12.dp),
            shape = RoundedCornerShape(3.dp),
            color = color,
        ) {}
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

// ==================== PREVIEWS ====================

@Preview(name = "Bar Chart - Light", showBackground = true)
@Composable
private fun AdvancedBarChartPreview() {
    HesapGunluguTheme(darkTheme = false) {
        val weekLabels = stringArrayResource(R.array.weekday_short_labels).toList()
        AdvancedBarChart(
            incomeData = listOf(1000f, 1500f, 800f, 2000f, 1200f, 1800f, 900f),
            expenseData = listOf(800f, 600f, 1200f, 500f, 900f, 1100f, 700f),
            labels = weekLabels,
        )
    }
}

@Preview(name = "Line Chart - Light", showBackground = true)
@Composable
private fun TrendLineChartPreview() {
    HesapGunluguTheme(darkTheme = false) {
        val monthLabels = stringArrayResource(R.array.month_short_labels).toList()
        TrendLineChart(
            data = listOf(5000f, 5500f, 4800f, 6200f, 5800f, 7000f),
            labels = monthLabels,
            title = stringResource(R.string.monthly_balance_trend),
        )
    }
}

@Preview(name = "Bar Chart - Dark", showBackground = true)
@Composable
private fun AdvancedBarChartDarkPreview() {
    HesapGunluguTheme(darkTheme = true) {
        val weekLabels = stringArrayResource(R.array.weekday_short_labels).toList()
        AdvancedBarChart(
            incomeData = listOf(1000f, 1500f, 800f, 2000f, 1200f, 1800f, 900f),
            expenseData = listOf(800f, 600f, 1200f, 500f, 900f, 1100f, 700f),
            labels = weekLabels,
        )
    }
}
