package com.hesapgunlugu.app.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import com.hesapgunlugu.app.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Baseline Profile Generator
 *
 * Generates a baseline profile for the app to improve startup performance.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class BaselineProfileGenerator {
    @get:Rule
    val rule = BaselineProfileRule()

    @Test
    fun generateBaselineProfile() {
        rule.collect(
            packageName = "com.hesapgunlugu.app",
            includeInStartupProfile = true,
            profileBlock = {
                val context = InstrumentationRegistry.getInstrumentation().targetContext
                val totalBalance = context.getString(R.string.total_balance)
                val addTransaction = context.getString(R.string.add_transaction_fab)
                val navTransactions = context.getString(R.string.nav_transactions)
                val navStatistics = context.getString(R.string.nav_statistics)
                val statisticsTitle = context.getString(R.string.statistics)
                val navScheduled = context.getString(R.string.nav_scheduled)
                val scheduledTitle = context.getString(R.string.scheduled_payments)
                val navSettings = context.getString(R.string.nav_settings)
                val navHome = context.getString(R.string.nav_home)
                val noTransactions = context.getString(R.string.no_transactions_yet)
                val categoryMarket = context.getString(R.string.category_market)
                val descriptionLabel = context.getString(R.string.description)
                val amountLabel = context.getString(R.string.amount)
                val saveLabel = context.getString(R.string.save)

                pressHome()
                startActivityAndWait()

                device.wait(Until.hasObject(By.text(totalBalance)), 10_000)
                device.waitForIdle()

                val homeScroll = device.findObject(By.scrollable(true))
                homeScroll?.scroll(Direction.DOWN, 0.5f)
                device.waitForIdle()
                homeScroll?.scroll(Direction.DOWN, 0.5f)
                device.waitForIdle()

                homeScroll?.scroll(Direction.UP, 1f)
                device.waitForIdle()

                device.findObject(By.desc(addTransaction))?.let {
                    it.click()
                    device.waitForIdle()
                    device.findObject(By.text(categoryMarket))?.click()
                    device.waitForIdle()
                    device.findObject(By.text(descriptionLabel))?.let { field ->
                        field.click()
                        field.text = "Market"
                        device.waitForIdle()
                    }
                    device.findObject(By.text(amountLabel))?.let { field ->
                        field.click()
                        field.text = "25"
                        device.waitForIdle()
                    }
                    device.findObject(By.text(saveLabel))?.click()
                    device.waitForIdle()
                }

                device.findObject(By.text(navTransactions))?.click()
                device.wait(Until.hasObject(By.text(noTransactions)), 5_000)
                device.waitForIdle()

                val historyScroll = device.findObject(By.scrollable(true))
                historyScroll?.scroll(Direction.DOWN, 0.6f)
                device.waitForIdle()
                historyScroll?.scroll(Direction.UP, 1f)
                device.waitForIdle()

                device.findObject(By.text(navStatistics))?.click()
                device.wait(Until.hasObject(By.text(statisticsTitle)), 5_000)
                device.waitForIdle()

                val statsScroll = device.findObject(By.scrollable(true))
                statsScroll?.scroll(Direction.DOWN, 0.5f)
                device.waitForIdle()
                statsScroll?.scroll(Direction.DOWN, 0.5f)
                device.waitForIdle()
                statsScroll?.scroll(Direction.UP, 1f)
                device.waitForIdle()

                device.findObject(By.text(navScheduled))?.click()
                device.wait(Until.hasObject(By.text(scheduledTitle)), 5_000)
                device.waitForIdle()

                val scheduledScroll = device.findObject(By.scrollable(true))
                scheduledScroll?.scroll(Direction.DOWN, 0.5f)
                device.waitForIdle()
                scheduledScroll?.scroll(Direction.UP, 1f)
                device.waitForIdle()

                device.findObject(By.text(navSettings))?.click()
                device.wait(Until.hasObject(By.text(navSettings)), 5_000)
                device.waitForIdle()

                val settingsScroll = device.findObject(By.scrollable(true))
                settingsScroll?.scroll(Direction.DOWN, 0.5f)
                device.waitForIdle()
                settingsScroll?.scroll(Direction.DOWN, 0.5f)
                device.waitForIdle()
                settingsScroll?.scroll(Direction.UP, 1f)
                device.waitForIdle()

                device.findObject(By.text(navHome))?.click()
                device.wait(Until.hasObject(By.text(totalBalance)), 5_000)
                device.waitForIdle()

                val finalScroll = device.findObject(By.scrollable(true))
                finalScroll?.scroll(Direction.DOWN, 1f)
                device.waitForIdle()
                finalScroll?.scroll(Direction.UP, 1f)
                device.waitForIdle()
            },
        )
    }

    /**
     * Generate startup-only baseline profile
     * Focuses on app cold start and initial screen load
     */
    @Test
    fun generateStartupProfile() {
        rule.collect(
            packageName = "com.hesapgunlugu.app",
            includeInStartupProfile = true,
            maxIterations = 10,
            profileBlock = {
                val context = InstrumentationRegistry.getInstrumentation().targetContext
                val totalBalance = context.getString(R.string.total_balance)

                pressHome()
                startActivityAndWait()
                device.wait(Until.hasObject(By.text(totalBalance)), 10_000)
                device.waitForIdle()
            },
        )
    }
}
