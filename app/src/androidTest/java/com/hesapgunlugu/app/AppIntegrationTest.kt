package com.hesapgunlugu.app

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.hesapgunlugu.app.core.data.local.AppDatabase
import com.hesapgunlugu.app.core.data.model.Category
import com.hesapgunlugu.app.core.data.model.Transaction
import com.hesapgunlugu.app.core.data.model.TransactionType
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import javax.inject.Inject
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Integration tests for complete app flows
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class AppIntegrationTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var database: AppDatabase

    @Before
    fun setup() {
        hiltRule.inject()

        // Clear database before each test
        runBlocking {
            database.clearAllTables()
        }
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testCompleteTransactionFlow() =
        runBlocking {
            val context = InstrumentationRegistry.getInstrumentation().targetContext

            // 1. Create category
            val category =
                Category(
                    id = 0,
                    name = "Test Category",
                    emoji = "📊",
                    color = android.graphics.Color.BLUE,
                    type = TransactionType.EXPENSE,
                )
            val categoryId = database.categoryDao().insert(category)

            // 2. Create transaction
            val transaction =
                Transaction(
                    id = 0,
                    title = "Test Transaction",
                    amount = 100.0,
                    type = TransactionType.EXPENSE,
                    categoryId = categoryId,
                    date = LocalDate.now(),
                    description = "Integration test",
                )
            val transactionId = database.transactionDao().insert(transaction)

            // 3. Verify transaction exists
            val retrievedTransaction = database.transactionDao().getById(transactionId).first()
            assertNotNull(retrievedTransaction)
            assertEquals("Test Transaction", retrievedTransaction.title)
            assertEquals(100.0, retrievedTransaction.amount)

            // 4. Calculate balance
            val balance = database.transactionDao().getBalance().first()
            assertEquals(-100.0, balance)

            // 5. Delete transaction
            database.transactionDao().delete(retrievedTransaction)

            // 6. Verify deleted
            val allTransactions = database.transactionDao().getAll().first()
            assertTrue(allTransactions.isEmpty())
        }

    @Test
    fun testCategoryWithMultipleTransactions() =
        runBlocking {
            // Create category
            val category =
                Category(
                    id = 0,
                    name = "Food",
                    emoji = "🍔",
                    color = android.graphics.Color.RED,
                    type = TransactionType.EXPENSE,
                )
            val categoryId = database.categoryDao().insert(category)

            // Create multiple transactions
            val transaction1 =
                Transaction(
                    id = 0,
                    title = "Breakfast",
                    amount = 50.0,
                    type = TransactionType.EXPENSE,
                    categoryId = categoryId,
                    date = LocalDate.now(),
                    description = null,
                )

            val transaction2 =
                Transaction(
                    id = 0,
                    title = "Lunch",
                    amount = 75.0,
                    type = TransactionType.EXPENSE,
                    categoryId = categoryId,
                    date = LocalDate.now(),
                    description = null,
                )

            database.transactionDao().insert(transaction1)
            database.transactionDao().insert(transaction2)

            // Get category expenses
            val categoryExpenses =
                database.transactionDao()
                    .getExpensesByCategory(LocalDate.now().minusDays(30), LocalDate.now())
                    .first()

            val foodExpense = categoryExpenses.find { it.categoryId == categoryId }
            assertNotNull(foodExpense)
            assertEquals(125.0, foodExpense.totalAmount)
        }

    @Test
    fun testIncomeAndExpenseBalance() =
        runBlocking {
            val category =
                Category(
                    id = 0,
                    name = "General",
                    emoji = "💰",
                    color = android.graphics.Color.GREEN,
                    type = TransactionType.INCOME,
                )
            val categoryId = database.categoryDao().insert(category)

            // Add income
            database.transactionDao().insert(
                Transaction(
                    id = 0,
                    title = "Salary",
                    amount = 1000.0,
                    type = TransactionType.INCOME,
                    categoryId = categoryId,
                    date = LocalDate.now(),
                    description = null,
                ),
            )

            // Add expense
            database.transactionDao().insert(
                Transaction(
                    id = 0,
                    title = "Rent",
                    amount = 500.0,
                    type = TransactionType.EXPENSE,
                    categoryId = categoryId,
                    date = LocalDate.now(),
                    description = null,
                ),
            )

            // Check balance
            val balance = database.transactionDao().getBalance().first()
            assertEquals(500.0, balance) // 1000 - 500

            // Check total income
            val totalIncome =
                database.transactionDao()
                    .getTotalIncome(LocalDate.now().minusDays(7), LocalDate.now())
                    .first()
            assertEquals(1000.0, totalIncome)

            // Check total expense
            val totalExpense =
                database.transactionDao()
                    .getTotalExpense(LocalDate.now().minusDays(7), LocalDate.now())
                    .first()
            assertEquals(500.0, totalExpense)
        }

    @Test
    fun testRecurringRuleCreation() =
        runBlocking {
            // Create scheduled payment
            val category =
                Category(
                    id = 0,
                    name = "Bills",
                    emoji = "💡",
                    color = android.graphics.Color.YELLOW,
                    type = TransactionType.EXPENSE,
                )
            val categoryId = database.categoryDao().insert(category)

            val scheduledPayment =
                com.hesapgunlugu.app.core.data.model.ScheduledPayment(
                    id = 0,
                    title = "Electric Bill",
                    amount = 100.0,
                    type = TransactionType.EXPENSE,
                    categoryId = categoryId,
                    nextDate = LocalDate.now().plusDays(7),
                    frequency = "MONTHLY",
                    isActive = true,
                    description = null,
                )
            val scheduledId = database.scheduledPaymentDao().insert(scheduledPayment)

            // Create recurring rule
            val recurringRule =
                com.hesapgunlugu.app.core.data.model.RecurringRule(
                    id = 0,
                    scheduledPaymentId = scheduledId,
                    recurrenceType = "MONTHLY",
                    interval = 1,
                    dayOfMonth = 5,
                    startDate = LocalDate.now(),
                    endDate = null,
                    maxOccurrences = null,
                    isActive = true,
                )
            database.recurringRuleDao().insert(recurringRule)

            // Verify recurring rule
            val rules = database.recurringRuleDao().getAllActive().first()
            assertEquals(1, rules.size)
            assertEquals("MONTHLY", rules[0].recurrenceType)
        }

    @Test
    fun testSavingsGoalProgress() =
        runBlocking {
            // Create savings goal
            val savingsGoal =
                com.hesapgunlugu.app.core.data.model.SavingsGoal(
                    id = 0,
                    name = "Vacation",
                    targetAmount = 1000.0,
                    currentAmount = 0.0,
                    deadline = LocalDate.now().plusMonths(3),
                    categoryId = null,
                    isCompleted = false,
                )
            val goalId = database.savingsGoalDao().insert(savingsGoal)

            // Update progress
            database.savingsGoalDao().updateProgress(goalId, 500.0)

            // Verify progress
            val updatedGoal = database.savingsGoalDao().getById(goalId).first()
            assertNotNull(updatedGoal)
            assertEquals(500.0, updatedGoal.currentAmount)
            assertEquals(50.0, updatedGoal.progress, 0.01)
        }
}
