package com.hesapgunlugu.app.core.data.mapper

import com.hesapgunlugu.app.core.data.local.DataCategoryTotal
import com.hesapgunlugu.app.core.data.local.TransactionEntity
import com.hesapgunlugu.app.core.domain.model.CategoryTotal
import com.hesapgunlugu.app.core.domain.model.Transaction
import com.hesapgunlugu.app.core.domain.model.TransactionType

/**
 * Transaction Mapper
 *
 * Handles mapping between data layer entities and domain models.
 * Senior Level Best Practice: Dedicated mapper classes for clean separation.
 *
 * Benefits:
 * - Single responsibility
 * - Easier to test
 * - Centralized mapping logic
 * - Type-safe conversions
 */
object TransactionMapper {
    /**
     * Maps data layer entity to domain model
     */
    fun toDomain(entity: TransactionEntity): Transaction {
        return Transaction(
            id = entity.id,
            title = entity.title,
            amount = entity.amount,
            type = TransactionType.valueOf(entity.type),
            timestamp = entity.date,
            category = entity.category,
            emoji = entity.emoji,
            scheduledPaymentId = entity.scheduledPaymentId,
        )
    }

    /**
     * Maps domain model to data layer entity
     */
    fun toEntity(domain: Transaction): TransactionEntity {
        return TransactionEntity(
            id = domain.id,
            title = domain.title,
            amount = domain.amount,
            type = domain.type.name,
            date = domain.timestamp,
            category = domain.category,
            emoji = domain.emoji,
            scheduledPaymentId = domain.scheduledPaymentId,
        )
    }

    /**
     * Maps list of entities to list of domain models
     */
    fun toDomainList(entities: List<TransactionEntity>): List<Transaction> {
        return entities.map { toDomain(it) }
    }

    /**
     * Maps list of domain models to list of entities
     */
    fun toEntityList(domains: List<Transaction>): List<TransactionEntity> {
        return domains.map { toEntity(it) }
    }

    /**
     * Maps DataCategoryTotal to CategoryTotal
     */
    fun toDomain(data: DataCategoryTotal): CategoryTotal {
        return CategoryTotal(
            category = data.category,
            total = data.total,
        )
    }
}
