package com.hesapgunlugu.app.core.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hesapgunlugu.app.core.ui.R
import com.hesapgunlugu.app.core.ui.theme.CategoryEntertainment
import com.hesapgunlugu.app.core.ui.theme.ExpenseRed
import com.hesapgunlugu.app.core.ui.theme.IncomeGreen
import com.hesapgunlugu.app.core.ui.theme.PrimaryBlue
import com.hesapgunlugu.app.core.ui.theme.WarningOrange

/**
 * Financial insight card component.
 */
@Composable
fun FinancialInsightCard(
    insights: List<FinancialInsight>,
    modifier: Modifier = Modifier,
) {
    if (insights.isEmpty()) return

    Card(
        modifier = modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = WarningOrange,
                    modifier = Modifier.padding(end = 8.dp),
                )
                Text(
                    text = stringResource(R.string.financial_insights_title),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            insights.take(3).forEach { insight ->
                InsightItem(insight)
                if (insight != insights.last()) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun InsightItem(insight: FinancialInsight) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(
                    color = insight.type.backgroundColor,
                    shape = RoundedCornerShape(12.dp),
                )
                .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(insight.type.emojiRes),
            fontSize = 20.sp,
            modifier = Modifier.padding(end = 8.dp),
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = insight.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (insight.description.isNotEmpty()) {
                Text(
                    text = insight.description,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

data class FinancialInsight(
    val title: String,
    val description: String = "",
    val type: InsightType = InsightType.INFO,
)

enum class InsightType(
    @StringRes val emojiRes: Int,
    val backgroundColor: Color,
) {
    SUCCESS(R.string.insight_emoji_success, IncomeGreen.copy(alpha = 0.12f)),
    WARNING(R.string.insight_emoji_warning, WarningOrange.copy(alpha = 0.12f)),
    ALERT(R.string.insight_emoji_alert, ExpenseRed.copy(alpha = 0.12f)),
    INFO(R.string.insight_emoji_info, PrimaryBlue.copy(alpha = 0.12f)),
    REMINDER(R.string.insight_emoji_reminder, CategoryEntertainment.copy(alpha = 0.12f)),
}
