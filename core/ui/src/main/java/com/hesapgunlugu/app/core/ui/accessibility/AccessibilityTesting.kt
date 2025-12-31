package com.hesapgunlugu.app.core.ui.accessibility

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp

@Composable
fun FontScaleTestComponent(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    val fontScale = density.fontScale
    val category = FontScaleCategory.fromScale(fontScale)

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Font Scale: ${String.format("%.2f", fontScale)}x - $category",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
            )

            Spacer(modifier = Modifier.height(8.dp))

            content()
        }
    }
}

@Composable
fun ContrastTestComponent(
    foreground: androidx.compose.ui.graphics.Color,
    background: androidx.compose.ui.graphics.Color,
    content: @Composable () -> Unit,
) {
    val ratio = foreground.contrastRatio(background)
    val meetsAA = foreground.meetsContrastAA(background)
    val meetsAAA = foreground.meetsContrastAAA(background)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = background,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text =
                    buildString {
                        append("Contrast Ratio: ${String.format("%.2f", ratio)}:1\n")
                        append("WCAG AA: ${if (meetsAA) "âœ“ Pass" else "âœ— Fail"}\n")
                        append("WCAG AAA: ${if (meetsAAA) "âœ“ Pass" else "âœ— Fail"}")
                    },
                style = MaterialTheme.typography.labelSmall,
                color = foreground,
            )

            Spacer(modifier = Modifier.height(8.dp))

            content()
        }
    }
}

object AccessibilityTestTags {
    const val HEADING = "accessibility_heading"
    const val BUTTON = "accessibility_button"
    const val INPUT_FIELD = "accessibility_input"
    const val ERROR_MESSAGE = "accessibility_error"
    const val TOGGLE = "accessibility_toggle"
    const val SLIDER = "accessibility_slider"
    const val IMAGE = "accessibility_image"
    const val CUSTOM_CONTENT = "accessibility_custom"
}

fun Modifier.accessibilityTestTag(tag: String): Modifier {
    return this.semantics {
        testTag = tag
    }
}
