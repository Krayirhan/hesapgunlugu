package com.hesapgunlugu.app.core.domain.repository

/**
 * Abstraction for clearing and closing the local database without exposing data-layer types.
 */
interface DatabaseWiperRepository {
    suspend fun clearAllTables()

    fun closeDatabase()
}
