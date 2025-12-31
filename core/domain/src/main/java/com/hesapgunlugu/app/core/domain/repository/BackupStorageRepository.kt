package com.hesapgunlugu.app.core.domain.repository

import com.hesapgunlugu.app.core.domain.model.ScheduledPayment
import com.hesapgunlugu.app.core.domain.model.Transaction

/**
 * Transactional storage operations for backup import/restore.
 */
interface BackupStorageRepository {
    /**
     * Replace all existing data in a single transaction.
     */
    suspend fun replaceAll(
        transactions: List<Transaction>,
        scheduledPayments: List<ScheduledPayment>,
    )

    /**
     * Append data in a single transaction.
     */
    suspend fun addAll(
        transactions: List<Transaction>,
        scheduledPayments: List<ScheduledPayment>,
    )
}
