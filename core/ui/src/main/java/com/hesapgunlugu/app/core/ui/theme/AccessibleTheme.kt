package com.hesapgunlugu.app.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalDensity

val LocalFontScale = compositionLocalOf { 1.0f }

val LocalAccessibilityEnabled = compositionLocalOf { false }

@Composable
fun AccessibleTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    val fontScale = density.fontScale

    CompositionLocalProvider(
        LocalFontScale provides fontScale,
        LocalAccessibilityEnabled provides (fontScale > 1.3f),
    ) {
        MaterialTheme(
            content = content,
        )
    }
}

@Composable
fun currentFontScale(): Float {
    return LocalFontScale.current
}

@Composable
fun isAccessibilityMode(): Boolean {
    return LocalAccessibilityEnabled.current
}
