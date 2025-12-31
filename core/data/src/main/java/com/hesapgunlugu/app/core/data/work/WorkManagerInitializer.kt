package com.hesapgunlugu.app.core.data.work

import android.content.Context
import androidx.work.*
import com.hesapgunlugu.app.core.data.worker.RecurringPaymentWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * WorkManager task scheduler
 */
@Singleton
class WorkManagerInitializer
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        /**
         * Tüm background task'leri schedule et
         */
        fun initialize() {
            scheduleRecurringPaymentWorker()
        }

        /**
         * Tekrarlayan ödemeler için daily worker schedule et
         */
        private fun scheduleRecurringPaymentWorker() {
            val constraints =
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .setRequiresBatteryNotLow(true)
                    .build()

            val recurringWork =
                PeriodicWorkRequestBuilder<RecurringPaymentWorker>(
                    repeatInterval = 1,
                    repeatIntervalTimeUnit = TimeUnit.DAYS,
                )
                    .setConstraints(constraints)
                    .setInitialDelay(1, TimeUnit.HOURS) // İlk çalışma 1 saat sonra
                    .addTag("recurring_payment_worker")
                    .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "recurring_payment_sync",
                ExistingPeriodicWorkPolicy.KEEP,
                recurringWork,
            )
        }

        /**
         * Tüm scheduled işleri iptal et
         */
        fun cancelAll() {
            WorkManager.getInstance(context).cancelAllWork()
        }
    }
