package com.hesapgunlugu.app.feature.settings.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hesapgunlugu.app.core.ui.theme.*
import com.hesapgunlugu.app.feature.settings.R
import com.hesapgunlugu.app.feature.settings.components.SettingsSectionHeader

/**
 * Veri yönetimi bölümü bileşeni.
 * Single Responsibility Principle uygulanarak SettingsScreen'den ayrıldı.
 */

@Composable
fun DataManagementSection(
    isDarkTheme: Boolean,
    isLoading: Boolean,
    onExportClick: () -> Unit,
    onImportClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    SettingsSectionHeader(title = stringResource(R.string.data_management), isDark = isDarkTheme)

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
            // Dışa Aktar
            DataManagementItem(
                icon = Icons.Outlined.Upload,
                iconColor = IncomeGreen,
                title = stringResource(R.string.export_data),
                subtitle = stringResource(R.string.export_data_subtitle),
                onClick = onExportClick,
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                modifier = Modifier.padding(horizontal = 16.dp),
            )

            // İçe Aktar
            DataManagementItem(
                icon = Icons.Outlined.Download,
                iconColor = PrimaryBlue,
                title = stringResource(R.string.import_data),
                subtitle = stringResource(R.string.import_data_subtitle),
                onClick = onImportClick,
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                modifier = Modifier.padding(horizontal = 16.dp),
            )

            // Verileri Temizle
            DataManagementItem(
                icon = Icons.Outlined.DeleteForever,
                iconColor = ExpenseRed,
                title = stringResource(R.string.clear_data),
                subtitle = stringResource(R.string.clear_data_subtitle),
                onClick = onDeleteClick,
            )
        }
    }

    // Loading indicator
    if (isLoading) {
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth(),
            color = PrimaryBlue,
        )
    }
}

@Composable
private fun DataManagementItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: androidx.compose.ui.graphics.Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(20.dp),
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                subtitle,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Icon(
            Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(20.dp),
        )
    }
}
