package com.hesapgunlugu.app.benchmark

import androidx.benchmark.macro.BaselineProfileMode
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import com.hesapgunlugu.app.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Navigation Performance Benchmark
 *
 * Measures screen transition performance
 * Tests navigation between different screens
 */
@LargeTest
@RunWith(AndroidJUnit4::class)
class NavigationBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    /**
     * Measures performance of navigating between all main screens
     */
    @Test
    fun navigationBetweenScreens() =
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
            },
        ) {
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            val navStatistics = context.getString(R.string.nav_statistics)
            val navScheduled = context.getString(R.string.nav_scheduled)
            val navCalendar = context.getString(R.string.calendar)
            val navSettings = context.getString(R.string.nav_settings)
            val navHome = context.getString(R.string.nav_home)

            device.wait(Until.hasObject(By.desc(navStatistics)), 3000)
            device.findObject(By.desc(navStatistics))?.click()
            device.waitForIdle()

            device.wait(Until.hasObject(By.desc(navScheduled)), 3000)
            device.findObject(By.desc(navScheduled))?.click()
            device.waitForIdle()

            device.wait(Until.hasObject(By.desc(navCalendar)), 3000)
            device.findObject(By.desc(navCalendar))?.click()
            device.waitForIdle()

            device.wait(Until.hasObject(By.desc(navSettings)), 3000)
            device.findObject(By.desc(navSettings))?.click()
            device.waitForIdle()

            device.wait(Until.hasObject(By.desc(navHome)), 3000)
            device.findObject(By.desc(navHome))?.click()
            device.waitForIdle()
        }
}
