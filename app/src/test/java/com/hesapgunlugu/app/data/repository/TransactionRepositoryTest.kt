package com.hesapgunlugu.app.data.repository

import com.hesapgunlugu.app.core.data.local.TransactionDao
import com.hesapgunlugu.app.core.data.local.TransactionEntity
import com.hesapgunlugu.app.core.data.remote.FirestoreDataSource
import com.hesapgunlugu.app.core.data.repository.TransactionRepositoryImpl
import com.hesapgunlugu.app.core.domain.model.Transaction
import com.hesapgunlugu.app.core.domain.model.TransactionType
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * TransactionRepository için Unit Testler
 */
class TransactionRepositoryTest {
    private lateinit var dao: TransactionDao
    private lateinit var firestoreDataSource: FirestoreDataSource
    private lateinit var repository: TransactionRepositoryImpl

    @Before
    fun setup() {
        dao = mockk(relaxed = true)
        firestoreDataSource = mockk(relaxed = true)
        repository = TransactionRepositoryImpl(dao, firestoreDataSource)
    }

    @Test
    fun `getAllTransactions should return flow of transactions`() =
        runBlocking {
            // Given
            val entities =
                listOf(
                    TransactionEntity(
                        id = 1,
                        title = "Test",
                        amount = 100.0,
                        date = System.currentTimeMillis(),
                        type = "INCOME",
                        category = "Maaş",
                        emoji = ":)",
                    ),
                )
            coEvery { dao.getAllTransactions() } returns flowOf(entities)

            // When
            val result = repository.getAllTransactions().first()

            // Then
            assertEquals(1, result.size)
            assertEquals("Test", result[0].title)
            assertEquals(100.0, result[0].amount, 0.01)
        }

    @Test
    fun `addTransaction should call dao insert`() =
        runBlocking {
            // Given
            val transaction =
                Transaction(
                    id = 0,
                    title = "Test",
                    amount = 100.0,
                    type = TransactionType.INCOME,
                    category = "Maaş",
                    emoji = ":)",
                )
            coEvery { dao.insertTransaction(any()) } just Runs

            // When
            val result = repository.addTransaction(transaction)

            // Then
            assertTrue(result.isSuccess)
            coVerify { dao.insertTransaction(any()) }
        }

    @Test
    fun `deleteTransaction should call dao delete`() =
        runBlocking {
            // Given
            val transaction =
                Transaction(
                    id = 1,
                    title = "Test",
                    amount = 100.0,
                    type = TransactionType.INCOME,
                    category = "Maaş",
                    emoji = ":)",
                )
            coEvery { dao.deleteTransaction(any()) } just Runs

            // When
            val result = repository.deleteTransaction(transaction)

            // Then
            assertTrue(result.isSuccess)
            coVerify { dao.deleteTransaction(any()) }
        }

    @Test
    fun `getTotalIncome should return correct sum`() =
        runBlocking {
            // Given
            coEvery { dao.getTotalIncome() } returns flowOf(5000.0)

            // When
            val result = repository.getTotalIncome().first()

            // Then
            assertEquals(5000.0, result, 0.01)
        }

    @Test
    fun `getTotalExpense should return correct sum`() =
        runBlocking {
            // Given
            coEvery { dao.getTotalExpense() } returns flowOf(3000.0)

            // When
            val result = repository.getTotalExpense().first()

            // Then
            assertEquals(3000.0, result, 0.01)
        }

    @Test
    fun `getTransactionsByType should filter by type`() =
        runBlocking {
            // Given
            val incomeEntities =
                listOf(
                    TransactionEntity(
                        id = 1,
                        title = "Salary",
                        amount = 5000.0,
                        date = System.currentTimeMillis(),
                        type = "INCOME",
                        category = "Maaş",
                        emoji = ":)",
                    ),
                )
            coEvery { dao.getTransactionsByType("INCOME") } returns flowOf(incomeEntities)

            // When
            val result = repository.getTransactionsByType(TransactionType.INCOME).first()

            // Then
            assertEquals(1, result.size)
            assertEquals(TransactionType.INCOME, result[0].type)
        }
}
