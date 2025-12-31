package com.hesapgunlugu.app.core.domain.usecase.scheduled

import com.hesapgunlugu.app.core.domain.model.ScheduledPayment
import com.hesapgunlugu.app.core.domain.repository.ScheduledPaymentRepository
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import javax.inject.Inject

/**
 * Yaklaşan ödemeleri getiren Use Case
 */
class GetUpcomingPaymentsUseCase
    @Inject
    constructor(
        private val repository: ScheduledPaymentRepository,
    ) {
        /**
         * Belirtilen gün sayısı içindeki yaklaşan ödemeleri getirir
         * @param days Kaç gün ilerisi (varsayılan 7 gün)
         */
        operator fun invoke(days: Int = 7): Flow<List<ScheduledPayment>> {
            val today =
                Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.time

            val endDate =
                Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_MONTH, days)
                    set(Calendar.HOUR_OF_DAY, 23)
                    set(Calendar.MINUTE, 59)
                    set(Calendar.SECOND, 59)
                }.time

            return repository.getUpcomingPayments(today, endDate)
        }
    }
