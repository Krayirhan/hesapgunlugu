package com.hesapgunlugu.app.data.repository

import app.cash.turbine.test
import com.hesapgunlugu.app.core.data.local.DataCategoryTotal
import com.hesapgunlugu.app.core.data.local.TransactionDao
import com.hesapgunlugu.app.core.data.local.TransactionEntity
import com.hesapgunlugu.app.core.data.remote.FirestoreDataSource
import com.hesapgunlugu.app.core.domain.model.Transaction
import com.hesapgunlugu.app.core.domain.model.TransactionType
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.Date

/**
 * Unit tests for TransactionRepositoryImpl
 * Tests all CRUD operations and data transformations
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TransactionRepositoryImplTest {
    private lateinit var repository: TransactionRepositoryImpl
    private lateinit var dao: TransactionDao
    private lateinit var firestoreDataSource: FirestoreDataSource

    private val testDate = Date()

    private val testTransaction =
        Transaction(
            id = 1,
            title = "Test Transaction",
            amount = 100.0,
            date = testDate,
            type = TransactionType.EXPENSE,
            category = "Food",
            emoji = ":)",
        )

    private val testEntity =
        TransactionEntity(
            id = 1,
            title = "Test Transaction",
            amount = 100.0,
            date = testDate,
            type = "EXPENSE",
            category = "Food",
            emoji = ":)",
        )

    @Before
    fun setUp() {
        dao = mockk(relaxed = true)
        firestoreDataSource = mockk(relaxed = true)
        repository = TransactionRepositoryImpl(dao, firestoreDataSource)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // ==================== ADD TRANSACTION TESTS ====================

    @Test
    fun `addTransaction should return success when dao insert succeeds`() =
        runTest {
            // Given
            coEvery { dao.insertTransaction(any()) } just Runs

            // When
            val result = repository.addTransaction(testTransaction)

            // Then
            assertTrue(result.isSuccess)
            coVerify(exactly = 1) { dao.insertTransaction(any()) }
        }

    @Test
    fun `addTransaction should return failure when dao throws exception`() =
        runTest {
            // Given
            val exception = RuntimeException("Database error")
            coEvery { dao.insertTransaction(any()) } throws exception

            // When
            val result = repository.addTransaction(testTransaction)

            // Then
            assertTrue(result.isFailure)
            assertEquals(exception, result.exceptionOrNull())
        }

    // ==================== DELETE TRANSACTION TESTS ====================

    @Test
    fun `deleteTransaction should return success when dao delete succeeds`() =
        runTest {
            // Given
            coEvery { dao.deleteTransaction(any()) } just Runs

            // When
            val result = repository.deleteTransaction(testTransaction)

            // Then
            assertTrue(result.isSuccess)
            coVerify(exactly = 1) { dao.deleteTransaction(any()) }
        }

    @Test
    fun `deleteTransaction should return failure when dao throws exception`() =
        runTest {
            // Given
            val exception = RuntimeException("Delete failed")
            coEvery { dao.deleteTransaction(any()) } throws exception

            // When
            val result = repository.deleteTransaction(testTransaction)

            // Then
            assertTrue(result.isFailure)
            assertEquals(exception, result.exceptionOrNull())
        }

    // ==================== UPDATE TRANSACTION TESTS ====================

    @Test
    fun `updateTransaction should return success when dao update succeeds`() =
        runTest {
            // Given
            coEvery { dao.updateTransaction(any()) } just Runs

            // When
            val result = repository.updateTransaction(testTransaction)

            // Then
            assertTrue(result.isSuccess)
            coVerify(exactly = 1) { dao.updateTransaction(any()) }
        }

    @Test
    fun `updateTransaction should return failure when dao throws exception`() =
        runTest {
            // Given
            val exception = RuntimeException("Update failed")
            coEvery { dao.updateTransaction(any()) } throws exception

            // When
            val result = repository.updateTransaction(testTransaction)

            // Then
            assertTrue(result.isFailure)
            assertEquals(exception, result.exceptionOrNull())
        }

    // ==================== GET ALL TRANSACTIONS TESTS ====================

    @Test
    fun `getAllTransactions should return mapped domain models`() =
        runTest {
            // Given
            val entities = listOf(testEntity)
            every { dao.getAllTransactions() } returns flowOf(entities)

            // When & Then
            repository.getAllTransactions().test {
                val result = awaitItem()
                assertEquals(1, result.size)
                assertEquals(testTransaction.id, result[0].id)
                assertEquals(testTransaction.title, result[0].title)
                assertEquals(testTransaction.amount, result[0].amount, 0.01)
                assertEquals(testTransaction.type, result[0].type)
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `getAllTransactions should return empty list when dao returns empty`() =
        runTest {
            // Given
            every { dao.getAllTransactions() } returns flowOf(emptyList())

            // When & Then
            repository.getAllTransactions().test {
                val result = awaitItem()
                assertTrue(result.isEmpty())
                cancelAndConsumeRemainingEvents()
            }
        }

    // ==================== AGGREGATION TESTS ====================

    @Test
    fun `getTotalIncome should return dao value`() =
        runTest {
            // Given
            val expectedIncome = 5000.0
            every { dao.getTotalIncome() } returns flowOf(expectedIncome)

            // When & Then
            repository.getTotalIncome().test {
                assertEquals(expectedIncome, awaitItem(), 0.01)
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `getTotalExpense should return dao value`() =
        runTest {
            // Given
            val expectedExpense = 2500.0
            every { dao.getTotalExpense() } returns flowOf(expectedExpense)

            // When & Then
            repository.getTotalExpense().test {
                assertEquals(expectedExpense, awaitItem(), 0.01)
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `getTotalIncomeInRange should return filtered income`() =
        runTest {
            // Given
            val startDate = 1000L
            val endDate = 2000L
            val expectedIncome = 3000.0
            every { dao.getTotalIncomeInRange(startDate, endDate) } returns flowOf(expectedIncome)

            // When & Then
            repository.getTotalIncomeInRange(startDate, endDate).test {
                assertEquals(expectedIncome, awaitItem(), 0.01)
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `getTotalExpenseInRange should return filtered expense`() =
        runTest {
            // Given
            val startDate = 1000L
            val endDate = 2000L
            val expectedExpense = 1500.0
            every { dao.getTotalExpenseInRange(startDate, endDate) } returns flowOf(expectedExpense)

            // When & Then
            repository.getTotalExpenseInRange(startDate, endDate).test {
                assertEquals(expectedExpense, awaitItem(), 0.01)
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `getCategoryTotalInRange should return category total`() =
        runTest {
            // Given
            val category = "Food"
            val startDate = 1000L
            val endDate = 2000L
            val expectedTotal = 500.0
            every { dao.getCategoryTotalInRange(category, startDate, endDate) } returns flowOf(expectedTotal)

            // When & Then
            repository.getCategoryTotalInRange(category, startDate, endDate).test {
                assertEquals(expectedTotal, awaitItem(), 0.01)
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `getTransactionsInRange should return mapped transactions`() =
        runTest {
            // Given
            val startDate = 1000L
            val endDate = 2000L
            val entities = listOf(testEntity)
            every { dao.getTransactionsInRange(startDate, endDate) } returns flowOf(entities)

            // When & Then
            repository.getTransactionsInRange(startDate, endDate).test {
                val result = awaitItem()
                assertEquals(1, result.size)
                assertEquals(testTransaction.title, result[0].title)
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `getCategoryTotals should return mapped category totals`() =
        runTest {
            // Given
            val startDate = 1000L
            val endDate = 2000L
            val dataCategoryTotals =
                listOf(
                    DataCategoryTotal("Food", 500.0),
                    DataCategoryTotal("Transport", 200.0),
                )
            every { dao.getCategoryTotals(startDate, endDate) } returns flowOf(dataCategoryTotals)

            // When & Then
            repository.getCategoryTotals(startDate, endDate).test {
                val result = awaitItem()
                assertEquals(2, result.size)
                assertEquals("Food", result[0].category)
                assertEquals(500.0, result[0].total, 0.01)
                assertEquals("Transport", result[1].category)
                assertEquals(200.0, result[1].total, 0.01)
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `getTransactionsByType should return filtered transactions`() =
        runTest {
            // Given
            val type = "EXPENSE"
            val entities = listOf(testEntity)
            every { dao.getTransactionsByType(type) } returns flowOf(entities)

            // When & Then
            repository.getTransactionsByType(type).test {
                val result = awaitItem()
                assertEquals(1, result.size)
                assertEquals(TransactionType.EXPENSE, result[0].type)
                cancelAndConsumeRemainingEvents()
            }
        }

    // ==================== MAPPER TESTS ====================

    @Test
    fun `Transaction toEntity should map correctly`() {
        // When
        val entity = testTransaction.toEntity()

        // Then
        assertEquals(testTransaction.id, entity.id)
        assertEquals(testTransaction.title, entity.title)
        assertEquals(testTransaction.amount, entity.amount, 0.01)
        assertEquals(testTransaction.date, entity.date)
        assertEquals(testTransaction.type.name, entity.type)
        assertEquals(testTransaction.category, entity.category)
        assertEquals(testTransaction.emoji, entity.emoji)
    }

    @Test
    fun `TransactionEntity toDomain should map correctly`() {
        // When
        val domain = testEntity.toDomain()

        // Then
        assertEquals(testEntity.id, domain.id)
        assertEquals(testEntity.title, domain.title)
        assertEquals(testEntity.amount, domain.amount, 0.01)
        assertEquals(testEntity.date, domain.date)
        assertEquals(TransactionType.valueOf(testEntity.type), domain.type)
        assertEquals(testEntity.category, domain.category)
        assertEquals(testEntity.emoji, domain.emoji)
    }

    @Test
    fun `DataCategoryTotal toDomain should map correctly`() {
        // Given
        val dataCategoryTotal = DataCategoryTotal("Food", 500.0)

        // When
        val domain = dataCategoryTotal.toDomain()

        // Then
        assertEquals("Food", domain.category)
        assertEquals(500.0, domain.total, 0.01)
    }
}
