package com.hesapgunlugu.app.core.util

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import java.text.NumberFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Currency
import java.util.Locale

object LocalizationUtils {
    fun setLocale(
        baseContext: Context,
        languageCode: String,
    ): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration(baseContext.resources.configuration)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val localeList = LocaleList(locale)
            LocaleList.setDefault(localeList)
            config.setLocales(localeList)
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
        }

        // RTL layout direction'ı ayarla
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLayoutDirection(locale)
        }

        return baseContext.createConfigurationContext(config)
    }

    fun updateConfiguration(
        context: Context,
        languageCode: String,
    ) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val resources = context.resources
        val config = Configuration(resources.configuration)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val localeList = LocaleList(locale)
            LocaleList.setDefault(localeList)
            config.setLocales(localeList)
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
        }

        // RTL layout direction'ı ayarla
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLayoutDirection(locale)
        }

        @Suppress("DEPRECATION")
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    fun isRtl(context: Context): Boolean {
        val locale = context.resources.configuration.locales[0]
        return android.text.TextUtils.getLayoutDirectionFromLocale(locale) == android.view.View.LAYOUT_DIRECTION_RTL
    }

    fun getCurrentLanguageCode(context: Context): String {
        val locale = context.resources.configuration.locales[0]
        return locale.language.orEmpty()
    }

    fun formatCurrency(
        context: Context,
        amount: Double,
        currencyCode: String,
    ): String {
        val locale = context.resources.configuration.locales[0]
        val formatter = NumberFormat.getCurrencyInstance(locale)
        runCatching {
            formatter.currency = Currency.getInstance(currencyCode)
        }
        return formatter.format(amount)
    }

    fun formatDate(
        context: Context,
        instant: Instant,
    ): String {
        val locale = context.resources.configuration.locales[0]
        val formatter =
            DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                .withLocale(locale)
                .withZone(ZoneId.systemDefault())
        return formatter.format(instant)
    }

    fun formatNumber(
        context: Context,
        number: Double,
    ): String {
        val locale = context.resources.configuration.locales[0]
        val formatter = NumberFormat.getNumberInstance(locale)
        return formatter.format(number)
    }

    fun getSupportedLanguages(): List<Pair<String, String>> {
        return listOf(
            "tr" to getLanguageDisplayName("tr"),
            "en" to getLanguageDisplayName("en"),
        )
    }

    fun getLanguageDisplayName(languageCode: String): String {
        val locale = Locale(languageCode)
        return locale.getDisplayLanguage(locale).ifBlank { languageCode }
    }
}
