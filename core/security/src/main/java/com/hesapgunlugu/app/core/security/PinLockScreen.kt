package com.hesapgunlugu.app.core.security

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hesapgunlugu.app.core.ui.theme.HeaderGradientEnd
import com.hesapgunlugu.app.core.ui.theme.HeaderGradientLightEnd
import com.hesapgunlugu.app.core.ui.theme.HeaderGradientLightStart
import com.hesapgunlugu.app.core.ui.theme.HeaderGradientStart

/**
 * PIN entry screen
 */
@Composable
fun PinLockScreen(
    title: String? = null,
    subtitle: String? = null,
    showBiometric: Boolean = true,
    onPinEntered: (String) -> Unit,
    onBiometricClick: () -> Unit = {},
    onForgotPin: () -> Unit = {},
    error: String? = null,
) {
    var pin by remember { mutableStateOf("") }
    val maxPinLength = 4
    val resolvedTitle = title ?: stringResource(R.string.pin_enter_title)
    val resolvedSubtitle = subtitle ?: stringResource(R.string.pin_enter_subtitle)
    val isDarkTheme = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val gradientColors =
        if (isDarkTheme) {
            listOf(HeaderGradientStart, HeaderGradientEnd)
        } else {
            listOf(HeaderGradientLightStart, HeaderGradientLightEnd)
        }
    val titleColor = if (isDarkTheme) Color.White else MaterialTheme.colorScheme.onSurface
    val subtitleColor = if (isDarkTheme) Color.White.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    brush =
                        Brush.verticalGradient(
                            colors = gradientColors,
                        ),
                ),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // Logo/Icon
            Box(
                modifier =
                    Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp),
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Title
            Text(
                text = resolvedTitle,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = titleColor,
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = resolvedSubtitle,
                fontSize = 14.sp,
                color = subtitleColor,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(40.dp))

            // PIN dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                repeat(maxPinLength) { index ->
                    Box(
                        modifier =
                            Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(
                                    if (index < pin.length) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        Color.Transparent
                                    },
                                )
                                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                    )
                }
            }

            // Error message
            AnimatedVisibility(
                visible = error != null,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                error?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 16.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Numpad
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                ).forEach { row ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                    ) {
                        row.forEach { digit ->
                            NumpadButton(
                                text = digit,
                                onClick = {
                                    if (pin.length < maxPinLength) {
                                        pin += digit
                                        if (pin.length == maxPinLength) {
                                            onPinEntered(pin)
                                            pin = ""
                                        }
                                    }
                                },
                            )
                        }
                    }
                }

                // Last row: Biometric, 0, Backspace
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                ) {
                    if (showBiometric) {
                        IconButton(
                            onClick = onBiometricClick,
                            modifier =
                                Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = stringResource(R.string.pin_biometric_cd),
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp),
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.size(72.dp))
                    }

                    NumpadButton(
                        text = "0",
                        onClick = {
                            if (pin.length < maxPinLength) {
                                pin += "0"
                                if (pin.length == maxPinLength) {
                                    onPinEntered(pin)
                                    pin = ""
                                }
                            }
                        },
                    )

                    IconButton(
                        onClick = {
                            if (pin.isNotEmpty()) {
                                pin = pin.dropLast(1)
                            }
                        },
                        modifier =
                            Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Backspace,
                            contentDescription = stringResource(R.string.pin_backspace_cd),
                            tint = if (isDarkTheme) Color.White else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Forgot PIN button
            TextButton(
                onClick = onForgotPin,
                colors =
                    ButtonDefaults.textButtonColors(
                        contentColor =
                            if (isDarkTheme) {
                                Color.White.copy(alpha = 0.7f)
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                    ),
            ) {
                Text(
                    text = stringResource(R.string.pin_forgot_action),
                    fontSize = 14.sp,
                )
            }
        }
    }
}

@Composable
private fun NumpadButton(
    text: String,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            fontSize = 28.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
