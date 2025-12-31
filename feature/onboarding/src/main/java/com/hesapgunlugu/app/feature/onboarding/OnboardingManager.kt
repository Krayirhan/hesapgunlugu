package com.hesapgunlugu.app.feature.onboarding

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Onboarding durumunu yönetir.
 * İlk kullanım kontrolü için SharedPreferences kullanır.
 */
@Singleton
class OnboardingManager
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        companion object {
            private const val PREF_NAME = "onboarding_prefs"
            private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        }

        private val prefs: SharedPreferences by lazy {
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        }

        /**
         * Onboarding'in tamamlanıp tamamlanmadığını kontrol eder
         */
        fun isOnboardingCompleted(): Boolean {
            return prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)
        }

        /**
         * Onboarding'i tamamlandı olarak işaretler
         */
        fun setOnboardingCompleted() {
            prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, true).apply()
        }

        /**
         * Onboarding durumunu sıfırlar (test için)
         */
        fun resetOnboarding() {
            prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, false).apply()
        }
    }
