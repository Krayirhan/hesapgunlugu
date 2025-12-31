package com.hesapgunlugu.app.integration

import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.hesapgunlugu.app.MainActivity
import com.hesapgunlugu.app.R
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * End-to-End Integration Test
 *
 * Tests complete user flows from UI to database.
 */
@HiltAndroidTest
class TransactionFlowIntegrationTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    private fun string(id: Int) = composeTestRule.activity.getString(id)

    @Test
    fun addTransaction_shouldAppearInList() {
        // Wait for app to load
        composeTestRule.waitForIdle()

        // This test assumes PIN is disabled or already unlocked

        // Find and click FAB to add transaction
        composeTestRule.onNodeWithContentDescription(string(R.string.add_transaction_fab))
            .performClick()

        composeTestRule.waitForIdle()

        // Enter transaction details
        composeTestRule.onNodeWithText(string(R.string.title))
            .performTextInput(string(R.string.test_transaction_title))

        composeTestRule.onNodeWithText(string(R.string.amount))
            .performTextInput(string(R.string.test_amount_100))

        // Save transaction
        composeTestRule.onNodeWithText(string(R.string.save))
            .performClick()

        composeTestRule.waitForIdle()

        // Verify transaction appears in list
        composeTestRule.onNodeWithText(string(R.string.test_transaction_title))
            .assertIsDisplayed()
    }

    @Test
    fun navigateToStatistics_shouldShowCharts() {
        composeTestRule.waitForIdle()

        // Navigate to Statistics tab
        composeTestRule.onNodeWithText(string(R.string.nav_statistics))
            .performClick()

        composeTestRule.waitForIdle()

        // Verify statistics screen is displayed
        composeTestRule.onNodeWithText(string(R.string.statistics))
            .assertIsDisplayed()
    }

    @Test
    fun navigateToSettings_shouldShowOptions() {
        composeTestRule.waitForIdle()

        // Navigate to Settings
        composeTestRule.onNodeWithContentDescription(string(R.string.nav_settings))
            .performClick()

        composeTestRule.waitForIdle()

        // Verify settings screen elements
        composeTestRule.onNodeWithText(string(R.string.settings))
            .assertIsDisplayed()
    }

    @Test
    fun bottomNavigation_shouldSwitchScreens() {
        composeTestRule.waitForIdle()

        // Test navigation between tabs
        val tabs =
            listOf(
                string(R.string.nav_home),
                string(R.string.calendar),
                string(R.string.nav_statistics),
            )

        tabs.forEach { tabName ->
            composeTestRule.onNodeWithText(tabName)
                .performClick()
            composeTestRule.waitForIdle()
        }

        // Return to home
        composeTestRule.onNodeWithText(string(R.string.nav_home))
            .performClick()
        composeTestRule.waitForIdle()

        // Verify we're on home screen
        composeTestRule.onNodeWithText(string(R.string.total_balance), substring = true)
            .assertExists()
    }
}
