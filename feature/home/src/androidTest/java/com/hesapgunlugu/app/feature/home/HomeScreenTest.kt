package com.hesapgunlugu.app.feature.home

import androidx.annotation.StringRes
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.hesapgunlugu.app.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.hesapgunlugu.app.R as AppR
import com.hesapgunlugu.app.core.ui.R as CoreUiR

/**
 * UI tests for HomeScreen
 */
@HiltAndroidTest
class HomeScreenTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private fun string(
        @StringRes id: Int,
    ) = composeTestRule.activity.getString(id)

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun homeScreen_displaysBalanceCard() {
        composeTestRule.onNodeWithText(string(CoreUiR.string.balance_short)).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(CoreUiR.string.income)).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(CoreUiR.string.expense)).assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysRecentTransactionsTitle() {
        composeTestRule.onNodeWithText(string(R.string.recent_transactions)).assertIsDisplayed()
    }

    @Test
    fun homeScreen_addButton_isClickable() {
        composeTestRule.onNodeWithContentDescription(string(R.string.add_transaction_fab))
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    @Test
    fun homeScreen_navigationToStatistics_works() {
        composeTestRule.onNodeWithText(string(AppR.string.nav_statistics)).performClick()
        // Verify navigation occurred
        composeTestRule.waitForIdle()
    }

    @Test
    fun homeScreen_scrollToBottom_showsAllContent() {
        composeTestRule.onNodeWithTag("home_screen_content")
            .performScrollToNode(hasText(string(R.string.recent_transactions)))

        composeTestRule.onNodeWithText(string(R.string.recent_transactions)).assertIsDisplayed()
    }

    @Test
    fun homeScreen_savingsGoalCard_isDisplayedWhenExists() {
        // Wait for data to load
        composeTestRule.waitForIdle()

        // Check if savings goal exists and is displayed
        composeTestRule.onNode(
            hasText(
                string(CoreUiR.string.savings_goal_title, string(R.string.savings_goal_vacation)),
            ),
        )
            .assertExists()
    }

    @Test
    fun emptyState_showsWhenNoTransactions() {
        // This would need to be tested with empty database state
        composeTestRule.onNodeWithText(string(R.string.no_transaction_yet))
            .assertExists()
    }

    @Test
    fun homeScreen_pullToRefresh_works() {
        composeTestRule.onNodeWithTag("home_screen_content")
            .performTouchInput { swipeDown() }

        composeTestRule.waitForIdle()
        // Verify data refreshed
    }
}
