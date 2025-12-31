package com.hesapgunlugu.app.data.mapper

import com.hesapgunlugu.app.core.data.local.TransactionEntity
import com.hesapgunlugu.app.core.data.mapper.TransactionMapper
import com.hesapgunlugu.app.core.domain.model.Transaction
import com.hesapgunlugu.app.core.domain.model.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * TransactionMapper Tests
 *
 * Tests for data layer mapping functions.
 */
class TransactionMapperTest {
    @Test
    fun `toDomain should correctly map entity to domain model`() {
        // Given
        val entity =
            TransactionEntity(
                id = 1,
                title = "Test",
                amount = 100.0,
                date = 1_703_424_000_000L,
                type = "EXPENSE",
                category = "Market",
                emoji = ":)",
            )

        // When
        val domain = TransactionMapper.toDomain(entity)

        // Then
        assertEquals(1, domain.id)
        assertEquals("Test", domain.title)
        assertEquals(100.0, domain.amount, 0.01)
        assertEquals(TransactionType.EXPENSE, domain.type)
        assertEquals("Market", domain.category)
        assertEquals(1_703_424_000_000L, domain.timestamp)
        assertEquals(":)", domain.emoji)
    }

    @Test
    fun `toEntity should correctly map domain to entity`() {
        // Given
        val timestamp = 1_703_424_000_000L
        val domain =
            Transaction(
                id = 2,
                title = "Salary",
                amount = 5000.0,
                timestamp = timestamp,
                type = TransactionType.INCOME,
                category = "Maas",
                emoji = ":)",
            )

        // When
        val entity = TransactionMapper.toEntity(domain)

        // Then
        assertEquals(2, entity.id)
        assertEquals("Salary", entity.title)
        assertEquals(5000.0, entity.amount, 0.01)
        assertEquals("INCOME", entity.type)
        assertEquals("Maas", entity.category)
        assertEquals(":)", entity.emoji)
        assertEquals(timestamp, entity.date)
    }

    @Test
    fun `toDomainList should map all entities`() {
        // Given
        val entities =
            listOf(
                TransactionEntity(1, "Test1", 100.0, 1_703_424_000_000L, "EXPENSE", "Cat1", ":)"),
                TransactionEntity(2, "Test2", 200.0, 1_703_424_000_000L, "INCOME", "Cat2", ":)"),
            )

        // When
        val domains = TransactionMapper.toDomainList(entities)

        // Then
        assertEquals(2, domains.size)
        assertEquals("Test1", domains[0].title)
        assertEquals("Test2", domains[1].title)
        assertEquals(TransactionType.EXPENSE, domains[0].type)
        assertEquals(TransactionType.INCOME, domains[1].type)
    }

    @Test
    fun `toEntityList should map all domains`() {
        // Given
        val domains =
            listOf(
                Transaction(1, "Test1", 100.0, 1_703_424_000_000L, TransactionType.EXPENSE, "Cat1", ":)"),
                Transaction(2, "Test2", 200.0, 1_703_424_000_000L, TransactionType.INCOME, "Cat2", ":)"),
            )

        // When
        val entities = TransactionMapper.toEntityList(domains)

        // Then
        assertEquals(2, entities.size)
        assertEquals("EXPENSE", entities[0].type)
        assertEquals("INCOME", entities[1].type)
    }

    @Test
    fun `extension function toDomain should work correctly`() {
        // Given
        val entity = TransactionEntity(1, "Test", 100.0, 1_703_424_000_000L, "EXPENSE", "Cat", ":)")

        // When
        val domain = entity.toDomain()

        // Then
        assertEquals("Test", domain.title)
        assertEquals(TransactionType.EXPENSE, domain.type)
    }

    @Test
    fun `extension function toEntity should work correctly`() {
        // Given
        val domain = Transaction(1, "Test", 100.0, 1_703_424_000_000L, TransactionType.EXPENSE, "Cat", ":)")

        // When
        val entity = domain.toEntity()

        // Then
        assertEquals("Test", entity.title)
        assertEquals("EXPENSE", entity.type)
    }
}
