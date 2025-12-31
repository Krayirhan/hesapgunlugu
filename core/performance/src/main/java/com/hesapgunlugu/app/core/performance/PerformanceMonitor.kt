package com.hesapgunlugu.app.core.performance

import android.app.Application
import android.os.Build
import android.os.StrictMode
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Performance monitoring and optimization
 */
@Singleton
class PerformanceMonitor
    @Inject
    constructor(
        private val application: Application,
    ) {
        fun initialize(isDebug: Boolean) {
            if (isDebug) {
                enableStrictMode()
            }

            logAppStartupMetrics()
            setupMemoryMonitoring()
        }

        private fun enableStrictMode() {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build(),
            )

            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build(),
            )

            Timber.d("StrictMode enabled for performance monitoring")
        }

        private fun logAppStartupMetrics() {
            val startupTime = System.currentTimeMillis()
            Timber.d("App startup time: ${startupTime}ms")

            // Log device info
            Timber.d(
                """
                Device Info:
                - Model: ${Build.MODEL}
                - Android: ${Build.VERSION.SDK_INT}
                - RAM: ${getAvailableMemory()}MB
                - CPU: ${Runtime.getRuntime().availableProcessors()} cores
                """.trimIndent(),
            )
        }

        private fun setupMemoryMonitoring() {
            val runtime = Runtime.getRuntime()
            val maxMemory = runtime.maxMemory() / 1024 / 1024
            val totalMemory = runtime.totalMemory() / 1024 / 1024
            val freeMemory = runtime.freeMemory() / 1024 / 1024

            Timber.d(
                """
                Memory Info:
                - Max: ${maxMemory}MB
                - Total: ${totalMemory}MB
                - Free: ${freeMemory}MB
                - Used: ${totalMemory - freeMemory}MB
                """.trimIndent(),
            )
        }

        private fun getAvailableMemory(): Long {
            val runtime = Runtime.getRuntime()
            return runtime.maxMemory() / 1024 / 1024
        }

        /**
         * Monitor frame rendering performance
         */
        fun trackFrameMetrics(
            frameName: String,
            durationMs: Long,
        ) {
            if (durationMs > 16) { // 60fps threshold
                Timber.w("Slow frame: $frameName took ${durationMs}ms")
            }
        }

        /**
         * Track database query performance
         */
        fun trackDatabaseQuery(
            queryName: String,
            durationMs: Long,
        ) {
            if (durationMs > 100) {
                Timber.w("Slow query: $queryName took ${durationMs}ms")
            }
        }

        /**
         * Get current memory usage
         */
        fun getCurrentMemoryUsage(): MemoryStats {
            val runtime = Runtime.getRuntime()
            val maxMemory = runtime.maxMemory() / 1024 / 1024
            val totalMemory = runtime.totalMemory() / 1024 / 1024
            val freeMemory = runtime.freeMemory() / 1024 / 1024
            val usedMemory = totalMemory - freeMemory

            return MemoryStats(
                maxMB = maxMemory,
                totalMB = totalMemory,
                freeMB = freeMemory,
                usedMB = usedMemory,
                usagePercentage = (usedMemory.toFloat() / maxMemory.toFloat() * 100).toInt(),
            )
        }
    }

data class MemoryStats(
    val maxMB: Long,
    val totalMB: Long,
    val freeMB: Long,
    val usedMB: Long,
    val usagePercentage: Int,
)
