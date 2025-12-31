package com.hesapgunlugu.app.core.data.repository

import com.hesapgunlugu.app.core.data.local.ScheduledPaymentDao
import com.hesapgunlugu.app.core.data.local.ScheduledPaymentEntity
import com.hesapgunlugu.app.core.domain.model.ScheduledPayment
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Unit tests for ScheduledPaymentRepositoryImpl
 */
class ScheduledPaymentRepositoryImplTest {
    private lateinit var dao: ScheduledPaymentDao
    private lateinit var repository: ScheduledPaymentRepositoryImpl

    @Before
    fun setup() {
        dao = mockk(relaxed = true)
        repository = ScheduledPaymentRepositoryImpl(dao)
    }

    @Test
    fun getAllScheduledPayments_returnsMappedDomainModels() =
        runTest {
            // Arrange
            val entities =
                listOf(
                    ScheduledPaymentEntity(
                        id = 1,
                        title = "Rent",
                        amount = 3000.0,
                        isIncome = false,
                        isRecurring = false,
                        frequency = "Monthly",
                        dueDate = Date().time,
                        isPaid = false,
                        emoji = ":)",
                        category = "Housing",
                    ),
                )
            coEvery { dao.getAllScheduledPayments() } returns flowOf(entities)

            // Act
            val result = repository.getAllScheduledPayments().first()

            // Assert
            assertEquals(1, result.size)
            assertEquals("Rent", result[0].title)
            assertEquals(3000.0, result[0].amount)
            coVerify { dao.getAllScheduledPayments() }
        }

    @Test
    fun getRecurringIncomes_filtersCorrectly() =
        runTest {
            // Arrange
            val entities =
                listOf(
                    ScheduledPaymentEntity(
                        id = 1,
                        title = "Salary",
                        amount = 10000.0,
                        isIncome = true,
                        isRecurring = true,
                        frequency = "Monthly",
                        dueDate = Date().time,
                        isPaid = false,
                        emoji = ":)",
                        category = "Salary",
                    ),
                )
            coEvery { dao.getRecurringIncomes() } returns flowOf(entities)

            // Act
            val result = repository.getRecurringIncomes().first()

            // Assert
            assertEquals(1, result.size)
            assertEquals(true, result[0].isIncome)
            assertEquals(true, result[0].isRecurring)
        }

    @Test
    fun getRecurringExpenses_filtersCorrectly() =
        runTest {
            // Arrange
            val entities =
                listOf(
                    ScheduledPaymentEntity(
                        id = 1,
                        title = "Subscription",
                        amount = 50.0,
                        isIncome = false,
                        isRecurring = true,
                        frequency = "Monthly",
                        dueDate = Date().time,
                        isPaid = false,
                        emoji = ":)",
                        category = "Entertainment",
                    ),
                )
            coEvery { dao.getRecurringExpenses() } returns flowOf(entities)

            // Act
            val result = repository.getRecurringExpenses().first()

            // Assert
            assertEquals(1, result.size)
            assertEquals(false, result[0].isIncome)
            assertEquals(true, result[0].isRecurring)
        }

    @Test
    fun addScheduledPayment_insertsAndReturnsId() =
        runTest {
            // Arrange
            val payment =
                ScheduledPayment(
                    id = 0,
                    title = "New Payment",
                    amount = 100.0,
                    isIncome = false,
                    isRecurring = false,
                    frequency = "Weekly",
                    dueDate = Date(),
                    isPaid = false,
                    emoji = ":)",
                    category = "Other",
                )
            coEvery { dao.insert(any()) } returns 42L

            // Act
            val result = repository.addScheduledPayment(payment)

            // Assert
            assertEquals(42L, result)
            coVerify { dao.insert(any()) }
        }

    @Test
    fun updateScheduledPayment_callsDao() =
        runTest {
            // Arrange
            val payment =
                ScheduledPayment(
                    id = 1,
                    title = "Updated",
                    amount = 200.0,
                    isIncome = false,
                    isRecurring = false,
                    frequency = "Monthly",
                    dueDate = Date(),
                    isPaid = true,
                    emoji = ":)",
                    category = "Test",
                )

            // Act
            repository.updateScheduledPayment(payment)

            // Assert
            coVerify { dao.update(any()) }
        }

    @Test
    fun deleteScheduledPayment_callsDao() =
        runTest {
            // Arrange
            val payment =
                ScheduledPayment(
                    id = 1,
                    title = "To Delete",
                    amount = 100.0,
                    isIncome = false,
                    isRecurring = false,
                    frequency = "Monthly",
                    dueDate = Date(),
                    isPaid = false,
                    emoji = ":)",
                    category = "Test",
                )

            // Act
            repository.deleteScheduledPayment(payment)

            // Assert
            coVerify { dao.delete(any()) }
        }

    @Test
    fun deleteScheduledPaymentById_whenExists_deletesIt() =
        runTest {
            // Arrange
            val entity =
                ScheduledPaymentEntity(
                    id = 1,
                    title = "Test",
                    amount = 100.0,
                    isIncome = false,
                    isRecurring = false,
                    frequency = "Monthly",
                    dueDate = Date().time,
                    isPaid = false,
                    emoji = ":)",
                    category = "Test",
                )
            coEvery { dao.getById(1) } returns entity

            // Act
            repository.deleteScheduledPaymentById(1)

            // Assert
            coVerify { dao.getById(1) }
            coVerify { dao.delete(entity) }
        }

    @Test
    fun deleteScheduledPaymentById_whenNotExists_doesNothing() =
        runTest {
            // Arrange
            coEvery { dao.getById(999) } returns null

            // Act
            repository.deleteScheduledPaymentById(999)

            // Assert
            coVerify { dao.getById(999) }
            coVerify(exactly = 0) { dao.delete(any()) }
        }

    @Test
    fun markAsPaid_callsDao() =
        runTest {
            // Act
            repository.markAsPaid(1, true)

            // Assert
            coVerify { dao.markAsPaid(1, true) }
        }

    @Test
    fun getScheduledPaymentById_whenExists_returnsPayment() =
        runTest {
            // Arrange
            val entity =
                ScheduledPaymentEntity(
                    id = 1,
                    title = "Test",
                    amount = 100.0,
                    isIncome = false,
                    isRecurring = false,
                    frequency = "Monthly",
                    dueDate = Date().time,
                    isPaid = false,
                    emoji = ":)",
                    category = "Test",
                )
            coEvery { dao.getById(1) } returns entity

            // Act
            val result = repository.getScheduledPaymentById(1)

            // Assert
            assertNotNull(result)
            assertEquals("Test", result.title)
        }

    @Test
    fun getScheduledPaymentById_whenNotExists_returnsNull() =
        runTest {
            // Arrange
            coEvery { dao.getById(999) } returns null

            // Act
            val result = repository.getScheduledPaymentById(999)

            // Assert
            assertNull(result)
        }

    @Test
    fun getUpcomingPayments_filtersDateRange() =
        runTest {
            // Arrange
            val start = Date()
            val end = Date(start.time + 7 * 24 * 60 * 60 * 1000) // +7 days
            val entities =
                listOf(
                    ScheduledPaymentEntity(
                        id = 1,
                        title = "Upcoming",
                        amount = 100.0,
                        isIncome = false,
                        isRecurring = false,
                        frequency = "Monthly",
                        // +2 days
                        dueDate = start.time + 2 * 24 * 60 * 60 * 1000,
                        isPaid = false,
                        emoji = ":)",
                        category = "Test",
                    ),
                )
            coEvery { dao.getUpcomingPayments(start.time, end.time) } returns flowOf(entities)

            // Act
            val result = repository.getUpcomingPayments(start, end).first()

            // Assert
            assertEquals(1, result.size)
            coVerify { dao.getUpcomingPayments(start.time, end.time) }
        }
}
