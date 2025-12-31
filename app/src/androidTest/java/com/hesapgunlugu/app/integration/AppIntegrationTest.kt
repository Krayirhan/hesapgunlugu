package com.hesapgunlugu.app.integration

import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import com.hesapgunlugu.app.MainActivity
import com.hesapgunlugu.app.R
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.text.NumberFormat
import java.util.Locale
import com.hesapgunlugu.app.core.ui.R as CoreUiR

/**
 * End-to-End Integration Tests
 *
 * These tests verify complete user flows across multiple screens
 * and ensure all components work together correctly.
 */
@HiltAndroidTest
class AppIntegrationTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    private fun string(id: Int) = composeTestRule.activity.getString(id)

    private fun formatNumber(value: Int) = NumberFormat.getNumberInstance(Locale.getDefault()).format(value)

    // ==================== COMPLETE USER FLOWS ====================

    @Test
    fun userFlow_addExpenseAndVerifyBalance() {
        // Step 1: Note initial balance
        composeTestRule.waitForIdle()

        // Step 2: Open add transaction dialog
        composeTestRule.onNodeWithContentDescription(string(R.string.add_transaction_fab))
            .performClick()

        composeTestRule.waitForIdle()

        // Step 3: Fill in expense details
        composeTestRule.onNodeWithText(string(R.string.amount))
            .performTextInput(string(R.string.test_amount_150))
        composeTestRule.onNodeWithText(string(R.string.title)).performTextInput(
            string(R.string.preview_title_market_shopping),
        )

        // Step 4: Select expense type
        composeTestRule.onNodeWithText(string(R.string.expense)).performClick()

        // Step 5: Select category
        composeTestRule.onNodeWithText(string(R.string.category)).performClick()
        composeTestRule.onNodeWithText(string(R.string.category_market)).performClick()

        // Step 6: Save transaction
        composeTestRule.onNodeWithText(string(R.string.save)).performClick()

        composeTestRule.waitForIdle()

        // Step 7: Verify transaction appears in recent transactions
        composeTestRule.onNodeWithText(string(R.string.preview_title_market_shopping))
            .assertIsDisplayed()
    }

    @Test
    fun userFlow_addIncomeAndVerifyStats() {
        // Step 1: Add income
        composeTestRule.onNodeWithContentDescription(string(R.string.add_transaction_fab))
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(string(R.string.amount))
            .performTextInput(string(R.string.test_amount_5000))
        composeTestRule.onNodeWithText(string(R.string.title)).performTextInput(
            string(R.string.preview_title_salary),
        )
        composeTestRule.onNodeWithText(string(R.string.income)).performClick()
        composeTestRule.onNodeWithText(string(R.string.save)).performClick()

        composeTestRule.waitForIdle()

        // Step 2: Navigate to Statistics
        composeTestRule.onNodeWithText(string(R.string.nav_statistics)).performClick()

        composeTestRule.waitForIdle()

        // Step 3: Verify income is reflected
        composeTestRule.onNodeWithText(formatNumber(5000), substring = true)
            .assertExists()
    }

    @Test
    fun userFlow_scheduledPaymentReminder() {
        // Step 1: Navigate to Scheduled Payments
        composeTestRule.onNodeWithText(string(R.string.nav_scheduled)).performClick()

        composeTestRule.waitForIdle()

        // Step 2: Add new scheduled payment
        composeTestRule.onNodeWithContentDescription(string(CoreUiR.string.add_scheduled_transaction))
            .performClick()

        composeTestRule.waitForIdle()

        // Step 3: Fill details
        composeTestRule.onNodeWithText(string(R.string.title))
            .performTextInput(string(CoreUiR.string.category_rent))
        composeTestRule.onNodeWithText(string(R.string.amount))
            .performTextInput(string(R.string.test_amount_8000))

        // Step 4: Save
        composeTestRule.onNodeWithText(string(R.string.save)).performClick()

        composeTestRule.waitForIdle()

        // Step 5: Verify it appears in list
        composeTestRule.onNodeWithText(string(CoreUiR.string.category_rent)).assertIsDisplayed()
    }

    @Test
    fun userFlow_changeThemeAndVerify() {
        // Step 1: Navigate to Settings
        composeTestRule.onNodeWithText(string(R.string.nav_settings)).performClick()

        composeTestRule.waitForIdle()

        // Step 2: Find theme toggle
        composeTestRule.onNodeWithText(string(R.string.dark_theme)).assertIsDisplayed()

        // Step 3: Toggle theme
        composeTestRule.onNodeWithText(string(R.string.dark_theme))
            .performClick()

        composeTestRule.waitForIdle()

        // Step 4: Navigate back to home
        composeTestRule.onNodeWithText(string(R.string.nav_home)).performClick()

        // Step 5: Verify app still works
        composeTestRule.onNodeWithText(string(R.string.balance)).assertIsDisplayed()
    }

    @Test
    fun userFlow_exportData() {
        // Step 1: Navigate to Settings
        composeTestRule.onNodeWithText(string(R.string.nav_settings)).performClick()

        composeTestRule.waitForIdle()

        // Step 2: Find export option
        composeTestRule.onNodeWithText(string(R.string.export_data), substring = true)
            .performScrollTo()
            .assertIsDisplayed()

        // Step 3: Click export
        composeTestRule.onNodeWithText(string(R.string.export_data), substring = true)
            .performClick()

        composeTestRule.waitForIdle()

        // Step 4: Password dialog should appear
        composeTestRule.onNodeWithText(string(R.string.backup_password_title)).assertIsDisplayed()
    }

    @Test
    fun userFlow_searchTransactions() {
        // Step 1: Navigate to History
        composeTestRule.onNodeWithText(string(R.string.calendar)).performClick()

        composeTestRule.waitForIdle()

        // Step 2: Find search field
        composeTestRule.onNodeWithTag("search_field")
            .assertIsDisplayed()

        // Step 3: Enter search query
        composeTestRule.onNodeWithTag("search_field")
            .performTextInput(string(R.string.category_market))

        composeTestRule.waitForIdle()

        // Step 4: Verify filtered results
        composeTestRule.onAllNodesWithText(string(R.string.category_market), substring = true)
            .assertCountEquals(1)
    }

    @Test
    fun userFlow_filterByCategory() {
        // Step 1: Navigate to Statistics
        composeTestRule.onNodeWithText(string(R.string.nav_statistics)).performClick()

        composeTestRule.waitForIdle()

        // Step 2: Find category filter
        composeTestRule.onNodeWithText(string(R.string.category), substring = true)
            .assertExists()

        // Step 3: Select a category
        composeTestRule.onNodeWithText(string(R.string.category_market)).performClick()

        composeTestRule.waitForIdle()

        // Step 4: Verify filtered view
        composeTestRule.onNodeWithText(string(R.string.category_market))
            .assertIsDisplayed()
    }

    @Test
    fun userFlow_budgetSetupAndWarning() {
        // Step 1: Navigate to Settings
        composeTestRule.onNodeWithText(string(R.string.nav_settings)).performClick()

        composeTestRule.waitForIdle()

        // Step 2: Set monthly budget
        composeTestRule.onNodeWithText(string(R.string.monthly_limit), substring = true)
            .performScrollTo()
            .performClick()

        composeTestRule.waitForIdle()

        // Step 3: Enter budget amount
        composeTestRule.onNodeWithTag("budget_input")
            .performTextInput(string(R.string.test_amount_10000))

        composeTestRule.onNodeWithText(string(R.string.save)).performClick()

        composeTestRule.waitForIdle()

        // Step 4: Go back to Home
        composeTestRule.onNodeWithText(string(R.string.nav_home)).performClick()

        // Step 5: Verify budget is set (progress indicator visible)
        composeTestRule.onNodeWithTag("budget_progress")
            .assertExists()
    }

    // ==================== SECURITY FLOW TESTS ====================

    @Test
    fun userFlow_enablePinLock() {
        // Step 1: Navigate to Settings
        composeTestRule.onNodeWithText(string(R.string.nav_settings)).performClick()

        composeTestRule.waitForIdle()

        // Step 2: Find security settings
        composeTestRule.onNodeWithText(string(R.string.security), substring = true)
            .performScrollTo()
            .performClick()

        composeTestRule.waitForIdle()

        // Step 3: Enable PIN lock
        composeTestRule.onNodeWithText(string(R.string.enable_pin))
            .assertIsDisplayed()
    }

    @Test
    fun userFlow_editTransaction_updatesTitle() {
        // Add a transaction
        composeTestRule.onNodeWithContentDescription(string(R.string.add_transaction_fab))
            .performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(string(R.string.title))
            .performTextInput("Edit Me")
        composeTestRule.onNodeWithText(string(R.string.amount))
            .performTextInput("123")
        composeTestRule.onNodeWithText(string(R.string.save)).performClick()
        composeTestRule.waitForIdle()

        // Open History and select the transaction
        composeTestRule.onNodeWithText(string(R.string.calendar)).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Edit Me")
            .performClick()
        composeTestRule.waitForIdle()

        // Open edit screen
        composeTestRule.onNodeWithContentDescription(string(R.string.edit_transaction))
            .performClick()
        composeTestRule.waitForIdle()

        // Update title
        composeTestRule.onNodeWithText(string(R.string.title))
            .performTextClearance()
        composeTestRule.onNodeWithText(string(R.string.title))
            .performTextInput("Edited")
        composeTestRule.onNodeWithText(string(R.string.save)).performClick()
        composeTestRule.waitForIdle()

        // Verify updated title
        composeTestRule.onNodeWithText("Edited").assertIsDisplayed()
    }

    @Test
    fun userFlow_pinSetupDialog_completes() {
        composeTestRule.onNodeWithText(string(R.string.nav_settings)).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(string(R.string.security), substring = true)
            .performScrollTo()

        composeTestRule.onNode(
            isToggleable().and(hasAnyAncestor(hasText(string(R.string.app_lock_title)))),
        ).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(string(R.string.pin_setup))
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(string(R.string.pin_label))
            .performTextInput("2468")
        composeTestRule.onNodeWithText(string(R.string.next)).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(string(R.string.pin_label))
            .performTextInput("2468")
        composeTestRule.onNodeWithText(string(R.string.save)).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(string(R.string.pin_active))
            .assertIsDisplayed()
    }

    // ==================== NAVIGATION FLOW TESTS ====================

    @Test
    fun navigation_allTabsAccessible() {
        val tabs =
            listOf(
                string(R.string.nav_home),
                string(R.string.calendar),
                string(R.string.nav_scheduled),
                string(R.string.nav_statistics),
                string(R.string.nav_settings),
            )

        tabs.forEach { tabName ->
            composeTestRule.onNodeWithText(tabName).performClick()
            composeTestRule.waitForIdle()

            // Each tab should have content
            composeTestRule.onRoot().assertExists()
        }
    }

    @Test
    fun navigation_backButtonWorks() {
        // Navigate deep
        composeTestRule.onNodeWithText(string(R.string.nav_settings)).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(string(R.string.security), substring = true)
            .performScrollTo()
            .performClick()

        composeTestRule.waitForIdle()

        // Press back
        composeTestRule.activityRule.scenario.onActivity { activity ->
            activity.onBackPressedDispatcher.onBackPressed()
        }

        composeTestRule.waitForIdle()

        // Should be back at Settings
        composeTestRule.onNodeWithText(string(R.string.settings)).assertIsDisplayed()
    }

    // ==================== ERROR HANDLING TESTS ====================

    @Test
    fun errorHandling_invalidAmountShowsError() {
        // Open add transaction
        composeTestRule.onNodeWithContentDescription(string(R.string.add_transaction_fab))
            .performClick()

        composeTestRule.waitForIdle()

        // Try to save without amount
        composeTestRule.onNodeWithText(string(R.string.title))
            .performTextInput(string(R.string.test_title_simple))
        composeTestRule.onNodeWithText(string(R.string.save)).performClick()

        composeTestRule.waitForIdle()

        // Error should be shown
        composeTestRule.onNodeWithText(string(R.string.error_invalid_amount), substring = true)
            .assertExists()
    }

    @Test
    fun errorHandling_emptyTitleShowsError() {
        // Open add transaction
        composeTestRule.onNodeWithContentDescription(string(R.string.add_transaction_fab))
            .performClick()

        composeTestRule.waitForIdle()

        // Try to save with only amount
        composeTestRule.onNodeWithText(string(R.string.amount))
            .performTextInput(string(R.string.test_amount_100))
        composeTestRule.onNodeWithText(string(R.string.save)).performClick()

        composeTestRule.waitForIdle()

        // Error should be shown
        composeTestRule.onNodeWithText(string(R.string.error_empty_title), substring = true)
            .assertExists()
    }
}
