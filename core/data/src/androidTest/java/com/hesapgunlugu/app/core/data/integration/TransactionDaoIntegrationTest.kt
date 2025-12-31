package com.hesapgunlugu.app.core.data.integration

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hesapgunlugu.app.core.data.local.AppDatabase
import com.hesapgunlugu.app.core.data.local.dao.TransactionDao
import com.hesapgunlugu.app.core.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Integration tests for Transaction database operations
 */
@RunWith(AndroidJUnit4::class)
class TransactionDaoIntegrationTest {
    private lateinit var database: AppDatabase
    private lateinit var transactionDao: TransactionDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database =
            Room.inMemoryDatabaseBuilder(
                context,
                AppDatabase::class.java,
            ).allowMainThreadQueries().build()

        transactionDao = database.transactionDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertTransaction_andRetrieve_success() =
        runBlocking {
            // Given
            val transaction =
                TransactionEntity(
                    id = 1,
                    title = "Test Transaction",
                    amount = 100.0,
                    type = "INCOME",
                    categoryId = 1,
                    date = LocalDate.now(),
                    description = "Test description",
                )

            // When
            transactionDao.insert(transaction)
            val retrieved = transactionDao.getById(1).first()

            // Then
            assertNotNull(retrieved)
            assertEquals("Test Transaction", retrieved.title)
            assertEquals(100.0, retrieved.amount)
        }

    @Test
    fun getAllTransactions_returnsAllInserted() =
        runBlocking {
            // Given
            val transactions =
                listOf(
                    TransactionEntity(1, "Transaction 1", 100.0, "INCOME", 1, LocalDate.now(), null),
                    TransactionEntity(2, "Transaction 2", 200.0, "EXPENSE", 1, LocalDate.now(), null),
                    TransactionEntity(3, "Transaction 3", 300.0, "INCOME", 1, LocalDate.now(), null),
                )

            // When
            transactions.forEach { transactionDao.insert(it) }
            val allTransactions = transactionDao.getAll().first()

            // Then
            assertEquals(3, allTransactions.size)
            assertTrue(allTransactions.any { it.title == "Transaction 1" })
            assertTrue(allTransactions.any { it.title == "Transaction 2" })
            assertTrue(allTransactions.any { it.title == "Transaction 3" })
        }

    @Test
    fun updateTransaction_updatesCorrectly() =
        runBlocking {
            // Given
            val original =
                TransactionEntity(
                    id = 1,
                    title = "Original",
                    amount = 100.0,
                    type = "INCOME",
                    categoryId = 1,
                    date = LocalDate.now(),
                    description = null,
                )
            transactionDao.insert(original)

            // When
            val updated = original.copy(title = "Updated", amount = 200.0)
            transactionDao.update(updated)
            val retrieved = transactionDao.getById(1).first()

            // Then
            assertEquals("Updated", retrieved.title)
            assertEquals(200.0, retrieved.amount)
        }

    @Test
    fun deleteTransaction_removesFromDatabase() =
        runBlocking {
            // Given
            val transaction =
                TransactionEntity(
                    id = 1,
                    title = "To Delete",
                    amount = 100.0,
                    type = "INCOME",
                    categoryId = 1,
                    date = LocalDate.now(),
                    description = null,
                )
            transactionDao.insert(transaction)

            // When
            transactionDao.delete(transaction)
            val allTransactions = transactionDao.getAll().first()

            // Then
            assertTrue(allTransactions.isEmpty())
        }

    @Test
    fun getTransactionsByDateRange_filtersCorrectly() =
        runBlocking {
            // Given
            val today = LocalDate.now()
            val yesterday = today.minusDays(1)
            val tomorrow = today.plusDays(1)

            transactionDao.insert(TransactionEntity(1, "Yesterday", 100.0, "INCOME", 1, yesterday, null))
            transactionDao.insert(TransactionEntity(2, "Today", 200.0, "INCOME", 1, today, null))
            transactionDao.insert(TransactionEntity(3, "Tomorrow", 300.0, "INCOME", 1, tomorrow, null))

            // When
            val todayTransactions = transactionDao.getByDateRange(today, today).first()

            // Then
            assertEquals(1, todayTransactions.size)
            assertEquals("Today", todayTransactions.first().title)
        }

    @Test
    fun getBalance_calculatesCorrectly() =
        runBlocking {
            // Given
            transactionDao.insert(TransactionEntity(1, "Income 1", 1000.0, "INCOME", 1, LocalDate.now(), null))
            transactionDao.insert(TransactionEntity(2, "Income 2", 500.0, "INCOME", 1, LocalDate.now(), null))
            transactionDao.insert(TransactionEntity(3, "Expense 1", -300.0, "EXPENSE", 1, LocalDate.now(), null))
            transactionDao.insert(TransactionEntity(4, "Expense 2", -200.0, "EXPENSE", 1, LocalDate.now(), null))

            // When
            val balance = transactionDao.getBalance().first()

            // Then
            assertEquals(1000.0, balance) // 1000 + 500 - 300 - 200 = 1000
        }

    @Test
    fun getTransactionsByCategory_filtersCorrectly() =
        runBlocking {
            // Given
            transactionDao.insert(TransactionEntity(1, "Cat 1 Trans", 100.0, "INCOME", 1, LocalDate.now(), null))
            transactionDao.insert(TransactionEntity(2, "Cat 2 Trans", 200.0, "INCOME", 2, LocalDate.now(), null))
            transactionDao.insert(TransactionEntity(3, "Cat 1 Trans 2", 300.0, "INCOME", 1, LocalDate.now(), null))

            // When
            val category1Transactions = transactionDao.getByCategoryId(1).first()

            // Then
            assertEquals(2, category1Transactions.size)
            assertTrue(category1Transactions.all { it.categoryId == 1L })
        }
}
