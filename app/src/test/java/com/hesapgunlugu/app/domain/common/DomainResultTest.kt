package com.hesapgunlugu.app.domain.common

import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

/**
 * DomainResult Tests
 */
class DomainResultTest {
    @Test
    fun `Success should hold data correctly`() {
        // Given
        val data = "Test Data"

        // When
        val result = DomainResult.success(data)

        // Then
        assertTrue(result.isSuccess)
        assertFalse(result.isError)
        assertFalse(result.isLoading)
        assertEquals("Test Data", result.getOrNull())
    }

    @Test
    fun `Error should hold error info correctly`() {
        // Given
        val errorMessage = "Something went wrong"
        val exception = RuntimeException(errorMessage)

        // When
        val result = DomainResult.error(errorMessage, exception, ErrorCode.DATABASE_ERROR)

        // Then
        assertTrue(result.isError)
        assertFalse(result.isSuccess)
        assertNull(result.getOrNull())
        assertTrue(result is DomainResult.Error)
        assertEquals(ErrorCode.DATABASE_ERROR, (result as DomainResult.Error).errorCode)
    }

    @Test
    fun `Loading should be identified correctly`() {
        // When
        val result = DomainResult.loading()

        // Then
        assertTrue(result.isLoading)
        assertFalse(result.isSuccess)
        assertFalse(result.isError)
    }

    @Test
    fun `getOrThrow should return data for Success`() {
        // Given
        val result = DomainResult.success(42)

        // When
        val value = result.getOrThrow()

        // Then
        assertEquals(42, value)
    }

    @Test(expected = RuntimeException::class)
    fun `getOrThrow should throw for Error`() {
        // Given
        val result = DomainResult.error("Error", RuntimeException("Test"))

        // When
        result.getOrThrow()
    }

    @Test(expected = IllegalStateException::class)
    fun `getOrThrow should throw for Loading`() {
        // Given
        val result = DomainResult.loading()

        // When
        result.getOrThrow()
    }

    @Test
    fun `getOrDefault should return data for Success`() {
        // Given
        val result = DomainResult.success(42)

        // When
        val value = result.getOrDefault(0)

        // Then
        assertEquals(42, value)
    }

    @Test
    fun `getOrDefault should return default for Error`() {
        // Given
        val result: DomainResult<Int> = DomainResult.error("Error")

        // When
        val value = result.getOrDefault(99)

        // Then
        assertEquals(99, value)
    }

    @Test
    fun `map should transform Success data`() {
        // Given
        val result = DomainResult.success(10)

        // When
        val mapped = result.map { it * 2 }

        // Then
        assertTrue(mapped.isSuccess)
        assertEquals(20, mapped.getOrNull())
    }

    @Test
    fun `map should preserve Error`() {
        // Given
        val result: DomainResult<Int> = DomainResult.error("Error")

        // When
        val mapped = result.map { it * 2 }

        // Then
        assertTrue(mapped.isError)
    }

    @Test
    fun `flatMap should chain Success operations`() {
        // Given
        val result = DomainResult.success(10)

        // When
        val flatMapped = result.flatMap { DomainResult.success(it * 2) }

        // Then
        assertTrue(flatMapped.isSuccess)
        assertEquals(20, flatMapped.getOrNull())
    }

    @Test
    fun `flatMap should preserve Error`() {
        // Given
        val result: DomainResult<Int> = DomainResult.error("Error")

        // When
        val flatMapped = result.flatMap { DomainResult.success(it * 2) }

        // Then
        assertTrue(flatMapped.isError)
    }

    @Test
    fun `onSuccess should be called for Success`() {
        // Given
        var called = false
        val result = DomainResult.success("Data")

        // When
        result.onSuccess { called = true }

        // Then
        assertTrue(called)
    }

    @Test
    fun `onSuccess should not be called for Error`() {
        // Given
        var called = false
        val result: DomainResult<String> = DomainResult.error("Error")

        // When
        result.onSuccess { called = true }

        // Then
        assertFalse(called)
    }

    @Test
    fun `onError should be called for Error`() {
        // Given
        var called = false
        val result: DomainResult<String> = DomainResult.error("Error")

        // When
        result.onError { called = true }

        // Then
        assertTrue(called)
    }

    @Test
    fun `fromKotlinResult should convert success`() {
        // Given
        val kotlinResult = Result.success("Data")

        // When
        val domainResult = DomainResult.fromKotlinResult(kotlinResult)

        // Then
        assertTrue(domainResult.isSuccess)
        assertEquals("Data", domainResult.getOrNull())
    }

    @Test
    fun `fromKotlinResult should convert failure`() {
        // Given
        val kotlinResult = Result.failure<String>(RuntimeException("Error"))

        // When
        val domainResult = DomainResult.fromKotlinResult(kotlinResult)

        // Then
        assertTrue(domainResult.isError)
    }

    @Test
    fun `runCatchingDomain should catch exceptions`() =
        runTest {
            // When
            val result =
                runCatchingDomain<Int> {
                    throw RuntimeException("Test error")
                }

            // Then
            assertTrue(result.isError)
        }

    @Test
    fun `runCatchingDomain should return success`() =
        runTest {
            // When
            val result =
                runCatchingDomain {
                    42
                }

            // Then
            assertTrue(result.isSuccess)
            assertEquals(42, result.getOrNull())
        }

    @Test
    fun `toDomainResult extension should work`() {
        // Given
        val kotlinResult = Result.success(100)

        // When
        val domainResult = kotlinResult.toDomainResult()

        // Then
        assertTrue(domainResult.isSuccess)
        assertEquals(100, domainResult.getOrNull())
    }
}
