package com.hesapgunlugu.app.core.feedback

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages user feedback and beta testing
 */
@Singleton
class FeedbackManager
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val appInfo: AppInfoProvider,
    ) {
        /**
         * Send feedback via email
         */
        fun sendFeedback(
            feedbackType: FeedbackType,
            message: String,
            includeDeviceInfo: Boolean = true,
        ) {
            val subject =
                when (feedbackType) {
                    FeedbackType.BUG -> "Bug Report - Finance Tracker"
                    FeedbackType.FEATURE -> "Feature Request - Finance Tracker"
                    FeedbackType.GENERAL -> "Feedback - Finance Tracker"
                    FeedbackType.BETA -> "Beta Feedback - Finance Tracker"
                }

            val body = buildFeedbackBody(message, includeDeviceInfo, feedbackType)

            val intent =
                Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:")
                    putExtra(Intent.EXTRA_EMAIL, arrayOf("feedback@example.com"))
                    putExtra(Intent.EXTRA_SUBJECT, subject)
                    putExtra(Intent.EXTRA_TEXT, body)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }

            try {
                context.startActivity(
                    Intent.createChooser(intent, "Send feedback").apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    },
                )
            } catch (e: Exception) {
                Timber.e(e, "Failed to send feedback")
            }
        }

        private fun buildFeedbackBody(
            message: String,
            includeDeviceInfo: Boolean,
            feedbackType: FeedbackType,
        ): String {
            val builder = StringBuilder()

            builder.append("Feedback Type: ${feedbackType.name}\n\n")
            builder.append("Message:\n")
            builder.append(message)
            builder.append("\n\n")

            if (includeDeviceInfo) {
                builder.append("---\n")
                builder.append("Device Information:\n")
                builder.append("App Version: ${appInfo.versionName} (${appInfo.versionCode})\n")
                builder.append("Build Type: ${appInfo.buildType}\n")
                builder.append("Device: ${Build.MANUFACTURER} ${Build.MODEL}\n")
                builder.append("Android Version: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})\n")
                builder.append("Screen Resolution: ${getScreenResolution()}\n")
                builder.append("Available Memory: ${getAvailableMemoryMB()}MB\n")
            }

            return builder.toString()
        }

        private fun getScreenResolution(): String {
            val displayMetrics = context.resources.displayMetrics
            return "${displayMetrics.widthPixels}x${displayMetrics.heightPixels}"
        }

        private fun getAvailableMemoryMB(): Long {
            val runtime = Runtime.getRuntime()
            return runtime.maxMemory() / 1024 / 1024
        }

        /**
         * Report a crash
         */
        fun reportCrash(
            throwable: Throwable,
            additionalInfo: Map<String, String> = emptyMap(),
        ) {
            val crashReport = buildCrashReport(throwable, additionalInfo)

            Timber.e(throwable, "Crash reported")

            // In production, send to crash reporting service
            // For now, save to local storage or send via email
            sendFeedback(
                feedbackType = FeedbackType.BUG,
                message = crashReport,
                includeDeviceInfo = true,
            )
        }

        private fun buildCrashReport(
            throwable: Throwable,
            additionalInfo: Map<String, String>,
        ): String {
            val builder = StringBuilder()

            builder.append("CRASH REPORT\n")
            builder.append("=============\n\n")
            builder.append("Exception: ${throwable.javaClass.simpleName}\n")
            builder.append("Message: ${throwable.message}\n\n")
            builder.append("Stack Trace:\n")
            builder.append(throwable.stackTraceToString())
            builder.append("\n\n")

            if (additionalInfo.isNotEmpty()) {
                builder.append("Additional Information:\n")
                additionalInfo.forEach { (key, value) ->
                    builder.append("$key: $value\n")
                }
            }

            return builder.toString()
        }

        /**
         * Open Play Store for rating
         */
        fun requestRating() {
            val intent =
                Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("market://details?id=${context.packageName}")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }

            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                // Fallback to web browser
                val webIntent =
                    Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                context.startActivity(webIntent)
            }
        }

        /**
         * Share app with friends
         */
        fun shareApp() {
            val shareIntent =
                Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, "Finance Tracker App")
                    putExtra(
                        Intent.EXTRA_TEXT,
                        """
                        Check out Finance Tracker - a great app for managing your personal finances!
                        
                        Download: https://play.google.com/store/apps/details?id=${context.packageName}
                        """.trimIndent(),
                    )
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }

            context.startActivity(
                Intent.createChooser(shareIntent, "Share app").apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                },
            )
        }
    }

enum class FeedbackType {
    BUG,
    FEATURE,
    GENERAL,
    BETA,
}
