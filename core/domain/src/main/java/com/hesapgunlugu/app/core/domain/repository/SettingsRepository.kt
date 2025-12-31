package com.hesapgunlugu.app.core.domain.repository

import com.hesapgunlugu.app.core.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow

/**
 * Settings Repository Interface
 * Clean Architecture: Domain layer defines the contract
 * Data layer provides the implementation
 */
interface SettingsRepository {
    /**
     * Observe user settings
     */
    val settingsFlow: Flow<UserSettings>

    /**
     * Observe dark theme preference
     */
    val isDarkThemeFlow: Flow<Boolean>

    /**
     * Toggle dark theme
     */
    suspend fun toggleTheme(isDark: Boolean)

    /**
     * Update category budgets
     */
    suspend fun updateCategoryBudgets(budgets: Map<String, Double>)

    /**
     * Update single category budget
     */
    suspend fun updateCategoryBudget(
        category: String,
        limit: Double,
    )

    /**
     * Update monthly limit
     */
    suspend fun updateMonthlyLimit(limit: Double)

    /**
     * Update budget alert threshold (50-100%)
     */
    suspend fun updateBudgetAlertThreshold(threshold: Int)

    /**
     * Update user name
     */
    suspend fun updateUserName(name: String)

    /**
     * Update currency
     */
    suspend fun updateCurrency(currencyCode: String)

    /**
     * Clear all stored settings (GDPR data deletion)
     */
    suspend fun clearAllSettings()
}
