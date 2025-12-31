package com.hesapgunlugu.app.core.data.integration

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hesapgunlugu.app.core.data.local.AppDatabase
import com.hesapgunlugu.app.core.data.local.dao.RecurringRuleDao
import com.hesapgunlugu.app.core.data.local.dao.ScheduledPaymentDao
import com.hesapgunlugu.app.core.data.local.entity.RecurringRuleEntity
import com.hesapgunlugu.app.core.data.local.entity.ScheduledPaymentEntity
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
 * Integration tests for Scheduled Payment and Recurring Rule operations
 */
@RunWith(AndroidJUnit4::class)
class ScheduledPaymentIntegrationTest {
    private lateinit var database: AppDatabase
    private lateinit var scheduledPaymentDao: ScheduledPaymentDao
    private lateinit var recurringRuleDao: RecurringRuleDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database =
            Room.inMemoryDatabaseBuilder(
                context,
                AppDatabase::class.java,
            ).allowMainThreadQueries().build()

        scheduledPaymentDao = database.scheduledPaymentDao()
        recurringRuleDao = database.recurringRuleDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertScheduledPayment_andRetrieve_success() =
        runBlocking {
            // Given
            val payment =
                ScheduledPaymentEntity(
                    id = 1,
                    title = "Rent Payment",
                    amount = 5000.0,
                    type = "EXPENSE",
                    categoryId = 1,
                    nextDate = LocalDate.now().plusDays(5),
                    description = "Monthly rent",
                )

            // When
            scheduledPaymentDao.insert(payment)
            val retrieved = scheduledPaymentDao.getById(1).first()

            // Then
            assertNotNull(retrieved)
            assertEquals("Rent Payment", retrieved.title)
            assertEquals(5000.0, retrieved.amount)
        }

    @Test
    fun recurringRule_createsForScheduledPayment() =
        runBlocking {
            // Given
            val payment =
                ScheduledPaymentEntity(
                    id = 1,
                    title = "Rent",
                    amount = 5000.0,
                    type = "EXPENSE",
                    categoryId = 1,
                    nextDate = LocalDate.now(),
                    description = null,
                )
            scheduledPaymentDao.insert(payment)

            val recurringRule =
                RecurringRuleEntity(
                    id = 1,
                    scheduledPaymentId = 1,
                    recurrenceType = "MONTHLY",
                    interval = 1,
                    dayOfMonth = 1,
                    dayOfWeek = null,
                    maxOccurrences = null,
                    startDate = LocalDate.now(),
                    endDate = null,
                    lastProcessedDate = null,
                    occurrenceCount = 0,
                    isActive = true,
                )

            // When
            recurringRuleDao.insert(recurringRule)
            val retrieved = recurringRuleDao.getByScheduledPaymentId(1).first()

            // Then
            assertNotNull(retrieved)
            assertEquals("MONTHLY", retrieved.recurrenceType)
            assertEquals(1, retrieved.interval)
            assertTrue(retrieved.isActive)
        }

    @Test
    fun getActiveRecurringRules_filtersCorrectly() =
        runBlocking {
            // Given
            scheduledPaymentDao.insert(
                ScheduledPaymentEntity(1, "Payment 1", 100.0, "EXPENSE", 1, LocalDate.now(), null),
            )
            scheduledPaymentDao.insert(
                ScheduledPaymentEntity(2, "Payment 2", 200.0, "EXPENSE", 1, LocalDate.now(), null),
            )

            recurringRuleDao.insert(
                RecurringRuleEntity(1, 1, "MONTHLY", 1, 1, null, null, LocalDate.now(), null, null, 0, true),
            )
            recurringRuleDao.insert(
                RecurringRuleEntity(2, 2, "MONTHLY", 1, 1, null, null, LocalDate.now(), null, null, 0, false),
            )

            // When
            val activeRules = recurringRuleDao.getAllActive().first()

            // Then
            assertEquals(1, activeRules.size)
            assertTrue(activeRules.first().isActive)
        }

    @Test
    fun updateRecurringRule_updatesProcessedDate() =
        runBlocking {
            // Given
            val payment = ScheduledPaymentEntity(1, "Rent", 5000.0, "EXPENSE", 1, LocalDate.now(), null)
            scheduledPaymentDao.insert(payment)

            val rule = RecurringRuleEntity(1, 1, "MONTHLY", 1, 1, null, null, LocalDate.now(), null, null, 0, true)
            recurringRuleDao.insert(rule)

            // When
            val processedDate = LocalDate.now()
            val updated = rule.copy(lastProcessedDate = processedDate, occurrenceCount = 1)
            recurringRuleDao.update(updated)

            val retrieved = recurringRuleDao.getById(1).first()

            // Then
            assertEquals(processedDate, retrieved.lastProcessedDate)
            assertEquals(1, retrieved.occurrenceCount)
        }

    @Test
    fun deactivateRecurringRule_setsActiveToFalse() =
        runBlocking {
            // Given
            val payment = ScheduledPaymentEntity(1, "Rent", 5000.0, "EXPENSE", 1, LocalDate.now(), null)
            scheduledPaymentDao.insert(payment)

            val rule = RecurringRuleEntity(1, 1, "MONTHLY", 1, 1, null, null, LocalDate.now(), null, null, 0, true)
            recurringRuleDao.insert(rule)

            // When
            recurringRuleDao.updateActive(1, false)
            val retrieved = recurringRuleDao.getById(1).first()

            // Then
            assertEquals(false, retrieved.isActive)
        }

    @Test
    fun weeklyRecurringRule_storesDayOfWeek() =
        runBlocking {
            // Given
            val payment = ScheduledPaymentEntity(1, "Weekly Payment", 100.0, "EXPENSE", 1, LocalDate.now(), null)
            scheduledPaymentDao.insert(payment)

            val rule =
                RecurringRuleEntity(
                    id = 1,
                    scheduledPaymentId = 1,
                    recurrenceType = "WEEKLY",
                    interval = 1,
                    dayOfMonth = null,
                    // Monday
                    dayOfWeek = 1,
                    // 1 year
                    maxOccurrences = 52,
                    startDate = LocalDate.now(),
                    endDate = null,
                    lastProcessedDate = null,
                    occurrenceCount = 0,
                    isActive = true,
                )

            // When
            recurringRuleDao.insert(rule)
            val retrieved = recurringRuleDao.getById(1).first()

            // Then
            assertEquals("WEEKLY", retrieved.recurrenceType)
            assertEquals(1, retrieved.dayOfWeek)
            assertEquals(52, retrieved.maxOccurrences)
        }
}
