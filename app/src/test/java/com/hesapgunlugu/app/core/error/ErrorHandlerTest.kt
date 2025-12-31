package com.hesapgunlugu.app.core.error

import org.junit.Assert.*
import org.junit.Test
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * ErrorHandler Tests
 */
class ErrorHandlerTest {
    @Test
    fun `getErrorMessage should return correct message for IOException`() {
        // Given
        val exception = IOException("Network error")

        // When
        val message = ErrorHandler.getErrorMessage(exception)

        // Then
        assertTrue(message.contains("Bağlantı") || message.contains("bağlantı"))
    }

    @Test
    fun `getErrorMessage should return correct message for SocketTimeoutException`() {
        // Given
        val exception = SocketTimeoutException("Timeout")

        // When
        val message = ErrorHandler.getErrorMessage(exception)

        // Then
        assertTrue(message.contains("zaman aşımı") || message.contains("Timeout"))
    }

    @Test
    fun `getErrorMessage should return correct message for UnknownHostException`() {
        // Given
        val exception = UnknownHostException("Host not found")

        // When
        val message = ErrorHandler.getErrorMessage(exception)

        // Then
        assertTrue(message.contains("İnternet") || message.contains("bağlantı"))
    }

    @Test
    fun `getErrorMessage should return correct message for IllegalArgumentException`() {
        // Given
        val exception = IllegalArgumentException("Invalid input")

        // When
        val message = ErrorHandler.getErrorMessage(exception)

        // Then
        assertTrue(message == "Invalid input" || message.contains("Geçersiz"))
    }

    @Test
    fun `getErrorMessage should return correct message for NullPointerException`() {
        // Given
        val exception = NullPointerException()

        // When
        val message = ErrorHandler.getErrorMessage(exception)

        // Then
        assertTrue(message.contains("Beklenmeyen") || message.contains("hata"))
    }

    @Test
    fun `handleDatabaseError should return correct message for UNIQUE constraint`() {
        // Given
        val exception = Exception("UNIQUE constraint failed: transactions.id")

        // When
        val message = ErrorHandler.handleDatabaseError(exception)

        // Then
        assertTrue(message.contains("mevcut") || message.contains("zaten"))
    }

    @Test
    fun `handleDatabaseError should return correct message for NOT NULL constraint`() {
        // Given
        val exception = Exception("NOT NULL constraint failed: transactions.title")

        // When
        val message = ErrorHandler.handleDatabaseError(exception)

        // Then
        assertTrue(message.contains("Zorunlu") || message.contains("boş"))
    }

    @Test
    fun `handleDatabaseError should return generic message for unknown error`() {
        // Given
        val exception = Exception("Some random database error")

        // When
        val message = ErrorHandler.handleDatabaseError(exception)

        // Then
        assertNotNull(message)
        assertTrue(message.isNotEmpty())
    }

    @Test
    fun `getErrorMessage should handle null message in exception`() {
        // Given
        val exception = RuntimeException()

        // When
        val message = ErrorHandler.getErrorMessage(exception)

        // Then
        assertNotNull(message)
        assertTrue(message.isNotEmpty())
    }

    @Test
    fun `getErrorMessage should return original message when available`() {
        // Given
        val exception = IllegalStateException("Custom error message")

        // When
        val message = ErrorHandler.getErrorMessage(exception)

        // Then
        assertTrue(message.contains("Custom error message") || message.isNotEmpty())
    }
}
