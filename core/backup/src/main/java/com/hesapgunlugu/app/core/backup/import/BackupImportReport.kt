package com.hesapgunlugu.app.core.backup.import

data class BackupImportReport(
    val version: Int,
    val transactionCount: Int,
    val scheduledPaymentCount: Int,
    val invalidTransactions: Int,
    val invalidScheduledPayments: Int,
    val errors: List<String>,
) {
    val isVersionSupported: Boolean = errors.none { it.startsWith("Unsupported backup version:") }
}
