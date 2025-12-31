package com.hesapgunlugu.app.core.domain.model

data class UserSettings(
    val userName: String = "",
    val userTitle: String = "",
    val email: String = "",
    val notificationsEnabled: Boolean = true,
    val monthlyLimit: Double = 20000.0,
    val categoryBudgets: Map<String, Double> = emptyMap(),
    val currency: String = "₺",
    val currencyCode: String = "TRY",
    val currencySymbol: String = "₺",
    val isDarkTheme: Boolean = false,
    val languageCode: String = "tr",
    val appVersion: String = "v1.5.0",
    val budgetAlertThreshold: Int = 80,
)
