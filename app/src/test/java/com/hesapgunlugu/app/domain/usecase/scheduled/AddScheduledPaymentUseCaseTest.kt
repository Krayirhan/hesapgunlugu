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
 * AddScheduledPaymentUseCase Tests
 *
 * Comprehensive tests for scheduled payment creation.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AddScheduledPaymentUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var useCase: AddScheduledPaymentUseCase
    private lateinit var repository: FakeScheduledPaymentRepository

    @Before
    fun setUp() {
        repository = FakeScheduledPaymentRepository()
        useCase = AddScheduledPaymentUseCase(repository)
    }

    // ==================== HAPPY PATH TESTS ====================

    @Test
    fun `invoke with valid payment should return success`() =
        runTest {
            // Given
            val payment = TestFixtures.createScheduledPayment()

            // When
            val result = useCase(payment)

            // Then
            assertTrue(result.isSuccess)
            assertEquals(1, repository.getPaymentCount())
        }

    @Test
    fun `invoke with non-recurring payment should succeed`() =
        runTest {
            // Given
            val payment =
                TestFixtures.createScheduledPayment(
                    isRecurring = false,
                    frequency = "",
                )

            // When
            val result = useCase(payment)

            // Then
            assertTrue(result.isSuccess)
        }

    @Test
    fun `invoke with recurring payment and frequency should succeed`() =
        runTest {
            // Given
            val payment =
                TestFixtures.createScheduledPayment(
                    isRecurring = true,
                    frequency = "Aylık",
                )

            // When
            val result = useCase(payment)

            // Then
            assertTrue(result.isSuccess)
        }

    // ==================== VALIDATION TESTS ====================

    @Test
    fun `invoke with blank title should return failure`() =
        runTest {
            // Given
            val payment = TestFixtures.createScheduledPayment(title = "")

            // When
            val result = useCase(payment)

            // Then
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is IllegalArgumentException)
            assertTrue(result.exceptionOrNull()?.message?.contains("Başlık") == true)
        }

    @Test
    fun `invoke with zero amount should return failure`() =
        runTest {
            // Given
            val payment = TestFixtures.createScheduledPayment(amount = 0.0)

            // When
            val result = useCase(payment)

            // Then
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull()?.message?.contains("Tutar") == true)
        }

    @Test
    fun `invoke with negative amount should return failure`() =
        runTest {
            // Given
            val payment = TestFixtures.createScheduledPayment(amount = -50.0)

            // When
            val result = useCase(payment)

            // Then
            assertTrue(result.isFailure)
        }

    @Test
    fun `invoke with recurring but no frequency should return failure`() =
        runTest {
            // Given
            val payment =
                TestFixtures.createScheduledPayment(
                    isRecurring = true,
                    frequency = "",
                )

            // When
            val result = useCase(payment)

            // Then
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull()?.message?.contains("sıklık") == true)
        }

    // ==================== ERROR HANDLING TESTS ====================

    @Test
    fun `invoke when repository fails should return failure`() =
        runTest {
            // Given
            repository.shouldReturnError = true
            repository.errorToReturn = RuntimeException("Database error")
            val payment = TestFixtures.createScheduledPayment()

            // When
            val result = useCase(payment)

            // Then
            assertTrue(result.isFailure)
        }

    // ==================== EDGE CASES ====================

    @Test
    fun `invoke with income payment should succeed`() =
        runTest {
            // Given
            val payment = TestFixtures.createScheduledPayment(isIncome = true)

            // When
            val result = useCase(payment)

            // Then
            assertTrue(result.isSuccess)
        }

    @Test
    fun `invoke multiple payments should all succeed`() =
        runTest {
            // Given
            val payments = TestFixtures.createScheduledPaymentList(5)

            // When
            payments.forEach { useCase(it) }

            // Then
            assertEquals(5, repository.getPaymentCount())
        }
}
