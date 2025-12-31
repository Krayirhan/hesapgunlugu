package com.hesapgunlugu.app.snapshot

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.hesapgunlugu.app.core.ui.theme.HesapGunluguTheme
import org.junit.Rule
import org.junit.Test

/**
 * Screenshot tests using Paparazzi.
 *
 * Run:
 * ./gradlew :app:recordPaparazziDebug
 * ./gradlew :app:verifyPaparazziDebug
 */
class ScreenshotTest {
    @get:Rule
    val paparazzi =
        Paparazzi(
            deviceConfig = DeviceConfig.PIXEL_5,
            theme = "android:Theme.Material.Light.NoActionBar",
        )

    @Test
    fun transactionCard_expense_light() {
        paparazzi.snapshot {
            HesapGunluguTheme(darkTheme = false) {
                TransactionCardPreview(isIncome = false)
            }
        }
    }

    @Test
    fun transactionCard_income_dark() {
        paparazzi.snapshot {
            HesapGunluguTheme(darkTheme = true) {
                TransactionCardPreview(isIncome = true)
            }
        }
    }

    @Test
    fun balanceCard_positive_light() {
        paparazzi.snapshot {
            HesapGunluguTheme(darkTheme = false) {
                BalanceCardPreview(balance = 5000.0)
            }
        }
    }

    @Test
    fun emptyState_light() {
        paparazzi.snapshot {
            HesapGunluguTheme(darkTheme = false) {
                EmptyStatePreview()
            }
        }
    }

    @Test
    fun loadingState_light() {
        paparazzi.snapshot {
            HesapGunluguTheme(darkTheme = false) {
                LoadingPreview()
            }
        }
    }

    @Test
    fun primaryButton_dark() {
        paparazzi.snapshot {
            HesapGunluguTheme(darkTheme = true) {
                Button(onClick = {}) {
                    Text("Save")
                }
            }
        }
    }

    @Test
    fun textField_error_light() {
        paparazzi.snapshot {
            HesapGunluguTheme(darkTheme = false) {
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = { Text("Title") },
                    isError = true,
                    supportingText = { Text("Title is required") },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                )
            }
        }
    }

    @Test
    fun balanceCard_tablet() {
        val tabletPaparazzi =
            Paparazzi(
                deviceConfig = DeviceConfig.NEXUS_10,
            )
        tabletPaparazzi.snapshot {
            HesapGunluguTheme {
                BalanceCardPreview(balance = 10000.0)
            }
        }
    }

    @Composable
    private fun TransactionCardPreview(isIncome: Boolean) {
        Card(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = if (isIncome) "Salary" else "Groceries",
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = if (isIncome) "Income" else "Expense",
                    style = MaterialTheme.typography.bodySmall,
                )
                Text(
                    text = if (isIncome) "+$5,000" else "-$150",
                    style = MaterialTheme.typography.titleMedium,
                    color =
                        if (isIncome) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.error
                        },
                )
            }
        }
    }

    @Composable
    private fun BalanceCardPreview(balance: Double) {
        Card(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
            ) {
                Text(
                    text = "Total balance",
                    style = MaterialTheme.typography.labelMedium,
                )
                Text(
                    text = "$${String.format("%,.2f", balance)}",
                    style = MaterialTheme.typography.headlineLarge,
                    color =
                        if (balance >= 0) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.error
                        },
                )
            }
        }
    }

    @Composable
    private fun EmptyStatePreview() {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = ":)",
                style = MaterialTheme.typography.displayLarge,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No transactions yet",
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = "Add your first item",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }

    @Composable
    private fun LoadingPreview() {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
    }
}
