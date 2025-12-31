package com.hesapgunlugu.app.feature.settings

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNode
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hesapgunlugu.app.core.ui.theme.HesapGunluguTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Locale
import com.hesapgunlugu.app.core.ui.R as CoreUiR

/**
 * UI tests for SettingsScreen.
 */
@RunWith(AndroidJUnit4::class)
class SettingsScreenUITest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val context: Context = ApplicationProvider.getApplicationContext()

    private fun string(
        @StringRes id: Int,
    ) = context.getString(id)

    private val mockState =
        SettingsState(
            isPinEnabled = false,
            isBiometricEnabled = false,
            isDarkModeEnabled = false,
            isNotificationsEnabled = true,
            currency = string(CoreUiR.string.currency_code_try),
            language = Locale.getDefault().language,
            monthlyBudget = 5000.0,
        )

    @Test
    fun settingsScreen_displaysAllSections() {
        composeTestRule.setContent {
            HesapGunluguTheme {
                SettingsContent(
                    state = mockState,
                    onEvent = {},
                    onNavigateBack = {},
                )
            }
        }

        composeTestRule.onNodeWithText(string(R.string.security)).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.notifications_section)).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.appearance_theme)).assertIsDisplayed()
    }

    @Test
    fun settingsScreen_pinToggle_isClickable() {
        var pinToggled = false

        composeTestRule.setContent {
            HesapGunluguTheme {
                SettingsContent(
                    state = mockState,
                    onEvent = { event ->
                        if (event is SettingsEvent.TogglePin) {
                            pinToggled = true
                        }
                    },
                    onNavigateBack = {},
                )
            }
        }

        composeTestRule
            .onNode(hasTestTag("pin_toggle"))
            .performClick()

        assert(pinToggled)
    }

    @Test
    fun settingsScreen_biometricToggle_whenPinDisabled_isDisabled() {
        composeTestRule.setContent {
            HesapGunluguTheme {
                SettingsContent(
                    state = mockState.copy(isPinEnabled = false),
                    onEvent = {},
                    onNavigateBack = {},
                )
            }
        }

        composeTestRule
            .onNode(hasTestTag("biometric_toggle"))
            .assertIsNotEnabled()
    }

    @Test
    fun settingsScreen_currencySelection_displaysCorrectValue() {
        composeTestRule.setContent {
            HesapGunluguTheme {
                SettingsContent(
                    state = mockState,
                    onEvent = {},
                    onNavigateBack = {},
                )
            }
        }

        composeTestRule
            .onNodeWithText(string(CoreUiR.string.currency_code_try))
            .assertIsDisplayed()
    }

    @Test
    fun settingsScreen_budgetInput_acceptsNumericInput() {
        val inputValue = 10000.toString()

        composeTestRule.setContent {
            HesapGunluguTheme {
                SettingsContent(
                    state = mockState,
                    onEvent = {},
                    onNavigateBack = {},
                )
            }
        }

        composeTestRule
            .onNode(hasTestTag("budget_input"))
            .performTextInput(inputValue)

        composeTestRule
            .onNode(hasTestTag("budget_input"))
            .assertTextContains(inputValue)
    }

    @Test
    fun settingsScreen_themeSelector_changesTheme() {
        composeTestRule.setContent {
            HesapGunluguTheme {
                SettingsContent(
                    state = mockState,
                    onEvent = {},
                    onNavigateBack = {},
                )
            }
        }

        composeTestRule
            .onNodeWithText(string(R.string.appearance_theme))
            .performClick()

        composeTestRule
            .onNodeWithText(string(R.string.dark_theme))
            .assertExists()
    }

    @Test
    fun settingsScreen_exportButton_isClickable() {
        composeTestRule.setContent {
            HesapGunluguTheme {
                SettingsContent(
                    state = mockState,
                    onEvent = {},
                    onNavigateBack = {},
                )
            }
        }

        composeTestRule
            .onNodeWithText(string(R.string.export_data))
            .performClick()
    }

    @Test
    fun settingsScreen_aboutButton_opensAboutDialog() {
        composeTestRule.setContent {
            HesapGunluguTheme {
                SettingsContent(
                    state = mockState,
                    onEvent = {},
                    onNavigateBack = {},
                )
            }
        }

        composeTestRule
            .onNodeWithText(string(R.string.about))
            .performClick()

        composeTestRule
            .onNodeWithText(string(R.string.version_info))
            .assertExists()
    }
}
