package com.hesapgunlugu.app.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.hesapgunlugu.app.MainActivity
import com.hesapgunlugu.app.R
import java.text.NumberFormat
import java.util.*

/**
 * Finance Summary Widget
 * Ana ekranda bakiye, gelir ve gider özetini gösterir
 * RemoteViews tabanlı basit widget implementasyonu
 */
class FinanceWidgetReceiver : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Widget ilk kez eklendiğinde
    }

    override fun onDisabled(context: Context) {
        // Son widget kaldırıldığında
    }

    companion object {
        private const val PREFS_NAME = "finance_widget_prefs"
        private const val KEY_BALANCE = "widget_balance"
        private const val KEY_INCOME = "widget_income"
        private const val KEY_EXPENSE = "widget_expense"

        /**
         * Widget verilerini güncelle
         */
        fun updateWidgetData(
            context: Context,
            balance: Double,
            income: Double,
            expense: Double,
        ) {
            // Verileri SharedPreferences'a kaydet
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().apply {
                putFloat(KEY_BALANCE, balance.toFloat())
                putFloat(KEY_INCOME, income.toFloat())
                putFloat(KEY_EXPENSE, expense.toFloat())
                apply()
            }

            // Tüm widget'ları güncelle
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, FinanceWidgetReceiver::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)

            for (appWidgetId in appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        }

        private fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
        ) {
            // Verileri oku
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val balance = prefs.getFloat(KEY_BALANCE, 0f).toDouble()
            val income = prefs.getFloat(KEY_INCOME, 0f).toDouble()
            val expense = prefs.getFloat(KEY_EXPENSE, 0f).toDouble()

            // RemoteViews oluştur
            val views = RemoteViews(context.packageName, R.layout.widget_finance)

            // Verileri ayarla
            views.setTextViewText(R.id.widget_balance, formatCurrency(balance))
            views.setTextViewText(R.id.widget_income, formatCurrencyShort(context, income))
            views.setTextViewText(R.id.widget_expense, formatCurrencyShort(context, expense))

            // Tıklama ile uygulamayı aç
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent =
                PendingIntent.getActivity(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                )
            views.setOnClickPendingIntent(R.id.widget_container, pendingIntent)

            // Widget'ı güncelle
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun formatCurrency(amount: Double): String {
            val formatter = NumberFormat.getCurrencyInstance(Locale.getDefault())
            return formatter.format(amount)
        }

        private fun formatCurrencyShort(
            context: Context,
            amount: Double,
        ): String {
            val locale = Locale.getDefault()
            val millionFormat = context.getString(R.string.currency_short_million_format)
            val thousandFormat = context.getString(R.string.currency_short_thousand_format)
            val defaultFormat = context.getString(R.string.currency_short_default_format)
            return when {
                amount >= 1_000_000 -> String.format(locale, millionFormat, amount / 1_000_000)
                amount >= 1_000 -> String.format(locale, thousandFormat, amount / 1_000)
                else -> String.format(locale, defaultFormat, amount)
            }
        }
    }
}
