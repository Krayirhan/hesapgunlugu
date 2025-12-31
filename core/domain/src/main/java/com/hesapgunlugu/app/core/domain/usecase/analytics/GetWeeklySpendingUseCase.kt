package com.hesapgunlugu.app.core.domain.usecase.analytics

import com.hesapgunlugu.app.core.domain.model.Transaction
import com.hesapgunlugu.app.core.domain.model.TransactionType
import java.util.Calendar
import javax.inject.Inject

/**
 * Haftalık harcama verilerini hesaplar (son 7 gün)
 */
class GetWeeklySpendingUseCase
    @Inject
    constructor() {
        operator fun invoke(transactions: List<Transaction>): List<Double> {
            val expenseTransactions = transactions.filter { it.type == TransactionType.EXPENSE }

            return (0..6).map { dayOffset ->
                val targetDate =
                    Calendar.getInstance().apply {
                        add(Calendar.DAY_OF_YEAR, -dayOffset)
                    }

                expenseTransactions
                    .filter { isSameDay(it.timestamp, targetDate.timeInMillis) }
                    .sumOf { it.amount }
            }.reversed() // Kronolojik sıraya çevir
        }

        private fun isSameDay(
            date1: Long,
            date2: Long,
        ): Boolean {
            val cal1 = Calendar.getInstance().apply { timeInMillis = date1 }
            val cal2 = Calendar.getInstance().apply { timeInMillis = date2 }

            return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
        }
    }
