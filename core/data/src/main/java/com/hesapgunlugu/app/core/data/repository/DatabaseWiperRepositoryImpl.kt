package com.hesapgunlugu.app.core.data.repository

import com.hesapgunlugu.app.core.data.local.AppDatabase
import com.hesapgunlugu.app.core.domain.repository.DatabaseWiperRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseWiperRepositoryImpl
    @Inject
    constructor(
        private val database: AppDatabase,
    ) : DatabaseWiperRepository {
        override suspend fun clearAllTables() {
            database.clearAllTables()
        }

        override fun closeDatabase() {
            database.close()
        }
    }
