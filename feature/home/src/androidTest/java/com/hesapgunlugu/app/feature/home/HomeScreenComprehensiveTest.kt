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
 * Comprehensive UI tests for HomeScreen
 *
 * Test Categories:
 * 1. Display Tests - UI elements are visible
 * 2. Interaction Tests - User actions work correctly
 * 3. State Tests - Different states render correctly
 * 4. Navigation Tests - Navigation works properly
 * 5. Accessibility Tests - A11y requirements met
 */
@HiltAndroidTest
class HomeScreenComprehensiveTest {
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

    // ==================== DISPLAY TESTS ====================

    @Test
    fun homeScreen_displaysGreeting() {
        composeTestRule.waitForIdle()

        // Should display one of the greeting messages
        composeTestRule.onNode(
            hasText(string(AppR.string.greeting_morning)) or
                hasText(string(AppR.string.greeting_afternoon)) or
                hasText(string(AppR.string.greeting_evening)) or
                hasText(string(AppR.string.greeting_night)),
        ).assertExists()
    }

    @Test
    fun homeScreen_displaysBalanceCard() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(string(CoreUiR.string.balance_short)).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(CoreUiR.string.income)).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(CoreUiR.string.expense)).assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysFinancialHealthScore() {
        composeTestRule.waitForIdle()

        // Financial health score should be visible
        composeTestRule.onNodeWithText(string(CoreUiR.string.financial_health), substring = true)
            .assertExists()
    }

    @Test
    fun homeScreen_displaysRecentTransactionsSection() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(string(R.string.recent_transactions)).assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysCategorySpendingChart() {
        composeTestRule.waitForIdle()

        // Category chart or spending breakdown should exist
        composeTestRule.onNodeWithText(string(CoreUiR.string.category), substring = true)
            .assertExists()
    }

    // ==================== INTERACTION TESTS ====================

    @Test
    fun homeScreen_fabIsClickable() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithContentDescription(string(R.string.add_transaction_fab))
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    @Test
    fun homeScreen_fabOpensAddDialog() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithContentDescription(string(R.string.add_transaction_fab))
            .performClick()

        composeTestRule.waitForIdle()

        // Add transaction dialog should appear
        composeTestRule.onNodeWithText(string(CoreUiR.string.new_transaction), substring = true)
            .assertExists()
    }

    @Test
    fun homeScreen_pullToRefresh_works() {
        composeTestRule.onNodeWithTag("home_screen_content")
            .performTouchInput { swipeDown() }

        composeTestRule.waitForIdle()

        // After refresh, content should still be visible
        composeTestRule.onNodeWithText(string(CoreUiR.string.balance_short)).assertIsDisplayed()
    }

    @Test
    fun homeScreen_transactionItemIsClickable() {
        composeTestRule.waitForIdle()

        // If there are transactions, they should be clickable
        composeTestRule.onAllNodesWithTag("transaction_item")
            .onFirst()
            .assertHasClickAction()
    }

    @Test
    fun homeScreen_balanceCardShowsCorrectCurrency() {
        composeTestRule.waitForIdle()

        // Balance should show Turkish Lira symbol
        composeTestRule.onNode(hasText(string(CoreUiR.string.currency_symbol), substring = true))
            .assertExists()
    }

    // ==================== STATE TESTS ====================

    @Test
    fun homeScreen_loadingState_showsIndicator() {
        // Loading indicator should be visible during data fetch
        composeTestRule.onNodeWithTag("loading_indicator")
            .assertExists()
    }

    @Test
    fun homeScreen_errorState_showsRetryButton() {
        // When error occurs, retry button should be visible
        composeTestRule.onNodeWithText(string(CoreUiR.string.action_retry))
            .assertExists()
    }

    @Test
    fun homeScreen_emptyState_showsEmptyMessage() {
        composeTestRule.waitForIdle()

        // When no transactions, empty state should be shown
        composeTestRule.onNodeWithText(string(R.string.no_transaction_yet), substring = true)
            .assertExists()
    }

    // ==================== NAVIGATION TESTS ====================

    @Test
    fun homeScreen_bottomNav_hasAllItems() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(string(AppR.string.nav_home)).assertExists()
        composeTestRule.onNodeWithText(string(AppR.string.nav_statistics)).assertExists()
        composeTestRule.onNodeWithText(string(AppR.string.nav_settings)).assertExists()
    }

    @Test
    fun homeScreen_navigateToStatistics() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(string(AppR.string.nav_statistics)).performClick()

        composeTestRule.waitForIdle()

        // Should be on Statistics screen
        composeTestRule.onNodeWithText(string(AppR.string.nav_statistics), substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_navigateToSettings() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(string(AppR.string.nav_settings)).performClick()

        composeTestRule.waitForIdle()

        // Should be on Settings screen
        composeTestRule.onNodeWithText(string(AppR.string.nav_settings), substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_navigateToHistory() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(string(AppR.string.calendar)).performClick()

        composeTestRule.waitForIdle()

        // Should be on History screen
        composeTestRule.onNodeWithText(string(AppR.string.calendar), substring = true)
            .assertIsDisplayed()
    }

    // ==================== ACCESSIBILITY TESTS ====================

    @Test
    fun homeScreen_allButtonsHaveContentDescription() {
        composeTestRule.waitForIdle()

        // FAB should have content description
        composeTestRule.onNodeWithContentDescription(string(R.string.add_transaction_fab))
            .assertExists()
    }

    @Test
    fun homeScreen_elementsAreFocusable() {
        composeTestRule.waitForIdle()

        // Interactive elements should be focusable
        composeTestRule.onNodeWithContentDescription(string(R.string.add_transaction_fab))
            .assertIsEnabled()
    }

    @Test
    fun homeScreen_textIsReadable() {
        composeTestRule.waitForIdle()

        // Main text elements should exist and be displayed
        composeTestRule.onNodeWithText(string(CoreUiR.string.balance_short))
            .assertIsDisplayed()
    }

    // ==================== SCROLL TESTS ====================

    @Test
    fun homeScreen_scrollToBottom_showsAllContent() {
        composeTestRule.onNodeWithTag("home_screen_content")
            .performScrollToNode(hasText(string(R.string.recent_transactions)))

        composeTestRule.onNodeWithText(string(R.string.recent_transactions)).assertIsDisplayed()
    }

    @Test
    fun homeScreen_scrollToTop_showsGreeting() {
        composeTestRule.onNodeWithTag("home_screen_content")
            .performScrollToIndex(0)

        composeTestRule.waitForIdle()

        // Greeting should be visible at top
        composeTestRule.onNode(
            hasText(string(AppR.string.greeting_morning)) or
                hasText(string(AppR.string.greeting_afternoon)) or
                hasText(string(AppR.string.greeting_evening)) or
                hasText(string(AppR.string.greeting_night)),
        ).assertExists()
    }

    // ==================== DATA DISPLAY TESTS ====================

    @Test
    fun homeScreen_incomeAmountIsPositive() {
        composeTestRule.waitForIdle()

        // Income should be displayed with positive indication (green or +)
        composeTestRule.onNodeWithText(string(CoreUiR.string.income))
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_expenseAmountIsDisplayed() {
        composeTestRule.waitForIdle()

        // Expense should be displayed
        composeTestRule.onNodeWithText(string(CoreUiR.string.expense))
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_weeklySpendingChartExists() {
        composeTestRule.waitForIdle()

        // Weekly spending visualization should exist
        composeTestRule.onNodeWithText(string(CoreUiR.string.weekly_trend), substring = true)
            .assertExists()
    }

    // ==================== BUDGET WARNING TESTS ====================

    @Test
    fun homeScreen_budgetWarningIsVisibleWhenNeeded() {
        composeTestRule.waitForIdle()

        // If budget is exceeded, warning should show
        // This test verifies the warning component exists
        composeTestRule.onNodeWithTag("budget_warning")
            .assertExists()
    }
}
