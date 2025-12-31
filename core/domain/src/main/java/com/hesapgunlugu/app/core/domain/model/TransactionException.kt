package com.hesapgunlugu.app.core.domain.model

sealed class TransactionException : Exception() {
    object EmptyTitle : TransactionException()

    object InvalidAmount : TransactionException()
}
