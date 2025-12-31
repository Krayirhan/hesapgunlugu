package com.hesapgunlugu.app.core.data.repository

import com.hesapgunlugu.app.core.data.local.SettingsManager
import com.hesapgunlugu.app.core.domain.model.UserSettings
import com.hesapgunlugu.app.core.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Settings Repository Implementation
 * Clean Architecture: Data layer implements domain interface
 * Delegates to SettingsManager (data source)
 */
@Singleton
class SettingsRepositoryImpl
    @Inject
    constructor(
        private val settingsManager: SettingsManager,
    ) : SettingsRepository {
        override val settingsFlow: Flow<UserSettings> = settingsManager.settingsFlow

        override val isDarkThemeFlow: Flow<Boolean> = settingsManager.isDarkThemeFlow

        override suspend fun toggleTheme(isDark: Boolean) {
            settingsManager.toggleTheme(isDark)
        }

        override suspend fun updateCategoryBudgets(budgets: Map<String, Double>) {
            settingsManager.updateCategoryBudgets(budgets)
        }

        override suspend fun updateCategoryBudget(
            category: String,
            limit: Double,
        ) {
            settingsManager.updateCategoryBudget(category, limit)
        }

        override suspend fun updateMonthlyLimit(limit: Double) {
            settingsManager.updateMonthlyLimit(limit)
        }

        override suspend fun updateBudgetAlertThreshold(threshold: Int) {
            settingsManager.updateBudgetAlertThreshold(threshold)
        }

        override suspend fun updateUserName(name: String) {
            settingsManager.updateUserName(name)
        }

        override suspend fun updateCurrency(currencyCode: String) {
            settingsManager.updateCurrency(currencyCode)
        }

        override suspend fun clearAllSettings() {
            settingsManager.clearAllSettings()
        }
    }
