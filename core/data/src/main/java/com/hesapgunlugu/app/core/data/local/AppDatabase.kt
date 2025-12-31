package com.hesapgunlugu.app.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.hesapgunlugu.app.core.data.local.dao.RecurringRuleDao
import com.hesapgunlugu.app.core.data.model.RecurringRule

/**
 * Room Database for the application.
 *
 * Contains all entities and DAOs for local data persistence.
 *
 * ## Entities
 * - [TransactionEntity]: Financial transactions (income/expense)
 * - [ScheduledPaymentEntity]: Scheduled/recurring payments
 * - [RecurringTransactionEntity]: Auto-generated recurring transactions
 * - [NotificationEntity]: User notifications
 *
 * ## Migration Strategy
 * - exportSchema = true for version tracking
 * - Manual migrations for complex changes
 * - Fallback to destructive migration only in debug
 *
 * @see TransactionDao
 * @see ScheduledPaymentDao
 * @see RecurringTransactionDao
 * @see NotificationDao
 */
@Database(
    entities = [
        TransactionEntity::class,
        ScheduledPaymentEntity::class,
        RecurringTransactionEntity::class,
        NotificationEntity::class,
        RecurringRule::class,
    ],
    // CRITICAL FIX: Incremented for scheduledPaymentId migration
    version = 8,
    // Re-enabled with proper schema location in build.gradle.kts
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao

    abstract fun scheduledPaymentDao(): ScheduledPaymentDao

    abstract fun recurringTransactionDao(): RecurringTransactionDao

    abstract fun notificationDao(): NotificationDao

    abstract fun recurringRuleDao(): RecurringRuleDao

    companion object {
        /**
         * Migration from version 1 to 2
         * Added category field to transactions
         */
        val MIGRATION_1_2 =
            object : Migration(1, 2) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL("ALTER TABLE transactions ADD COLUMN category TEXT NOT NULL DEFAULT ''")
                }
            }

        /**
         * Migration from version 6 to 7
         * Added recurring_rules table
         */
        val MIGRATION_6_7 =
            object : Migration(6, 7) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS recurring_rules (
                            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            scheduledPaymentId INTEGER NOT NULL,
                            recurrenceType TEXT NOT NULL,
                            interval INTEGER NOT NULL DEFAULT 1,
                            daysOfWeek TEXT,
                            dayOfMonth INTEGER,
                            endDate INTEGER,
                            maxOccurrences INTEGER,
                            currentOccurrences INTEGER NOT NULL DEFAULT 0,
                            lastGenerated INTEGER,
                            isActive INTEGER NOT NULL DEFAULT 1,
                            createdAt INTEGER NOT NULL,
                            updatedAt INTEGER NOT NULL
                        )
                        """.trimIndent(),
                    )
                }
            }

        /**
         * Migration from version 7 to 8
         * CRITICAL FIX: Added scheduledPaymentId to transactions for idempotency
         */
        val MIGRATION_7_8 =
            object : Migration(7, 8) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL("ALTER TABLE transactions ADD COLUMN scheduledPaymentId INTEGER DEFAULT NULL")
                    database.execSQL(
                        "CREATE UNIQUE INDEX IF NOT EXISTS index_transactions_scheduledPaymentId_date ON transactions(scheduledPaymentId, date) WHERE scheduledPaymentId IS NOT NULL",
                    )
                }
            }

        // Add other migrations here if needed
    }
}
