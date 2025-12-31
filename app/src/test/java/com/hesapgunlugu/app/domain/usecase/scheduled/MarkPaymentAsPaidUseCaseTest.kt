package com.hesapgunlugu.app.domain.usecase.scheduled

import com.hesapgunlugu.app.core.domain.model.TransactionType
import com.hesapgunlugu.app.core.domain.usecase.scheduled.MarkPaymentAsPaidUseCase
import com.hesapgunlugu.app.testutil.FakeRecurringRuleRepository
import com.hesapgunlugu.app.testutil.FakeScheduledPaymentRepository
import com.hesapgunlugu.app.testutil.FakeTransactionRepository
import com.hesapgunlugu.app.testutil.MainDispatcherRule
import com.hesapgunlugu.app.testutil.TestFixtures
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class MarkPaymentAsPaidUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var useCase: MarkPaymentAsPaidUseCase
    private lateinit var scheduledRepository: FakeScheduledPaymentRepository
    private lateinit var transactionRepository: FakeTransactionRepository
    private lateinit var recurringRuleRepository: FakeRecurringRuleRepository

    @Before
    fun setUp() {
        scheduledRepository = FakeScheduledPaymentRepository()
        transactionRepository = FakeTransactionRepository()
        recurringRuleRepository = FakeRecurringRuleRepository()
        useCase =
            MarkPaymentAsPaidUseCase(
                scheduledRepository,
                transactionRepository,
                recurringRuleRepository,
            )
    }

    @Test
    fun `invoke should mark one-time payment as paid and create transaction`() =
        runTest {
            val payment =
                TestFixtures.createScheduledPayment(
                    id = 1,
                    isPaid = false,
                    isRecurring = false,
                    isIncome = false,
                )
            scheduledRepository.addPaymentSync(payment)

            val result = useCase(payment, paidDate = Date(), isPaid = true)

            assertTrue(result.isSuccess)
            val updated = scheduledRepository.getScheduledPaymentById(1)
            assertTrue(updated?.isPaid == true)
            assertEquals(1, transactionRepository.getTransactionCount())
            assertEquals(TransactionType.EXPENSE, transactionRepository.findById(1)?.type)
        }

    @Test
    fun `invoke should create transaction for recurring payment without marking paid`() =
        runTest {
            val payment =
                TestFixtures.createScheduledPayment(
                    id = 1,
                    isPaid = false,
                    isRecurring = true,
                    isIncome = true,
                )
            scheduledRepository.addPaymentSync(payment)

            val paidDate = Date()
            val result = useCase(payment, paidDate = paidDate, isPaid = true)

            assertTrue(result.isSuccess)
            val updated = scheduledRepository.getScheduledPaymentById(1)
            assertTrue(updated?.isPaid == false)
            assertNotNull(transactionRepository.findByScheduledPaymentAndDate(1, paidDate))
        }
}
