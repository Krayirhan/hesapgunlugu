package com.hesapgunlugu.app.feature.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hesapgunlugu.app.core.ui.theme.*
import java.text.DateFormat
import java.util.*

data class NotificationItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val message: String,
    val type: NotificationType,
    val timestamp: Date = Date(),
    val isRead: Boolean = false,
)

enum class NotificationType {
    BUDGET_WARNING,
    PAYMENT_REMINDER,
    PAYMENT_DUE,
    GENERAL,
    SUCCESS,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationCenterScreen(onBackClick: () -> Unit = {}) {
    // Sample notifications - In a real app, this would come from a ViewModel
    val budgetWarning = stringResource(R.string.budget_warning)
    val upcomingPayment = stringResource(R.string.upcoming_payment)
    val paymentCompleted = stringResource(R.string.payment_completed)

    var notifications by remember {
        mutableStateOf(
            listOf(
                NotificationItem(
                    title = budgetWarning,
                    message = "Market kategorisinde bütçenizin %80'ine ulaştınız.",
                    type = NotificationType.BUDGET_WARNING,
                ),
                NotificationItem(
                    title = upcomingPayment,
                    message = "Netflix aboneliğiniz yarın sona eriyor.",
                    type = NotificationType.PAYMENT_REMINDER,
                    timestamp = Date(System.currentTimeMillis() - 3600000),
                ),
                NotificationItem(
                    title = paymentCompleted,
                    message = "Elektrik faturası başarıyla ödendi.",
                    type = NotificationType.SUCCESS,
                    timestamp = Date(System.currentTimeMillis() - 86400000),
                    isRead = true,
                ),
            ),
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(bottom = paddingValues.calculateBottomPadding()),
        ) {
            // Header - Tek Parça Tasarım
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.background,
            ) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back_button),
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }

                    Text(
                        text = stringResource(R.string.notification_center),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f),
                    )

                    if (notifications.isNotEmpty()) {
                        TextButton(onClick = { notifications = emptyList() }) {
                            Text(
                                stringResource(R.string.clear_all),
                                color = PrimaryBlue,
                                fontSize = 13.sp,
                            )
                        }
                    }
                }
            }

            if (notifications.isEmpty()) {
                // Empty State
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box(
                            modifier =
                                Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                Icons.Outlined.Notifications,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(40.dp),
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = stringResource(R.string.no_notifications),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(notifications, key = { it.id }) { notification ->
                        NotificationCard(
                            notification = notification,
                            onDelete = {
                                notifications = notifications.filter { it.id != notification.id }
                            },
                            onMarkRead = {
                                notifications =
                                    notifications.map {
                                        if (it.id == notification.id) it.copy(isRead = true) else it
                                    }
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationCard(
    notification: NotificationItem,
    onDelete: () -> Unit,
    onMarkRead: () -> Unit,
) {
    val (icon, iconColor) =
        when (notification.type) {
            NotificationType.BUDGET_WARNING -> Icons.Outlined.Warning to WarningOrange
            NotificationType.PAYMENT_REMINDER -> Icons.Outlined.Schedule to PrimaryBlue
            NotificationType.PAYMENT_DUE -> Icons.Outlined.Payment to ExpenseRed
            NotificationType.SUCCESS -> Icons.Outlined.CheckCircle to IncomeGreen
            NotificationType.GENERAL -> Icons.Outlined.Notifications to TextSecondaryLight
        }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    if (notification.isRead) {
                        MaterialTheme.colorScheme.surface
                    } else {
                        MaterialTheme.colorScheme.surface
                    },
            ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(if (notification.isRead) 0.dp else 2.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            verticalAlignment = Alignment.Top,
        ) {
            // Icon
            Box(
                modifier =
                    Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp),
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = notification.title,
                        fontSize = 14.sp,
                        fontWeight = if (notification.isRead) FontWeight.Normal else FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )

                    if (!notification.isRead) {
                        Box(
                            modifier =
                                Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryBlue),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.message,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = formatTimeAgo(notification.timestamp),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.outline,
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp),
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete),
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

private fun formatTimeAgo(date: Date): String {
    val now = Date()
    val diff = now.time - date.time

    return when {
        diff < 60000 -> "Az önce"
        diff < 3600000 -> "${diff / 60000} dakika önce"
        diff < 86400000 -> "${diff / 3600000} saat önce"
        diff < 172800000 -> "Dün"
        else -> DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault()).format(date)
    }
}
