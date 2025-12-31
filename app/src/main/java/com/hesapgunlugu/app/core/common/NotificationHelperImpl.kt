package com.hesapgunlugu.app.core.common

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.hesapgunlugu.app.MainActivity
import com.hesapgunlugu.app.R
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of NotificationHelper for Android
 * Handles system notifications using NotificationManager
 */
@Singleton
class NotificationHelperImpl
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : NotificationHelper {
        private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        companion object {
            private const val CHANNEL_ID_BUDGET = "budget_channel"
            private const val CHANNEL_ID_PAYMENT = "payment_channel"
            private const val CHANNEL_ID_GENERAL = "general_channel"

            private const val NOTIFICATION_ID_BUDGET = 1000
            private const val NOTIFICATION_ID_CATEGORY = 2000
            private const val NOTIFICATION_ID_PAYMENT = 3000
            private const val NOTIFICATION_ID_GENERAL = 4000
        }

        init {
            createNotificationChannels()
        }

        private fun createNotificationChannels() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val budgetChannel =
                    NotificationChannel(
                        CHANNEL_ID_BUDGET,
                        context.getString(R.string.notification_channel_budget),
                        NotificationManager.IMPORTANCE_HIGH,
                    ).apply {
                        description = context.getString(R.string.notification_channel_budget_desc)
                    }

                val paymentChannel =
                    NotificationChannel(
                        CHANNEL_ID_PAYMENT,
                        context.getString(R.string.notification_channel_payment),
                        NotificationManager.IMPORTANCE_DEFAULT,
                    ).apply {
                        description = context.getString(R.string.notification_channel_payment_desc)
                    }

                val generalChannel =
                    NotificationChannel(
                        CHANNEL_ID_GENERAL,
                        context.getString(R.string.notification_channel_general),
                        NotificationManager.IMPORTANCE_DEFAULT,
                    ).apply {
                        description = context.getString(R.string.notification_channel_general_desc)
                    }

                notificationManager.createNotificationChannel(budgetChannel)
                notificationManager.createNotificationChannel(paymentChannel)
                notificationManager.createNotificationChannel(generalChannel)
            }
        }

        override fun showBudgetAlert(
            title: String,
            message: String,
        ) {
            val intent =
                Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }

            val pendingIntent =
                PendingIntent.getActivity(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE,
                )

            val notification =
                NotificationCompat.Builder(context, CHANNEL_ID_BUDGET)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build()

            notificationManager.notify(NOTIFICATION_ID_BUDGET, notification)
        }

        override fun showCategoryBudgetWarning(
            category: String,
            currentSpending: Double,
            limit: Double,
        ) {
            val percentage = (currentSpending / limit * 100).toInt()
            val formatter = NumberFormat.getCurrencyInstance(Locale.getDefault())
            val title = context.getString(R.string.budget_warning_title, category)
            val message =
                context.getString(
                    R.string.budget_warning_message,
                    percentage,
                    category,
                    formatter.format(currentSpending),
                    formatter.format(limit),
                )

            val intent =
                Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }

            val pendingIntent =
                PendingIntent.getActivity(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE,
                )

            val notification =
                NotificationCompat.Builder(context, CHANNEL_ID_BUDGET)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build()

            notificationManager.notify(NOTIFICATION_ID_CATEGORY, notification)
        }

        override fun showPaymentReminder(
            paymentId: Long,
            title: String,
            message: String,
            dueDate: Long,
        ) {
            val intent =
                Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    putExtra("payment_id", paymentId)
                }

            val pendingIntent =
                PendingIntent.getActivity(
                    context,
                    paymentId.toInt(),
                    intent,
                    PendingIntent.FLAG_IMMUTABLE,
                )

            val notification =
                NotificationCompat.Builder(context, CHANNEL_ID_PAYMENT)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build()

            notificationManager.notify(NOTIFICATION_ID_PAYMENT + paymentId.toInt(), notification)
        }

        override fun showGeneralNotification(
            title: String,
            message: String,
        ) {
            val intent =
                Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }

            val pendingIntent =
                PendingIntent.getActivity(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE,
                )

            val notification =
                NotificationCompat.Builder(context, CHANNEL_ID_GENERAL)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build()

            notificationManager.notify(NOTIFICATION_ID_GENERAL, notification)
        }
    }
