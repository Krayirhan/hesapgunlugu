package com.hesapgunlugu.app.core.domain.usecase.transaction

import com.hesapgunlugu.app.core.domain.model.Transaction
import com.hesapgunlugu.app.core.domain.repository.TransactionRepository
import javax.inject.Inject

/**
 * İşlem güncelleyen Use Case
 */
class UpdateTransactionUseCase
    @Inject
    constructor(
        private val repository: TransactionRepository,
    ) {
        suspend operator fun invoke(transaction: Transaction): Result<Unit> {
            return try {
                require(transaction.id > 0) { "Geçersiz işlem ID" }
                require(transaction.title.isNotBlank()) { "Başlık boş olamaz" }
                require(transaction.amount > 0) { "Tutar 0'dan büyük olmalı" }

                repository.updateTransaction(transaction)
            } catch (e: IllegalArgumentException) {
                Result.failure(e)
            }
        }
    }
