package com.hesapgunlugu.app.core.util.analytics

import com.google.firebase.ktx.Firebase
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.ktx.performance
import com.google.firebase.perf.metrics.Trace
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper class for Firebase Performance Monitoring
 *
 * Usage:
 * ```kotlin
 * performanceHelper.trace("calculate_dashboard") {
 *     // expensive operation
 * }
 *
 * val trace = performanceHelper.startTrace("database_query")
 * // ... do work
 * trace.stop()
 * ```
 */
@Singleton
class PerformanceHelper
    @Inject
    constructor() {
        @PublishedApi
        internal val performance: FirebasePerformance by lazy {
            Firebase.performance
        }

        /**
         * Execute a block of code and measure its performance
         */
        inline fun <T> trace(
            name: String,
            block: () -> T,
        ): T {
            val trace = performance.newTrace(name)
            trace.start()
            return try {
                block()
            } finally {
                trace.stop()
            }
        }

        /**
         * Start a custom trace
         */
        fun startTrace(name: String): Trace {
            val trace = performance.newTrace(name)
            trace.start()
            Timber.d("Performance: Trace started - $name")
            return trace
        }

        /**
         * Stop a trace
         */
        fun stopTrace(trace: Trace) {
            trace.stop()
            Timber.d("Performance: Trace stopped")
        }

        /**
         * Add custom attributes to trace
         */
        fun addTraceAttribute(
            trace: Trace,
            key: String,
            value: String,
        ) {
            trace.putAttribute(key, value)
        }

        /**
         * Increment metric in trace
         */
        fun incrementMetric(
            trace: Trace,
            metricName: String,
            incrementBy: Long = 1,
        ) {
            trace.incrementMetric(metricName, incrementBy)
        }

        /**
         * Enable/disable performance monitoring
         */
        fun setPerformanceCollectionEnabled(enabled: Boolean) {
            performance.isPerformanceCollectionEnabled = enabled
            Timber.d("Performance: Collection enabled = $enabled")
        }

        /**
         * Common traces for the app
         */
        object TraceNames {
            const val APP_STARTUP = "app_startup"
            const val DATABASE_INIT = "database_init"
            const val CALCULATE_DASHBOARD = "calculate_dashboard"
            const val LOAD_TRANSACTIONS = "load_transactions"
            const val EXPORT_DATA = "export_data"
            const val BACKUP_CREATE = "backup_create"
            const val BACKUP_RESTORE = "backup_restore"
            const val ENCRYPTION = "encryption"
            const val DECRYPTION = "decryption"
        }
    }
