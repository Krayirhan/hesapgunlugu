package com.hesapgunlugu.app.core.data.mapper

import com.hesapgunlugu.app.core.data.local.ScheduledPaymentEntity
import com.hesapgunlugu.app.core.domain.model.ScheduledPayment
import java.util.Date

/**
 * Scheduled Payment Mapper
 *
 * Handles mapping between data layer entities and domain models.
 */
object ScheduledPaymentMapper {
    /**
     * Maps data layer entity to domain model
     */
    fun toDomain(entity: ScheduledPaymentEntity): ScheduledPayment {
        return ScheduledPayment(
            id = entity.id,
            title = entity.title,
            amount = entity.amount,
            isIncome = entity.isIncome,
            isRecurring = entity.isRecurring,
            frequency = entity.frequency,
            dueDate = Date(entity.dueDate),
            emoji = entity.emoji,
            isPaid = entity.isPaid,
            category = entity.category,
            createdAt = Date(entity.createdAt),
        )
    }

    /**
     * Maps domain model to data layer entity
     */
    fun toEntity(domain: ScheduledPayment): ScheduledPaymentEntity {
        return ScheduledPaymentEntity(
            id = domain.id,
            title = domain.title,
            amount = domain.amount,
            isIncome = domain.isIncome,
            isRecurring = domain.isRecurring,
            frequency = domain.frequency,
            dueDate = domain.dueDate.time,
            emoji = domain.emoji,
            isPaid = domain.isPaid,
            category = domain.category,
            createdAt = domain.createdAt.time,
        )
    }

    /**
     * Maps list of entities to list of domain models
     */
    fun toDomainList(entities: List<ScheduledPaymentEntity>): List<ScheduledPayment> {
        return entities.map { toDomain(it) }
    }

    /**
     * Maps list of domain models to list of entities
     */
    fun toEntityList(domains: List<ScheduledPayment>): List<ScheduledPaymentEntity> {
        return domains.map { toEntity(it) }
    }
}
