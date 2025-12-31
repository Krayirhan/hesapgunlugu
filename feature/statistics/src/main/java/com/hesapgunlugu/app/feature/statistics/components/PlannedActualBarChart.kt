package com.hesapgunlugu.app.feature.statistics.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PlannedActualBarChart(
    title: String,
    subtitle: String,
    plannedLabel: String,
    actualLabel: String,
    plannedColor: Color,
    actualColor: Color,
    plannedData: List<Float>,
    actualData: List<Float>,
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
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier =
                            Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(plannedColor),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(plannedLabel, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier =
                            Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(actualColor),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(actualLabel, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val maxValue = (plannedData + actualData).maxOrNull() ?: 1f

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

                    val plannedHeight = if (maxValue > 0) (plannedData.getOrElse(index) { 0f } / maxValue) * size.height * 0.8f else 0f
                    drawRoundRect(
                        color = plannedColor,
                        topLeft = Offset(x, size.height - plannedHeight),
                        size = Size(barWidth, plannedHeight),
                        cornerRadius = CornerRadius(4.dp.toPx()),
                    )

                    val actualHeight = if (maxValue > 0) (actualData.getOrElse(index) { 0f } / maxValue) * size.height * 0.8f else 0f
                    drawRoundRect(
                        color = actualColor,
                        topLeft = Offset(x + barWidth + 4.dp.toPx(), size.height - actualHeight),
                        size = Size(barWidth, actualHeight),
                        cornerRadius = CornerRadius(4.dp.toPx()),
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

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
