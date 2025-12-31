package com.hesapgunlugu.app.domain.usecase.transaction

import com.hesapgunlugu.app.testutil.FakeTransactionRepository
import com.hesapgunlugu.app.testutil.MainDispatcherRule
import com.hesapgunlugu.app.testutil.TestFixtures
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * AddTransactionUseCase Tests
 *
 * Senior Level Test Coverage:
 * - Happy path tests
 * - Edge case tests
 * - Error handling tests
 * - Validation tests
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AddTransactionUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var useCase: AddTransactionUseCase
    private lateinit var repository: FakeTransactionRepository

    @Before
    fun setUp() {
        repository = FakeTransactionRepository()
        useCase = AddTransactionUseCase(repository)
    }

    // ==================== HAPPY PATH TESTS ====================

    @Test
    fun `invoke with valid transaction should return success`() =
        runTest {
            // Given
            val transaction = TestFixtures.createExpenseTransaction()

            // When
            val result = useCase(transaction)

            // Then
            assertTrue(result.isSuccess)
            assertEquals(1, repository.getTransactionCount())
        }

    @Test
    fun `invoke with valid income transaction should return success`() =
        runTest {
            // Given
            val transaction = TestFixtures.createIncomeTransaction()

            // When
            val result = useCase(transaction)

            // Then
            assertTrue(result.isSuccess)
            assertNotNull(repository.findById(1))
        }

    @Test
    fun `invoke should add transaction to repository`() =
        runTest {
            // Given
            val transaction =
                TestFixtures.createTransaction(
                    title = "Test Title",
                    amount = 500.0,
                    category = "Test Category",
                )

            // When
            useCase(transaction)

            // Then
            val addedTransaction = repository.findById(1)
            assertNotNull(addedTransaction)
            assertEquals("Test Title", addedTransaction?.title)
            assertEquals(500.0, addedTransaction?.amount ?: 0.0, 0.01)
        }

    // ==================== VALIDATION TESTS ====================

    @Test
    fun `invoke with blank title should return failure`() =
        runTest {
            // Given
            val transaction = TestFixtures.createTransaction(title = "")

            // When
            val result = useCase(transaction)

            // Then
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is IllegalArgumentException)
            assertTrue(result.exceptionOrNull()?.message?.contains("Başlık") == true)
        }

    @Test
    fun `invoke with whitespace title should return failure`() =
        runTest {
            // Given
            val transaction = TestFixtures.createTransaction(title = "   ")

            // When
            val result = useCase(transaction)

            // Then
            assertTrue(result.isFailure)
        }

    @Test
    fun `invoke with zero amount should return failure`() =
        runTest {
            // Given
            val transaction = TestFixtures.createTransaction(amount = 0.0)

            // When
            val result = useCase(transaction)

            // Then
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is IllegalArgumentException)
            assertTrue(result.exceptionOrNull()?.message?.contains("Tutar") == true)
        }

    @Test
    fun `invoke with negative amount should return failure`() =
        runTest {
            // Given
            val transaction = TestFixtures.createTransaction(amount = -100.0)

            // When
            val result = useCase(transaction)

            // Then
            assertTrue(result.isFailure)
        }

    // ==================== ERROR HANDLING TESTS ====================

    @Test
    fun `invoke when repository fails should return failure`() =
        runTest {
            // Given
            repository.shouldReturnError = true
            repository.errorToReturn = RuntimeException("Database error")
            val transaction = TestFixtures.createExpenseTransaction()

            // When
            val result = useCase(transaction)

            // Then
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is RuntimeException)
        }

    // ==================== EDGE CASE TESTS ====================

    @Test
    fun `invoke with very small amount should succeed`() =
        runTest {
            // Given
            val transaction = TestFixtures.createTransaction(amount = 0.01)

            // When
            val result = useCase(transaction)

            // Then
            assertTrue(result.isSuccess)
        }

    @Test
    fun `invoke with very large amount should succeed`() =
        runTest {
            // Given
            val transaction = TestFixtures.createTransaction(amount = 999_999_999.99)

            // When
            val result = useCase(transaction)

            // Then
            assertTrue(result.isSuccess)
        }

    @Test
    fun `invoke with special characters in title should succeed`() =
        runTest {
            // Given
            val transaction = TestFixtures.createTransaction(title = "Test @#$%^& İşlem")

            // When
            val result = useCase(transaction)

            // Then
            assertTrue(result.isSuccess)
        }

    @Test
    fun `invoke multiple times should add all transactions`() =
        runTest {
            // Given
            val transactions = TestFixtures.createTransactionList(5)

            // When
            transactions.forEach { useCase(it) }

            // Then
            assertEquals(5, repository.getTransactionCount())
        }
}
