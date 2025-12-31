package com.hesapgunlugu.app.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.hesapgunlugu.app.core.ui.accessibility.AdaptiveFontSizes

// Kurumsal Tipografi - Okunabilirlik ve Profesyonellik
val CorporateTypography =
    Typography(
        // Büyük Başlıklar
        displayLarge =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Bold,
                fontSize = AdaptiveFontSizes.displayLarge,
                lineHeight = 64.sp,
                letterSpacing = (-0.25).sp,
            ),
        displayMedium =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Bold,
                fontSize = AdaptiveFontSizes.displayMedium,
                lineHeight = 52.sp,
                letterSpacing = 0.sp,
            ),
        displaySmall =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.SemiBold,
                fontSize = AdaptiveFontSizes.displaySmall,
                lineHeight = 44.sp,
                letterSpacing = 0.sp,
            ),
        // Başlıklar
        headlineLarge =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Bold,
                fontSize = AdaptiveFontSizes.headlineLarge,
                lineHeight = 40.sp,
                letterSpacing = 0.sp,
            ),
        headlineMedium =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.SemiBold,
                fontSize = AdaptiveFontSizes.headlineMedium,
                lineHeight = 36.sp,
                letterSpacing = 0.sp,
            ),
        headlineSmall =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.SemiBold,
                fontSize = AdaptiveFontSizes.headlineSmall,
                lineHeight = 32.sp,
                letterSpacing = 0.sp,
            ),
        // Alt Başlıklar
        titleLarge =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.SemiBold,
                fontSize = AdaptiveFontSizes.titleLarge,
                lineHeight = 28.sp,
                letterSpacing = 0.sp,
            ),
        titleMedium =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Medium,
                fontSize = AdaptiveFontSizes.titleMedium,
                lineHeight = 24.sp,
                letterSpacing = 0.15.sp,
            ),
        titleSmall =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Medium,
                fontSize = AdaptiveFontSizes.titleSmall,
                lineHeight = 20.sp,
                letterSpacing = 0.1.sp,
            ),
        // Gövde Metni
        bodyLarge =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = AdaptiveFontSizes.bodyLarge,
                lineHeight = 24.sp,
                letterSpacing = 0.5.sp,
            ),
        bodyMedium =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = AdaptiveFontSizes.bodyMedium,
                lineHeight = 20.sp,
                letterSpacing = 0.25.sp,
            ),
        bodySmall =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = AdaptiveFontSizes.bodySmall,
                lineHeight = 16.sp,
                letterSpacing = 0.4.sp,
            ),
        // Etiketler
        labelLarge =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Medium,
                fontSize = AdaptiveFontSizes.labelLarge,
                lineHeight = 20.sp,
                letterSpacing = 0.1.sp,
            ),
        labelMedium =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Medium,
                fontSize = AdaptiveFontSizes.labelMedium,
                lineHeight = 16.sp,
                letterSpacing = 0.5.sp,
            ),
        labelSmall =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Medium,
                fontSize = AdaptiveFontSizes.labelSmall,
                lineHeight = 16.sp,
                letterSpacing = 0.5.sp,
            ),
    )
