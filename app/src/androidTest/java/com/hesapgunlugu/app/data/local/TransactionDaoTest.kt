package com.hesapgunlugu.app.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Date

/**
 * Instrumented tests for TransactionDao
 * Uses in-memory Room database for fast, isolated testing
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class TransactionDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var dao: TransactionDao

    private val testDate = Date()
    private val testDateLong = testDate.time

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database =
            Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        dao = database.transactionDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    // ==================== INSERT TESTS ====================

    @Test
    fun insertTransaction_shouldAddToDatabase() =
        runTest {
            // Given
            val transaction = createTestEntity(1, "Test", 100.0, "EXPENSE")

            // When
            dao.insertTransaction(transaction)

            // Then
            dao.getAllTransactions().test {
                val result = awaitItem()
                assertEquals(1, result.size)
                assertEquals("Test", result[0].title)
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun insertTransaction_withSameId_shouldReplace() =
        runTest {
            // Given
            val transaction1 = createTestEntity(1, "Original", 100.0, "EXPENSE")
            val transaction2 = createTestEntity(1, "Updated", 200.0, "INCOME")

            // When
            dao.insertTransaction(transaction1)
            dao.insertTransaction(transaction2)

            // Then
            dao.getAllTransactions().test {
                val result = awaitItem()
                assertEquals(1, result.size)
                assertEquals("Updated", result[0].title)
                assertEquals(200.0, result[0].amount, 0.01)
                cancelAndConsumeRemainingEvents()
            }
        }

    // ==================== DELETE TESTS ====================

    @Test
    fun deleteTransaction_shouldRemoveFromDatabase() =
        runTest {
            // Given
            val transaction = createTestEntity(1, "ToDelete", 100.0, "EXPENSE")
            dao.insertTransaction(transaction)

            // When
            dao.deleteTransaction(transaction)

            // Then
            dao.getAllTransactions().test {
                val result = awaitItem()
                assertTrue(result.isEmpty())
                cancelAndConsumeRemainingEvents()
            }
        }

    // ==================== UPDATE TESTS ====================

    @Test
    fun updateTransaction_shouldModifyExisting() =
        runTest {
            // Given
            val original = createTestEntity(1, "Original", 100.0, "EXPENSE")
            dao.insertTransaction(original)
            val updated = original.copy(title = "Modified", amount = 250.0)

            // When
            dao.updateTransaction(updated)

            // Then
            dao.getAllTransactions().test {
                val result = awaitItem()
                assertEquals(1, result.size)
                assertEquals("Modified", result[0].title)
                assertEquals(250.0, result[0].amount, 0.01)
                cancelAndConsumeRemainingEvents()
            }
        }

    // ==================== AGGREGATION TESTS ====================

    @Test
    fun getTotalIncome_shouldSumOnlyIncomeTransactions() =
        runTest {
            // Given
            dao.insertTransaction(createTestEntity(1, "Salary", 5000.0, "INCOME"))
            dao.insertTransaction(createTestEntity(2, "Bonus", 1000.0, "INCOME"))
            dao.insertTransaction(createTestEntity(3, "Groceries", 200.0, "EXPENSE"))

            // Then
            dao.getTotalIncome().test {
                assertEquals(6000.0, awaitItem(), 0.01)
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun getTotalExpense_shouldSumOnlyExpenseTransactions() =
        runTest {
            // Given
            dao.insertTransaction(createTestEntity(1, "Salary", 5000.0, "INCOME"))
            dao.insertTransaction(createTestEntity(2, "Groceries", 200.0, "EXPENSE"))
            dao.insertTransaction(createTestEntity(3, "Transport", 100.0, "EXPENSE"))

            // Then
            dao.getTotalExpense().test {
                assertEquals(300.0, awaitItem(), 0.01)
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun getTotalIncome_withNoData_shouldReturnZero() =
        runTest {
            dao.getTotalIncome().test {
                assertEquals(0.0, awaitItem(), 0.01)
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun getTotalExpense_withNoData_shouldReturnZero() =
        runTest {
            dao.getTotalExpense().test {
                assertEquals(0.0, awaitItem(), 0.01)
                cancelAndConsumeRemainingEvents()
            }
        }

    // ==================== DATE RANGE TESTS ====================

    @Test
    fun getTotalIncomeInRange_shouldFilterByDate() =
        runTest {
            // Given
            val now = System.currentTimeMillis()
            val yesterday = now - 86400000
            val lastWeek = now - 604800000

            dao.insertTransaction(createTestEntity(1, "Today", 1000.0, "INCOME", Date(now)))
            dao.insertTransaction(createTestEntity(2, "Yesterday", 500.0, "INCOME", Date(yesterday)))
            dao.insertTransaction(createTestEntity(3, "LastWeek", 2000.0, "INCOME", Date(lastWeek)))

            // When - Query for today and yesterday only
            dao.getTotalIncomeInRange(yesterday, now).test {
                assertEquals(1500.0, awaitItem(), 0.01)
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun getTransactionsInRange_shouldReturnFilteredTransactions() =
        runTest {
            // Given
            val now = System.currentTimeMillis()
            val yesterday = now - 86400000
            val lastWeek = now - 604800000

            dao.insertTransaction(createTestEntity(1, "Today", 100.0, "EXPENSE", Date(now)))
            dao.insertTransaction(createTestEntity(2, "Yesterday", 200.0, "EXPENSE", Date(yesterday)))
            dao.insertTransaction(createTestEntity(3, "LastWeek", 300.0, "EXPENSE", Date(lastWeek)))

            // When - Query for today and yesterday only
            dao.getTransactionsInRange(yesterday, now).test {
                val result = awaitItem()
                assertEquals(2, result.size)
                cancelAndConsumeRemainingEvents()
            }
        }

    // ==================== CATEGORY TESTS ====================

    @Test
    fun getCategoryTotals_shouldGroupByCategory() =
        runTest {
            // Given
            val now = System.currentTimeMillis()
            dao.insertTransaction(createTestEntity(1, "Market1", 100.0, "EXPENSE", Date(now), "Food"))
            dao.insertTransaction(createTestEntity(2, "Market2", 150.0, "EXPENSE", Date(now), "Food"))
            dao.insertTransaction(createTestEntity(3, "Bus", 50.0, "EXPENSE", Date(now), "Transport"))
            dao.insertTransaction(createTestEntity(4, "Salary", 5000.0, "INCOME", Date(now), "Salary"))

            // When
            dao.getCategoryTotals(now - 86400000, now + 86400000).test {
                val result = awaitItem()
                assertTrue(result.isNotEmpty())

                val foodCategory = result.find { it.category == "Food" }
                assertNotNull(foodCategory)
                assertEquals(250.0, foodCategory!!.total, 0.01)

                val transportCategory = result.find { it.category == "Transport" }
                assertNotNull(transportCategory)
                assertEquals(50.0, transportCategory!!.total, 0.01)

                // Income should not be included (only EXPENSE)
                val salaryCategory = result.find { it.category == "Salary" }
                assertNull(salaryCategory)

                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun getCategoryTotalInRange_shouldReturnSpecificCategory() =
        runTest {
            // Given
            val now = System.currentTimeMillis()
            dao.insertTransaction(createTestEntity(1, "Market1", 100.0, "EXPENSE", Date(now), "Food"))
            dao.insertTransaction(createTestEntity(2, "Market2", 150.0, "EXPENSE", Date(now), "Food"))
            dao.insertTransaction(createTestEntity(3, "Bus", 50.0, "EXPENSE", Date(now), "Transport"))

            // When
            dao.getCategoryTotalInRange("Food", now - 86400000, now + 86400000).test {
                assertEquals(250.0, awaitItem(), 0.01)
                cancelAndConsumeRemainingEvents()
            }
        }

    // ==================== PAGINATION TESTS ====================

    @Test
    fun getAllTransactionsPaged_shouldReturnLimitedResults() =
        runTest {
            // Given - Insert 30 transactions
            repeat(30) { i ->
                dao.insertTransaction(createTestEntity(i, "Transaction$i", 100.0, "EXPENSE"))
            }

            // When - Get first page (20 items)
            val firstPage = dao.getAllTransactionsPaged(20, 0)
            assertEquals(20, firstPage.size)

            // When - Get second page (remaining 10 items)
            val secondPage = dao.getAllTransactionsPaged(20, 20)
            assertEquals(10, secondPage.size)
        }

    @Test
    fun searchTransactionsPaged_shouldFilterByQuery() =
        runTest {
            // Given
            dao.insertTransaction(createTestEntity(1, "Grocery Shopping", 100.0, "EXPENSE", category = "Food"))
            dao.insertTransaction(createTestEntity(2, "Gas Station", 50.0, "EXPENSE", category = "Transport"))
            dao.insertTransaction(createTestEntity(3, "Restaurant Food", 75.0, "EXPENSE", category = "Food"))

            // When - Search for "Food"
            val results = dao.searchTransactionsPaged("%Food%", 10, 0)

            // Then
            assertEquals(2, results.size)
        }

    @Test
    fun getTransactionsByTypePaged_shouldFilterByType() =
        runTest {
            // Given
            dao.insertTransaction(createTestEntity(1, "Salary", 5000.0, "INCOME"))
            dao.insertTransaction(createTestEntity(2, "Groceries", 200.0, "EXPENSE"))
            dao.insertTransaction(createTestEntity(3, "Bonus", 1000.0, "INCOME"))

            // When
            val incomeResults = dao.getTransactionsByTypePaged("INCOME", 10, 0)
            val expenseResults = dao.getTransactionsByTypePaged("EXPENSE", 10, 0)

            // Then
            assertEquals(2, incomeResults.size)
            assertEquals(1, expenseResults.size)
        }

    // ==================== HELPER FUNCTIONS ====================

    private fun createTestEntity(
        id: Int,
        title: String,
        amount: Double,
        type: String,
        date: Date = testDate,
        category: String = "General",
    ): TransactionEntity {
        return TransactionEntity(
            id = id,
            title = title,
            amount = amount,
            date = date,
            type = type,
            category = category,
            emoji = "💰",
        )
    }
}
