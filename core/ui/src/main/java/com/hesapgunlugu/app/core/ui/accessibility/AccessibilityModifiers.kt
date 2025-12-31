package com.hesapgunlugu.app.core.ui.accessibility

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import com.hesapgunlugu.app.core.ui.R

fun Modifier.accessibleClickable(
    label: String,
    stateDescription: String? = null,
): Modifier =
    this.semantics {
        contentDescription = label
        stateDescription?.let {
            this.stateDescription = it
        }
    }

fun Modifier.accessibleHeading(description: String? = null): Modifier =
    this.semantics {
        heading()
        description?.let { contentDescription = it }
    }

@Composable
fun Modifier.accessibleToggle(
    label: String,
    isChecked: Boolean,
    stateOn: String? = null,
    stateOff: String? = null,
): Modifier {
    val resolvedStateOn = stateOn ?: stringResource(R.string.accessibility_state_on)
    val resolvedStateOff = stateOff ?: stringResource(R.string.accessibility_state_off)
    val state = if (isChecked) resolvedStateOn else resolvedStateOff
    return this.semantics {
        contentDescription = label
        stateDescription = state
    }
}

@Composable
fun Modifier.accessibleValue(
    label: String,
    currentValue: String,
    valueRange: String? = null,
): Modifier {
    val currentText = stringResource(R.string.accessibility_value_current, currentValue)
    val rangeText = valueRange?.let { stringResource(R.string.accessibility_value_range, it) }
    val state =
        buildString {
            append(currentText)
            rangeText?.let { append(", $it") }
        }
    return this.semantics {
        contentDescription = label
        stateDescription = state
    }
}

fun Modifier.accessibleImage(description: String): Modifier =
    this.semantics {
        contentDescription = description
    }

fun Modifier.accessibleCustomContent(description: String): Modifier =
    this.semantics {
        contentDescription = description
    }
