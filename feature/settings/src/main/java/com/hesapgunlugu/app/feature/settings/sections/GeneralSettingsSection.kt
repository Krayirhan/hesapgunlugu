package com.hesapgunlugu.app.feature.settings.sections

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.CurrencyExchange
import androidx.compose.material.icons.outlined.Language
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hesapgunlugu.app.core.domain.model.UserSettings
import com.hesapgunlugu.app.core.ui.theme.IncomeGreen
import com.hesapgunlugu.app.core.ui.theme.PrimaryBlue
import com.hesapgunlugu.app.feature.settings.R
import com.hesapgunlugu.app.feature.settings.components.SettingsOptionCard
import com.hesapgunlugu.app.feature.settings.components.SettingsSectionHeader

@Composable
fun GeneralSettingsSection(
    settingsState: UserSettings,
    isDarkTheme: Boolean,
    currentLanguageLabel: String,
    onLimitClick: () -> Unit,
    onCurrencyClick: () -> Unit,
    onLanguageClick: () -> Unit,
    onCategoryClick: () -> Unit,
    onThemeChange: (Boolean) -> Unit,
    notificationsEnabled: Boolean,
    onNotificationsToggle: (Boolean) -> Unit,
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
        icon = Icons.Outlined.Language,
        iconColor = PrimaryBlue,
        title = stringResource(R.string.language),
        subtitle = currentLanguageLabel,
        isDark = isDarkTheme,
        onClick = onLanguageClick,
    )

    Spacer(modifier = Modifier.height(8.dp))

    SettingsOptionCard(
        icon = Icons.Outlined.Category,
        iconColor = androidx.compose.material3.MaterialTheme.colorScheme.tertiary,
        title = stringResource(R.string.category_management),
        subtitle = stringResource(R.string.custom_categories_add_edit),
        isDark = isDarkTheme,
        onClick = onCategoryClick,
    )

    Spacer(modifier = Modifier.height(28.dp))

    ThemeSection(
        isDarkTheme = isDarkTheme,
        onThemeChange = onThemeChange,
    )

    Spacer(modifier = Modifier.height(28.dp))

    NotificationSection(
        isDarkTheme = isDarkTheme,
        isNotificationsEnabled = notificationsEnabled,
        onToggle = onNotificationsToggle,
    )
}
