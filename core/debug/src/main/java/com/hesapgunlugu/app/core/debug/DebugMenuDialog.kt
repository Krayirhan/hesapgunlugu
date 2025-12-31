@file:Suppress("FunctionName")

package com.hesapgunlugu.app.core.debug

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Helper function to get app version info
private fun Context.getAppVersionInfo(): Triple<String, Int, Boolean> {
    val unknown = getString(R.string.debug_unknown)
    return try {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        val versionName = packageInfo.versionName ?: unknown
        val versionCode =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode.toInt()
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode
            }
        val isDebug = (applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0
        Triple(versionName, versionCode, isDebug)
    } catch (e: PackageManager.NameNotFoundException) {
        Triple(unknown, 0, true)
    }
}

/**
 * Debug menu for development
 * Shows app info, device info, and debug actions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebugMenuDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val (versionName, versionCode, isDebug) = remember { context.getAppVersionInfo() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(stringResource(R.string.debug_menu_title))
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = stringResource(R.string.close))
                }
            }
        },
        text = {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .heightIn(max = 500.dp)
                        .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // App Info Section
                DebugSection(title = stringResource(R.string.debug_app_info_title)) {
                    DebugInfoRow(stringResource(R.string.debug_package_name), context.packageName)
                    DebugInfoRow(
                        stringResource(R.string.debug_version_label),
                        stringResource(R.string.debug_version_value, versionName, versionCode),
                    )
                    DebugInfoRow(
                        stringResource(R.string.debug_build_type_label),
                        if (isDebug) {
                            stringResource(R.string.debug_build_type_debug)
                        } else {
                            stringResource(R.string.debug_build_type_release)
                        },
                    )
                    DebugInfoRow(
                        stringResource(R.string.debug_debuggable_label),
                        if (isDebug) stringResource(R.string.yes) else stringResource(R.string.no),
                    )
                }

                // Device Info Section
                DebugSection(title = stringResource(R.string.debug_device_info_title)) {
                    DebugInfoRow(
                        stringResource(R.string.debug_device_model),
                        "${Build.MANUFACTURER} ${Build.MODEL}",
                    )
                    DebugInfoRow(
                        stringResource(R.string.debug_device_android),
                        stringResource(
                            R.string.debug_device_android_value,
                            Build.VERSION.RELEASE,
                            Build.VERSION.SDK_INT,
                        ),
                    )
                    DebugInfoRow(
                        stringResource(R.string.debug_device_abi),
                        Build.SUPPORTED_ABIS.firstOrNull() ?: stringResource(R.string.debug_unknown),
                    )
                }

                // Database Info
                DebugSection(title = stringResource(R.string.debug_database_title)) {
                    val dbPath = context.getDatabasePath("my_money_db_v2")
                    DebugInfoRow(
                        stringResource(R.string.debug_database_location),
                        dbPath?.absolutePath ?: stringResource(R.string.debug_not_available),
                    )
                    DebugInfoRow(
                        stringResource(R.string.debug_database_size),
                        formatFileSize(context, dbPath?.length() ?: 0),
                    )
                    DebugInfoRow(stringResource(R.string.debug_database_version), "7")
                }

                // Memory Info
                DebugSection(title = stringResource(R.string.debug_memory_title)) {
                    val runtime = Runtime.getRuntime()
                    val maxMemory = runtime.maxMemory() / (1024 * 1024)
                    val totalMemory = runtime.totalMemory() / (1024 * 1024)
                    val freeMemory = runtime.freeMemory() / (1024 * 1024)
                    val usedMemory = totalMemory - freeMemory

                    DebugInfoRow(
                        stringResource(R.string.debug_memory_used),
                        stringResource(R.string.debug_memory_value_mb, usedMemory),
                    )
                    DebugInfoRow(
                        stringResource(R.string.debug_memory_total),
                        stringResource(R.string.debug_memory_value_mb, totalMemory),
                    )
                    DebugInfoRow(
                        stringResource(R.string.debug_memory_max),
                        stringResource(R.string.debug_memory_value_mb, maxMemory),
                    )
                    DebugInfoRow(
                        stringResource(R.string.debug_memory_free),
                        stringResource(R.string.debug_memory_value_mb, freeMemory),
                    )
                }

                // Runtime Info
                DebugSection(title = stringResource(R.string.debug_runtime_title)) {
                    val currentTime =
                        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            .format(Date())
                    DebugInfoRow(stringResource(R.string.debug_runtime_now), currentTime)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.close))
            }
        },
    )
}

@Composable
private fun DebugSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontSize = 13.sp,
            )
            Divider()
            content()
        }
    }
}

@Composable
private fun DebugInfoRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

private fun formatFileSize(
    context: Context,
    bytes: Long,
): String {
    return when {
        bytes < 1024 -> context.getString(R.string.debug_file_size_bytes, bytes)
        bytes < 1024 * 1024 -> context.getString(R.string.debug_file_size_kb, bytes / 1024)
        else -> context.getString(R.string.debug_file_size_mb, bytes / (1024 * 1024))
    }
}
