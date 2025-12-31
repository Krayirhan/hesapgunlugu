package com.hesapgunlugu.app.navigation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import com.hesapgunlugu.app.MainActivity
import com.hesapgunlugu.app.R
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * UI tests for app navigation
 */
@HiltAndroidTest
class NavigationTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    private fun string(id: Int) = composeTestRule.activity.getString(id)

    @Test
    fun navigationBar_hasAllItems() {
        composeTestRule.onNodeWithText(string(R.string.nav_home)).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.nav_statistics)).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.nav_scheduled)).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.calendar)).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.nav_settings)).assertIsDisplayed()
    }

    @Test
    fun navigation_homeToStatistics_works() {
        composeTestRule.onNodeWithText(string(R.string.nav_statistics)).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(string(R.string.total_income)).assertIsDisplayed()
    }

    @Test
    fun navigation_homeToScheduled_works() {
        composeTestRule.onNodeWithText(string(R.string.nav_scheduled)).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(string(R.string.scheduled_header_title)).assertIsDisplayed()
    }

    @Test
    fun navigation_homeToHistory_works() {
        composeTestRule.onNodeWithText(string(R.string.calendar)).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(string(R.string.account_movements)).assertIsDisplayed()
    }

    @Test
    fun navigation_homeToSettings_works() {
        composeTestRule.onNodeWithText(string(R.string.nav_settings)).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(string(R.string.security)).assertIsDisplayed()
    }

    @Test
    fun navigation_backToHome_works() {
        // Navigate away
        composeTestRule.onNodeWithText(string(R.string.nav_statistics)).performClick()
        composeTestRule.waitForIdle()

        // Navigate back
        composeTestRule.onNodeWithText(string(R.string.nav_home)).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(string(R.string.balance)).assertIsDisplayed()
    }

    @Test
    fun navigation_retainsState() {
        // Perform action on home
        composeTestRule.onNodeWithTag("home_screen_content")
            .performScrollToNode(hasText(string(R.string.recent_transactions)))

        // Navigate away
        composeTestRule.onNodeWithText(string(R.string.nav_statistics)).performClick()
        composeTestRule.waitForIdle()

        // Navigate back
        composeTestRule.onNodeWithText(string(R.string.nav_home)).performClick()
        composeTestRule.waitForIdle()

        // Verify state retained
        composeTestRule.onNodeWithText(string(R.string.recent_transactions)).assertIsDisplayed()
    }

    @Test
    fun fabButton_opensAddTransactionDialog() {
        composeTestRule.onNodeWithContentDescription(string(R.string.add_transaction_fab))
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(string(R.string.new_transaction)).assertIsDisplayed()
    }
}
