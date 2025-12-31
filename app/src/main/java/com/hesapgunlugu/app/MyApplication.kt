package com.hesapgunlugu.app

import android.app.Application
import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.google.firebase.FirebaseApp
import com.hesapgunlugu.app.core.crash.CrashReportingManager
import com.hesapgunlugu.app.core.data.work.WorkManagerInitializer
import com.hesapgunlugu.app.core.error.GlobalExceptionHandler
import com.hesapgunlugu.app.core.notification.PaymentReminderWorker
import com.hesapgunlugu.app.core.notification.RecurringPaymentWorker
import com.hesapgunlugu.app.core.util.LocalizationUtils
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var workManagerInitializer: WorkManagerInitializer

    override fun attachBaseContext(base: Context) {
        // Kayıtlı dili al veya varsayılan olarak Türkçe kullan
        val prefs = base.getSharedPreferences("app_prefs", MODE_PRIVATE)
        val languageCode = prefs.getString("language_code", "tr") ?: "tr"
        val localizedContext = LocalizationUtils.setLocale(base, languageCode)
        super.attachBaseContext(localizedContext)

        // SECURITY: Initialize ACRA crash reporting (privacy-first, GDPR compliant)
        // Only enabled in release builds and if ENABLE_CRASH_REPORTING is true
        CrashReportingManager.initializeCrashReporting(localizedContext)
    }

    override fun onCreate() {
        super.onCreate()

        // Initialize Firebase first (before any Firebase service is accessed)
        FirebaseApp.initializeApp(this)

        // Initialize Global Exception Handler first
        GlobalExceptionHandler.initialize(this)

        // Timber'ı sadece debug build'lerde aktif et
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Timber.d("Application started - Timber initialized")
        }

        // SECURITY: Add user ID to crash reports (anonymous, for tracking)
        // Do NOT add PII (email, phone, real name) to crash reports
        CrashReportingManager.putCustomData("app_version", BuildConfig.VERSION_NAME)
        CrashReportingManager.putCustomData("build_type", BuildConfig.BUILD_TYPE)

        // Ödeme hatırlatıcı worker'ı başlat
        PaymentReminderWorker.schedule(this)

        // Tekrarlayan ödemeleri otomatik işleme worker'ı başlat
        RecurringPaymentWorker.schedule(this)

        // WorkManager initializer ile tüm background task'leri schedule et
        workManagerInitializer.initialize()
    }

    override val workManagerConfiguration: Configuration
        get() =
            Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .setMinimumLoggingLevel(if (BuildConfig.DEBUG) android.util.Log.DEBUG else android.util.Log.ERROR)
                .build()
}
