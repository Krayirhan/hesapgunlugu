package com.hesapgunlugu.app.data.local

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Database Migration Tests
 *
 * Tests all database migrations to ensure data integrity during upgrades.
 * Senior Level Best Practice: Always test migrations before release.
 */
@RunWith(AndroidJUnit4::class)
class MigrationTest {
    private val TEST_DB = "migration-test"

    @get:Rule
    val helper: MigrationTestHelper =
        MigrationTestHelper(
            InstrumentationRegistry.getInstrumentation(),
            AppDatabase::class.java.canonicalName,
            FrameworkSQLiteOpenHelperFactory(),
        )

    @Test
    @Throws(IOException::class)
    fun migrate1To2() {
        // Create database with version 1
        helper.createDatabase(TEST_DB, 1).apply {
            // Insert test data for version 1
            execSQL(
                """
                INSERT INTO transactions (id, title, amount, type, date) 
                VALUES (1, 'Test Transaction', 100.0, 'EXPENSE', ${System.currentTimeMillis()})
                """.trimIndent(),
            )
            close()
        }

        // Migrate to version 2
        val db =
            helper.runMigrationsAndValidate(
                TEST_DB,
                2,
                true,
                AppDatabase.MIGRATION_1_2,
            )

        // Verify the category column was added
        val cursor = db.query("SELECT category FROM transactions WHERE id = 1")
        assert(cursor.moveToFirst())
        val category = cursor.getString(cursor.getColumnIndex("category"))
        assert(category == "") // Default value
        cursor.close()
    }

    @Test
    @Throws(IOException::class)
    fun migrate2To3() {
        // Create database with version 2
        helper.createDatabase(TEST_DB, 2).apply {
            execSQL(
                """
                INSERT INTO transactions (id, title, amount, type, category, date) 
                VALUES (1, 'Test', 100.0, 'EXPENSE', 'Food', ${System.currentTimeMillis()})
                """.trimIndent(),
            )
            close()
        }

        // Migrate to version 3
        val db =
            helper.runMigrationsAndValidate(
                TEST_DB,
                3,
                true,
                AppDatabase.MIGRATION_2_3,
            )

        // Verify the emoji column was added
        val cursor = db.query("SELECT emoji FROM transactions WHERE id = 1")
        assert(cursor.moveToFirst())
        val emoji = cursor.getString(cursor.getColumnIndex("emoji"))
        assert(emoji == "💰") // Default value
        cursor.close()
    }

    @Test
    @Throws(IOException::class)
    fun migrate3To4() {
        // Create database with version 3
        helper.createDatabase(TEST_DB, 3).apply {
            close()
        }

        // Migrate to version 4
        val db =
            helper.runMigrationsAndValidate(
                TEST_DB,
                4,
                true,
                AppDatabase.MIGRATION_3_4,
            )

        // Verify scheduled_payments table was created
        val cursor = db.query("SELECT name FROM sqlite_master WHERE type='table' AND name='scheduled_payments'")
        assert(cursor.count == 1)
        cursor.close()
    }

    @Test
    @Throws(IOException::class)
    fun migrate4To5() {
        // Create database with version 4
        helper.createDatabase(TEST_DB, 4).apply {
            close()
        }

        // Migrate to version 5
        val db =
            helper.runMigrationsAndValidate(
                TEST_DB,
                5,
                true,
                DatabaseMigrations.MIGRATION_4_5,
            )

        // Verify recurring_transactions table was created
        val cursor = db.query("SELECT name FROM sqlite_master WHERE type='table' AND name='recurring_transactions'")
        assert(cursor.count == 1)
        cursor.close()
    }

    @Test
    @Throws(IOException::class)
    fun migrate5To6() {
        // Create database with version 5
        helper.createDatabase(TEST_DB, 5).apply {
            close()
        }

        // Migrate to version 6
        val db =
            helper.runMigrationsAndValidate(
                TEST_DB,
                6,
                true,
                DatabaseMigrations.MIGRATION_5_6,
            )

        // Verify notifications table was created
        val cursor = db.query("SELECT name FROM sqlite_master WHERE type='table' AND name='notifications'")
        assert(cursor.count == 1)

        // Verify indices were created
        val indexCursor = db.query("SELECT name FROM sqlite_master WHERE type='index' AND tbl_name='notifications'")
        assert(indexCursor.count >= 3) // Should have at least 3 indices
        cursor.close()
        indexCursor.close()
    }

    @Test
    @Throws(IOException::class)
    fun migrate6To7() {
        // Create database with version 6
        helper.createDatabase(TEST_DB, 6).apply {
            // Insert test scheduled payment
            execSQL(
                """
                INSERT INTO scheduled_payments (id, title, amount, isIncome, isRecurring, frequency, dueDate, emoji, isPaid, category, createdAt)
                VALUES (1, 'Test Recurring', 100.0, 0, 1, 'MONTHLY', ${System.currentTimeMillis()}, '💰', 0, 'Bills', ${System.currentTimeMillis()})
                """.trimIndent(),
            )
            close()
        }

        // Migrate to version 7
        val db =
            helper.runMigrationsAndValidate(
                TEST_DB,
                7,
                true,
                DatabaseMigrations.MIGRATION_6_7,
            )

        // Verify recurring_rules table was created
        val cursor = db.query("SELECT name FROM sqlite_master WHERE type='table' AND name='recurring_rules'")
        assert(cursor.count == 1)

        // Verify table schema
        val schemaCursor = db.query("PRAGMA table_info(recurring_rules)")
        val columnNames = mutableListOf<String>()
        while (schemaCursor.moveToNext()) {
            columnNames.add(schemaCursor.getString(schemaCursor.getColumnIndex("name")))
        }
        assert(columnNames.contains("scheduledPaymentId"))
        assert(columnNames.contains("recurrenceType"))
        assert(columnNames.contains("isActive"))
        assert(columnNames.contains("lastGenerated"))

        // Verify indices were created
        val indexCursor = db.query("SELECT name FROM sqlite_master WHERE type='index' AND tbl_name='recurring_rules'")
        assert(indexCursor.count >= 2) // Should have at least 2 indices

        cursor.close()
        schemaCursor.close()
        indexCursor.close()
    }

    @Test
    @Throws(IOException::class)
    fun migrate7To8() {
        // Create database with version 7
        helper.createDatabase(TEST_DB, 7).apply {
            // Insert test transaction without scheduledPaymentId
            execSQL(
                """
                INSERT INTO transactions (id, title, amount, date, type, category, emoji)
                VALUES (1, 'Manual Transaction', 50.0, ${System.currentTimeMillis()}, 'EXPENSE', 'Food', '🍔')
                """.trimIndent(),
            )
            close()
        }

        // Migrate to version 8
        val db =
            helper.runMigrationsAndValidate(
                TEST_DB,
                8,
                true,
                DatabaseMigrations.MIGRATION_7_8,
            )

        // Verify scheduledPaymentId column was added
        val schemaCursor = db.query("PRAGMA table_info(transactions)")
        val columnNames = mutableListOf<String>()
        while (schemaCursor.moveToNext()) {
            columnNames.add(schemaCursor.getString(schemaCursor.getColumnIndex("name")))
        }
        assert(columnNames.contains("scheduledPaymentId")) { "scheduledPaymentId column must exist" }

        // Verify existing transaction has NULL scheduledPaymentId
        val cursor = db.query("SELECT scheduledPaymentId FROM transactions WHERE id = 1")
        assert(cursor.moveToFirst())
        assert(cursor.isNull(cursor.getColumnIndex("scheduledPaymentId"))) {
            "Existing transactions should have NULL scheduledPaymentId"
        }

        // Verify unique index was created
        val indexCursor =
            db.query(
                "SELECT name FROM sqlite_master WHERE type='index' AND tbl_name='transactions' AND name='index_transactions_scheduledPaymentId_date'",
            )
        assert(indexCursor.count == 1) { "Unique index on (scheduledPaymentId, date) must exist" }

        // Test idempotency: Try to insert duplicate (scheduledPaymentId + date)
        try {
            db.execSQL(
                """
                INSERT INTO transactions (title, amount, date, type, category, emoji, scheduledPaymentId)
                VALUES ('Recurring 1', 100.0, 1000000, 'EXPENSE', 'Bills', '💰', 1)
                """.trimIndent(),
            )
            db.execSQL(
                """
                INSERT INTO transactions (title, amount, date, type, category, emoji, scheduledPaymentId)
                VALUES ('Recurring 2', 100.0, 1000000, 'EXPENSE', 'Bills', '💰', 1)
                """.trimIndent(),
            )
            assert(false) { "Duplicate insert should have failed due to unique constraint" }
        } catch (e: Exception) {
            // Expected: unique constraint violation
            assert(
                e.message?.contains("UNIQUE", ignoreCase = true) == true,
            ) { "Should fail with UNIQUE constraint error" }
        }

        schemaCursor.close()
        cursor.close()
        indexCursor.close()
    }

    @Test
    @Throws(IOException::class)
    fun migrateAll() {
        // Create database with version 1
        helper.createDatabase(TEST_DB, 1).apply {
            close()
        }

        // Migrate through all versions
        val db =
            Room.databaseBuilder(
                InstrumentationRegistry.getInstrumentation().targetContext,
                AppDatabase::class.java,
                TEST_DB,
            ).addMigrations(*AppDatabase.ALL_MIGRATIONS).build()

        // Verify database is usable
        db.openHelper.writableDatabase
        db.close()
    }
}
