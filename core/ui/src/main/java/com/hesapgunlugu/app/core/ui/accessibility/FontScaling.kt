package com.hesapgunlugu.app.core.ui.accessibility

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/*
 * Font Scaling Utilities
 *
 * Ensures UI adapts properly to user font size preferences.
 * Android supports font scaling from 0.85x to 2.0x (some devices go higher).
 *
 * Best Practices:
 * - Use sp for text sizes (scales automatically)
 * - Use dp for icons and spacing (doesn't scale with text)
 * - Provide scalable touch targets
 * - Test with large font sizes
 */

// MinimumTouchTargetSize is defined in AccessibilityHelpers.kt

/*
 * Recommended touch target size for important actions
 * 56dp x 56dp for primary buttons
 */
val RecommendedTouchTargetSize: Dp = 56.dp

/*
 * Font size categories for accessibility
 */
enum class FontScaleCategory {
    // 0.85x - 1.0x
    SMALL,

    // 1.0x - 1.15x
    NORMAL,

    // 1.15x - 1.3x
    LARGE,

    // 1.3x - 2.0x
    EXTRA_LARGE,

    // 2.0x+
    ACCESSIBILITY,

    ;

    companion object {
        fun fromScale(scale: Float): FontScaleCategory {
            return when {
                scale < 1.0f -> SMALL
                scale < 1.15f -> NORMAL
                scale < 1.3f -> LARGE
                scale < 2.0f -> EXTRA_LARGE
                else -> ACCESSIBILITY
            }
        }
    }
}

/*
 * Adaptive text size that adjusts for very large font scales
 *
 * When font scale is very high (>1.5x), we reduce the scaling factor
 * to prevent text from becoming too large and breaking layouts.
 *
 * Usage:
 * ```
 * Text(
 *     text = "Title",
 *     fontSize = 24.sp.adaptive(fontScale)
 * )
 * ```
 */
fun TextUnit.adaptive(fontScale: Float): TextUnit {
    // For very large scales, apply a dampening factor
    val adjustedScale =
        when {
            fontScale <= 1.3f -> fontScale
            fontScale <= 1.5f -> 1.3f + (fontScale - 1.3f) * 0.5f
            else -> 1.4f + (fontScale - 1.5f) * 0.3f
        }

    return this * adjustedScale
}

/*
 * Adaptive spacing that scales with font size for better readability
 *
 * Vertical spacing should scale with text to maintain readability
 *
 * Usage:
 * ```
 * Spacer(modifier = Modifier.height(16.dp.adaptiveSpacing(fontScale)))
 * ```
 */
fun Dp.adaptiveSpacing(fontScale: Float): Dp {
    return when {
        fontScale <= 1.0f -> this
        fontScale <= 1.3f -> this * 1.1f
        fontScale <= 1.5f -> this * 1.2f
        else -> this * 1.3f
    }
}

/*
 * Ensures touch target meets minimum size requirements
 *
 * Usage:
 * ```
 * IconButton(
 *     modifier = Modifier.minimumTouchTarget()
 * ) { ... }
 * ```
 */
fun Dp.ensureMinimumTouchTarget(): Dp {
    return maxOf(this, MinimumTouchTargetSize)
}

/*
 * Font size tokens that adapt to accessibility settings
 */
object AdaptiveFontSizes {
    // Display text (largest)
    val displayLarge: TextUnit = 57.sp
    val displayMedium: TextUnit = 45.sp
    val displaySmall: TextUnit = 36.sp

    // Headlines
    val headlineLarge: TextUnit = 32.sp
    val headlineMedium: TextUnit = 28.sp
    val headlineSmall: TextUnit = 24.sp

    // Titles
    val titleLarge: TextUnit = 22.sp
    val titleMedium: TextUnit = 16.sp
    val titleSmall: TextUnit = 14.sp

    // Body text
    val bodyLarge: TextUnit = 16.sp
    val bodyMedium: TextUnit = 14.sp
    val bodySmall: TextUnit = 12.sp

    // Labels
    val labelLarge: TextUnit = 14.sp
    val labelMedium: TextUnit = 12.sp
    val labelSmall: TextUnit = 11.sp
}

/*
 * Spacing tokens that adapt to font scale
 */
object AdaptiveSpacing {
    val extraSmall: Dp = 4.dp
    val small: Dp = 8.dp
    val medium: Dp = 16.dp
    val large: Dp = 24.dp
    val extraLarge: Dp = 32.dp

    fun small(fontScale: Float) = small.adaptiveSpacing(fontScale)

    fun medium(fontScale: Float) = medium.adaptiveSpacing(fontScale)

    fun large(fontScale: Float) = large.adaptiveSpacing(fontScale)
}
