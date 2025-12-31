package com.hesapgunlugu.app.feature.statistics

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ApplicationProvider
import com.hesapgunlugu.app.R
import com.hesapgunlugu.app.core.ui.theme.HesapGunluguTheme
import org.junit.Rule
import org.junit.Test

/**
 * Statistics Screen UI Tests
 */
class StatisticsScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val context: Context = ApplicationProvider.getApplicationContext()

    private fun string(
        @StringRes id: Int,
    ) = context.getString(id)

    @Test
    fun statisticsScreen_displaysTitle() {
        composeTestRule.setContent {
            HesapGunluguTheme {
                // Test content
            }
        }

        composeTestRule.onNodeWithText(string(R.string.statistics), substring = true)
            .assertExists()
    }

    @Test
    fun statisticsScreen_displaysPeriodSelector() {
        composeTestRule.setContent {
            HesapGunluguTheme {
                // Period selector should exist
            }
        }

        // Period selector buttons
        composeTestRule.onNodeWithText(string(R.string.weekly), substring = true)
            .assertExists()
    }
}
