package com.hesapgunlugu.app.core.ui.accessibility

import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val MinimumTouchTargetSize = 48.dp

fun Modifier.minimumTouchTarget() = this.size(MinimumTouchTargetSize)

@Composable
fun AccessibleContent(content: @Composable () -> Unit) {
    // Respect system font scaling
    val textStyle = MaterialTheme.typography.bodyMedium

    CompositionLocalProvider(
        LocalTextStyle provides textStyle,
    ) {
        content()
    }
}

@Composable
fun getScaledTextSize(
    baseSize: Int,
    minSize: Int = 12,
    maxSize: Int = 32,
): Int {
    val density = LocalDensity.current
    val scaledSize = (baseSize * density.fontScale).toInt()
    return scaledSize.coerceIn(minSize, maxSize)
}

object AccessibleTextStyles {
    /*
     * Large text (18sp or larger, or 14sp bold)
     * Requires 3:1 contrast ratio
     */
    val LargeText =
        TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal,
        )

    /*
     * Large bold text
     */
    val LargeBoldText =
        TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
        )

    /*
     * Normal text
     * Requires 4.5:1 contrast ratio
     */
    val NormalText =
        TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
        )

    /*
     * Small text (minimum 12sp for accessibility)
     */
    val SmallText =
        TextStyle(
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
        )
}

object AccessibilityContrast {
    /*
     * Minimum contrast ratios according to WCAG 2.1 Level AA
     */
    const val MIN_CONTRAST_NORMAL_TEXT = 4.5
    const val MIN_CONTRAST_LARGE_TEXT = 3.0
    const val MIN_CONTRAST_UI_COMPONENTS = 3.0

    /*
     * WCAG 2.1 Level AAA requirements
     */
    const val MIN_CONTRAST_NORMAL_TEXT_AAA = 7.0
    const val MIN_CONTRAST_LARGE_TEXT_AAA = 4.5
}

object AccessibleSpacing {
    val MinimumPadding = 8.dp
    val RecommendedPadding = 16.dp
    val MinimumSpacing = 4.dp
    val RecommendedSpacing = 8.dp
}
