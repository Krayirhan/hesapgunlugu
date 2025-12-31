package com.hesapgunlugu.app.core.backup

data class BackupPayload(
    val json: String,
    val transactionCount: Int,
    val scheduledPaymentCount: Int,
)
