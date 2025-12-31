package com.hesapgunlugu.app.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hesapgunlugu.app.core.ui.R

/**
 * A composable that acts as an error boundary.
 * It catches exceptions from its children and displays a fallback UI.
 *
 * Note: Jetpack Compose doesn't have a direct equivalent to React's ErrorBoundary
 * that catches exceptions during composition automatically in the same way.
 * However, this pattern is commonly used to handle known error states or
 * when wrapping risky content where we can control the error state.
 *
 * For true global exception handling, Thread.setDefaultUncaughtExceptionHandler is used,
 * but for UI-level recovery, we often use a state-based approach.
 *
 * This component assumes the parent manages the error state or we wrap specific
 * logic that might fail.
 */
@Composable
fun ErrorBoundary(
    hasError: Boolean,
    errorMessage: String? = null,
    onRetry: () -> Unit,
    content: @Composable () -> Unit,
) {
    if (hasError) {
        ErrorContent(
            message = errorMessage ?: stringResource(id = R.string.error_generic_unknown),
            onRetry = onRetry,
        )
    } else {
        content()
    }
}

@Composable
fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(48.dp),
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(id = R.string.error_title),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onRetry) {
                Text(text = stringResource(id = R.string.action_retry))
            }
        }
    }
}
