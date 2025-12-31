package com.hesapgunlugu.app.core.ui.accessibility

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.hesapgunlugu.app.core.ui.theme.HesapGunluguTheme
import org.junit.Rule
import org.junit.Test

/**
 * Accessibility Tests
 *
 * Tests to verify TalkBack and screen reader support.
 * Senior Level Best Practice: Ensure app is accessible to all users.
 */
class AccessibilityTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun currencyAccessibility_readsAmountCorrectly() {
        composeTestRule.setContent {
            HesapGunluguTheme {
                Text(
                    text = "₺1,234.56",
                    modifier =
                        Modifier
                            .testTag("currency_text")
                            .currencyAccessibility(1234.56, "TL"),
                )
            }
        }

        composeTestRule
            .onNodeWithTag("currency_text")
            .assertContentDescriptionContains("TL")
    }

    @Test
    fun transactionItem_hasProperAccessibility() {
        composeTestRule.setContent {
            HesapGunluguTheme {
                Card(
                    modifier =
                        Modifier
                            .testTag("transaction_card")
                            .transactionItemAccessibility(
                                title = "Market Alışverişi",
                                amount = 150.0,
                                isIncome = false,
                                category = "Market",
                                date = "24 Aralık 2024",
                            ),
                ) {
                    Text("Test")
                }
            }
        }

        composeTestRule
            .onNodeWithTag("transaction_card")
            .assertContentDescriptionContains("Gider")
            .assertContentDescriptionContains("Market Alışverişi")
    }

    @Test
    fun balanceCard_hasProperAccessibility() {
        composeTestRule.setContent {
            HesapGunluguTheme {
                Card(
                    modifier =
                        Modifier
                            .testTag("balance_card")
                            .balanceCardAccessibility(
                                label = "Toplam Bakiye",
                                amount = 5000.0,
                                isPositive = true,
                            ),
                ) {
                    Text("₺5,000")
                }
            }
        }

        composeTestRule
            .onNodeWithTag("balance_card")
            .assertContentDescriptionContains("Toplam Bakiye")
            .assertContentDescriptionContains("5000")
    }

    @Test
    fun switchAccessibility_indicatesState() {
        composeTestRule.setContent {
            HesapGunluguTheme {
                var isChecked by remember { mutableStateOf(false) }
                Switch(
                    checked = isChecked,
                    onCheckedChange = { isChecked = it },
                    modifier =
                        Modifier
                            .testTag("test_switch")
                            .switchAccessibility(
                                label = "Bildirimler",
                                isChecked = isChecked,
                            ),
                )
            }
        }

        composeTestRule
            .onNodeWithTag("test_switch")
            .assertContentDescriptionContains("Bildirimler")
            .assertContentDescriptionContains("kapalı")
    }

    @Test
    fun listItem_indicatesPosition() {
        composeTestRule.setContent {
            HesapGunluguTheme {
                Column {
                    (0..2).forEach { index ->
                        Text(
                            text = "Item $index",
                            modifier =
                                Modifier
                                    .testTag("list_item_$index")
                                    .listItemAccessibility(
                                        description = "Öğe $index",
                                        position = index,
                                        totalItems = 3,
                                    ),
                        )
                    }
                }
            }
        }

        composeTestRule
            .onNodeWithTag("list_item_0")
            .assertContentDescriptionContains("1 / 3")

        composeTestRule
            .onNodeWithTag("list_item_2")
            .assertContentDescriptionContains("3 / 3")
    }

    @Test
    fun bottomNavAccessibility_indicatesSelected() {
        composeTestRule.setContent {
            HesapGunluguTheme {
                Row {
                    listOf("Ana Sayfa", "Geçmiş", "İstatistik").forEachIndexed { index, label ->
                        Text(
                            text = label,
                            modifier =
                                Modifier
                                    .testTag("nav_$index")
                                    .bottomNavAccessibility(
                                        label = label,
                                        isSelected = index == 0,
                                        index = index,
                                        totalItems = 3,
                                    ),
                        )
                    }
                }
            }
        }

        composeTestRule
            .onNodeWithTag("nav_0")
            .assertContentDescriptionContains("seçili")
            .assertContentDescriptionContains("sekme 1 / 3")
    }

    @Test
    fun progressAccessibility_showsPercentage() {
        composeTestRule.setContent {
            HesapGunluguTheme {
                LinearProgressIndicator(
                    progress = { 0.75f },
                    modifier =
                        Modifier
                            .testTag("progress")
                            .progressAccessibility(
                                label = "Bütçe kullanımı",
                                progress = 0.75f,
                            ),
                )
            }
        }

        composeTestRule
            .onNodeWithTag("progress")
            .assertContentDescriptionContains("yüzde 75")
    }

    @Test
    fun alertAccessibility_hasLiveRegion() {
        composeTestRule.setContent {
            HesapGunluguTheme {
                Text(
                    text = "İşlem başarılı",
                    modifier =
                        Modifier
                            .testTag("alert")
                            .alertAccessibility(
                                message = "İşlem başarılı",
                                isError = false,
                            ),
                )
            }
        }

        composeTestRule
            .onNodeWithTag("alert")
            .assertContentDescriptionContains("İşlem başarılı")
    }

    @Test
    fun headingAccessibility_marksAsHeading() {
        composeTestRule.setContent {
            HesapGunluguTheme {
                Text(
                    text = "Ayarlar",
                    modifier =
                        Modifier
                            .testTag("heading")
                            .sectionHeaderAccessibility("Ayarlar"),
                )
            }
        }

        composeTestRule
            .onNodeWithTag("heading")
            .assertContentDescriptionContains("bölümü")
    }

    @Test
    fun iconButton_hasAccessibleAction() {
        composeTestRule.setContent {
            HesapGunluguTheme {
                IconButton(
                    onClick = { },
                    modifier =
                        Modifier
                            .testTag("icon_btn")
                            .iconButtonAccessibility("Ayarları aç"),
                ) {
                    Text("⚙️")
                }
            }
        }

        composeTestRule
            .onNodeWithTag("icon_btn")
            .assertContentDescriptionContains("Ayarları aç")
    }
}
