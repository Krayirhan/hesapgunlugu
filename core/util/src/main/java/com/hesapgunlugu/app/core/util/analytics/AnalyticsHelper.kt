package com.hesapgunlugu.app.core.util.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.ktx.Firebase
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper class for Firebase Analytics
 *
 * Usage:
 * ```kotlin
 * analyticsHelper.logScreenView("Home")
 * analyticsHelper.logEvent("transaction_added", mapOf("amount" to 100.0))
 * ```
 */
@Singleton
class AnalyticsHelper
    @Inject
    constructor() {
        private val analytics: FirebaseAnalytics by lazy {
            Firebase.analytics
        }

        /**
         * Log screen view event
         */
        fun logScreenView(
            screenName: String,
            screenClass: String? = null,
        ) {
            analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
                param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
                screenClass?.let {
                    param(FirebaseAnalytics.Param.SCREEN_CLASS, it)
                }
            }
            Timber.d("Analytics: Screen viewed - $screenName")
        }

        /**
         * Log custom event
         */
        fun logEvent(
            eventName: String,
            params: Map<String, Any>? = null,
        ) {
            analytics.logEvent(eventName) {
                params?.forEach { (key, value) ->
                    when (value) {
                        is String -> param(key, value)
                        is Long -> param(key, value)
                        is Double -> param(key, value)
                        is Int -> param(key, value.toLong())
                        is Float -> param(key, value.toDouble())
                        is Boolean -> param(key, if (value) 1L else 0L)
                        else -> param(key, value.toString())
                    }
                }
            }
            Timber.d("Analytics: Event logged - $eventName")
        }

        /**
         * Log transaction added event
         */
        fun logTransactionAdded(
            amount: Double,
            category: String,
            isIncome: Boolean,
        ) {
            analytics.logEvent("transaction_added") {
                param("amount", amount)
                param("category", category)
                param("type", if (isIncome) "income" else "expense")
            }
        }

        /**
         * Log transaction edited event
         */
        fun logTransactionEdited(
            amount: Double,
            category: String,
        ) {
            analytics.logEvent("transaction_edited") {
                param("amount", amount)
                param("category", category)
            }
        }

        /**
         * Log transaction deleted event
         */
        fun logTransactionDeleted(category: String) {
            analytics.logEvent("transaction_deleted") {
                param("category", category)
            }
        }

        /**
         * Log export data event
         */
        fun logDataExported(format: String) {
            analytics.logEvent("data_exported") {
                param("format", format)
            }
        }

        /**
         * Log backup created event
         */
        fun logBackupCreated(destination: String) {
            analytics.logEvent("backup_created") {
                param("destination", destination)
            }
        }

        /**
         * Log premium upgrade event
         */
        fun logPremiumUpgrade(source: String) {
            analytics.logEvent("premium_upgrade") {
                param("source", source)
            }
        }

        /**
         * Log biometric authentication enabled
         */
        fun logBiometricEnabled(enabled: Boolean) {
            analytics.logEvent("biometric_auth") {
                param("enabled", if (enabled) 1L else 0L)
            }
        }

        /**
         * Set user property
         */
        fun setUserProperty(
            name: String,
            value: String,
        ) {
            analytics.setUserProperty(name, value)
            Timber.d("Analytics: User property set - $name = $value")
        }

        /**
         * Set user ID (for tracking across devices)
         */
        fun setUserId(userId: String?) {
            analytics.setUserId(userId)
            Timber.d("Analytics: User ID set - $userId")
        }
    }
