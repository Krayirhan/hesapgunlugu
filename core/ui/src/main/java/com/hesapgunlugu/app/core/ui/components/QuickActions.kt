package com.hesapgunlugu.app.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hesapgunlugu.app.core.ui.R

/**
 * Quick Actions Row Component - Sade Tasarım
 */
@Composable
fun QuickActionsRow(
    onAddTransactionClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {},
    onBudgetClick: () -> Unit = {},
    onStatisticsClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        QuickActionItem(
            icon = Icons.Default.Add,
            label = stringResource(R.string.quick_action_add_transaction),
            onClick = onAddTransactionClick,
            modifier = Modifier.weight(1f),
        )
        QuickActionItem(
            icon = Icons.Outlined.History,
            label = stringResource(R.string.quick_action_history),
            onClick = onHistoryClick,
            modifier = Modifier.weight(1f),
        )
        QuickActionItem(
            icon = Icons.Outlined.CalendarMonth,
            label = stringResource(R.string.quick_action_scheduled),
            onClick = onBudgetClick,
            modifier = Modifier.weight(1f),
        )
        QuickActionItem(
            icon = Icons.Outlined.PieChart,
            label = stringResource(R.string.quick_action_reports),
            onClick = onStatisticsClick,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun QuickActionItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier =
            modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable { onClick() },
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 2,
            )
        }
    }
}
