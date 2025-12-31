package com.hesapgunlugu.app.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.TheaterComedy
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hesapgunlugu.app.core.domain.model.Transaction
import com.hesapgunlugu.app.core.domain.model.TransactionType
import com.hesapgunlugu.app.core.ui.R
import com.hesapgunlugu.app.core.ui.theme.CategoryBills
import com.hesapgunlugu.app.core.ui.theme.CategoryEntertainment
import com.hesapgunlugu.app.core.ui.theme.CategoryFood
import com.hesapgunlugu.app.core.ui.theme.CategoryHealth
import com.hesapgunlugu.app.core.ui.theme.CategoryMarket
import com.hesapgunlugu.app.core.ui.theme.CategoryOther
import com.hesapgunlugu.app.core.ui.theme.CategoryRent
import com.hesapgunlugu.app.core.ui.theme.CategorySalary
import com.hesapgunlugu.app.core.ui.theme.CategoryTransport
import com.hesapgunlugu.app.core.ui.theme.ExpenseRed
import com.hesapgunlugu.app.core.ui.theme.HesapGunluguTheme
import com.hesapgunlugu.app.core.ui.theme.IncomeGreen
import java.text.DateFormat
import java.text.NumberFormat
import java.util.Date
import java.util.Locale

/**
 * Professional transaction card - modern design
 */
@Composable
fun TransactionItem(
    transaction: Transaction,
    onDeleteClick: () -> Unit,
    onClick: () -> Unit = {},
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val isIncome = transaction.type == TransactionType.INCOME

    val categoryConfig =
        mapOf(
            stringResource(R.string.category_food) to Pair(Icons.Outlined.Restaurant, CategoryFood),
            stringResource(R.string.category_market) to Pair(Icons.Outlined.ShoppingCart, CategoryMarket),
            stringResource(R.string.category_transport) to Pair(Icons.Outlined.DirectionsCar, CategoryTransport),
            stringResource(R.string.category_entertainment) to Pair(Icons.Outlined.TheaterComedy, CategoryEntertainment),
            stringResource(R.string.category_bills) to Pair(Icons.Outlined.Receipt, CategoryBills),
            stringResource(R.string.category_health) to Pair(Icons.Outlined.LocalHospital, CategoryHealth),
            stringResource(R.string.category_salary) to Pair(Icons.Outlined.AccountBalance, CategorySalary),
            stringResource(R.string.category_rent) to Pair(Icons.Outlined.Home, CategoryRent),
            stringResource(R.string.category_other) to Pair(Icons.Outlined.Category, CategoryOther),
        )

    val fallbackCategory = stringResource(R.string.category_other)
    val (icon, categoryColor) =
        if (isIncome) {
            Pair(Icons.AutoMirrored.Outlined.TrendingUp, IncomeGreen)
        } else {
            categoryConfig[transaction.category] ?: categoryConfig.getValue(fallbackCategory)
        }

    val typeLabel = if (isIncome) stringResource(R.string.income) else stringResource(R.string.expense)
    val transactionDescription =
        stringResource(
            R.string.transaction_item_description,
            typeLabel,
            transaction.title,
            formatCurrency(transaction.amount),
            transaction.category,
            formatDate(transaction.date),
        )

    val doubleTapHint = stringResource(R.string.transaction_item_double_tap)

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .semantics { contentDescription = transactionDescription },
        shape = RoundedCornerShape(12.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .semantics {
                        stateDescription = doubleTapHint
                    }
                    .clickable { onClick() }
                    .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(categoryColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription =
                        stringResource(
                            R.string.category_with_value,
                            transaction.category,
                        ),
                    tint = categoryColor,
                    modifier = Modifier.size(22.dp),
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(
                        modifier =
                            Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(categoryColor.copy(alpha = 0.12f))
                                .padding(horizontal = 8.dp, vertical = 3.dp),
                    ) {
                        Text(
                            text = transaction.category,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = categoryColor,
                        )
                    }

                    Text(
                        text = formatDate(transaction.date),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text =
                        stringResource(
                            R.string.transaction_amount_with_sign,
                            if (isIncome) "+" else "-",
                            formatCurrency(transaction.amount),
                        ),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isIncome) IncomeGreen else ExpenseRed,
                )

                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier =
                        Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (isIncome) {
                                    IncomeGreen.copy(alpha = 0.1f)
                                } else {
                                    ExpenseRed.copy(alpha = 0.1f)
                                },
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                ) {
                    Text(
                        text = typeLabel,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isIncome) IncomeGreen else ExpenseRed,
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    stringResource(R.string.delete_transaction_title),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            },
            text = {
                Text(
                    stringResource(R.string.confirm_delete_transaction, transaction.title),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteClick()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed),
                    shape = RoundedCornerShape(10.dp),
                ) {
                    Text(stringResource(R.string.delete), color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.cancel), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
        )
    }
}

private fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale.getDefault())
    format.maximumFractionDigits = 0
    return format.format(amount)
}

private fun formatDate(date: Date): String {
    val formatter = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
    return formatter.format(date)
}

// ==================== PREVIEWS ====================

@Preview(name = "Expense - Light", showBackground = true)
@Composable
private fun TransactionItemExpensePreview() {
    HesapGunluguTheme(darkTheme = false) {
        TransactionItem(
            transaction =
                Transaction(
                    id = 1,
                    title = stringResource(R.string.preview_title_market_shopping),
                    amount = 450.0,
                    date = Date(),
                    type = TransactionType.EXPENSE,
                    category = stringResource(R.string.category_market),
                    emoji = "",
                ),
            onDeleteClick = {},
            onClick = {},
        )
    }
}

@Preview(name = "Income - Light", showBackground = true)
@Composable
private fun TransactionItemIncomePreview() {
    HesapGunluguTheme(darkTheme = false) {
        TransactionItem(
            transaction =
                Transaction(
                    id = 2,
                    title = stringResource(R.string.preview_title_salary),
                    amount = 25000.0,
                    date = Date(),
                    type = TransactionType.INCOME,
                    category = stringResource(R.string.category_salary),
                    emoji = "",
                ),
            onDeleteClick = {},
            onClick = {},
        )
    }
}

@Preview(name = "Expense - Dark", showBackground = true)
@Composable
private fun TransactionItemDarkPreview() {
    HesapGunluguTheme(darkTheme = true) {
        TransactionItem(
            transaction =
                Transaction(
                    id = 3,
                    title = stringResource(R.string.preview_title_electric_bill),
                    amount = 850.0,
                    date = Date(),
                    type = TransactionType.EXPENSE,
                    category = stringResource(R.string.category_bills),
                    emoji = "",
                ),
            onDeleteClick = {},
            onClick = {},
        )
    }
}
