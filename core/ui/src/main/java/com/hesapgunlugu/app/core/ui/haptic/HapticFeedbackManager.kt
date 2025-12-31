package com.hesapgunlugu.app.core.ui.haptic

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.HapticFeedbackConstants
import android.view.View
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Haptic feedback manager
 * Provides tactile feedback for user interactions
 */
@Singleton
class HapticFeedbackManager
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        private val vibrator: Vibrator? =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

        /**
         * Light tap feedback (for clicks)
         */
        fun lightTap() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator?.vibrate(
                    VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK),
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(10)
            }
        }

        /**
         * Medium tap feedback (for selections)
         */
        fun mediumTap() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator?.vibrate(
                    VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK),
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(20)
            }
        }

        /**
         * Heavy tap feedback (for important actions)
         */
        fun heavyTap() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator?.vibrate(
                    VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK),
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(30)
            }
        }

        /**
         * Success feedback
         */
        fun success() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator?.vibrate(
                    VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK),
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(longArrayOf(0, 50, 50, 50), -1)
            }
        }

        /**
         * Error feedback
         */
        fun error() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(
                    VibrationEffect.createWaveform(
                        longArrayOf(0, 100, 50, 100),
                        -1,
                    ),
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(longArrayOf(0, 100, 50, 100), -1)
            }
        }

        /**
         * View-based haptic feedback
         */
        fun performHapticFeedback(
            view: View,
            feedbackType: Int = HapticFeedbackConstants.VIRTUAL_KEY,
        ) {
            view.performHapticFeedback(feedbackType)
        }
    }
