package com.hesapgunlugu.app.di

import android.content.Context
import androidx.room.Room
import com.hesapgunlugu.app.core.data.local.AppDatabase
import com.hesapgunlugu.app.core.data.local.DatabaseEncryption
import com.hesapgunlugu.app.core.data.local.DatabaseMigrations
import com.hesapgunlugu.app.core.data.local.NotificationDao
import com.hesapgunlugu.app.core.data.local.RecurringTransactionDao
import com.hesapgunlugu.app.core.data.local.ScheduledPaymentDao
import com.hesapgunlugu.app.core.data.local.TransactionDao
import com.hesapgunlugu.app.core.data.local.dao.RecurringRuleDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "my_money_db_v2",
        )
            .openHelperFactory(DatabaseEncryption.createSupportFactory(context))
            .addMigrations(
                DatabaseMigrations.MIGRATION_1_2,
                DatabaseMigrations.MIGRATION_2_3,
                DatabaseMigrations.MIGRATION_3_4,
                DatabaseMigrations.MIGRATION_4_5,
                DatabaseMigrations.MIGRATION_5_6,
                DatabaseMigrations.MIGRATION_6_7,
                // CRITICAL FIX: scheduledPaymentId migration
                DatabaseMigrations.MIGRATION_7_8,
            )
            // REMOVED: fallbackToDestructiveMigration() - CRITICAL FIX
            // Production builds must NEVER delete user data on migration failure
            // All schema changes must have explicit, tested migrations
            .build()
    }

    @Provides
    @Singleton
    fun provideTransactionDao(database: AppDatabase): TransactionDao {
        return database.transactionDao()
    }

    @Provides
    @Singleton
    fun provideScheduledPaymentDao(database: AppDatabase): ScheduledPaymentDao {
        return database.scheduledPaymentDao()
    }

    @Provides
    @Singleton
    fun provideRecurringTransactionDao(database: AppDatabase): RecurringTransactionDao {
        return database.recurringTransactionDao()
    }

    @Provides
    @Singleton
    fun provideNotificationDao(database: AppDatabase): NotificationDao {
        return database.notificationDao()
    }

    @Provides
    @Singleton
    fun provideRecurringRuleDao(database: AppDatabase): RecurringRuleDao {
        return database.recurringRuleDao()
    }
}
