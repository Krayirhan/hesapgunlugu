package com.hesapgunlugu.app.core.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMigrations {
    // Migration from version 1 to 2 - Adding scheduled_payments table
    val MIGRATION_1_2 =
        object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS scheduled_payments (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        title TEXT NOT NULL,
                        amount REAL NOT NULL,
                        isIncome INTEGER NOT NULL,
                        isRecurring INTEGER NOT NULL,
                        frequency TEXT NOT NULL,
                        dueDate INTEGER NOT NULL,
                        emoji TEXT NOT NULL,
                        isPaid INTEGER NOT NULL DEFAULT 0,
                        category TEXT NOT NULL DEFAULT '',
                        createdAt INTEGER NOT NULL
                    )
                    """.trimIndent(),
                )
            }
        }

    // Migration from version 2 to 3 - Adding emoji column to transactions
    val MIGRATION_2_3 =
        object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE transactions ADD COLUMN emoji TEXT NOT NULL DEFAULT ''")
            }
        }

    // Migration from version 3 to 4 - Adding performance indices
    val MIGRATION_3_4 =
        object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add indices to transactions table for better query performance
                database.execSQL("CREATE INDEX IF NOT EXISTS index_transactions_date ON transactions(date)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_transactions_type ON transactions(type)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_transactions_category ON transactions(category)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_transactions_date_type ON transactions(date, type)")

                // Add indices to scheduled_payments table
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_scheduled_payments_dueDate ON scheduled_payments(dueDate)",
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_scheduled_payments_isRecurring ON scheduled_payments(isRecurring)",
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_scheduled_payments_isPaid ON scheduled_payments(isPaid)",
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_scheduled_payments_dueDate_isPaid ON scheduled_payments(dueDate, isPaid)",
                )
            }
        }

    // Migration from version 4 to 5 - Adding recurring_transactions table
    val MIGRATION_4_5 =
        object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS recurring_transactions (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        title TEXT NOT NULL,
                        amount REAL NOT NULL,
                        type TEXT NOT NULL,
                        category TEXT NOT NULL,
                        emoji TEXT NOT NULL,
                        frequency TEXT NOT NULL,
                        startDate INTEGER NOT NULL,
                        endDate INTEGER,
                        lastExecutionDate INTEGER,
                        nextExecutionDate INTEGER NOT NULL,
                        isActive INTEGER NOT NULL DEFAULT 1,
                        createdAt INTEGER NOT NULL
                    )
                    """.trimIndent(),
                )

                // Add indices for recurring_transactions
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_recurring_transactions_nextExecutionDate ON recurring_transactions(nextExecutionDate)",
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_recurring_transactions_isActive ON recurring_transactions(isActive)",
                )
            }
        }

    // Migration from version 5 to 6 - Adding notifications table
    val MIGRATION_5_6 =
        object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS notifications (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        title TEXT NOT NULL,
                        message TEXT NOT NULL,
                        type TEXT NOT NULL,
                        isRead INTEGER NOT NULL DEFAULT 0,
                        createdAt INTEGER NOT NULL,
                        relatedTransactionId INTEGER,
                        relatedScheduledPaymentId INTEGER
                    )
                    """.trimIndent(),
                )

                // Add indices for notifications
                database.execSQL("CREATE INDEX IF NOT EXISTS index_notifications_createdAt ON notifications(createdAt)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_notifications_isRead ON notifications(isRead)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_notifications_type ON notifications(type)")
            }
        }

    // Migration from version 6 to 7 - Adding recurring_rules table
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

                // Add indices for recurring_rules
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_recurring_rules_scheduledPaymentId ON recurring_rules(scheduledPaymentId)",
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_recurring_rules_isActive ON recurring_rules(isActive)",
                )
            }
        }

    // Migration from version 7 to 8 - Adding scheduledPaymentId to transactions
    // CRITICAL FIX: Enables idempotency for recurring payment worker
    val MIGRATION_7_8 =
        object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add scheduledPaymentId column (nullable for existing manual transactions)
                database.execSQL("ALTER TABLE transactions ADD COLUMN scheduledPaymentId INTEGER DEFAULT NULL")

                // Create unique index to prevent duplicate recurring transactions
                // This is the core idempotency mechanism
                database.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS index_transactions_scheduledPaymentId_date ON transactions(scheduledPaymentId, date) WHERE scheduledPaymentId IS NOT NULL",
                )
            }
        }
}
