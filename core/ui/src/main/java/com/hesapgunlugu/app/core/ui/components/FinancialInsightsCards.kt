package com.hesapgunlugu.app.core.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingFlat
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hesapgunlugu.app.core.ui.R
import com.hesapgunlugu.app.core.ui.theme.ChartColor3
import com.hesapgunlugu.app.core.ui.theme.PrimaryBlue
import com.hesapgunlugu.app.core.ui.theme.ScoreHigh
import com.hesapgunlugu.app.core.ui.theme.ScoreLow
import com.hesapgunlugu.app.core.ui.theme.WarningOrange
import java.text.NumberFormat
import java.util.Locale

@Composable
fun FinancialHealthScoreCard(
    score: Int,
    modifier: Modifier = Modifier,
) {
    val animatedScore = remember { Animatable(0f) }

    LaunchedEffect(score) {
        animatedScore.animateTo(
            targetValue = score.toFloat(),
            animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
        )
    }

    val (scoreColor, scoreLabel, scoreIcon) =
        when (score) {
            in 80..100 ->
                Triple(
                    ScoreHigh,
                    stringResource(R.string.score_label_excellent),
                    Icons.Default.EmojiEvents,
                )
            in 60..79 ->
                Triple(
                    PrimaryBlue,
                    stringResource(R.string.score_label_good),
                    Icons.Default.ThumbUp,
                )
            in 40..59 ->
                Triple(
                    ChartColor3,
                    stringResource(R.string.score_label_average),
                    Icons.Default.TrendingFlat,
                )
            else ->
                Triple(
                    ScoreLow,
                    stringResource(R.string.score_label_needs_improvement),
                    Icons.Default.TrendingDown,
                )
        }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.financial_health),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            scoreIcon,
                            contentDescription = null,
                            tint = scoreColor,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = scoreLabel,
                            fontSize = 13.sp,
                            color = scoreColor,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(80.dp),
                ) {
                    Canvas(modifier = Modifier.size(80.dp)) {
                        drawArc(
                            color = scoreColor.copy(alpha = 0.1f),
                            startAngle = -90f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round),
                            size = androidx.compose.ui.geometry.Size(size.width, size.height),
                        )

                        drawArc(
                            color = scoreColor,
                            startAngle = -90f,
                            sweepAngle = (animatedScore.value / 100f) * 360f,
                            useCenter = false,
                            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round),
                            size = androidx.compose.ui.geometry.Size(size.width, size.height),
                        )
                    }

                    Text(
                        text = animatedScore.value.toInt().toString(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = scoreColor,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                ScoreMetricChip(stringResource(R.string.balance_short), score >= 60)
                ScoreMetricChip(stringResource(R.string.savings_short), score >= 50)
                ScoreMetricChip(stringResource(R.string.budget_short), score >= 70)
            }
        }
    }
}

@Composable
private fun ScoreMetricChip(
    label: String,
    isGood: Boolean,
) {
    Surface(
        color =
            if (isGood) {
                ScoreHigh.copy(alpha = 0.1f)
            } else {
                ScoreLow.copy(alpha = 0.1f)
            },
        shape = RoundedCornerShape(8.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(if (isGood) ScoreHigh else ScoreLow),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                color = if (isGood) ScoreHigh else ScoreLow,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
fun WeeklySpendingTrendCard(
    weeklyData: List<Double>,
    trend: String,
    modifier: Modifier = Modifier,
) {
    val maxValue = weeklyData.maxOrNull() ?: 1.0
    val dayLabels = stringArrayResource(R.array.weekday_short_labels)

    val (trendIcon, trendColor, trendText) =
        when (trend) {
            "up" ->
                Triple(
                    Icons.Default.TrendingUp,
                    ScoreLow,
                    stringResource(R.string.trend_increasing),
                )
            "down" ->
                Triple(
                    Icons.Default.TrendingDown,
                    ScoreHigh,
                    stringResource(R.string.trend_decreasing),
                )
            else ->
                Triple(
                    Icons.Default.TrendingFlat,
                    MaterialTheme.colorScheme.onSurfaceVariant,
                    stringResource(R.string.trend_stable),
                )
        }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.weekly_trend),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            trendIcon,
                            contentDescription = null,
                            tint = trendColor,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = trendText,
                            fontSize = 13.sp,
                            color = trendColor,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                weeklyData.forEachIndexed { index, value ->
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        val heightFraction = if (maxValue > 0) (value / maxValue).toFloat() else 0f
                        val barHeight = (80.dp * heightFraction.coerceIn(0.1f, 1f))

                        Box(
                            modifier =
                                Modifier
                                    .width(28.dp)
                                    .height(barHeight)
                                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                    .background(
                                        brush =
                                            Brush.verticalGradient(
                                                colors =
                                                    listOf(
                                                        PrimaryBlue.copy(alpha = 0.8f),
                                                        PrimaryBlue.copy(alpha = 0.4f),
                                                    ),
                                            ),
                                    ),
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = dayLabels.getOrNull(index).orEmpty(),
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SavingsRateCard(
    savingsRate: Float,
    income: Double,
    expense: Double,
    modifier: Modifier = Modifier,
) {
    val savings = income - expense
    val formatter = NumberFormat.getNumberInstance(Locale.getDefault()).apply { maximumFractionDigits = 0 }
    val currencySymbol = stringResource(R.string.currency_symbol)
    val savingsFormatted =
        stringResource(
            R.string.amount_with_currency,
            formatter.format(savings),
            currencySymbol,
        )

    val (rateColor, rateEmoji) =
        when {
            savingsRate >= 30 -> ScoreHigh to stringResource(R.string.savings_rate_emoji_excellent)
            savingsRate >= 20 -> PrimaryBlue to stringResource(R.string.savings_rate_emoji_good)
            savingsRate >= 10 -> ChartColor3 to stringResource(R.string.savings_rate_emoji_ok)
            savingsRate > 0 -> WarningOrange to stringResource(R.string.savings_rate_emoji_low)
            else -> ScoreLow to stringResource(R.string.savings_rate_emoji_negative)
        }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.savings_rate),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = rateEmoji,
                            fontSize = 24.sp,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "%${savingsRate.toInt()}",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = rateColor,
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = stringResource(R.string.savings_short),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = savingsFormatted,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (savings >= 0) ScoreHigh else ScoreLow,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = { (savingsRate / 100f).coerceIn(0f, 1f) },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                color = rateColor,
                trackColor = rateColor.copy(alpha = 0.2f),
            )

            Spacer(modifier = Modifier.height(12.dp))

            val recommendation =
                when {
                    savingsRate >= 30 -> stringResource(R.string.savings_rate_reco_excellent)
                    savingsRate >= 20 -> stringResource(R.string.savings_rate_reco_good)
                    savingsRate >= 10 -> stringResource(R.string.savings_rate_reco_ok)
                    savingsRate > 0 -> stringResource(R.string.savings_rate_reco_keep_going)
                    else -> stringResource(R.string.savings_rate_reco_warning)
                }

            Text(
                text = recommendation,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
fun QuickStatsCard(
    topCategory: String,
    avgDailySpending: Double,
    modifier: Modifier = Modifier,
) {
    val formatter = NumberFormat.getNumberInstance(Locale.getDefault()).apply { maximumFractionDigits = 0 }
    val currencySymbol = stringResource(R.string.currency_symbol)
    val averageFormatted =
        stringResource(
            R.string.amount_with_currency,
            formatter.format(avgDailySpending),
            currencySymbol,
        )

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = WarningOrange,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.most),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = topCategory.ifEmpty { "-" },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.daily_avg_short),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = averageFormatted,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}
