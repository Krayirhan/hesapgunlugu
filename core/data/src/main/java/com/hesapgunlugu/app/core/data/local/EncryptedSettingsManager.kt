package com.hesapgunlugu.app.core.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.security.crypto.MasterKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hesapgunlugu.app.core.domain.model.UserSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Encrypted Settings Manager using AndroidX Security Crypto
 *
 * ## Security Features
 * - Uses EncryptedSharedPreferences for sensitive data
 * - MasterKey.Builder with AES256_GCM encryption
 * - Regular DataStore for non-sensitive preferences
 *
 * ## Data Categories
 * - **Encrypted**: PIN, biometric settings, backup data
 * - **Non-Encrypted**: Theme, language, UI preferences
 *
 * @see UserSettings
 */
@Singleton
class EncryptedSettingsManager
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        private val gson = Gson()

        // Non-sensitive data (plain DataStore)
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

        // Master key for encryption
        private val masterKey =
            MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

        // Encrypted SharedPreferences for sensitive data
        private val encryptedPrefs =
            androidx.security.crypto.EncryptedSharedPreferences.create(
                context,
                "encrypted_settings",
                masterKey,
                androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
            )

        companion object {
            // Non-sensitive keys (DataStore)
            val USER_NAME = stringPreferencesKey("user_name")
            val MONTHLY_LIMIT = doublePreferencesKey("monthly_limit")
            val CATEGORY_BUDGETS_JSON = stringPreferencesKey("category_budgets_json")
            val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
            val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
            val CURRENCY_CODE = stringPreferencesKey("currency_code")
            val LANGUAGE_CODE = stringPreferencesKey("language_code")
            val BUDGET_ALERT_THRESHOLD = intPreferencesKey("budget_alert_threshold")

            // Sensitive keys (EncryptedSharedPreferences)
            const val KEY_BACKUP_ENCRYPTION_PASSWORD = "backup_encryption_password"
            const val KEY_APP_LOCK_PIN = "app_lock_pin"
            const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
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
                    userName = preferences[USER_NAME] ?: "",
                    userTitle = "",
                    email = "",
                    monthlyLimit = preferences[MONTHLY_LIMIT] ?: 20000.0,
                    categoryBudgets = savedBudgets,
                    notificationsEnabled = preferences[NOTIFICATIONS_ENABLED] ?: true,
                    isDarkTheme = preferences[IS_DARK_THEME] ?: false,
                    currency = currencySymbol,
                    currencyCode = currencyCode,
                    currencySymbol = currencySymbol,
                    languageCode = preferences[LANGUAGE_CODE] ?: "tr",
                    appVersion = "v1.5.0",
                    budgetAlertThreshold = preferences[BUDGET_ALERT_THRESHOLD] ?: 80,
                )
            }

        suspend fun updateUserName(name: String) {
            context.dataStore.edit { preferences ->
                preferences[USER_NAME] = name
            }
        }

        suspend fun updateMonthlyLimit(limit: Double) {
            context.dataStore.edit { preferences ->
                preferences[MONTHLY_LIMIT] = limit
            }
        }

        suspend fun updateCategoryBudgets(budgets: Map<String, Double>) {
            context.dataStore.edit { preferences ->
                val json = gson.toJson(budgets)
                preferences[CATEGORY_BUDGETS_JSON] = json
            }
        }

        suspend fun updateNotificationsEnabled(enabled: Boolean) {
            context.dataStore.edit { preferences ->
                preferences[NOTIFICATIONS_ENABLED] = enabled
            }
        }

        suspend fun updateTheme(isDark: Boolean) {
            context.dataStore.edit { preferences ->
                preferences[IS_DARK_THEME] = isDark
            }
        }

        suspend fun updateCurrency(code: String) {
            context.dataStore.edit { preferences ->
                preferences[CURRENCY_CODE] = code
            }
        }

        suspend fun updateLanguage(code: String) {
            context.dataStore.edit { preferences ->
                preferences[LANGUAGE_CODE] = code
            }
        }

        suspend fun updateBudgetAlertThreshold(threshold: Int) {
            context.dataStore.edit { preferences ->
                preferences[BUDGET_ALERT_THRESHOLD] = threshold
            }
        }

        // Encrypted operations for sensitive data
        fun saveEncryptedBackupPassword(password: String) {
            encryptedPrefs.edit().putString(KEY_BACKUP_ENCRYPTION_PASSWORD, password).apply()
        }

        fun getEncryptedBackupPassword(): String? {
            return encryptedPrefs.getString(KEY_BACKUP_ENCRYPTION_PASSWORD, null)
        }

        fun saveBiometricEnabled(enabled: Boolean) {
            encryptedPrefs.edit().putBoolean(KEY_BIOMETRIC_ENABLED, enabled).apply()
        }

        fun isBiometricEnabled(): Boolean {
            return encryptedPrefs.getBoolean(KEY_BIOMETRIC_ENABLED, false)
        }

        /**
         * Clears all settings (GDPR compliance)
         */
        suspend fun clearAllSettings() {
            context.dataStore.edit { it.clear() }
            encryptedPrefs.edit().clear().apply()
        }
    }
