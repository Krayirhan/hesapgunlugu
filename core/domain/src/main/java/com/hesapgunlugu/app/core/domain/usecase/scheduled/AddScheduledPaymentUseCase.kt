package com.hesapgunlugu.app.core.domain.usecase.scheduled

import com.hesapgunlugu.app.core.domain.model.ScheduledPayment
import com.hesapgunlugu.app.core.domain.repository.ScheduledPaymentRepository
import javax.inject.Inject

/**
 * Yeni planlı işlem ekleyen Use Case
 */
class AddScheduledPaymentUseCase
    @Inject
    constructor(
        private val repository: ScheduledPaymentRepository,
    ) {
        suspend operator fun invoke(payment: ScheduledPayment): Result<Long> {
            return try {
                // Validasyonlar
                require(payment.title.isNotBlank()) { "Başlık boş olamaz" }
                require(payment.amount > 0) { "Tutar 0'dan büyük olmalı" }

                if (payment.isRecurring) {
                    require(payment.frequency.isNotBlank()) { "Tekrarlayan işlemler için sıklık belirtilmeli" }
                }

                val id = repository.addScheduledPayment(payment)
                Result.success(id)
            } catch (e: IllegalArgumentException) {
                Result.failure(e)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
