package com.hesapgunlugu.app.feature.settings

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
import com.hesapgunlugu.app.feature.settings.R as SettingsR

/**
 * Settings Screen UI Tests
 *
 * Tests for settings screen composables.
 */
class SettingsScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val context: Context = ApplicationProvider.getApplicationContext()

    private fun string(
        @StringRes id: Int,
    ) = context.getString(id)

    @Test
    fun settingsScreen_displaysTitle() {
        composeTestRule.setContent {
            HesapGunluguTheme {
                // Test with preview content
            }
        }

        // Settings title should be displayed
        composeTestRule.onNodeWithText(string(R.string.settings), substring = true)
            .assertExists()
    }

    @Test
    fun settingsScreen_displaysSecuritySection() {
        composeTestRule.setContent {
            HesapGunluguTheme {
                // Security section should exist
            }
        }

        composeTestRule.onNodeWithText(string(R.string.security), substring = true)
            .assertExists()
    }

    @Test
    fun settingsScreen_displaysThemeSection() {
        composeTestRule.setContent {
            HesapGunluguTheme {
                // Theme section should exist
            }
        }

        composeTestRule.onNodeWithText(string(SettingsR.string.appearance_theme), substring = true)
            .assertExists()
    }
}
