package com.hesapgunlugu.app.feature.home

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNode
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollToIndex
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hesapgunlugu.app.core.domain.model.Transaction
import com.hesapgunlugu.app.core.domain.model.TransactionType
import com.hesapgunlugu.app.core.ui.theme.HesapGunluguTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.text.NumberFormat
import java.util.Locale
import com.hesapgunlugu.app.core.ui.R as CoreUiR

/**
 * UI tests for HomeScreen components.
 */
@RunWith(AndroidJUnit4::class)
class HomeScreenUITest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val context: Context = ApplicationProvider.getApplicationContext()

    private fun string(
        @StringRes id: Int,
    ) = context.getString(id)

    private fun formatNumber(value: Int) = NumberFormat.getNumberInstance(Locale.getDefault()).format(value)

    private val mockState =
        HomeState(
            totalBalance = 5000.0,
            totalIncome = 10000.0,
            totalExpense = 5000.0,
            greeting = string(R.string.test_greeting_morning),
            recentTransactions =
                listOf(
                    Transaction(
                        id = 1,
                        title = string(R.string.test_transaction_title_market),
                        amount = 250.0,
                        type = TransactionType.EXPENSE,
                        category = string(CoreUiR.string.category_food),
                        date = System.currentTimeMillis(),
                    ),
                    Transaction(
                        id = 2,
                        title = string(R.string.test_transaction_title_salary),
                        amount = 10000.0,
                        type = TransactionType.INCOME,
                        category = string(CoreUiR.string.category_salary),
                        date = System.currentTimeMillis(),
                    ),
                ),
            financialHealthScore = 75,
            savingsRate = 50f,
            isLoading = false,
        )

    @Test
    fun homeScreen_displaysGreeting() {
        composeTestRule.setContent {
            HesapGunluguTheme {
                HomeContent(
                    state = mockState,
                    onEvent = {},
                    onNavigate = {},
                )
            }
        }

        composeTestRule
            .onNodeWithText(string(R.string.test_greeting_morning))
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysBalanceCard() {
        composeTestRule.setContent {
            HesapGunluguTheme {
                HomeContent(
                    state = mockState,
                    onEvent = {},
                    onNavigate = {},
                )
            }
        }

        composeTestRule
            .onNodeWithText(formatNumber(5000), substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysRecentTransactions() {
        composeTestRule.setContent {
            HesapGunluguTheme {
                HomeContent(
                    state = mockState,
                    onEvent = {},
                    onNavigate = {},
                )
            }
        }

        composeTestRule
            .onNodeWithText(string(R.string.test_transaction_title_market))
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(string(R.string.test_transaction_title_salary))
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_loadingState_showsProgressIndicator() {
        val loadingState = mockState.copy(isLoading = true)

        composeTestRule.setContent {
            HesapGunluguTheme {
                HomeContent(
                    state = loadingState,
                    onEvent = {},
                    onNavigate = {},
                )
            }
        }

        composeTestRule
            .onNode(hasTestTag("loading_indicator"))
            .assertExists()
    }

    @Test
    fun homeScreen_errorState_showsErrorMessage() {
        val errorState =
            mockState.copy(
                isLoading = false,
                error = string(R.string.test_error_message),
            )

        composeTestRule.setContent {
            HesapGunluguTheme {
                HomeContent(
                    state = errorState,
                    onEvent = {},
                    onNavigate = {},
                )
            }
        }

        composeTestRule
            .onNodeWithText(string(R.string.test_error_message))
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_scrollable_canScrollToBottom() {
        composeTestRule.setContent {
            HesapGunluguTheme {
                HomeContent(
                    state = mockState,
                    onEvent = {},
                    onNavigate = {},
                )
            }
        }

        composeTestRule
            .onNodeWithText(string(R.string.recent_transactions))
            .assertIsDisplayed()

        composeTestRule
            .onNode(hasTestTag("home_content"))
            .performScrollToIndex(2)
    }

    @Test
    fun homeScreen_transactionList_displaysItems() {
        composeTestRule.setContent {
            HesapGunluguTheme {
                HomeContent(
                    state = mockState,
                    onEvent = {},
                    onNavigate = {},
                )
            }
        }

        composeTestRule
            .onNodeWithText(string(CoreUiR.string.income))
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(string(CoreUiR.string.expense))
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_emptyState_displayedWhenNoTransactions() {
        val emptyState = mockState.copy(recentTransactions = emptyList())

        composeTestRule.setContent {
            HesapGunluguTheme {
                HomeContent(
                    state = emptyState,
                    onEvent = {},
                    onNavigate = {},
                )
            }
        }

        composeTestRule
            .onNodeWithText(string(R.string.no_transaction_yet))
            .assertIsDisplayed()
    }
}
