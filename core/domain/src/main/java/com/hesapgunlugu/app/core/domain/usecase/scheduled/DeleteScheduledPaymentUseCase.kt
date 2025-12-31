package com.hesapgunlugu.app.core.domain.usecase.scheduled

import com.hesapgunlugu.app.core.domain.repository.ScheduledPaymentRepository
import javax.inject.Inject

/**
 * Planlı işlem silen Use Case
 */
class DeleteScheduledPaymentUseCase
    @Inject
    constructor(
        private val repository: ScheduledPaymentRepository,
    ) {
        suspend operator fun invoke(id: Long): Result<Unit> {
            return try {
                require(id > 0) { "Geçersiz işlem ID" }
                repository.deleteScheduledPaymentById(id)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
