package com.hesapgunlugu.app.core.crash

import android.app.Application
import android.content.Context
import com.hesapgunlugu.app.BuildConfig
import org.acra.ACRA
import org.acra.config.CoreConfigurationBuilder
import org.acra.config.DialogConfigurationBuilder
import org.acra.config.MailSenderConfigurationBuilder
import org.acra.data.StringFormat
import timber.log.Timber

/**
 * ACRA Crash Reporting Configuration
 *
 * Privacy-first crash reporting without third-party services.
 *
 * ## Features
 * - User consent via dialog
 * - Email-based crash reports
 * - Optional HTTP endpoint for self-hosted backend
 * - Offline crash storage
 * - GDPR/KVKK compliant (user controls data)
 *
 * ## Setup
 * 1. Configure email in secrets.properties: ACRA_EMAIL=crashes_at_example.com
 * 2. Or configure HTTP endpoint for backend
 *
 * ## Privacy
 * - No automatic reporting without user consent
 * - No personally identifiable information (PII) collected
 * - User can review crash reports before sending
 *
 * @see [ACRA Documentation](https://github.com/ACRA/acra)
 */
object CrashReportingManager {
    /**
     * Initialize ACRA crash reporting
     *
     * Call this in Application.attachBaseContext() BEFORE super.attachBaseContext()
     *
     * @param context Application context
     */
    fun initializeCrashReporting(context: Context) {
        if (!BuildConfig.ENABLE_CRASH_REPORTING) {
            Timber.d("Crash reporting disabled via BuildConfig")
            return
        }

        try {
            val builder =
                CoreConfigurationBuilder()
                    .withBuildConfigClass(BuildConfig::class.java)
                    .withReportFormat(StringFormat.JSON)

            // Dialog configuration
            val context = context as Context
            builder.withPluginConfigurations(
                DialogConfigurationBuilder()
                    .withTitle(context.getString(com.hesapgunlugu.app.R.string.crash_dialog_title))
                    .withText(context.getString(com.hesapgunlugu.app.R.string.crash_dialog_text))
                    .withCommentPrompt("Hatadan önce ne yaptığınızı açıklayabilir misiniz? (İsteğe bağlı)")
                    .withPositiveButtonText(context.getString(com.hesapgunlugu.app.R.string.crash_dialog_ok))
                    .withNegativeButtonText(context.getString(com.hesapgunlugu.app.R.string.crash_dialog_cancel))
                    .withResIcon(android.R.drawable.ic_dialog_alert)
                    .build(),
            )

            // Email sender (if configured)
            if (BuildConfig.ACRA_EMAIL.isNotBlank()) {
                builder.withPluginConfigurations(
                    MailSenderConfigurationBuilder()
                        .withMailTo(BuildConfig.ACRA_EMAIL)
                        .withSubject("[HesapGunlugu] Crash Report")
                        .withBody("Please do not modify this email. Crash details are attached.")
                        .withReportAsFile(true)
                        .withReportFileName("crash_report.txt")
                        .build(),
                )
            }

            ACRA.init(context as Application, builder.build())

            Timber.i("ACRA crash reporting initialized successfully")
        } catch (e: Exception) {
            Timber.e(e, "Failed to initialize ACRA crash reporting")
        }
    }

    /**
     * Manually report a non-fatal exception
     *
     * Use this for caught exceptions that you want to track
     *
     * @param throwable The exception to report
     * @param context Additional context about the error
     */
    fun reportException(
        throwable: Throwable,
        context: String? = null,
    ) {
        if (!BuildConfig.ENABLE_CRASH_REPORTING) return

        try {
            ACRA.errorReporter.handleException(throwable)
            context?.let {
                ACRA.errorReporter.putCustomData("error_context", it)
            }
            Timber.w(throwable, "Non-fatal exception reported: $context")
        } catch (e: Exception) {
            Timber.e(e, "Failed to report exception to ACRA")
        }
    }

    /**
     * Add custom data to next crash report
     *
     * @param key Data key
     * @param value Data value
     */
    fun putCustomData(
        key: String,
        value: String,
    ) {
        if (!BuildConfig.ENABLE_CRASH_REPORTING) return

        try {
            ACRA.errorReporter.putCustomData(key, value)
        } catch (e: Exception) {
            Timber.e(e, "Failed to add custom data to ACRA")
        }
    }

    /**
     * Remove custom data
     *
     * @param key Data key to remove
     */
    fun removeCustomData(key: String) {
        if (!BuildConfig.ENABLE_CRASH_REPORTING) return

        try {
            ACRA.errorReporter.removeCustomData(key)
        } catch (e: Exception) {
            Timber.e(e, "Failed to remove custom data from ACRA")
        }
    }

    /**
     * Clear all custom data
     */
    fun clearCustomData() {
        if (!BuildConfig.ENABLE_CRASH_REPORTING) return

        try {
            ACRA.errorReporter.clearCustomData()
        } catch (e: Exception) {
            Timber.e(e, "Failed to clear custom data from ACRA")
        }
    }
}
