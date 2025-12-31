package com.hesapgunlugu.app.feature.statistics

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

/**
 * UI tests for StatisticsScreen
 */
@HiltAndroidTest
class StatisticsScreenTest {
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
        // Navigate to statistics
        composeTestRule.onNodeWithText(string(AppR.string.nav_statistics)).performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun statisticsScreen_displaysTotalIncomeAndExpense() {
        composeTestRule.onNodeWithText(string(R.string.total_income)).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.total_expense)).assertIsDisplayed()
    }

    @Test
    fun statisticsScreen_displaysPieChart() {
        composeTestRule.onNodeWithTag("pie_chart").assertIsDisplayed()
    }

    @Test
    fun statisticsScreen_displaysBarChart() {
        composeTestRule.onNodeWithTag("bar_chart").assertIsDisplayed()
    }

    @Test
    fun statisticsScreen_dateRangeSelector_works() {
        composeTestRule.onNodeWithText(string(R.string.monthly)).assertIsDisplayed().performClick()
        composeTestRule.onNodeWithText(string(R.string.yearly)).performClick()
        composeTestRule.waitForIdle()
        // Verify data updated
    }

    @Test
    fun statisticsScreen_categoryList_isScrollable() {
        composeTestRule.onNodeWithTag("category_list")
            .performScrollToIndex(2)

        composeTestRule.waitForIdle()
    }

    @Test
    fun emptyStatistics_showsEmptyState() {
        // With empty data
        composeTestRule.onNodeWithText(string(R.string.no_data_yet)).assertExists()
    }

    @Test
    fun statisticsScreen_filterByDateRange_updatesData() {
        composeTestRule.onNodeWithText(string(R.string.weekly)).performClick()
        composeTestRule.waitForIdle()

        // Verify date range changed
        composeTestRule.onNode(hasText(string(R.string.weekly)) and isSelected()).assertExists()
    }
}
