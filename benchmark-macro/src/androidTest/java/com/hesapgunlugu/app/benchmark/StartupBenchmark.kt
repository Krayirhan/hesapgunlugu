package com.hesapgunlugu.app.benchmark

import androidx.benchmark.macro.MacrobenchmarkRule
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MeasureBlock
import androidx.benchmark.macro.junit4.MacrobenchmarkScope
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Simple cold/warm startup benchmark to validate startup perf in CI.
 * Run with: ./gradlew :benchmark-macro:connectedBenchmarkAndroidTest
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class StartupBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun coldStartup() = runStartup(StartupMode.COLD)

    @Test
    fun warmStartup() = runStartup(StartupMode.WARM)

    private fun runStartup(mode: StartupMode) {
        benchmarkRule.measureRepeated(
            packageName = "com.hesapgunlugu.app",
            metrics = listOf(androidx.benchmark.macro.StartupTimingMetric()),
            compilationMode = androidx.benchmark.macro.CompilationMode.Partial,
            startupMode = mode,
            iterations = 3
        ) {
            pressHome()
            startActivityAndWait()
        }
    }
}
