package com.hesapgunlugu.app.core.domain.schedule

import com.hesapgunlugu.app.core.domain.model.RecurrenceType
import com.hesapgunlugu.app.core.domain.model.ScheduledPayment
import com.hesapgunlugu.app.core.domain.repository.RecurringRuleData
import java.util.Calendar
import java.util.Date
import kotlin.math.max

private const val MAX_OCCURRENCES_GUARD = 1000

object ScheduleOccurrenceCalculator {
    fun generateOccurrences(
        payment: ScheduledPayment,
        rules: List<RecurringRuleData>,
        startDate: Date,
        endDate: Date,
    ): List<PlannedOccurrence> {
        if (!payment.isRecurring) {
            return if (!payment.isPaid && payment.dueDate.inRange(startDate..endDate)) {
                listOf(PlannedOccurrence(payment, payment.dueDate))
            } else {
                emptyList()
            }
        }

        val effectiveRules =
            if (rules.isEmpty()) {
                listOf(
                    RecurringRuleData(
                        id = 0,
                        scheduledPaymentId = payment.id,
                        recurrenceType = payment.frequency.toRecurrenceType(),
                        interval = 1,
                        dayOfMonth = null,
                        daysOfWeek = null,
                        endDate = null,
                        maxOccurrences = null,
                        currentOccurrences = 0,
                        lastGenerated = null,
                        isActive = true,
                    ),
                )
            } else {
                rules.filter { it.isActive }
            }

        return effectiveRules.flatMap { rule ->
            generateOccurrencesForRule(payment, rule, startDate, endDate)
        }
    }

    private fun generateOccurrencesForRule(
        payment: ScheduledPayment,
        rule: RecurringRuleData,
        startDate: Date,
        endDate: Date,
    ): List<PlannedOccurrence> {
        val results = mutableListOf<PlannedOccurrence>()
        val maxOccurrences = rule.maxOccurrences ?: MAX_OCCURRENCES_GUARD
        val rangeEnd = rule.endDate?.let { if (it.before(endDate)) it else endDate } ?: endDate
        val anchor = payment.dueDate

        var occurrenceDate = alignToFirstOccurrence(anchor, rule, startDate)
        var count = 0

        while (occurrenceDate != null && !occurrenceDate.after(rangeEnd) && count < maxOccurrences) {
            if (!occurrenceDate.before(startDate)) {
                results.add(PlannedOccurrence(payment, occurrenceDate))
            }
            occurrenceDate = nextOccurrence(occurrenceDate, anchor, rule)
            count += 1
        }

        return results
    }

    private fun alignToFirstOccurrence(
        anchor: Date,
        rule: RecurringRuleData,
        startDate: Date,
    ): Date? {
        val base = if (anchor.after(startDate)) anchor else startDate
        var candidate = anchor
        var guard = 0

        while (candidate.before(base) && guard < MAX_OCCURRENCES_GUARD) {
            candidate = nextOccurrence(candidate, anchor, rule) ?: return null
            guard += 1
        }

        return candidate
    }

    private fun nextOccurrence(
        current: Date,
        anchor: Date,
        rule: RecurringRuleData,
    ): Date? {
        val calendar = Calendar.getInstance().apply { time = current }

        when (rule.recurrenceType) {
            RecurrenceType.DAILY -> calendar.add(Calendar.DAY_OF_MONTH, rule.interval)
            RecurrenceType.WEEKLY -> {
                if (!rule.daysOfWeek.isNullOrEmpty()) {
                    return nextWeeklyByDays(current, anchor, rule)
                }
                calendar.add(Calendar.WEEK_OF_YEAR, rule.interval)
            }
            RecurrenceType.MONTHLY -> {
                calendar.add(Calendar.MONTH, rule.interval)
                val day = rule.dayOfMonth ?: Calendar.getInstance().apply { time = anchor }.get(Calendar.DAY_OF_MONTH)
                calendar.set(Calendar.DAY_OF_MONTH, day.coerceIn(1, calendar.getActualMaximum(Calendar.DAY_OF_MONTH)))
            }
            RecurrenceType.YEARLY -> {
                calendar.add(Calendar.YEAR, rule.interval)
                val anchorCal = Calendar.getInstance().apply { time = anchor }
                calendar.set(Calendar.MONTH, anchorCal.get(Calendar.MONTH))
                calendar.set(Calendar.DAY_OF_MONTH, anchorCal.get(Calendar.DAY_OF_MONTH))
            }
        }

        return calendar.time
    }

    private fun nextWeeklyByDays(
        current: Date,
        anchor: Date,
        rule: RecurringRuleData,
    ): Date? {
        val targetDays = rule.daysOfWeek?.sorted() ?: return null
        val anchorCal = Calendar.getInstance().apply { time = anchor }
        val currentCal = Calendar.getInstance().apply { time = current }

        // Find next day in the same or future week, then jump by interval weeks.
        for (day in targetDays) {
            val candidate = currentCal.clone() as Calendar
            candidate.set(Calendar.DAY_OF_WEEK, day.toCalendarDayOfWeek())
            if (candidate.time.after(current)) {
                return candidate.time
            }
        }

        // No remaining day in this week; jump interval weeks from anchor week start.
        val weekStart = anchorCal.clone() as Calendar
        weekStart.set(Calendar.DAY_OF_WEEK, weekStart.firstDayOfWeek)
        weekStart.add(Calendar.WEEK_OF_YEAR, max(1, rule.interval))
        weekStart.set(Calendar.DAY_OF_WEEK, targetDays.first().toCalendarDayOfWeek())
        return weekStart.time
    }

    private fun Int.toCalendarDayOfWeek(): Int {
        // Domain: 1=Monday..7=Sunday -> Calendar: Sunday=1..Saturday=7
        return when (this) {
            1 -> Calendar.MONDAY
            2 -> Calendar.TUESDAY
            3 -> Calendar.WEDNESDAY
            4 -> Calendar.THURSDAY
            5 -> Calendar.FRIDAY
            6 -> Calendar.SATURDAY
            7 -> Calendar.SUNDAY
            else -> Calendar.MONDAY
        }
    }
}

private fun String.toRecurrenceType(): RecurrenceType {
    return when (parseScheduleFrequency(this)) {
        ScheduleFrequency.DAILY -> RecurrenceType.DAILY
        ScheduleFrequency.WEEKLY -> RecurrenceType.WEEKLY
        ScheduleFrequency.MONTHLY -> RecurrenceType.MONTHLY
        ScheduleFrequency.YEARLY -> RecurrenceType.YEARLY
    }
}

private operator fun Date.compareTo(other: Date): Int = this.time.compareTo(other.time)

private fun ClosedRange<Date>.contains(date: Date): Boolean = date.time >= start.time && date.time <= endInclusive.time

private fun Date.inRange(range: ClosedRange<Date>): Boolean = range.contains(this)
