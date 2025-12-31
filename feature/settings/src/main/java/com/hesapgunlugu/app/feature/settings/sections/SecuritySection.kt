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
import com.hesapgunlugu.app.core.security.SecurityState
import com.hesapgunlugu.app.core.ui.theme.*
import com.hesapgunlugu.app.feature.settings.R
import com.hesapgunlugu.app.feature.settings.components.SettingsSectionHeader

/**
 * Güvenlik ayarları bölümü bileşeni.
 * Single Responsibility Principle uygulanarak SettingsScreen'den ayrıldı.
 */

@Composable
fun SecuritySection(
    securityState: SecurityState,
    isDarkTheme: Boolean,
    canUseBiometric: Boolean,
    onAppLockToggle: (Boolean) -> Unit,
    onBiometricToggle: (Boolean) -> Unit,
    onChangePinClick: () -> Unit,
    onSetupPinClick: () -> Unit,
) {
    SettingsSectionHeader(title = "Güvenlik", isDark = isDarkTheme)

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
            // Uygulama Kilidi Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier =
                            Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(WarningOrange.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Outlined.Lock,
                            contentDescription = stringResource(R.string.app_lock_desc),
                            tint = WarningOrange,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            "Uygulama Kilidi",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            if (securityState.hasPinSet) "PIN aktif" else "PIN ayarlanmadı",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                Switch(
                    checked = securityState.isAppLockEnabled,
                    onCheckedChange = { enabled ->
                        if (enabled && !securityState.hasPinSet) {
                            onSetupPinClick()
                        } else {
                            onAppLockToggle(enabled)
                        }
                    },
                    colors =
                        SwitchDefaults.colors(
                            checkedThumbColor = PrimaryBlue,
                            checkedTrackColor = PrimaryBlue.copy(alpha = 0.5f),
                        ),
                )
            }

            if (securityState.hasPinSet) {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    modifier = Modifier.padding(vertical = 12.dp),
                )

                // Parmak İzi Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier =
                                Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(IncomeGreen.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                Icons.Outlined.Fingerprint,
                                contentDescription = stringResource(R.string.fingerprint_desc),
                                tint = IncomeGreen,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                "Parmak İzi / Yüz",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                "Hızlı giriş",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                    Switch(
                        checked = securityState.isBiometricEnabled,
                        onCheckedChange = onBiometricToggle,
                        enabled = canUseBiometric,
                        colors =
                            SwitchDefaults.colors(
                                checkedThumbColor = IncomeGreen,
                                checkedTrackColor = IncomeGreen.copy(alpha = 0.5f),
                            ),
                    )
                }

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    modifier = Modifier.padding(vertical = 12.dp),
                )

                // PIN Değiştir
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clickable { onChangePinClick() }
                            .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier =
                                Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(PrimaryBlue.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                Icons.Outlined.Password,
                                contentDescription = stringResource(R.string.pin_change_desc),
                                tint = PrimaryBlue,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "PIN Değiştir",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                    Icon(
                        Icons.Outlined.ChevronRight,
                        contentDescription = stringResource(R.string.pin_change_desc),
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }
    }
}
