package com.hesapgunlugu.app.core.security

import android.content.Context
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Root Detection for Security
 *
 * Detects if the device is rooted/jailbroken.
 * Helps prevent security vulnerabilities in financial apps.
 *
 * ## Detection Methods
 * 1. Check for SU binary
 * 2. Check for root management apps
 * 3. Check for writable /system partition
 * 4. Check build tags
 *
 * ## Usage
 * ```kotlin
 * if (rootDetector.isDeviceRooted()) {
 *     // Show warning or restrict features
 * }
 * ```
 *
 * @see <a href="https://developer.android.com/google/play/integrity">Play Integrity API</a>
 */
@Singleton
class RootDetector
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        /**
         * Checks if device is rooted
         *
         * @return true if any root indicators are found
         */
        fun isDeviceRooted(): Boolean {
            return checkRootMethod1() ||
                checkRootMethod2() ||
                checkRootMethod3() ||
                checkBuildTags()
        }

        /**
         * Method 1: Check for SU binary in common paths
         */
        private fun checkRootMethod1(): Boolean {
            val suPaths =
                arrayOf(
                    "/system/app/Superuser.apk",
                    "/sbin/su",
                    "/system/bin/su",
                    "/system/xbin/su",
                    "/data/local/xbin/su",
                    "/data/local/bin/su",
                    "/system/sd/xbin/su",
                    "/system/bin/failsafe/su",
                    "/data/local/su",
                    "/su/bin/su",
                )

            return suPaths.any { path ->
                File(path).exists()
            }
        }

        /**
         * Method 2: Check for root management apps
         */
        private fun checkRootMethod2(): Boolean {
            val rootApps =
                arrayOf(
                    "com.noshufou.android.su",
                    "com.noshufou.android.su.elite",
                    "eu.chainfire.supersu",
                    "com.koushikdutta.superuser",
                    "com.thirdparty.superuser",
                    "com.yellowes.su",
                    "com.topjohnwu.magisk",
                )

            val pm = context.packageManager
            return rootApps.any { packageName ->
                try {
                    pm.getPackageInfo(packageName, 0)
                    true
                } catch (e: Exception) {
                    false
                }
            }
        }

        /**
         * Method 3: Check if /system is writable (should be read-only)
         */
        private fun checkRootMethod3(): Boolean {
            return try {
                val process = Runtime.getRuntime().exec(arrayOf("mount"))
                val output = process.inputStream.bufferedReader().readText()
                process.waitFor()

                // Check if /system is mounted as rw (read-write)
                output.contains("/system") && output.contains("rw,")
            } catch (e: Exception) {
                false
            }
        }

        /**
         * Method 4: Check build tags for test-keys
         */
        private fun checkBuildTags(): Boolean {
            val buildTags = Build.TAGS
            return buildTags != null && buildTags.contains("test-keys")
        }

        /**
         * Get detailed root detection report
         *
         * @return Map of detection method to result
         */
        fun getRootDetectionReport(): Map<String, Boolean> {
            return mapOf(
                "SU Binary Found" to checkRootMethod1(),
                "Root Management App Found" to checkRootMethod2(),
                "System Partition Writable" to checkRootMethod3(),
                "Test Keys in Build Tags" to checkBuildTags(),
            )
        }

        /**
         * Check if device is an emulator
         *
         * Emulators might bypass some security checks
         */
        fun isEmulator(): Boolean {
            return (
                Build.FINGERPRINT.startsWith("generic") ||
                    Build.FINGERPRINT.startsWith("unknown") ||
                    Build.MODEL.contains("google_sdk") ||
                    Build.MODEL.contains("Emulator") ||
                    Build.MODEL.contains("Android SDK built for x86") ||
                    Build.MANUFACTURER.contains("Genymotion") ||
                    Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic") ||
                    "google_sdk" == Build.PRODUCT
            )
        }
    }
