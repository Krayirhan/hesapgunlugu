package com.hesapgunlugu.app.core.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•
// KOYU TEMA PALETÄ° (Mavi TonlarÄ± - OkunaklÄ± ve Modern)
// â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•
private val DarkColorPalette =
    darkColorScheme(
        primary = PrimaryBlue,
        onPrimary = Color.White,
        primaryContainer = Color(0xFF1565C0),
        // Material Blue 800
        onPrimaryContainer = Color(0xFFE3F2FD),
        // AÃ§Ä±k mavi
        background = BackgroundDark,
        onBackground = TextPrimaryDark,
        surface = SurfaceDark,
        onSurface = TextPrimaryDark,
        surfaceVariant = SurfaceVariantDark,
        onSurfaceVariant = TextSecondaryDark,
        outline = BorderColorDark,
        outlineVariant = DividerColorDark,
        secondary = AccentBlue,
        onSecondary = Color.White,
        secondaryContainer = Color(0xFF1B3A57),
        // Koyu mavi container
        onSecondaryContainer = Color(0xFFB3D4FC),
        // AÃ§Ä±k mavi yazÄ±
        tertiary = Color(0xFF4DD0E1),
        // Cyan accent
        onTertiary = Color(0xFF00363D),
        error = ExpenseRed,
        onError = Color.White,
        errorContainer = Color(0xFF93000A),
        onErrorContainer = Color(0xFFFFDAD6),
        inverseSurface = Color(0xFFE0E6ED),
        inverseOnSurface = Color(0xFF0D1B2A),
        inversePrimary = Color(0xFF0D47A1),
    )

// â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•
// AÃ‡IK TEMA PALETÄ° (CanlÄ± ve YÃ¼ksek Kontrast)
// â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•
private val LightColorPalette =
    lightColorScheme(
        // Ana marka rengi - Parlak mavi
        primary = PrimaryBlue,
        onPrimary = Color.White,
        primaryContainer = Color(0xFFDBEAFE),
        // Soft blue container
        onPrimaryContainer = Color(0xFF1E3A8A),
        // Deep blue text
        // Arka planlar - Temiz beyaz
        background = BackgroundLight,
        onBackground = TextPrimaryLight,
        // YÃ¼zeyler - Beyaz kartlar
        surface = SurfaceLight,
        onSurface = TextPrimaryLight,
        surfaceVariant = SurfaceVariantLight,
        onSurfaceVariant = TextSecondaryLight,
        // KenarlÄ±klar - Belirgin
        outline = BorderColorLight,
        outlineVariant = DividerColorLight,
        // Ä°kincil renk - CanlÄ± yeÅŸil (Gelir)
        secondary = Color(0xFF0F766E),
        // Calm teal
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFCCFBF1),
        // Soft teal
        onSecondaryContainer = Color(0xFF134E4A),
        // Deep teal
        // ÃœÃ§Ã¼ncÃ¼l renk - CanlÄ± turuncu
        tertiary = Color(0xFFB45309),
        // Warm amber
        onTertiary = Color.White,
        tertiaryContainer = Color(0xFFFDE68A),
        // Soft amber
        onTertiaryContainer = Color(0xFF78350F),
        // Deep amber
        // Hata rengi - Parlak kÄ±rmÄ±zÄ± (Gider)
        error = ExpenseRed,
        onError = Color.White,
        errorContainer = Color(0xFFFEE2E2),
        // Soft red
        onErrorContainer = Color(0xFF7F1D1D),
        // Deep red
        // Ters renkler
        inverseSurface = Color(0xFF111827),
        // Slate
        inverseOnSurface = Color(0xFFF8FAFC),
        // Near white
        inversePrimary = Color(0xFF93C5FD),
        // Soft blue
        // Surface tint
        surfaceTint = PrimaryBlue,
        scrim = Color(0xFF000000),
    )

@Composable
fun HesapGunluguTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    HesapGunluguTheme(
        darkTheme = darkTheme,
        dynamicColor = false,
        content = content,
    )
}

@Composable
fun HesapGunluguTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+ (Material You)
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val colorScheme =
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }
            darkTheme -> DarkColorPalette
            else -> LightColorPalette
        }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CorporateTypography,
        shapes = Shapes,
        content = content,
    )
}
