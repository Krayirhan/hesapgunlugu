package com.hesapgunlugu.app.benchmark

import androidx.benchmark.macro.BaselineProfileMode
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Scroll Performance Benchmark
 *
 * Measures frame timing during scrolling operations
 * Detects jank (dropped frames) and performance issues
 *
 * Metrics:
 * - frameOverrunMs: How many ms frames are overrunning 16.6ms budget
 * - frameDurationCpuMs: CPU time per frame
 */
@LargeTest
@RunWith(AndroidJUnit4::class)
class ScrollBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    /**
     * Measures scroll performance on History screen with transaction list
     */
    @Test
    fun scrollHistoryList() =
        benchmarkRule.measureRepeated(
            packageName = "com.hesapgunlugu.app",
            metrics = listOf(FrameTimingMetric()),
            compilationMode =
                CompilationMode.Partial(
                    baselineProfileMode = BaselineProfileMode.Require,
                ),
            startupMode = StartupMode.WARM,
            iterations = 5,
            setupBlock = {
                pressHome()
                startActivityAndWait()

                // Navigate to History screen
                device.wait(Until.hasObject(By.desc("History")), 5000)
                val historyTab = device.findObject(By.desc("History"))
                historyTab?.click()
                device.waitForIdle()
            },
        ) {
            // Find scrollable container
            val scrollable =
                device.findObject(
                    By.scrollable(true),
                )

            // Scroll down and up to measure performance
            scrollable?.apply {
                // Fling down
                setGestureMargin(device.displayWidth / 5)
                fling(Direction.DOWN)
                device.waitForIdle()

                // Fling up
                fling(Direction.UP)
                device.waitForIdle()
            }
        }

    /**
     * Measures scroll performance on Statistics screen with charts
     */
    @Test
    fun scrollStatisticsScreen() =
        benchmarkRule.measureRepeated(
            packageName = "com.hesapgunlugu.app",
            metrics = listOf(FrameTimingMetric()),
            compilationMode =
                CompilationMode.Partial(
                    baselineProfileMode = BaselineProfileMode.Require,
                ),
            startupMode = StartupMode.WARM,
            iterations = 5,
            setupBlock = {
                pressHome()
                startActivityAndWait()

                // Navigate to Statistics screen
                device.wait(Until.hasObject(By.desc("Statistics")), 5000)
                val statsTab = device.findObject(By.desc("Statistics"))
                statsTab?.click()
                device.waitForIdle()
            },
        ) {
            val scrollable = device.findObject(By.scrollable(true))

            scrollable?.apply {
                setGestureMargin(device.displayWidth / 5)

                // Scroll through charts
                fling(Direction.DOWN)
                device.waitForIdle()

                fling(Direction.UP)
                device.waitForIdle()
            }
        }
}
