package com.hesapgunlugu.app.core.util.analytics

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper class for Firebase Crashlytics
 *
 * Usage:
 * ```kotlin
 * crashlyticsHelper.log("User tapped button")
 * crashlyticsHelper.recordException(exception)
 * crashlyticsHelper.setUserId(userId)
 * ```
 */
@Singleton
class CrashlyticsHelper
    @Inject
    constructor() {
        private val crashlytics: FirebaseCrashlytics by lazy {
            Firebase.crashlytics
        }

        /**
         * Log a message to Crashlytics
         */
        fun log(message: String) {
            crashlytics.log(message)
            Timber.d("Crashlytics: $message")
        }

        /**
         * Record a non-fatal exception
         */
        fun recordException(throwable: Throwable) {
            crashlytics.recordException(throwable)
            Timber.e(throwable, "Crashlytics: Exception recorded")
        }

        /**
         * Set custom key-value pair
         */
        fun setCustomKey(
            key: String,
            value: String,
        ) {
            crashlytics.setCustomKey(key, value)
        }

        fun setCustomKey(
            key: String,
            value: Boolean,
        ) {
            crashlytics.setCustomKey(key, value)
        }

        fun setCustomKey(
            key: String,
            value: Int,
        ) {
            crashlytics.setCustomKey(key, value)
        }

        fun setCustomKey(
            key: String,
            value: Long,
        ) {
            crashlytics.setCustomKey(key, value)
        }

        fun setCustomKey(
            key: String,
            value: Float,
        ) {
            crashlytics.setCustomKey(key, value)
        }

        fun setCustomKey(
            key: String,
            value: Double,
        ) {
            crashlytics.setCustomKey(key, value)
        }

        /**
         * Set user identifier for crash reports
         */
        fun setUserId(userId: String?) {
            crashlytics.setUserId(userId ?: "")
            Timber.d("Crashlytics: User ID set - $userId")
        }

        /**
         * Enable/disable crash collection
         */
        fun setCrashlyticsCollectionEnabled(enabled: Boolean) {
            crashlytics.setCrashlyticsCollectionEnabled(enabled)
            Timber.d("Crashlytics: Collection enabled = $enabled")
        }

        /**
         * Send any unsent crash reports
         */
        fun sendUnsentReports() {
            crashlytics.sendUnsentReports()
            Timber.d("Crashlytics: Unsent reports sent")
        }

        /**
         * Delete any unsent crash reports
         */
        fun deleteUnsentReports() {
            crashlytics.deleteUnsentReports()
            Timber.d("Crashlytics: Unsent reports deleted")
        }

        /**
         * Check if there are any unsent crash reports
         */
        suspend fun checkForUnsentReports(): Boolean {
            return crashlytics.didCrashOnPreviousExecution()
        }
    }
