package com.hesapgunlugu.app.core.domain.usecase.scheduled

import com.hesapgunlugu.app.core.domain.repository.RecurringRuleRepository
import com.hesapgunlugu.app.core.domain.repository.ScheduledPaymentRepository
import com.hesapgunlugu.app.core.domain.schedule.PlannedOccurrence
import com.hesapgunlugu.app.core.domain.schedule.ScheduleOccurrenceCalculator
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Date
import javax.inject.Inject

/**
 * Generates planned occurrences for scheduled payments in a date range.
 */
class GetPlannedOccurrencesUseCase
    @Inject
    constructor(
        private val scheduledPaymentRepository: ScheduledPaymentRepository,
        private val recurringRuleRepository: RecurringRuleRepository,
    ) {
        operator fun invoke(
            startDate: Date,
            endDate: Date,
        ): Flow<List<PlannedOccurrence>> =
            flow {
                scheduledPaymentRepository.getAllScheduledPayments().collect { payments ->
                    val occurrences =
                        coroutineScope {
                            payments.map { payment ->
                                async {
                                    val rules =
                                        if (payment.isRecurring) {
                                            recurringRuleRepository.getByScheduledPaymentId(payment.id)
                                        } else {
                                            emptyList()
                                        }
                                    ScheduleOccurrenceCalculator.generateOccurrences(
                                        payment = payment,
                                        rules = rules,
                                        startDate = startDate,
                                        endDate = endDate,
                                    )
                                }
                            }.awaitAll().flatten()
                        }
                    emit(occurrences)
                }
            }
    }
