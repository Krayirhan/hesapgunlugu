package com.hesapgunlugu.app.feature.settings.sections

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hesapgunlugu.app.core.ui.theme.*
import com.hesapgunlugu.app.feature.settings.R
import com.hesapgunlugu.app.feature.settings.components.SettingsSectionHeader
import com.hesapgunlugu.app.feature.settings.components.ThemeOption

/**
 * Tema ve görünüm ayarları bölümü bileşeni.
 * Single Responsibility Principle uygulanarak SettingsScreen'den ayrıldı.
 */

@Composable
fun ThemeSection(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
) {
    SettingsSectionHeader(title = stringResource(R.string.appearance_theme), isDark = isDarkTheme)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(if (isDarkTheme) 0.dp else 1.dp),
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            ThemeOption(
                icon = Icons.Outlined.LightMode,
                title = stringResource(R.string.light_theme),
                subtitle = stringResource(R.string.light_theme_subtitle),
                isSelected = !isDarkTheme,
                isDark = isDarkTheme,
                onClick = { onThemeChange(false) },
            )
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            ThemeOption(
                icon = Icons.Outlined.DarkMode,
                title = stringResource(R.string.dark_theme),
                subtitle = stringResource(R.string.dark_theme_subtitle),
                isSelected = isDarkTheme,
                isDark = isDarkTheme,
                onClick = { onThemeChange(true) },
            )
        }
    }
}

@Composable
fun NotificationSection(
    isDarkTheme: Boolean,
    isNotificationsEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
) {
    SettingsSectionHeader(title = "Bildirimler", isDark = isDarkTheme)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(if (isDarkTheme) 0.dp else 1.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            com.hesapgunlugu.app.feature.settings.components.SettingsToggleItem(
                icon = Icons.Outlined.Notifications,
                iconColor = PrimaryBlue,
                title = stringResource(R.string.push_notifications),
                subtitle = stringResource(R.string.budget_reminders),
                isChecked = isNotificationsEnabled,
                onCheckedChange = onToggle,
            )
        }
    }
}
