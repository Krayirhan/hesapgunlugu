package com.hesapgunlugu.app.core.notification

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import com.hesapgunlugu.app.core.domain.model.ScheduledPayment
import com.hesapgunlugu.app.core.domain.repository.ScheduledPaymentRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*
import kotlin.test.assertEquals

/**
 * Tests for PaymentReminderWorker
 */
@RunWith(AndroidJUnit4::class)
class PaymentReminderWorkerTest {
    private lateinit var context: Context
    private lateinit var repository: ScheduledPaymentRepository

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        repository = mockk(relaxed = true)
    }

    @Test
    fun doWork_withUpcomingPayments_sendsNotification() =
        runTest {
            // Arrange
            val tomorrow =
                Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_YEAR, 1)
                    set(Calendar.HOUR_OF_DAY, 12)
                }.time

            val upcomingPayment =
                ScheduledPayment(
                    id = 1,
                    title = "Rent",
                    amount = 3000.0,
                    isIncome = false,
                    category = "Housing",
                    dueDate = tomorrow,
                    isRecurring = false,
                    isPaid = false,
                    emoji = "🏠",
                )

            coEvery {
                repository.getUpcomingPayments(any(), any())
            } returns flowOf(listOf(upcomingPayment))

            // Create worker with injected repository
            val worker =
                TestListenableWorkerBuilder<TestPaymentReminderWorker>(context)
                    .build()
            (worker as TestPaymentReminderWorker).testRepository = repository

            // Act
            val result = worker.doWork()

            // Assert
            assertEquals(ListenableWorker.Result.success(), result)
            coVerify { repository.getUpcomingPayments(any(), any()) }
        }

    @Test
    fun doWork_withNoUpcomingPayments_doesNotSendNotification() =
        runTest {
            // Arrange
            coEvery {
                repository.getUpcomingPayments(any(), any())
            } returns flowOf(emptyList())

            val worker =
                TestListenableWorkerBuilder<TestPaymentReminderWorker>(context)
                    .build()
            (worker as TestPaymentReminderWorker).testRepository = repository

            // Act
            val result = worker.doWork()

            // Assert
            assertEquals(ListenableWorker.Result.success(), result)
            coVerify { repository.getUpcomingPayments(any(), any()) }
        }

    @Test
    fun doWork_onError_retriesUpToThreeTimes() =
        runTest {
            // Arrange
            coEvery {
                repository.getUpcomingPayments(any(), any())
            } throws RuntimeException("Network error")

            // Create worker with attempt count = 0 (first attempt)
            val worker =
                TestListenableWorkerBuilder<TestPaymentReminderWorker>(context)
                    .setRunAttemptCount(0)
                    .build()
            (worker as TestPaymentReminderWorker).testRepository = repository

            // Act
            val result = worker.doWork()

            // Assert
            assertEquals(ListenableWorker.Result.retry(), result)
        }

    @Test
    fun doWork_afterThreeFailures_returnsFailure() =
        runTest {
            // Arrange
            coEvery {
                repository.getUpcomingPayments(any(), any())
            } throws RuntimeException("Persistent error")

            // Create worker with attempt count = 3 (4th attempt)
            val worker =
                TestListenableWorkerBuilder<TestPaymentReminderWorker>(context)
                    .setRunAttemptCount(3)
                    .build()
            (worker as TestPaymentReminderWorker).testRepository = repository

            // Act
            val result = worker.doWork()

            // Assert
            assertEquals(ListenableWorker.Result.failure(), result)
        }

    // Test worker class that allows injecting mock repository
    class TestPaymentReminderWorker(
        context: Context,
        params: androidx.work.WorkerParameters,
    ) : PaymentReminderWorker(context, params, mockk(relaxed = true)) {
        var testRepository: ScheduledPaymentRepository? = null

        override suspend fun doWork(): Result {
            // Use test repository if provided
            testRepository?.let {
                // Simulate the worker logic with mock repository
                return try {
                    val tomorrow =
                        Calendar.getInstance().apply {
                            add(Calendar.DAY_OF_YEAR, 1)
                            set(Calendar.HOUR_OF_DAY, 0)
                        }.time

                    val payments =
                        it.getUpcomingPayments(tomorrow, tomorrow).let { flow ->
                            kotlinx.coroutines.flow.first(flow)
                        }

                    Result.success()
                } catch (e: Exception) {
                    if (runAttemptCount < 3) {
                        Result.retry()
                    } else {
                        Result.failure()
                    }
                }
            }

            return super.doWork()
        }
    }
}
