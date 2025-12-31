package com.hesapgunlugu.app.core.ui.accessibility

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

fun Color.contrastRatio(other: Color): Double {
    val lum1 = this.luminance()
    val lum2 = other.luminance()

    val lighter = maxOf(lum1, lum2)
    val darker = minOf(lum1, lum2)

    return (lighter + 0.05) / (darker + 0.05)
}

fun Color.meetsContrastAA(
    other: Color,
    largeText: Boolean = false,
): Boolean {
    val ratio = contrastRatio(other)
    val threshold = if (largeText) 3.0 else 4.5
    return ratio >= threshold
}

fun Color.meetsContrastAAA(
    other: Color,
    largeText: Boolean = false,
): Boolean {
    val ratio = contrastRatio(other)
    val threshold = if (largeText) 4.5 else 7.0
    return ratio >= threshold
}

fun Color.readableTextColor(): Color {
    val whiteContrast = this.contrastRatio(Color.White)
    val blackContrast = this.contrastRatio(Color.Black)

    return if (whiteContrast > blackContrast) Color.White else Color.Black
}

@Composable
fun accessibleColorPair(
    foreground: Color,
    background: Color,
): AccessibleColorPair {
    val ratio = foreground.contrastRatio(background)
    val meetsAA = ratio >= 4.5
    val meetsAAA = ratio >= 7.0

    return AccessibleColorPair(
        foreground = foreground,
        background = background,
        contrastRatio = ratio,
        meetsAA = meetsAA,
        meetsAAA = meetsAAA,
    )
}

data class AccessibleColorPair(
    val foreground: Color,
    val background: Color,
    val contrastRatio: Double,
    val meetsAA: Boolean,
    val meetsAAA: Boolean,
)

@Composable
fun validateThemeAccessibility(): ThemeAccessibilityReport {
    val colors = MaterialTheme.colorScheme

    val issues = mutableListOf<String>()

    // Check primary text on background
    if (!colors.onBackground.meetsContrastAA(colors.background)) {
        issues.add("Primary text on background: Insufficient contrast")
    }

    // Check primary text on surface
    if (!colors.onSurface.meetsContrastAA(colors.surface)) {
        issues.add("Primary text on surface: Insufficient contrast")
    }

    // Check primary button
    if (!colors.onPrimary.meetsContrastAA(colors.primary)) {
        issues.add("Primary button text: Insufficient contrast")
    }

    // Check error text
    if (!colors.onError.meetsContrastAA(colors.error)) {
        issues.add("Error text: Insufficient contrast")
    }

    return ThemeAccessibilityReport(
        isAccessible = issues.isEmpty(),
        issues = issues,
    )
}

data class ThemeAccessibilityReport(
    val isAccessible: Boolean,
    val issues: List<String>,
)
