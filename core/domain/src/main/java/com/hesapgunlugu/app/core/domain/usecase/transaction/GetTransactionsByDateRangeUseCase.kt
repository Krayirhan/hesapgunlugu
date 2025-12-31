package com.hesapgunlugu.app.core.domain.usecase.transaction

import com.hesapgunlugu.app.core.domain.model.Transaction
import com.hesapgunlugu.app.core.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject

/**
 * Returns transactions within a specific date range.
 */
class GetTransactionsByDateRangeUseCase
    @Inject
    constructor(
        private val repository: TransactionRepository,
    ) {
        operator fun invoke(
            startDate: Date,
            endDate: Date,
        ): Flow<List<Transaction>> {
            return repository.getTransactionsByDateRange(startDate, endDate)
        }
    }
