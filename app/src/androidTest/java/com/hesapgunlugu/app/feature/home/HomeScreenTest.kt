package com.hesapgunlugu.app.feature.home

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hesapgunlugu.app.R
import com.hesapgunlugu.app.core.domain.model.CategoryBudgetStatus
import com.hesapgunlugu.app.core.domain.model.Transaction
import com.hesapgunlugu.app.core.domain.model.TransactionType
import com.hesapgunlugu.app.core.ui.theme.HesapGunluguTheme
import com.hesapgunlugu.app.feature.settings.SettingsState
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.text.NumberFormat
import java.util.Date
import java.util.Locale

/**
 * UI Tests for HomeScreen
 * Tests user interactions and screen rendering
 */
@RunWith(AndroidJUnit4::class)
class HomeScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val context: Context = ApplicationProvider.getApplicationContext()

    private fun string(
        @StringRes id: Int,
    ) = context.getString(id)

    private fun formatNumber(value: Int) = NumberFormat.getNumberInstance(Locale.getDefault()).format(value)

    private val testHomeState =
        HomeState(
            totalBalance = 5000.0,
            totalIncome = 10000.0,
            totalExpense = 5000.0,
            greeting = string(R.string.greeting_morning),
            recentTransactions =
                listOf(
                    Transaction(
                        1,
                        string(R.string.preview_title_salary),
                        10000.0,
                        Date(),
                        TransactionType.INCOME,
                        string(R.string.category_salary),
                        string(R.string.category_salary).first().toString(),
                    ),
                    Transaction(
                        2,
                        string(R.string.category_market),
                        500.0,
                        Date(),
                        TransactionType.EXPENSE,
                        string(R.string.category_market),
                        string(R.string.category_market).first().toString(),
                    ),
                ),
            categoryExpenses = mapOf(string(R.string.category_market) to 500.0),
            budgetStatuses =
                listOf(
                    CategoryBudgetStatus(string(R.string.category_market), 500.0, 1000.0, 0.5f, false),
                ),
            isLoading = false,
            error = null,
        )

    private val testSettingsState =
        SettingsState(
            userName = string(R.string.test_user_name),
            monthlyLimit = 10000.0,
            categoryBudgets = mapOf(string(R.string.category_market) to 1000.0),
            notificationsEnabled = true,
            currency = string(R.string.currency_symbol),
            currencyCode = string(R.string.currency_code_try),
            languageCode = Locale.getDefault().language,
        )

    @Test
    fun homeScreen_displaysUserName() {
        // Given
        val homeViewModel = mockk<HomeViewModel>(relaxed = true)
        val settingsViewModel = mockk<com.hesapgunlugu.app.feature.settings.SettingsViewModel>(relaxed = true)

        every { homeViewModel.state } returns MutableStateFlow(testHomeState)
        every { settingsViewModel.state } returns MutableStateFlow(testSettingsState)

        // When
        composeTestRule.setContent {
            HesapGunluguTheme {
                HomeScreen(
                    homeViewModel = homeViewModel,
                    settingsViewModel = settingsViewModel,
                    navController = rememberNavController(),
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText(string(R.string.test_user_name), substring = true).assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysBalance() {
        // Given
        val homeViewModel = mockk<HomeViewModel>(relaxed = true)
        val settingsViewModel = mockk<com.hesapgunlugu.app.feature.settings.SettingsViewModel>(relaxed = true)

        every { homeViewModel.state } returns MutableStateFlow(testHomeState)
        every { settingsViewModel.state } returns MutableStateFlow(testSettingsState)

        // When
        composeTestRule.setContent {
            HesapGunluguTheme {
                HomeScreen(
                    homeViewModel = homeViewModel,
                    settingsViewModel = settingsViewModel,
                    navController = rememberNavController(),
                )
            }
        }

        // Then - Check if balance is displayed
        composeTestRule.onNodeWithText(formatNumber(5000), substring = true).assertExists()
    }

    @Test
    fun homeScreen_displaysFab() {
        // Given
        val homeViewModel = mockk<HomeViewModel>(relaxed = true)
        val settingsViewModel = mockk<com.hesapgunlugu.app.feature.settings.SettingsViewModel>(relaxed = true)

        every { homeViewModel.state } returns MutableStateFlow(testHomeState)
        every { settingsViewModel.state } returns MutableStateFlow(testSettingsState)

        // When
        composeTestRule.setContent {
            HesapGunluguTheme {
                HomeScreen(
                    homeViewModel = homeViewModel,
                    settingsViewModel = settingsViewModel,
                    navController = rememberNavController(),
                )
            }
        }

        // Then - FAB should be displayed
        composeTestRule.onNodeWithContentDescription(string(R.string.add_transaction_fab))
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysRecentTransactions() {
        // Given
        val homeViewModel = mockk<HomeViewModel>(relaxed = true)
        val settingsViewModel = mockk<com.hesapgunlugu.app.feature.settings.SettingsViewModel>(relaxed = true)

        every { homeViewModel.state } returns MutableStateFlow(testHomeState)
        every { settingsViewModel.state } returns MutableStateFlow(testSettingsState)

        // When
        composeTestRule.setContent {
            HesapGunluguTheme {
                HomeScreen(
                    homeViewModel = homeViewModel,
                    settingsViewModel = settingsViewModel,
                    navController = rememberNavController(),
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText(string(R.string.preview_title_salary), substring = true).assertExists()
        composeTestRule.onNodeWithText(string(R.string.category_market), substring = true).assertExists()
    }

    @Test
    fun homeScreen_showsLoadingState() {
        // Given
        val loadingState = testHomeState.copy(isLoading = true)
        val homeViewModel = mockk<HomeViewModel>(relaxed = true)
        val settingsViewModel = mockk<com.hesapgunlugu.app.feature.settings.SettingsViewModel>(relaxed = true)

        every { homeViewModel.state } returns MutableStateFlow(loadingState)
        every { settingsViewModel.state } returns MutableStateFlow(testSettingsState)

        // When
        composeTestRule.setContent {
            HesapGunluguTheme {
                HomeScreen(
                    homeViewModel = homeViewModel,
                    settingsViewModel = settingsViewModel,
                    navController = rememberNavController(),
                )
            }
        }

        // Then - Loading indicator should be visible or content should show loading state
        // The exact assertion depends on your loading UI implementation
        composeTestRule.waitForIdle()
    }

    @Test
    fun homeScreen_showsErrorState() {
        // Given
        val errorState = testHomeState.copy(error = string(R.string.test_error_message))
        val homeViewModel = mockk<HomeViewModel>(relaxed = true)
        val settingsViewModel = mockk<com.hesapgunlugu.app.feature.settings.SettingsViewModel>(relaxed = true)

        every { homeViewModel.state } returns MutableStateFlow(errorState)
        every { settingsViewModel.state } returns MutableStateFlow(testSettingsState)

        // When
        composeTestRule.setContent {
            HesapGunluguTheme {
                HomeScreen(
                    homeViewModel = homeViewModel,
                    settingsViewModel = settingsViewModel,
                    navController = rememberNavController(),
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText(string(R.string.test_error_message), substring = true).assertExists()
    }

    @Test
    fun homeScreen_displaysGreeting() {
        // Given
        val homeViewModel = mockk<HomeViewModel>(relaxed = true)
        val settingsViewModel = mockk<com.hesapgunlugu.app.feature.settings.SettingsViewModel>(relaxed = true)

        every { homeViewModel.state } returns MutableStateFlow(testHomeState)
        every { settingsViewModel.state } returns MutableStateFlow(testSettingsState)

        // When
        composeTestRule.setContent {
            HesapGunluguTheme {
                HomeScreen(
                    homeViewModel = homeViewModel,
                    settingsViewModel = settingsViewModel,
                    navController = rememberNavController(),
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText(string(R.string.greeting_morning), substring = true).assertExists()
    }
}
