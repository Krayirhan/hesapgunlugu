package com.hesapgunlugu.app.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.hesapgunlugu.app.core.domain.repository.ScheduledPaymentRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import timber.log.Timber
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Background worker that sends payment reminders for upcoming scheduled payments.
 * Runs daily to check for payments due tomorrow and sends notifications.
 *
 * ## Features
 * - Checks for payments due within next 24 hours
 * - Sends notification for each upcoming payment
 * - Automatically retries on failure (max 3 attempts)
 * - Respects battery and storage constraints
 *
 * ## Scheduling
 * - Runs every 24 hours
 * - Initial delay: 1 hour after app start
 * - Uses REPLACE policy to avoid duplicates
 */
@HiltWorker
class PaymentReminderWorker
    @AssistedInject
    constructor(
        @Assisted private val context: Context,
        @Assisted params: WorkerParameters,
        private val scheduledPaymentRepository: ScheduledPaymentRepository,
    ) : CoroutineWorker(context, params) {
        override suspend fun doWork(): Result {
            return try {
                Timber.d("PaymentReminderWorker started")

                // Calculate tomorrow's date range (00:00 - 23:59)
                val tomorrow =
                    Calendar.getInstance().apply {
                        add(Calendar.DAY_OF_YEAR, 1)
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }

                val tomorrowStart = tomorrow.time
                val tomorrowEnd =
                    Calendar.getInstance().apply {
                        time = tomorrowStart
                        set(Calendar.HOUR_OF_DAY, 23)
                        set(Calendar.MINUTE, 59)
                        set(Calendar.SECOND, 59)
                    }.time

                // Fetch upcoming payments for tomorrow
                val upcomingPayments =
                    scheduledPaymentRepository
                        .getUpcomingPayments(tomorrowStart, tomorrowEnd)
                        .first()

                Timber.d("Found ${upcomingPayments.size} payments due tomorrow")

                if (upcomingPayments.isNotEmpty()) {
                    // Send notification for upcoming payments
                    val message =
                        when (upcomingPayments.size) {
                            1 -> "${upcomingPayments[0].title} - ${upcomingPayments[0].amount} TL yarın ödenecek"
                            else -> "${upcomingPayments.size} ödeme yarın vadesi geliyor"
                        }

                    showReminderNotification(
                        title = "Ödeme Hatırlatması",
                        message = message,
                    )
                }

                Result.success()
            } catch (t: Throwable) {
                Timber.e(t, "PaymentReminderWorker error")

                // Retry up to 3 times
                if (runAttemptCount < 3) {
                    Result.retry()
                } else {
                    Result.failure()
                }
            }
        }

        private fun showReminderNotification(
            title: String,
            message: String,
        ) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Android 8.0+ için notification channel oluştur
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel =
                    NotificationChannel(
                        CHANNEL_ID,
                        "Ödeme Hatırlatmaları",
                        NotificationManager.IMPORTANCE_HIGH,
                    ).apply {
                        description = "Planlı ödeme hatırlatmaları"
                        enableVibration(true)
                    }
                notificationManager.createNotificationChannel(channel)
            }

            val notification =
                NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .build()

            notificationManager.notify(NOTIFICATION_ID, notification)
        }

        companion object {
            private const val WORK_NAME = "payment_reminder_worker"
            private const val CHANNEL_ID = "payment_reminders"
            private const val NOTIFICATION_ID = 1001

            fun schedule(context: Context) {
                val constraints =
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                        .setRequiresBatteryNotLow(true)
                        .setRequiresStorageNotLow(true)
                        .build()

                val request =
                    PeriodicWorkRequestBuilder<PaymentReminderWorker>(
                        repeatInterval = 24,
                        repeatIntervalTimeUnit = TimeUnit.HOURS,
                    )
                        .setConstraints(constraints)
                        .setInitialDelay(1, TimeUnit.HOURS)
                        .build()

                WorkManager.getInstance(context)
                    .enqueueUniquePeriodicWork(WORK_NAME, ExistingPeriodicWorkPolicy.UPDATE, request)
            }
        }
    }
