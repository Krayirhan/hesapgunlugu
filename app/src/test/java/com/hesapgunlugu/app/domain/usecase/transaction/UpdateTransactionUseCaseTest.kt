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
 * UpdateTransactionUseCase Tests
 */
@OptIn(ExperimentalCoroutinesApi::class)
class UpdateTransactionUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var useCase: UpdateTransactionUseCase
    private lateinit var repository: FakeTransactionRepository

    @Before
    fun setUp() {
        repository = FakeTransactionRepository()
        useCase = UpdateTransactionUseCase(repository)
    }

    @Test
    fun `invoke with valid transaction should return success`() =
        runTest {
            // Given
            val original = TestFixtures.createExpenseTransaction(id = 1, title = "Original")
            repository.addTransactionSync(original)

            val updated = original.copy(title = "Updated", amount = 200.0)

            // When
            val result = useCase(updated)

            // Then
            assertTrue(result.isSuccess)
            assertEquals("Updated", repository.findById(1)?.title)
            assertEquals(200.0, repository.findById(1)?.amount ?: 0.0, 0.01)
        }

    @Test
    fun `invoke with zero id should return failure`() =
        runTest {
            // Given
            val transaction = TestFixtures.createTransaction(id = 0)

            // When
            val result = useCase(transaction)

            // Then
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        }

    @Test
    fun `invoke with negative id should return failure`() =
        runTest {
            // Given
            val transaction = TestFixtures.createTransaction(id = -1)

            // When
            val result = useCase(transaction)

            // Then
            assertTrue(result.isFailure)
        }

    @Test
    fun `invoke with blank title should return failure`() =
        runTest {
            // Given
            val transaction = TestFixtures.createTransaction(id = 1, title = "")

            // When
            val result = useCase(transaction)

            // Then
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull()?.message?.contains("Başlık") == true)
        }

    @Test
    fun `invoke with zero amount should return failure`() =
        runTest {
            // Given
            val transaction = TestFixtures.createTransaction(id = 1, amount = 0.0)

            // When
            val result = useCase(transaction)

            // Then
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull()?.message?.contains("Tutar") == true)
        }

    @Test
    fun `invoke when repository fails should return failure`() =
        runTest {
            // Given
            repository.shouldReturnError = true
            val transaction = TestFixtures.createTransaction(id = 1)

            // When
            val result = useCase(transaction)

            // Then
            assertTrue(result.isFailure)
        }

    @Test
    fun `invoke should preserve other transactions`() =
        runTest {
            // Given
            val transaction1 = TestFixtures.createExpenseTransaction(id = 1, title = "First")
            val transaction2 = TestFixtures.createExpenseTransaction(id = 2, title = "Second")
            repository.addTransactionSync(transaction1)
            repository.addTransactionSync(transaction2)

            // When
            val updated = transaction1.copy(title = "Updated First")
            useCase(updated)

            // Then
            assertEquals("Updated First", repository.findById(1)?.title)
            assertEquals("Second", repository.findById(2)?.title)
        }
}
