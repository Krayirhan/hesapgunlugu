package com.hesapgunlugu.app.core.data.di

import com.hesapgunlugu.app.core.data.repository.BackupStorageRepositoryImpl
import com.hesapgunlugu.app.core.data.repository.DatabaseWiperRepositoryImpl
import com.hesapgunlugu.app.core.data.repository.ScheduledPaymentRepositoryImpl
import com.hesapgunlugu.app.core.data.repository.TransactionRepositoryImpl
import com.hesapgunlugu.app.core.domain.repository.BackupStorageRepository
import com.hesapgunlugu.app.core.domain.repository.DatabaseWiperRepository
import com.hesapgunlugu.app.core.domain.repository.ScheduledPaymentRepository
import com.hesapgunlugu.app.core.domain.repository.TransactionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindTransactionRepository(impl: TransactionRepositoryImpl): TransactionRepository

    @Binds
    @Singleton
    abstract fun bindScheduledPaymentRepository(impl: ScheduledPaymentRepositoryImpl): ScheduledPaymentRepository

    @Binds
    @Singleton
    abstract fun bindBackupStorageRepository(impl: BackupStorageRepositoryImpl): BackupStorageRepository

    @Binds
    @Singleton
    abstract fun bindDatabaseWiperRepository(impl: DatabaseWiperRepositoryImpl): DatabaseWiperRepository
}
