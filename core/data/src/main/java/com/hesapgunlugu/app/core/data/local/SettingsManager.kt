package com.hesapgunlugu.app.core.data.local

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hesapgunlugu.app.core.domain.model.UserSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "user_settings")

@Singleton
class SettingsManager
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        private val gson = Gson()

        companion object {
            val USER_NAME = stringPreferencesKey("user_name")
            val MONTHLY_LIMIT = doublePreferencesKey("monthly_limit")
            val CATEGORY_BUDGETS_JSON = stringPreferencesKey("category_budgets_json")
            val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
            val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
            val CURRENCY_CODE = stringPreferencesKey("currency_code")
            val BUDGET_ALERT_THRESHOLD = intPreferencesKey("budget_alert_threshold")
        }

        val settingsFlow: Flow<UserSettings> =
            context.dataStore.data.map { preferences ->
                val jsonBudgets = preferences[CATEGORY_BUDGETS_JSON] ?: ""
                val type = object : TypeToken<Map<String, Double>>() {}.type

                val savedBudgets: Map<String, Double> =
                    if (jsonBudgets.isNotEmpty()) {
                        gson.fromJson(jsonBudgets, type)
                    } else {
                        mapOf("Genel" to 5000.0)
                    }

                val currencyCode = preferences[CURRENCY_CODE] ?: "TRY"
                val currencySymbol =
                    when (currencyCode) {
                        "TRY" -> "₺"
                        "USD" -> "$"
                        "EUR" -> "€"
                        "GBP" -> "£"
                        else -> "₺"
                    }

                UserSettings(
                    userName = preferences[USER_NAME] ?: "Kullanıcı",
                    monthlyLimit = preferences[MONTHLY_LIMIT] ?: 20000.0,
                    categoryBudgets = savedBudgets,
                    notificationsEnabled = preferences[NOTIFICATIONS_ENABLED] ?: true,
                    currency = currencySymbol,
                    currencyCode = currencyCode,
                    budgetAlertThreshold = preferences[BUDGET_ALERT_THRESHOLD] ?: 80,
                )
            }

        // Tema Akışı
        val isDarkThemeFlow: Flow<Boolean> =
            context.dataStore.data.map { preferences ->
                preferences[IS_DARK_THEME] ?: true
            }

        // Tema Güncelleme
        suspend fun toggleTheme(isDark: Boolean) {
            context.dataStore.edit { it[IS_DARK_THEME] = isDark }
        }

        suspend fun updateCategoryBudgets(budgets: Map<String, Double>) {
            context.dataStore.edit { it[CATEGORY_BUDGETS_JSON] = gson.toJson(budgets) }
        }

        suspend fun updateCategoryBudget(
            category: String,
            limit: Double,
        ) {
            context.dataStore.edit { preferences ->
                val currentJson = preferences[CATEGORY_BUDGETS_JSON] ?: "{}"
                val currentBudgets =
                    try {
                        gson.fromJson<Map<String, Double>>(currentJson, object : com.google.gson.reflect.TypeToken<Map<String, Double>>() {}.type) ?: mutableMapOf()
                    } catch (e: Exception) {
                        mutableMapOf<String, Double>()
                    }
                val updatedBudgets = currentBudgets.toMutableMap()
                updatedBudgets[category] = limit
                preferences[CATEGORY_BUDGETS_JSON] = gson.toJson(updatedBudgets)
            }
        }

        suspend fun updateMonthlyLimit(limit: Double) {
            context.dataStore.edit { it[MONTHLY_LIMIT] = limit }
        }

        suspend fun updateBudgetAlertThreshold(threshold: Int) {
            context.dataStore.edit { it[BUDGET_ALERT_THRESHOLD] = threshold.coerceIn(50, 100) }
        }

        suspend fun updateUserName(name: String) {
            context.dataStore.edit { it[USER_NAME] = name }
        }

        suspend fun updateCurrency(currencyCode: String) {
            context.dataStore.edit { it[CURRENCY_CODE] = currencyCode }
        }

        suspend fun clearAllSettings() {
            context.dataStore.edit { it.clear() }
        }
    }
