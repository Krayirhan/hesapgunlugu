package com.hesapgunlugu.app.core.ui.theme

import androidx.compose.ui.graphics.Color

// ═══════════════════════════════════════════════════════════════════════
// KOYU TEMA RENKLERİ (Dark Mode - Mavi Tonları, Okunabilir)
// ═══════════════════════════════════════════════════════════════════════

// Arka Planlar - Dark (Koyu Mavi Tonları)
val BackgroundDark = Color(0xFF0D1B2A) // Derin Lacivert Ana Zemin
val SurfaceDark = Color(0xFF1B263B) // Kart Yüzeyi (Koyu Mavi)
val SurfaceVariantDark = Color(0xFF253B52) // Input Alanları (Orta Mavi)

// Yazı Renkleri - Dark
val TextPrimaryDark = Color(0xFFE0E6ED) // Başlık/Ana metin (Açık gri-beyaz)
val TextSecondaryDark = Color(0xFF8BA3BB) // Alt metin (Mavi-gri)

// Kenarlık/Bölücü - Dark
val BorderColorDark = Color(0xFF2E4559) // Mavi tonlu kenarlık
val DividerColorDark = Color(0xFF3A5569) // Açık mavi-gri

// ═══════════════════════════════════════════════════════════════════════
// AÇIK TEMA RENKLERİ (Light Mode - Gri Arka Plan, Beyaz Kartlar)
// ═══════════════════════════════════════════════════════════════════════

// Arka Planlar - Light (Gri zemin, beyaz kartlar - net ayrım)
val BackgroundLight = Color(0xFFF6F8FB) // Soft off-white background
val SurfaceLight = Color(0xFFFFFFFF) // Beyaz kart
val SurfaceVariantLight = Color(0xFFEEF2F6) // Subtle variant surface

// Yazı Renkleri - Light
val TextPrimaryLight = Color(0xFF0F172A) // Deep navy text
val TextSecondaryLight = Color(0xFF475569) // Muted blue-gray

// Kenarlık/Bölücü - Light
val BorderColorLight = Color(0xFFCBD5E1) // Soft outline
val DividerColorLight = Color(0xFFE2E8F0) // Light divider

// ═══════════════════════════════════════════════════════════════════════
// ORTAK MARKA RENKLERİ (Her iki tema için - Daha yumuşak tonlar)
// ═══════════════════════════════════════════════════════════════════════

val PrimaryBlue = Color(0xFF007AFF) // Ana Marka (iOS Blue)
val BrandBlue = Color(0xFF0A84FF) // Koyu Marka
val AccentBlue = Color(0xFF5AC8FA) // Vurgulama (Açık mavi)
val PrimaryContainer = Color(0xFF1E3A5F) // Dark için container

// Durum Renkleri (Daha soft tonlar)
val IncomeGreen = Color(0xFF34C759) // Gelir (iOS Green)
val ExpenseRed = Color(0xFFFF3B30) // Gider (iOS Red)
val WarningOrange = Color(0xFFFF9500) // Uyarı (iOS Orange)

// ═══════════════════════════════════════════════════════════════════════
// DASHBOARD & GRADIENT RENKLERİ (Mavi Tonları)
// ═══════════════════════════════════════════════════════════════════════

// Header Gradient (Her iki temada kullanılır - Mavi tonları)
val HeaderGradientStart = Color(0xFF0D47A1) // Material Blue 900
val HeaderGradientEnd = Color(0xFF1565C0) // Material Blue 800
val HeaderGradientLightStart = Color(0xFFE8F1FF) // Light header gradient start
val HeaderGradientLightEnd = Color(0xFFDCE9FF) // Light header gradient end

// Dashboard Premium Gradient (Mavi tonları)
val DashboardGradientStart = Color(0xFF0A1929) // Dark Blue
val DashboardGradientEnd = Color(0xFF1B3A57) // Deep Navy Blue
val DashboardGradientLightStart = Color(0xFFF5F8FF) // Light dashboard start
val DashboardGradientLightEnd = Color(0xFFE9F1FF) // Light dashboard end

// Skor Renkleri
val ScoreHigh = Color(0xFF10B981)
val ScoreMed = Color(0xFFF59E0B)
val ScoreLow = Color(0xFFEF4444)

// ═══════════════════════════════════════════════════════════════════════
// GRAFİK RENKLERİ
// ═══════════════════════════════════════════════════════════════════════

val ChartColor1 = Color(0xFF60A5FA) // Blue
val ChartColor2 = Color(0xFF34D399) // Emerald
val ChartColor3 = Color(0xFFFBBF24) // Amber
val ChartColor4 = Color(0xFFF87171) // Red
val ChartColor5 = Color(0xFFA78BFA) // Violet
val ChartColor6 = Color(0xFFF472B6) // Pink

// Kategori Renkleri (Tema uyumlu tek kaynak)
val CategoryFood = Color(0xFFF59E0B)
val CategoryMarket = Color(0xFF10B981)
val CategoryTransport = Color(0xFF3B82F6)
val CategoryEntertainment = Color(0xFF8B5CF6)
val CategoryEducation = Color(0xFF778BEB)
val CategoryBills = Color(0xFFEF4444)
val CategoryHealth = Color(0xFFEC4899)
val CategorySalary = Color(0xFF06B6D4)
val CategoryRent = Color(0xFF6366F1)
val CategoryOther = Color(0xFF64748B)

val CategoryPalette =
    listOf(
        CategoryFood,
        CategoryMarket,
        CategoryTransport,
        CategoryEntertainment,
        CategoryEducation,
        CategoryBills,
        CategoryHealth,
        CategorySalary,
        CategoryRent,
    )

// Kategori Renkleri (Dark için - Mavi Tonları)
val CatBg = Color(0xFF1B3A57) // Koyu mavi arka plan
val CatText = Color(0xFFB3D4FC) // Açık mavi yazı

// ═══════════════════════════════════════════════════════════════════════
// UYUMLULUK İÇİN ESKİ DEĞİŞKENLER
// ═══════════════════════════════════════════════════════════════════════

val CardSurfaceLighter = SurfaceDark
val BorderColor = BorderColorDark
val DividerColor = DividerColorDark
val TextPrimary = TextPrimaryDark
val TextSecondary = TextSecondaryDark

// Ek uyumluluk değişkenleri
val BackgroundGradientStart = HeaderGradientStart
val BackgroundGradientEnd = HeaderGradientEnd
val CardBackgroundDark = SurfaceDark
