package com.hesapgunlugu.app.data.local

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hesapgunlugu.app.core.data.local.AppDatabase
import com.hesapgunlugu.app.core.data.local.ScheduledPaymentDao
import com.hesapgunlugu.app.core.data.local.ScheduledPaymentEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Integration Tests for ScheduledPaymentDao
 */
@RunWith(AndroidJUnit4::class)
class ScheduledPaymentDaoTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase
    private lateinit var scheduledPaymentDao: ScheduledPaymentDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database =
            Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        scheduledPaymentDao = database.scheduledPaymentDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertScheduledPayment_andRetrieve() =
        runTest {
            // Given
            val payment = createTestPayment(id = 1, title = "Rent")

            // When
            scheduledPaymentDao.insert(payment)
            val retrieved = scheduledPaymentDao.getScheduledPaymentById(1)

            // Then
            assertNotNull(retrieved)
            assertEquals("Rent", retrieved.title)
        }

    @Test
    fun getUpcomingPayments_filtersCorrectly() =
        runTest {
            // Given
            val future = createTestPayment(id = 1, dueDate = System.currentTimeMillis() / 1000 + 86400)
            val past = createTestPayment(id = 2, dueDate = System.currentTimeMillis() / 1000 - 86400)

            scheduledPaymentDao.insert(future)
            scheduledPaymentDao.insert(past)

            // When
            val upcoming = scheduledPaymentDao.getUpcomingPayments(System.currentTimeMillis() / 1000).first()

            // Then
            assertEquals(1, upcoming.size)
            assertEquals(1, upcoming[0].id)
        }

    @Test
    fun markAsPaid_updatesStatus() =
        runTest {
            // Given
            val payment = createTestPayment(id = 1, isPaid = false)
            scheduledPaymentDao.insert(payment)

            // When
            scheduledPaymentDao.markAsPaid(1, true)
            val updated = scheduledPaymentDao.getScheduledPaymentById(1)

            // Then
            assertTrue(updated!!.isPaid)
        }

    private fun createTestPayment(
        id: Int,
        title: String = "Test Payment",
        amount: Double = 1000.0,
        dueDate: Long = System.currentTimeMillis() / 1000,
        isPaid: Boolean = false,
    ) = ScheduledPaymentEntity(
        id = id,
        title = title,
        amount = amount,
        isIncome = false,
        isRecurring = false,
        frequency = "MONTHLY",
        dueDate = dueDate,
        emoji = "💳",
        isPaid = isPaid,
        category = "Bills",
        createdAt = System.currentTimeMillis(),
    )
}
