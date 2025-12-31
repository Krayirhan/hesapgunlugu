package com.hesapgunlugu.app.core.domain.integration

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hesapgunlugu.app.core.data.local.AppDatabase
import com.hesapgunlugu.app.core.data.remote.FirestoreDataSource
import com.hesapgunlugu.app.core.data.repository.TransactionRepositoryImpl
import com.hesapgunlugu.app.core.domain.usecase.*
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Integration tests for Use Cases with real database
 */
@RunWith(AndroidJUnit4::class)
class UseCaseIntegrationTest {
    private lateinit var database: AppDatabase
    private lateinit var repository: TransactionRepositoryImpl
    private lateinit var firestoreDataSource: FirestoreDataSource
    private lateinit var getBalanceUseCase: GetBalanceUseCase
    private lateinit var addTransactionUseCase: AddTransactionUseCase
    private lateinit var getRecentTransactionsUseCase: GetRecentTransactionsUseCase

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database =
            Room.inMemoryDatabaseBuilder(
                context,
                AppDatabase::class.java,
            ).allowMainThreadQueries().build()

        // Mock FirestoreDataSource for tests
        firestoreDataSource = mockk(relaxed = true)

        repository =
            TransactionRepositoryImpl(
                dao = database.transactionDao(),
                firestoreDataSource = firestoreDataSource,
            )

        getBalanceUseCase = GetBalanceUseCase(repository)
        addTransactionUseCase = AddTransactionUseCase(repository)
        getRecentTransactionsUseCase = GetRecentTransactionsUseCase(repository)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun addTransaction_increasesBalance() =
        runBlocking {
            // Given
            val initialBalance = getBalanceUseCase().first()

            // When
            addTransactionUseCase(
                title = "Salary",
                amount = 10000.0,
                type = "INCOME",
                categoryId = 1,
                date = LocalDate.now(),
                description = "Monthly salary",
            )

            val newBalance = getBalanceUseCase().first()

            // Then
            assertEquals(initialBalance + 10000.0, newBalance)
        }

    @Test
    fun addExpense_decreasesBalance() =
        runBlocking {
            // Given - Start with some income
            addTransactionUseCase("Income", 5000.0, "INCOME", 1, LocalDate.now(), null)
            val balanceBeforeExpense = getBalanceUseCase().first()

            // When
            addTransactionUseCase("Expense", 1000.0, "EXPENSE", 1, LocalDate.now(), null)
            val balanceAfterExpense = getBalanceUseCase().first()

            // Then
            assertEquals(balanceBeforeExpense - 1000.0, balanceAfterExpense)
        }

    @Test
    fun getRecentTransactions_returnsLatestFirst() =
        runBlocking {
            // Given
            addTransactionUseCase("Old Transaction", 100.0, "INCOME", 1, LocalDate.now().minusDays(5), null)
            addTransactionUseCase("Recent Transaction", 200.0, "INCOME", 1, LocalDate.now().minusDays(1), null)
            addTransactionUseCase("Latest Transaction", 300.0, "INCOME", 1, LocalDate.now(), null)

            // When
            val recentTransactions = getRecentTransactionsUseCase().first()

            // Then
            assertTrue(recentTransactions.size >= 3)
            assertEquals("Latest Transaction", recentTransactions.first().title)
        }

    @Test
    fun multipleTransactions_calculateCorrectBalance() =
        runBlocking {
            // Given
            addTransactionUseCase("Income 1", 10000.0, "INCOME", 1, LocalDate.now(), null)
            addTransactionUseCase("Income 2", 5000.0, "INCOME", 1, LocalDate.now(), null)
            addTransactionUseCase("Expense 1", 3000.0, "EXPENSE", 1, LocalDate.now(), null)
            addTransactionUseCase("Expense 2", 2000.0, "EXPENSE", 1, LocalDate.now(), null)

            // When
            val balance = getBalanceUseCase().first()

            // Then
            // 10000 + 5000 - 3000 - 2000 = 10000
            assertEquals(10000.0, balance)
        }

    @Test
    fun endToEndWorkflow_addAndRetrieveTransaction() =
        runBlocking {
            // When - Add a transaction
            addTransactionUseCase(
                title = "Test Transaction",
                amount = 1500.0,
                type = "INCOME",
                categoryId = 1,
                date = LocalDate.now(),
                description = "Integration test",
            )

            // Then - Verify it appears in recent transactions
            val transactions = getRecentTransactionsUseCase().first()

            assertTrue(transactions.isNotEmpty())
            assertTrue(
                transactions.any {
                    it.title == "Test Transaction" && it.amount == 1500.0
                },
            )

            // And - Verify balance is updated
            val balance = getBalanceUseCase().first()
            assertEquals(1500.0, balance)
        }
}
