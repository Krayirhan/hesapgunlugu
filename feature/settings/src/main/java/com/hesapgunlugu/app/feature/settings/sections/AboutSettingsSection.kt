package com.hesapgunlugu.app.feature.settings.sections

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AboutSettingsSection(
    isDarkTheme: Boolean,
    onNotificationsClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    onAboutClick: () -> Unit,
    onHelpClick: () -> Unit,
) {
    AppInfoSection(
        isDarkTheme = isDarkTheme,
        onNotificationsClick = onNotificationsClick,
        onPrivacyClick = onPrivacyClick,
        onAboutClick = onAboutClick,
        onHelpClick = onHelpClick,
    )

    Spacer(modifier = Modifier.height(100.dp))
}
