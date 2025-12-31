package com.hesapgunlugu.app.benchmark

import androidx.benchmark.macro.BaselineProfileMode
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Startup Benchmark Tests
 *
 * Measures app startup time with different compilation modes:
 * - COLD: First launch after installation
 * - WARM: Launch after app was in background
 * - HOT: Launch when app is already in memory
 *
 * Run with:
 * ```
 * ./gradlew :benchmark-macro:connectedBenchmarkAndroidTest
 * ```
 *
 * Results will be in:
 * benchmark-macro/build/outputs/androidTest-results/
 */
@LargeTest
@RunWith(AndroidJUnit4::class)
class StartupBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    /**
     * Cold startup with baseline profile
     * This is the most realistic scenario for first-time users
     */
    @Test
    fun startupColdWithBaselineProfile() =
        startup(
            startupMode = StartupMode.COLD,
            compilationMode =
                CompilationMode.Partial(
                    baselineProfileMode = BaselineProfileMode.Require,
                ),
        )

    /**
     * Cold startup without any compilation
     * Worst-case scenario
     */
    @Test
    fun startupColdNoCompilation() =
        startup(
            startupMode = StartupMode.COLD,
            compilationMode = CompilationMode.None(),
        )

    /**
     * Warm startup - app was recently used
     */
    @Test
    fun startupWarm() =
        startup(
            startupMode = StartupMode.WARM,
            compilationMode =
                CompilationMode.Partial(
                    baselineProfileMode = BaselineProfileMode.Require,
                ),
        )

    /**
     * Hot startup - app is in memory
     * Best-case scenario
     */
    @Test
    fun startupHot() =
        startup(
            startupMode = StartupMode.HOT,
            compilationMode =
                CompilationMode.Partial(
                    baselineProfileMode = BaselineProfileMode.Require,
                ),
        )

    private fun startup(
        startupMode: StartupMode,
        compilationMode: CompilationMode,
    ) = benchmarkRule.measureRepeated(
        packageName = "com.hesapgunlugu.app",
        metrics = listOf(StartupTimingMetric()),
        compilationMode = compilationMode,
        iterations = 10,
        startupMode = startupMode,
        setupBlock = {
            pressHome()
        },
    ) {
        startActivityAndWait()
    }
}
