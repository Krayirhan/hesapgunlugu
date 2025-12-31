package com.hesapgunlugu.app.feature.statistics

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hesapgunlugu.app.core.ui.theme.HesapGunluguTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI tests for StatisticsScreen
 */
@RunWith(AndroidJUnit4::class)
class StatisticsScreenUITest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val context: Context = ApplicationProvider.getApplicationContext()

    private fun string(
        @StringRes id: Int,
    ) = context.getString(id)

    @Test
    fun statisticsScreen_displaysCharts() {
        // Given
        composeTestRule.setContent {
            HesapGunluguTheme {
                // Mock StatisticsScreen with data
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(string(R.string.statistics))
            .assertIsDisplayed()
    }

    @Test
    fun statisticsScreen_dateRangeSelector_isClickable() {
        // Given
        composeTestRule.setContent {
            HesapGunluguTheme {
                // Mock screen
            }
        }

        // When
        composeTestRule
            .onNodeWithText(string(R.string.monthly))
            .performClick()

        // Then
        composeTestRule
            .onNodeWithText(string(R.string.weekly))
            .assertExists()
    }

    @Test
    fun statisticsScreen_pieChart_isRendered() {
        // Given
        composeTestRule.setContent {
            HesapGunluguTheme {
                // Mock screen with pie chart
            }
        }

        // Then - Check if chart container exists
        composeTestRule
            .onNodeWithTag("PieChart")
            .assertExists()
    }

    @Test
    fun statisticsScreen_categoryList_displaysItems() {
        // Given
        composeTestRule.setContent {
            HesapGunluguTheme {
                // Mock screen with categories
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(string(R.string.category_based_spending), substring = true)
            .assertExists()
    }

    @Test
    fun statisticsScreen_emptyState_whenNoData() {
        // Given
        composeTestRule.setContent {
            HesapGunluguTheme {
                // Mock screen with no data
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(string(R.string.no_data_yet))
            .assertIsDisplayed()
    }
}
