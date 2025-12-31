package com.hesapgunlugu.app.core.domain.usecase.recurring

import com.hesapgunlugu.app.core.domain.model.RecurrenceType
import com.hesapgunlugu.app.core.domain.repository.RecurringRuleRepository
import java.util.*
import javax.inject.Inject

/**
 * Tekrarlayan ödeme kuralı ekle use case
 * Clean Architecture: Use case only depends on domain interfaces
 */
class AddRecurringRuleUseCase
    @Inject
    constructor(
        private val recurringRuleRepository: RecurringRuleRepository,
    ) {
        suspend operator fun invoke(
            scheduledPaymentId: Long,
            recurrenceType: RecurrenceType,
            interval: Int = 1,
            dayOfMonth: Int? = null,
            daysOfWeek: List<Int>? = null,
            endDate: String? = null,
            maxOccurrences: Int? = null,
        ): Result<Long> {
            return try {
                val id =
                    recurringRuleRepository.insert(
                        scheduledPaymentId = scheduledPaymentId,
                        recurrenceType = recurrenceType,
                        interval = interval,
                        dayOfMonth = dayOfMonth,
                        daysOfWeek = daysOfWeek,
                        endDate = endDate?.let { Date(it) },
                        maxOccurrences = maxOccurrences,
                    )
                Result.success(id)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
