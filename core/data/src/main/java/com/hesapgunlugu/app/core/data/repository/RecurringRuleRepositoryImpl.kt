package com.hesapgunlugu.app.core.data.repository

import com.hesapgunlugu.app.core.data.local.dao.RecurringRuleDao
import com.hesapgunlugu.app.core.data.model.RecurringRule
import com.hesapgunlugu.app.core.domain.model.RecurrenceType
import com.hesapgunlugu.app.core.domain.repository.RecurringRuleData
import com.hesapgunlugu.app.core.domain.repository.RecurringRuleRepository
import kotlinx.coroutines.flow.first
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of RecurringRuleRepository
 * Clean Architecture: Data layer implements domain interface
 */
@Singleton
class RecurringRuleRepositoryImpl
    @Inject
    constructor(
        private val recurringRuleDao: RecurringRuleDao,
    ) : RecurringRuleRepository {
        override suspend fun insert(
            scheduledPaymentId: Long,
            recurrenceType: RecurrenceType,
            interval: Int,
            dayOfMonth: Int?,
            daysOfWeek: List<Int>?,
            endDate: Date?,
            maxOccurrences: Int?,
        ): Long {
            val rule =
                RecurringRule(
                    scheduledPaymentId = scheduledPaymentId,
                    recurrenceType = recurrenceType.toDataModel(),
                    interval = interval,
                    dayOfMonth = dayOfMonth,
                    daysOfWeek = daysOfWeek,
                    endDate = endDate,
                    maxOccurrences = maxOccurrences,
                    currentOccurrences = 0,
                    lastGenerated = null,
                    isActive = true,
                    createdAt = Date(),
                    updatedAt = Date(),
                )
            return recurringRuleDao.insert(rule)
        }

        override suspend fun getByScheduledPaymentId(scheduledPaymentId: Long): List<RecurringRuleData> {
            return recurringRuleDao.getByScheduledPaymentId(scheduledPaymentId)
                .first()
                .map { it.toDomainModel() }
        }

        override suspend fun delete(id: Long) {
            val rule = recurringRuleDao.getById(id).first()
            rule?.let { recurringRuleDao.delete(it) }
        }

        override suspend fun updateLastGenerated(
            id: Long,
            date: Date,
        ) {
            val rule = recurringRuleDao.getById(id).first()
            rule?.let {
                recurringRuleDao.update(it.copy(lastGenerated = date, updatedAt = Date()))
            }
        }

        // Extension functions for mapping
        private fun RecurrenceType.toDataModel(): com.hesapgunlugu.app.core.data.model.RecurrenceType {
            return when (this) {
                RecurrenceType.DAILY -> com.hesapgunlugu.app.core.data.model.RecurrenceType.DAILY
                RecurrenceType.WEEKLY -> com.hesapgunlugu.app.core.data.model.RecurrenceType.WEEKLY
                RecurrenceType.MONTHLY -> com.hesapgunlugu.app.core.data.model.RecurrenceType.MONTHLY
                RecurrenceType.YEARLY -> com.hesapgunlugu.app.core.data.model.RecurrenceType.YEARLY
            }
        }

        private fun com.hesapgunlugu.app.core.data.model.RecurrenceType.toDomainModel(): RecurrenceType {
            return when (this) {
                com.hesapgunlugu.app.core.data.model.RecurrenceType.DAILY -> RecurrenceType.DAILY
                com.hesapgunlugu.app.core.data.model.RecurrenceType.WEEKLY -> RecurrenceType.WEEKLY
                com.hesapgunlugu.app.core.data.model.RecurrenceType.MONTHLY -> RecurrenceType.MONTHLY
                com.hesapgunlugu.app.core.data.model.RecurrenceType.YEARLY -> RecurrenceType.YEARLY
            }
        }

        private fun RecurringRule.toDomainModel(): RecurringRuleData {
            return RecurringRuleData(
                id = id,
                scheduledPaymentId = scheduledPaymentId,
                recurrenceType = recurrenceType.toDomainModel(),
                interval = interval,
                dayOfMonth = dayOfMonth,
                daysOfWeek = daysOfWeek,
                endDate = endDate,
                maxOccurrences = maxOccurrences,
                currentOccurrences = currentOccurrences,
                lastGenerated = lastGenerated,
                isActive = isActive,
            )
        }
    }
