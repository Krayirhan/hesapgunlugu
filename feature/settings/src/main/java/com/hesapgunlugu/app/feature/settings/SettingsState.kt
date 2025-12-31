package com.hesapgunlugu.app.feature.settings

import androidx.compose.runtime.Stable

@Stable
data class SettingsState(
    val userName: String = "",
    val userTitle: String = "",
    val email: String = "",
    val notificationsEnabled: Boolean = true,
    val monthlyLimit: Double = 20000.0,
    val categoryBudgets: Map<String, Double> = emptyMap(),
    val currency: String = "â‚º",
    val currencyCode: String = "TRY",
    val appVersion: String = "v1.5.0",
    // 80% default alert threshold
    val budgetAlertThreshold: Int = 80,
)

data class BackupState(
    val isLoading: Boolean = false,
    @androidx.annotation.StringRes val messageRes: Int? = null,
    val messageArgs: List<Any> = emptyList(),
    val isSuccess: Boolean = false,
)
