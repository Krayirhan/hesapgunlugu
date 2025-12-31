package com.hesapgunlugu.app.feature.history

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ApplicationProvider
import com.hesapgunlugu.app.R
import com.hesapgunlugu.app.core.ui.theme.HesapGunluguTheme
import org.junit.Rule
import org.junit.Test

/**
 * History Screen UI Tests
 */
class HistoryScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val context: Context = ApplicationProvider.getApplicationContext()

    private fun string(
        @StringRes id: Int,
    ) = context.getString(id)

    @Test
    fun historyScreen_displaysTitle() {
        composeTestRule.setContent {
            HesapGunluguTheme {
                // Test content
            }
        }

        composeTestRule.onNodeWithText(string(R.string.account_movements), substring = true)
            .assertExists()
    }

    @Test
    fun historyScreen_displaysFilterButtons() {
        composeTestRule.setContent {
            HesapGunluguTheme {
                // Filter buttons should exist
            }
        }

        composeTestRule.onNodeWithText(string(R.string.all_types), substring = true)
            .assertExists()
    }

    @Test
    fun historyScreen_displaysPagingList() {
        composeTestRule.setContent {
            HesapGunluguTheme {
                // Paging list should exist
            }
        }

        // LazyColumn should be present
        composeTestRule.onNodeWithTag("transaction_list")
            .assertExists()
    }
}
