package com.hesapgunlugu.app.domain.usecase.transaction

import app.cash.turbine.test
import com.hesapgunlugu.app.core.domain.model.TransactionType
import com.hesapgunlugu.app.testutil.FakeTransactionRepository
import com.hesapgunlugu.app.testutil.MainDispatcherRule
import com.hesapgunlugu.app.testutil.TestFixtures
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * GetTransactionsUseCase Tests
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GetTransactionsUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var useCase: GetTransactionsUseCase
    private lateinit var repository: FakeTransactionRepository

    @Before
    fun setUp() {
        repository = FakeTransactionRepository()
        useCase = GetTransactionsUseCase(repository)
    }

    @Test
    fun `invoke should return all transactions`() =
        runTest {
            // Given
            val transactions = TestFixtures.createTransactionList(5)
            repository.setTransactions(transactions)

            // When & Then
            useCase().test {
                val result = awaitItem()
                assertEquals(5, result.size)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `invoke should return empty list when no transactions`() =
        runTest {
            // Given - empty repository

            // When & Then
            useCase().test {
                val result = awaitItem()
                assertTrue(result.isEmpty())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `invoke should emit updates when transactions change`() =
        runTest {
            // Given
            val initialTransaction = TestFixtures.createExpenseTransaction(id = 1)
            repository.setTransactions(listOf(initialTransaction))

            // When & Then
            useCase().test {
                // Initial emission
                assertEquals(1, awaitItem().size)

                // Add new transaction
                repository.addTransactionSync(TestFixtures.createIncomeTransaction(id = 2))
                assertEquals(2, awaitItem().size)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `invoke should return transactions in correct order`() =
        runTest {
            // Given
            val transactions =
                listOf(
                    TestFixtures.createTransaction(id = 1, title = "First"),
                    TestFixtures.createTransaction(id = 2, title = "Second"),
                    TestFixtures.createTransaction(id = 3, title = "Third"),
                )
            repository.setTransactions(transactions)

            // When & Then
            useCase().test {
                val result = awaitItem()
                assertEquals("First", result[0].title)
                assertEquals("Second", result[1].title)
                assertEquals("Third", result[2].title)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `invoke should return mixed transaction types`() =
        runTest {
            // Given
            val transactions = TestFixtures.createMixedTransactions()
            repository.setTransactions(transactions)

            // When & Then
            useCase().test {
                val result = awaitItem()
                val incomeCount = result.count { it.type == TransactionType.INCOME }
                val expenseCount = result.count { it.type == TransactionType.EXPENSE }

                assertTrue(incomeCount > 0)
                assertTrue(expenseCount > 0)
                assertEquals(6, result.size)
                cancelAndIgnoreRemainingEvents()
            }
        }
}
