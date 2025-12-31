package com.hesapgunlugu.app.core.domain.usecase.transaction

import com.hesapgunlugu.app.core.domain.model.Transaction
import com.hesapgunlugu.app.core.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Tüm işlemleri getiren Use Case
 */
class GetTransactionsUseCase
    @Inject
    constructor(
        private val repository: TransactionRepository,
    ) {
        operator fun invoke(): Flow<List<Transaction>> {
            return repository.getAllTransactions()
        }
    }
