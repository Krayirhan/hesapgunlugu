package com.hesapgunlugu.app.core.data.remote

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackendTokenStore
    @Inject
    constructor(
        @ApplicationContext context: Context,
    ) {
        private val prefs: SharedPreferences by lazy {
            val masterKey =
                MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build()

            EncryptedSharedPreferences.create(
                context,
                "backend_auth_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
            )
        }

        companion object {
            private const val KEY_ACCESS_TOKEN = "backend_access_token"
            private const val KEY_TOKEN_EXPIRY = "backend_token_expiry"
            private const val EXPIRY_SAFETY_WINDOW_MS = 60_000L
        }

        fun getValidToken(nowMillis: Long = System.currentTimeMillis()): String? {
            val token = prefs.getString(KEY_ACCESS_TOKEN, null)
            if (token.isNullOrBlank()) return null

            val expiry = prefs.getLong(KEY_TOKEN_EXPIRY, 0L)
            if (expiry == 0L) return null

            val safeExpiry = expiry - EXPIRY_SAFETY_WINDOW_MS
            return if (nowMillis >= safeExpiry) null else token
        }

        fun saveToken(
            token: String,
            expiresAtMillis: Long,
        ) {
            prefs.edit()
                .putString(KEY_ACCESS_TOKEN, token)
                .putLong(KEY_TOKEN_EXPIRY, expiresAtMillis)
                .apply()
        }

        fun clear() {
            prefs.edit().clear().apply()
        }
    }
