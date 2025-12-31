package com.hesapgunlugu.app.domain.usecase.scheduled

import com.hesapgunlugu.app.testutil.FakeScheduledPaymentRepository
import com.hesapgunlugu.app.testutil.MainDispatcherRule
import com.hesapgunlugu.app.testutil.TestFixtures
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * DeleteScheduledPaymentUseCase Tests
 */
@OptIn(ExperimentalCoroutinesApi::class)
class DeleteScheduledPaymentUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var useCase: DeleteScheduledPaymentUseCase
    private lateinit var repository: FakeScheduledPaymentRepository

    @Before
    fun setUp() {
        repository = FakeScheduledPaymentRepository()
        useCase = DeleteScheduledPaymentUseCase(repository)
    }

    @Test
    fun `invoke should delete payment from repository`() =
        runTest {
            // Given
            val payment = TestFixtures.createScheduledPayment(id = 1)
            repository.addPaymentSync(payment)
            assertEquals(1, repository.getPaymentCount())

            // When
            val result = useCase(1)

            // Then
            assertTrue(result.isSuccess)
            assertEquals(0, repository.getPaymentCount())
        }

    @Test
    fun `invoke when repository fails should return failure`() =
        runTest {
            // Given
            repository.shouldReturnError = true
            val payment = TestFixtures.createScheduledPayment(id = 1)
            repository.addPaymentSync(payment)

            // When
            val result = useCase(1)

            // Then
            assertTrue(result.isFailure)
        }

    @Test
    fun `invoke should only delete specified payment`() =
        runTest {
            // Given
            val payment1 = TestFixtures.createScheduledPayment(id = 1)
            val payment2 = TestFixtures.createScheduledPayment(id = 2)
            repository.addPaymentSync(payment1)
            repository.addPaymentSync(payment2)

            // When
            useCase(1)

            // Then
            assertEquals(1, repository.getPaymentCount())
            assertNull(repository.getById(1))
            assertNotNull(repository.getById(2))
        }

    @Test
    fun `invoke multiple deletes should work correctly`() =
        runTest {
            // Given
            val payments = TestFixtures.createScheduledPaymentList(5)
            payments.forEach { repository.addPaymentSync(it) }
            assertEquals(5, repository.getPaymentCount())

            // When
            useCase(1)
            useCase(2)
            useCase(3)

            // Then
            assertEquals(2, repository.getPaymentCount())
        }
}
