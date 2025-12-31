package com.hesapgunlugu.app.domain.usecase.scheduled

import app.cash.turbine.test
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
 * GetScheduledPaymentsUseCase Tests
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GetScheduledPaymentsUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var useCase: GetScheduledPaymentsUseCase
    private lateinit var repository: FakeScheduledPaymentRepository

    @Before
    fun setUp() {
        repository = FakeScheduledPaymentRepository()
        useCase = GetScheduledPaymentsUseCase(repository)
    }

    @Test
    fun `invoke should return all scheduled payments`() =
        runTest {
            // Given
            val payments = TestFixtures.createScheduledPaymentList(5)
            repository.setPayments(payments)

            // When & Then
            useCase().test {
                val result = awaitItem()
                assertEquals(5, result.size)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `invoke should return empty list when no payments`() =
        runTest {
            // When & Then
            useCase().test {
                val result = awaitItem()
                assertTrue(result.isEmpty())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `invoke should emit updates when payments change`() =
        runTest {
            // Given
            val initialPayment = TestFixtures.createScheduledPayment(id = 1)
            repository.setPayments(listOf(initialPayment))

            // When & Then
            useCase().test {
                assertEquals(1, awaitItem().size)

                repository.addPaymentSync(TestFixtures.createScheduledPayment(id = 2))
                assertEquals(2, awaitItem().size)

                cancelAndIgnoreRemainingEvents()
            }
        }
}
