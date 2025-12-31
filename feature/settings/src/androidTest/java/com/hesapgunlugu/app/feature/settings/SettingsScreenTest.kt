package com.hesapgunlugu.app.feature.settings

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
 * UI tests for SettingsScreen
 */
@HiltAndroidTest
class SettingsScreenTest {
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
        // Navigate to settings
        composeTestRule.onNodeWithText(string(AppR.string.nav_settings)).performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun settingsScreen_displaysAllSections() {
        composeTestRule.onNodeWithText(string(R.string.security)).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.appearance_theme)).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.data_management)).assertIsDisplayed()
    }

    @Test
    fun settingsScreen_pinToggle_works() {
        composeTestRule.onNodeWithText(string(R.string.pin_label))
            .assertIsDisplayed()

        composeTestRule.onNode(hasText(string(R.string.pin_label)) and hasClickAction())
            .performClick()

        composeTestRule.waitForIdle()
    }

    @Test
    fun settingsScreen_biometricToggle_works() {
        composeTestRule.onNodeWithText(string(R.string.enable_biometric))
            .assertIsDisplayed()

        composeTestRule.onNode(hasText(string(R.string.enable_biometric)) and hasClickAction())
            .performClick()

        composeTestRule.waitForIdle()
    }

    @Test
    fun settingsScreen_themeSelector_showsDialog() {
        composeTestRule.onNodeWithText(string(R.string.appearance_theme)).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(string(R.string.light_theme)).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.dark_theme)).assertIsDisplayed()
    }

    @Test
    fun settingsScreen_exportButton_isClickable() {
        composeTestRule.onNodeWithText(string(R.string.export_data))
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    @Test
    fun settingsScreen_scrollToBottom_showsAboutSection() {
        composeTestRule.onNodeWithTag("settings_content")
            .performScrollToNode(hasText(string(R.string.about)))

        composeTestRule.onNodeWithText(string(R.string.about)).assertIsDisplayed()
    }

    @Test
    fun settingsScreen_privacyPolicy_opensLink() {
        composeTestRule.onNodeWithText(string(R.string.privacy_policy))
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()
    }
}
