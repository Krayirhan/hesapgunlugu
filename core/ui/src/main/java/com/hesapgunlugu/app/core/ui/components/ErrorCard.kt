package com.hesapgunlugu.app.core.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hesapgunlugu.app.core.ui.R
import com.hesapgunlugu.app.core.ui.theme.ExpenseRed

/**
 * Error Card Component
 */
@Composable
fun ErrorCard(
    message: String? = null,
    onRetry: (() -> Unit)? = null,
) {
    val resolvedMessage = message ?: stringResource(R.string.error_occurred)
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = ExpenseRed.copy(alpha = 0.1f),
            ),
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Outlined.ErrorOutline,
                contentDescription = null,
                tint = ExpenseRed,
                modifier = Modifier.size(24.dp),
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = resolvedMessage,
                fontSize = 14.sp,
                color = ExpenseRed,
                modifier = Modifier.weight(1f),
            )

            if (onRetry != null) {
                TextButton(onClick = onRetry) {
                    Text(
                        stringResource(R.string.action_retry),
                        color = ExpenseRed,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}
