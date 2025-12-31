package com.hesapgunlugu.app.core.domain.usecase.transaction

import com.hesapgunlugu.app.core.domain.model.Transaction
import com.hesapgunlugu.app.core.domain.model.TransactionException
import com.hesapgunlugu.app.core.domain.repository.TransactionRepository
import javax.inject.Inject

/**
 * Yeni işlem ekleyen Use Case
 */
class AddTransactionUseCase
    @Inject
    constructor(
        private val repository: TransactionRepository,
    ) {
        suspend operator fun invoke(transaction: Transaction): Result<Unit> {
            return try {
                if (transaction.title.isBlank()) {
                    return Result.failure(TransactionException.EmptyTitle)
                }
                if (transaction.amount <= 0) {
                    return Result.failure(TransactionException.InvalidAmount)
                }

                repository.addTransaction(transaction)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
