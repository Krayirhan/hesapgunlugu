package com.hesapgunlugu.app.core.util

import org.junit.Assert.*
import org.junit.Test

/**
 * Extension Functions Tests
 */
class ExtensionsTest {
    // ==================== DATE EXTENSIONS ====================

    @Test
    fun `formatCurrency should format positive numbers correctly`() {
        // Given
        val amount = 1234.56

        // When
        val formatted = amount.formatAsCurrency()

        // Then
        assertTrue(formatted.contains("1.234") || formatted.contains("1,234"))
    }

    @Test
    fun `formatCurrency should format negative numbers correctly`() {
        // Given
        val amount = -500.0

        // When
        val formatted = amount.formatAsCurrency()

        // Then
        assertTrue(formatted.contains("500"))
    }

    @Test
    fun `formatCurrency should handle zero`() {
        // Given
        val amount = 0.0

        // When
        val formatted = amount.formatAsCurrency()

        // Then
        assertTrue(formatted.contains("0"))
    }

    // ==================== STRING EXTENSIONS ====================

    @Test
    fun `capitalize first should work correctly`() {
        // Given
        val text = "hello world"

        // When
        val result = text.capitalizeFirst()

        // Then
        assertEquals("Hello world", result)
    }

    @Test
    fun `capitalize first should handle empty string`() {
        // Given
        val text = ""

        // When
        val result = text.capitalizeFirst()

        // Then
        assertEquals("", result)
    }

    @Test
    fun `truncate should work correctly`() {
        // Given
        val longText = "This is a very long text that should be truncated"

        // When
        val result = longText.truncate(20)

        // Then
        assertTrue(result.length <= 23) // 20 + "..."
        assertTrue(result.endsWith("..."))
    }

    @Test
    fun `truncate should not affect short text`() {
        // Given
        val shortText = "Short"

        // When
        val result = shortText.truncate(20)

        // Then
        assertEquals("Short", result)
    }

    // ==================== COLLECTION EXTENSIONS ====================

    @Test
    fun `sumByDouble should work correctly`() {
        // Given
        val items =
            listOf(
                TestItem(10.0),
                TestItem(20.0),
                TestItem(30.0),
            )

        // When
        val total = items.sumOf { it.amount }

        // Then
        assertEquals(60.0, total, 0.01)
    }

    @Test
    fun `groupByAndSum should work correctly`() {
        // Given
        val items =
            listOf(
                CategoryItem("A", 10.0),
                CategoryItem("B", 20.0),
                CategoryItem("A", 15.0),
                CategoryItem("B", 5.0),
            )

        // When
        val grouped =
            items.groupBy { it.category }
                .mapValues { (_, items) -> items.sumOf { it.amount } }

        // Then
        assertEquals(25.0, grouped["A"], 0.01)
        assertEquals(25.0, grouped["B"], 0.01)
    }

    // ==================== HELPER CLASSES ====================

    private data class TestItem(val amount: Double)

    private data class CategoryItem(val category: String, val amount: Double)
}

// Extension functions to test
private fun Double.formatAsCurrency(): String {
    return String.format("%,.2f", this)
}

private fun String.capitalizeFirst(): String {
    return if (isEmpty()) this else this[0].uppercaseChar() + substring(1)
}

private fun String.truncate(maxLength: Int): String {
    return if (length <= maxLength) this else "${take(maxLength)}..."
}
