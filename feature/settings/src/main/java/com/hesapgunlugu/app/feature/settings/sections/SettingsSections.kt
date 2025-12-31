package com.hesapgunlugu.app.feature.settings.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hesapgunlugu.app.core.ui.theme.*
import com.hesapgunlugu.app.feature.settings.R
import com.hesapgunlugu.app.feature.settings.SettingsState
import com.hesapgunlugu.app.feature.settings.components.SettingsOptionCard
import com.hesapgunlugu.app.feature.settings.components.SettingsSectionHeader

/**
 * Ayarlar ekranı için bölüm bileşenleri.
 * Single Responsibility Principle uygulanarak SettingsScreen'den ayrıldı.
 */

@Composable
fun SettingsHeader(
    userName: String,
    isDarkTheme: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .clickable { onClick() }
                    .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val displayName = userName.ifEmpty { stringResource(R.string.user_placeholder) }
            Box(
                modifier =
                    Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            brush =
                                Brush.linearGradient(
                                    colors =
                                        listOf(
                                            MaterialTheme.colorScheme.primary,
                                            MaterialTheme.colorScheme.secondary,
                                        ),
                                ),
                        ),
                contentAlignment = Alignment.Center,
            ) {
                val initial = displayName.take(1).uppercase()
                Text(
                    text = initial,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = displayName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(R.string.personal_account),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Icon(
                Icons.Outlined.Edit,
                contentDescription = stringResource(R.string.edit_profile),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
fun FinancialManagementSection(
    settingsState: SettingsState,
    isDarkTheme: Boolean,
    onLimitClick: () -> Unit,
    onCurrencyClick: () -> Unit,
    onCategoryClick: () -> Unit,
) {
    SettingsSectionHeader(title = stringResource(R.string.financial_management), isDark = isDarkTheme)

    SettingsOptionCard(
        icon = Icons.Outlined.AccountBalanceWallet,
        iconColor = PrimaryBlue,
        title = stringResource(R.string.monthly_spending_limit),
        subtitle =
            stringResource(
                R.string.current_limit,
                "${settingsState.monthlyLimit.toInt()} ${settingsState.currency}",
            ),
        isDark = isDarkTheme,
        onClick = onLimitClick,
    )

    Spacer(modifier = Modifier.height(8.dp))

    SettingsOptionCard(
        icon = Icons.Outlined.CurrencyExchange,
        iconColor = IncomeGreen,
        title = stringResource(R.string.currency),
        subtitle =
            when (settingsState.currencyCode) {
                "TRY" -> stringResource(R.string.currency_try)
                "USD" -> stringResource(R.string.currency_usd)
                "EUR" -> stringResource(R.string.currency_eur)
                "GBP" -> stringResource(R.string.currency_gbp)
                else -> settingsState.currency
            },
        isDark = isDarkTheme,
        onClick = onCurrencyClick,
    )

    Spacer(modifier = Modifier.height(8.dp))

    SettingsOptionCard(
        icon = Icons.Outlined.Category,
        iconColor = MaterialTheme.colorScheme.tertiary,
        title = stringResource(R.string.category_management),
        subtitle = stringResource(R.string.custom_categories_add_edit),
        isDark = isDarkTheme,
        onClick = onCategoryClick,
    )
}

@Composable
fun AppInfoSection(
    isDarkTheme: Boolean,
    onNotificationsClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    onAboutClick: () -> Unit,
    onHelpClick: () -> Unit,
) {
    SettingsSectionHeader(title = stringResource(R.string.application), isDark = isDarkTheme)

    SettingsOptionCard(
        icon = Icons.Outlined.Notifications,
        iconColor = PrimaryBlue,
        title = stringResource(R.string.notifications),
        subtitle = stringResource(R.string.notification_center),
        isDark = isDarkTheme,
        onClick = onNotificationsClick,
    )

    Spacer(modifier = Modifier.height(8.dp))

    SettingsOptionCard(
        icon = Icons.Outlined.Security,
        iconColor = WarningOrange,
        title = stringResource(R.string.privacy_policy),
        subtitle = stringResource(R.string.gdpr_info),
        isDark = isDarkTheme,
        onClick = onPrivacyClick,
    )

    Spacer(modifier = Modifier.height(8.dp))

    SettingsOptionCard(
        icon = Icons.Outlined.Info,
        iconColor = TextSecondaryLight,
        title = stringResource(R.string.about),
        subtitle = stringResource(R.string.version_info),
        isDark = isDarkTheme,
        onClick = onAboutClick,
    )

    Spacer(modifier = Modifier.height(8.dp))

    SettingsOptionCard(
        icon = Icons.Outlined.QuestionMark,
        iconColor = IncomeGreen,
        title = stringResource(R.string.help_support),
        subtitle = stringResource(R.string.faq_contact),
        isDark = isDarkTheme,
        onClick = onHelpClick,
    )
}
