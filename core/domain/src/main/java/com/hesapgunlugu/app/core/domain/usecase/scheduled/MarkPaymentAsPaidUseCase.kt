package com.hesapgunlugu.app.core.domain.usecase.scheduled

import com.hesapgunlugu.app.core.domain.model.ScheduledPayment
import com.hesapgunlugu.app.core.domain.model.Transaction
import com.hesapgunlugu.app.core.domain.model.TransactionType
import com.hesapgunlugu.app.core.domain.repository.RecurringRuleRepository
import com.hesapgunlugu.app.core.domain.repository.ScheduledPaymentRepository
import com.hesapgunlugu.app.core.domain.repository.TransactionRepository
import java.util.Date
import javax.inject.Inject

/**
 * Marks a scheduled payment as paid and creates a transaction entry when needed.
 */
class MarkPaymentAsPaidUseCase
    @Inject
    constructor(
        private val scheduledPaymentRepository: ScheduledPaymentRepository,
        private val transactionRepository: TransactionRepository,
        private val recurringRuleRepository: RecurringRuleRepository,
    ) {
        suspend operator fun invoke(
            payment: ScheduledPayment,
            paidDate: Date = payment.dueDate,
            isPaid: Boolean = true,
        ): Result<Unit> {
            return try {
                require(payment.id > 0) { "Invalid scheduled payment ID" }

                if (!isPaid) {
                    if (!payment.isRecurring) {
                        scheduledPaymentRepository.markAsPaid(payment.id, false)
                    }
                    return Result.success(Unit)
                }

                if (!payment.isRecurring) {
                    scheduledPaymentRepository.markAsPaid(payment.id, true)
                }

                val existing =
                    transactionRepository.findByScheduledPaymentAndDate(
                        scheduledPaymentId = payment.id,
                        date = paidDate,
                    )

                if (existing == null) {
                    val transaction =
                        Transaction(
                            title = payment.title,
                            amount = payment.amount,
                            type = if (payment.isIncome) TransactionType.INCOME else TransactionType.EXPENSE,
                            category = payment.category,
                            emoji = payment.emoji,
                            scheduledPaymentId = payment.id,
                            date = paidDate,
                        )
                    transactionRepository.addTransaction(transaction).getOrThrow()
                }

                if (payment.isRecurring) {
                    val rules = recurringRuleRepository.getByScheduledPaymentId(payment.id)
                    rules.forEach { rule ->
                        recurringRuleRepository.updateLastGenerated(rule.id, paidDate)
                    }
                }

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
