package com.hesapgunlugu.app.core.data.repository

import com.hesapgunlugu.app.core.data.local.ScheduledPaymentDao
import com.hesapgunlugu.app.core.data.mapper.ScheduledPaymentMapper
import com.hesapgunlugu.app.core.domain.model.ScheduledPayment
import com.hesapgunlugu.app.core.domain.repository.ScheduledPaymentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject

/**
 * Planlı İşlemler Repository Implementation
 * Try-catch ile hata yönetimi sağlar
 */
class ScheduledPaymentRepositoryImpl
    @Inject
    constructor(
        private val dao: ScheduledPaymentDao,
    ) : ScheduledPaymentRepository {
        override fun getAllScheduledPayments(): Flow<List<ScheduledPayment>> {
            return dao.getAllScheduledPayments().map { entities ->
                ScheduledPaymentMapper.toDomainList(entities)
            }
        }

        override fun getRecurringIncomes(): Flow<List<ScheduledPayment>> {
            return dao.getRecurringIncomes().map { entities ->
                ScheduledPaymentMapper.toDomainList(entities)
            }
        }

        override fun getRecurringExpenses(): Flow<List<ScheduledPayment>> {
            return dao.getRecurringExpenses().map { entities ->
                ScheduledPaymentMapper.toDomainList(entities)
            }
        }

        override fun getUpcomingPayments(
            startDate: Date,
            endDate: Date,
        ): Flow<List<ScheduledPayment>> {
            return dao.getUpcomingPayments(startDate.time, endDate.time).map { entities ->
                ScheduledPaymentMapper.toDomainList(entities)
            }
        }

        override suspend fun addScheduledPayment(payment: ScheduledPayment): Long {
            val entity = ScheduledPaymentMapper.toEntity(payment)
            return dao.insert(entity) // Room will return generated ID
        }

        override suspend fun updateScheduledPayment(payment: ScheduledPayment) {
            dao.update(ScheduledPaymentMapper.toEntity(payment))
        }

        override suspend fun deleteScheduledPayment(payment: ScheduledPayment) {
            dao.delete(ScheduledPaymentMapper.toEntity(payment))
        }

        override suspend fun deleteScheduledPaymentById(id: Long) {
            val payment = dao.getById(id)
            if (payment != null) {
                dao.delete(payment)
            }
        }

        override suspend fun markAsPaid(
            id: Long,
            isPaid: Boolean,
        ) {
            dao.markAsPaid(id, isPaid)
        }

        override suspend fun getScheduledPaymentById(id: Long): ScheduledPayment? {
            return dao.getById(id)?.let { ScheduledPaymentMapper.toDomain(it) }
        }
    }
