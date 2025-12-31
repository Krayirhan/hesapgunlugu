package com.hesapgunlugu.app.core.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hesapgunlugu.app.core.ui.R

/**
 * Enhanced empty state component
 */
@Composable
fun EmptyStateView(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // Icon with background
        Surface(
            modifier = Modifier.size(80.dp),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize(),
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Title
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Description
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
        )

        // Action button (optional)
        if (actionText != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onActionClick,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                    ),
            ) {
                Text(actionText)
            }
        }
    }
}

/**
 * No transactions empty state
 */
@Composable
fun NoTransactionsEmptyState(
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    EmptyStateView(
        icon = Icons.Outlined.Inbox,
        title = stringResource(R.string.no_transactions_yet),
        description = stringResource(R.string.empty_transactions_description),
        actionText = stringResource(R.string.add_transaction_action),
        onActionClick = onAddClick,
        modifier = modifier,
    )
}

/**
 * No scheduled payments empty state
 */
@Composable
fun NoScheduledPaymentsEmptyState(
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    EmptyStateView(
        icon = Icons.Outlined.EventAvailable,
        title = stringResource(R.string.no_scheduled_payments),
        description = stringResource(R.string.empty_scheduled_description),
        actionText = stringResource(R.string.add_scheduled_payment_action),
        onActionClick = onAddClick,
        modifier = modifier,
    )
}

/**
 * No notifications empty state
 */
@Composable
fun NoNotificationsEmptyState(modifier: Modifier = Modifier) {
    EmptyStateView(
        icon = Icons.Outlined.NotificationsNone,
        title = stringResource(R.string.empty_notifications_title),
        description = stringResource(R.string.empty_notifications_description),
        modifier = modifier,
    )
}

/**
 * No search results empty state
 */
@Composable
fun NoSearchResultsEmptyState(
    searchQuery: String,
    modifier: Modifier = Modifier,
) {
    EmptyStateView(
        icon = Icons.Outlined.SearchOff,
        title = stringResource(R.string.no_search_results),
        description = stringResource(R.string.no_search_results_description, searchQuery),
        modifier = modifier,
    )
}

/**
 * No statistics empty state
 */
@Composable
fun NoStatisticsEmptyState(modifier: Modifier = Modifier) {
    EmptyStateView(
        icon = Icons.Outlined.BarChart,
        title = stringResource(R.string.no_statistics),
        description = stringResource(R.string.no_statistics_description),
        modifier = modifier,
    )
}

/**
 * Error state
 */
@Composable
fun ErrorStateView(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    EmptyStateView(
        icon = Icons.Outlined.ErrorOutline,
        title = stringResource(R.string.error_occurred),
        description = message,
        actionText = stringResource(R.string.action_retry),
        onActionClick = onRetry,
        modifier = modifier,
    )
}
