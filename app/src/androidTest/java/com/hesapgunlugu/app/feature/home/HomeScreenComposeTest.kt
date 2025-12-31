package com.hesapgunlugu.app.feature.home

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNode
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.hesapgunlugu.app.R
import com.hesapgunlugu.app.core.domain.model.Transaction
import com.hesapgunlugu.app.ui.theme.HesapGunluguTheme
import org.junit.Rule
import org.junit.Test
import com.hesapgunlugu.app.core.ui.R as CoreUiR

/**
 * Compose UI Tests for Home Screen Components
 *
 * Tests user interactions, state rendering, and accessibility.
 */
class HomeScreenComposeTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val context: Context = ApplicationProvider.getApplicationContext()

    private fun string(
        @StringRes id: Int,
    ) = context.getString(id)

    @Test
    fun homeScreen_displaysBalance() {
        // Given
        val state =
            HomeState(
                totalBalance = 5000.0,
                totalIncome = 8000.0,
                totalExpense = 3000.0,
                isLoading = false,
            )

        // When
        composeTestRule.setContent {
            HesapGunluguTheme {
                // HomeScreen(state) // Uncomment when HomeScreen is a composable function
            }
        }

        // Then
        composeTestRule.onNodeWithText(string(R.string.test_amount_5000)).assertExists()
    }

    @Test
    fun homeScreen_showsLoadingIndicator() {
        // Given
        val state = HomeState(isLoading = true)

        // When
        composeTestRule.setContent {
            HesapGunluguTheme {
                // HomeScreen(state)
            }
        }

        // Then
        composeTestRule.onNode(hasTestTag("loading_indicator")).assertExists()
    }

    @Test
    fun transactionItem_clickable() {
        // Given
        val transaction =
            Transaction(
                id = 1,
                title = string(R.string.test_item_title_coffee),
                amount = 50.0,
                type = com.hesapgunlugu.app.core.domain.model.TransactionType.EXPENSE,
                category = string(CoreUiR.string.category_food),
                emoji = string(R.string.test_item_title_coffee).first().toString(),
                date = System.currentTimeMillis() / 1000,
            )
        var clicked = false

        // When
        composeTestRule.setContent {
            HesapGunluguTheme {
                // TransactionItem(transaction, onClick = { clicked = true })
            }
        }

        // Then
        composeTestRule.onNodeWithText(string(R.string.test_item_title_coffee)).performClick()
        // assert(clicked) { "Transaction item should be clickable" }
    }

    @Test
    fun addTransactionButton_hasCorrectContentDescription() {
        composeTestRule.setContent {
            HesapGunluguTheme {
                // HomeScreen(HomeState())
            }
        }

        composeTestRule
            .onNodeWithContentDescription(string(R.string.add_transaction_fab))
            .assertExists()
            .assert(hasClickAction())
    }
}
