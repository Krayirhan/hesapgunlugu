package com.hesapgunlugu.app.core.domain.usecase.scheduled

import com.hesapgunlugu.app.core.domain.model.ScheduledPayment
import com.hesapgunlugu.app.core.domain.repository.ScheduledPaymentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Tekrarlayan gelirleri getiren Use Case
 */
class GetRecurringIncomesUseCase
    @Inject
    constructor(
        private val repository: ScheduledPaymentRepository,
    ) {
        operator fun invoke(): Flow<List<ScheduledPayment>> {
            return repository.getRecurringIncomes()
        }
    }
