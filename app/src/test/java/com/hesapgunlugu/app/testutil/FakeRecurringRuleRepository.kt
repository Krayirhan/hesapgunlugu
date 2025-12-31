package com.hesapgunlugu.app.testutil

import com.hesapgunlugu.app.core.domain.model.RecurrenceType
import com.hesapgunlugu.app.core.domain.repository.RecurringRuleData
import com.hesapgunlugu.app.core.domain.repository.RecurringRuleRepository
import java.util.Date

class FakeRecurringRuleRepository : RecurringRuleRepository {
    private val rules = mutableListOf<RecurringRuleData>()
    private var nextId = 1L

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
            RecurringRuleData(
                id = nextId++,
                scheduledPaymentId = scheduledPaymentId,
                recurrenceType = recurrenceType,
                interval = interval,
                dayOfMonth = dayOfMonth,
                daysOfWeek = daysOfWeek,
                endDate = endDate,
                maxOccurrences = maxOccurrences,
                currentOccurrences = 0,
                lastGenerated = null,
                isActive = true,
            )
        rules.add(rule)
        return rule.id
    }

    override suspend fun getByScheduledPaymentId(scheduledPaymentId: Long): List<RecurringRuleData> {
        return rules.filter { it.scheduledPaymentId == scheduledPaymentId }
    }

    override suspend fun delete(id: Long) {
        rules.removeAll { it.id == id }
    }

    override suspend fun updateLastGenerated(
        id: Long,
        date: Date,
    ) {
        val index = rules.indexOfFirst { it.id == id }
        if (index >= 0) {
            val existing = rules[index]
            rules[index] = existing.copy(lastGenerated = date)
        }
    }

    fun addRule(rule: RecurringRuleData) {
        rules.add(rule)
        if (rule.id >= nextId) {
            nextId = rule.id + 1
        }
    }
}
