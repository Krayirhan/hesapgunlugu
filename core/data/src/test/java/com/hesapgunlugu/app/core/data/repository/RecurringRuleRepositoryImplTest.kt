package com.hesapgunlugu.app.core.data.repository

import com.hesapgunlugu.app.core.data.local.dao.RecurringRuleDao
import com.hesapgunlugu.app.core.data.model.RecurringRule
import com.hesapgunlugu.app.core.domain.model.RecurrenceType
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.util.Date
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Unit tests for RecurringRuleRepositoryImpl
 */
class RecurringRuleRepositoryImplTest {
    private lateinit var dao: RecurringRuleDao
    private lateinit var repository: RecurringRuleRepositoryImpl

    @Before
    fun setup() {
        dao = mockk(relaxed = true)
        repository = RecurringRuleRepositoryImpl(dao)
    }

    @Test
    fun insert_buildsExpectedRuleAndReturnsId() =
        runTest {
            val endDate = Date()
            val captured = slot<RecurringRule>()
            coEvery { dao.insert(capture(captured)) } returns 42L

            val result =
                repository.insert(
                    scheduledPaymentId = 10,
                    recurrenceType = RecurrenceType.MONTHLY,
                    interval = 2,
                    dayOfMonth = 5,
                    daysOfWeek = listOf(1, 3),
                    endDate = endDate,
                    maxOccurrences = 12,
                )

            assertEquals(42L, result)
            assertEquals(10, captured.captured.scheduledPaymentId)
            assertEquals(
                com.hesapgunlugu.app.core.data.model.RecurrenceType.MONTHLY,
                captured.captured.recurrenceType,
            )
            assertEquals(2, captured.captured.interval)
            assertEquals(5, captured.captured.dayOfMonth)
            assertEquals(listOf(1, 3), captured.captured.daysOfWeek)
            assertEquals(endDate, captured.captured.endDate)
            assertEquals(12, captured.captured.maxOccurrences)
            assertEquals(0, captured.captured.currentOccurrences)
            assertEquals(true, captured.captured.isActive)
            assertNotNull(captured.captured.createdAt)
            assertNotNull(captured.captured.updatedAt)
        }

    @Test
    fun getByScheduledPaymentId_returnsMappedDomainModels() =
        runTest {
            val rule =
                RecurringRule(
                    id = 1,
                    scheduledPaymentId = 10,
                    recurrenceType = com.hesapgunlugu.app.core.data.model.RecurrenceType.WEEKLY,
                    interval = 3,
                    dayOfMonth = null,
                    daysOfWeek = listOf(2, 4),
                    endDate = null,
                    maxOccurrences = 5,
                    currentOccurrences = 2,
                    lastGenerated = null,
                    isActive = true,
                    createdAt = Date(),
                    updatedAt = Date(),
                )
            every { dao.getByScheduledPaymentId(10) } returns flowOf(listOf(rule))

            val result = repository.getByScheduledPaymentId(10)

            assertEquals(1, result.size)
            assertEquals(RecurrenceType.WEEKLY, result[0].recurrenceType)
            assertEquals(3, result[0].interval)
            assertEquals(listOf(2, 4), result[0].daysOfWeek)
            assertEquals(5, result[0].maxOccurrences)
        }

    @Test
    fun delete_whenRuleExists_deletesIt() =
        runTest {
            val rule =
                RecurringRule(
                    id = 1,
                    scheduledPaymentId = 10,
                    recurrenceType = com.hesapgunlugu.app.core.data.model.RecurrenceType.DAILY,
                    interval = 1,
                    dayOfMonth = null,
                    daysOfWeek = null,
                    endDate = null,
                    maxOccurrences = null,
                    currentOccurrences = 0,
                    lastGenerated = null,
                    isActive = true,
                    createdAt = Date(),
                    updatedAt = Date(),
                )
            every { dao.getById(1) } returns flowOf(rule)

            repository.delete(1)

            coVerify { dao.delete(rule) }
        }

    @Test
    fun updateLastGenerated_updatesRule() =
        runTest {
            val rule =
                RecurringRule(
                    id = 1,
                    scheduledPaymentId = 10,
                    recurrenceType = com.hesapgunlugu.app.core.data.model.RecurrenceType.DAILY,
                    interval = 1,
                    dayOfMonth = null,
                    daysOfWeek = null,
                    endDate = null,
                    maxOccurrences = null,
                    currentOccurrences = 0,
                    lastGenerated = null,
                    isActive = true,
                    createdAt = Date(),
                    updatedAt = Date(),
                )
            val updated = slot<RecurringRule>()
            every { dao.getById(1) } returns flowOf(rule)
            coEvery { dao.update(capture(updated)) } returns Unit

            val newDate = Date()
            repository.updateLastGenerated(1, newDate)

            assertEquals(newDate, updated.captured.lastGenerated)
            assertNotNull(updated.captured.updatedAt)
        }
}
