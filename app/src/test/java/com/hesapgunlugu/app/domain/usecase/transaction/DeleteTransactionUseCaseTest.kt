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
 * DeleteTransactionUseCase Tests
 */
@OptIn(ExperimentalCoroutinesApi::class)
class DeleteTransactionUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var useCase: DeleteTransactionUseCase
    private lateinit var repository: FakeTransactionRepository

    @Before
    fun setUp() {
        repository = FakeTransactionRepository()
        useCase = DeleteTransactionUseCase(repository)
    }

    @Test
    fun `invoke should delete transaction from repository`() =
        runTest {
            // Given
            val transaction = TestFixtures.createExpenseTransaction(id = 1)
            repository.addTransactionSync(transaction)
            assertEquals(1, repository.getTransactionCount())

            // When
            val result = useCase(transaction)

            // Then
            assertTrue(result.isSuccess)
            assertEquals(0, repository.getTransactionCount())
        }

    @Test
    fun `invoke when repository fails should return failure`() =
        runTest {
            // Given
            repository.shouldReturnError = true
            val transaction = TestFixtures.createExpenseTransaction()

            // When
            val result = useCase(transaction)

            // Then
            assertTrue(result.isFailure)
        }

    @Test
    fun `invoke should only delete specified transaction`() =
        runTest {
            // Given
            val transaction1 = TestFixtures.createExpenseTransaction(id = 1, title = "First")
            val transaction2 = TestFixtures.createExpenseTransaction(id = 2, title = "Second")
            repository.addTransactionSync(transaction1)
            repository.addTransactionSync(transaction2)

            // When
            useCase(transaction1)

            // Then
            assertEquals(1, repository.getTransactionCount())
            assertNotNull(repository.findById(2))
            assertNull(repository.findById(1))
        }
}
