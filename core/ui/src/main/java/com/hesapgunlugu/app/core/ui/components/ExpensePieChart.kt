package com.hesapgunlugu.app.core.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hesapgunlugu.app.core.ui.R
import com.hesapgunlugu.app.core.ui.theme.CategoryPalette

/**
 * Expense Pie Chart Component
 */
@Composable
fun ExpensePieChart(
    categoryExpenses: Map<String, Double>,
    modifier: Modifier = Modifier,
) {
    val total = categoryExpenses.values.sum()
    if (total == 0.0) {
        Box(
            modifier =
                modifier
                    .fillMaxWidth()
                    .height(200.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.no_expenses_yet),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        return
    }

    val colors = CategoryPalette
    val donutColor = MaterialTheme.colorScheme.surface

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Pie Chart
        Canvas(
            modifier =
                Modifier
                    .size(200.dp)
                    .padding(16.dp),
        ) {
            val canvasSize = size.minDimension
            val radius = canvasSize / 2
            val center = Offset(size.width / 2, size.height / 2)

            var startAngle = -90f
            categoryExpenses.entries.forEachIndexed { index, (_, amount) ->
                val sweepAngle = (amount / total * 360).toFloat()
                drawArc(
                    color = colors[index % colors.size],
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2),
                )
                startAngle += sweepAngle
            }

            // Center circle for donut effect
            drawCircle(
                color = donutColor,
                radius = radius * 0.5f,
                center = center,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Legend
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            categoryExpenses.entries.forEachIndexed { index, (category, amount) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Canvas(modifier = Modifier.size(12.dp)) {
                            drawCircle(color = colors[index % colors.size])
                        }
                        Text(
                            text = category,
                            fontSize = 14.sp,
                        )
                    }
                    Text(
                        text = "${(amount / total * 100).toInt()}%",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors[index % colors.size],
                    )
                }
            }
        }
    }
}
