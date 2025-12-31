package com.hesapgunlugu.app.core.backup

import android.content.Context
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.Gson
import com.hesapgunlugu.app.core.backup.export.BackupExporter
import com.hesapgunlugu.app.core.backup.import.BackupImporter
import com.hesapgunlugu.app.core.backup.serialization.BackupData
import com.hesapgunlugu.app.core.backup.serialization.BackupSerializer
import com.hesapgunlugu.app.core.common.time.TimeProvider
import com.hesapgunlugu.app.core.domain.model.CategoryTotal
import com.hesapgunlugu.app.core.domain.model.ScheduledPayment
import com.hesapgunlugu.app.core.domain.model.Transaction
import com.hesapgunlugu.app.core.domain.model.TransactionType
import com.hesapgunlugu.app.core.domain.repository.ScheduledPaymentRepository
import com.hesapgunlugu.app.core.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.Date

@RunWith(AndroidJUnit4::class)
class BackupExportImportInstrumentedTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val timeProvider = FixedTimeProvider(1_700_000_000_000)
    private val serializer = BackupSerializer(timeProvider)
    private val transactionRepository = FakeTransactionRepository()
    private val scheduledPaymentRepository = FakeScheduledPaymentRepository()
    private val exporter =
        BackupExporter(
            context = context,
            transactionRepository = transactionRepository,
            scheduledPaymentRepository = scheduledPaymentRepository,
            serializer = serializer,
        )
    private val importer =
        BackupImporter(
            context = context,
            transactionRepository = transactionRepository,
            scheduledPaymentRepository = scheduledPaymentRepository,
            serializer = serializer,
        )

    @Before
    fun setup() =
        runBlocking {
            transactionRepository.clearAllTransactions()
            scheduledPaymentRepository.clearAll()
        }

    @Test
    fun exportPlain_writesBackupData() =
        runBlocking {
            transactionRepository.addTransaction(
                Transaction(
                    id = 1,
                    title = "Coffee",
                    amount = 25.0,
                    type = TransactionType.EXPENSE,
                    category = "Food",
                    emoji = "C",
                    date = Date(timeProvider.nowMillis()),
                ),
            )
            scheduledPaymentRepository.addScheduledPayment(
                ScheduledPayment(
                    id = 1,
                    title = "Rent",
                    amount = 5000.0,
                    isIncome = false,
                    isRecurring = true,
                    frequency = "MONTHLY",
                    dueDate = Date(timeProvider.nowMillis()),
                    emoji = "R",
                    isPaid = false,
                    category = "Bills",
                    createdAt = Date(timeProvider.nowMillis()),
                ),
            )

            val file = File(context.cacheDir, "backup_test.json")
            val result = exporter.exportPlain(Uri.fromFile(file))
            assertTrue(result is BackupResult.Success)

            val json = file.readText()
            val data = Gson().fromJson(json, BackupData::class.java)
            assertEquals(1, data.transactions.size)
            assertEquals(1, data.scheduledPayments.size)
            assertEquals("Coffee", data.transactions.first().title)
        }

    @Test
    fun importPlain_replaceExisting_clearsAndImports() =
        runBlocking {
            transactionRepository.addTransaction(
                Transaction(
                    id = 1,
                    title = "Existing",
                    amount = 10.0,
                    type = TransactionType.EXPENSE,
                    category = "Misc",
                    emoji = "E",
                    date = Date(timeProvider.nowMillis()),
                ),
            )

            val backupData =
                serializer.createBackupData(
                    transactions =
                        listOf(
                            Transaction(
                                id = 2,
                                title = "Imported",
                                amount = 99.0,
                                type = TransactionType.INCOME,
                                category = "Salary",
                                emoji = "I",
                                date = Date(timeProvider.nowMillis()),
                            ),
                        ),
                    scheduledPayments = emptyList(),
                    encrypted = false,
                )

            val file = File(context.cacheDir, "backup_import_test.json")
            file.writeText(Gson().toJson(backupData))

            val result = importer.importPlain(Uri.fromFile(file), replaceExisting = true)
            assertTrue(result is BackupResult.Success)

            val transactions = transactionRepository.getAllTransactions().first()
            assertEquals(1, transactions.size)
            assertEquals("Imported", transactions.first().title)
        }

    private class FixedTimeProvider(private val fixedMillis: Long) : TimeProvider {
        override fun nowMillis(): Long = fixedMillis
    }

    private class FakeTransactionRepository : TransactionRepository {
        private val state = MutableStateFlow<List<Transaction>>(emptyList())

        override suspend fun addTransaction(transaction: Transaction): Result<Unit> {
            state.value = state.value + transaction
            return Result.success(Unit)
        }

        override suspend fun deleteTransaction(transaction: Transaction): Result<Unit> {
            state.value = state.value.filterNot { it.id == transaction.id }
            return Result.success(Unit)
        }

        override suspend fun updateTransaction(transaction: Transaction): Result<Unit> {
            state.value = state.value.map { if (it.id == transaction.id) transaction else it }
            return Result.success(Unit)
        }

        override fun getAllTransactions(): Flow<List<Transaction>> = state

        override fun getBalance(): Flow<Double> =
            state.map { list ->
                val income = list.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
                val expense = list.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
                income - expense
            }

        override fun getTotalIncome(): Flow<Double> =
            state.map { list ->
                list.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
            }

        override fun getTotalExpense(): Flow<Double> =
            state.map { list ->
                list.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
            }

        override fun getCategoryTotals(
            startDate: Date,
            endDate: Date,
        ): Flow<List<CategoryTotal>> {
            return state.map { emptyList() }
        }

        override fun getTransactionsByDateRange(
            startDate: Date,
            endDate: Date,
        ): Flow<List<Transaction>> {
            return state.map { list ->
                list.filter { it.date >= startDate && it.date <= endDate }
            }
        }

        override suspend fun findByScheduledPaymentAndDate(
            scheduledPaymentId: Long,
            date: Date,
        ): Transaction? = null

        override fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>> {
            return state.map { list -> list.filter { it.type == type } }
        }

        override fun getRecentTransactions(): Flow<List<Transaction>> {
            return state.map { list -> list.take(5) }
        }

        override suspend fun clearAllTransactions(): Result<Unit> {
            state.value = emptyList()
            return Result.success(Unit)
        }
    }

    private class FakeScheduledPaymentRepository : ScheduledPaymentRepository {
        private val state = MutableStateFlow<List<ScheduledPayment>>(emptyList())

        override fun getAllScheduledPayments(): Flow<List<ScheduledPayment>> = state

        override fun getRecurringIncomes(): Flow<List<ScheduledPayment>> {
            return state.map { list -> list.filter { it.isRecurring && it.isIncome } }
        }

        override fun getRecurringExpenses(): Flow<List<ScheduledPayment>> {
            return state.map { list -> list.filter { it.isRecurring && !it.isIncome } }
        }

        override fun getUpcomingPayments(
            startDate: Date,
            endDate: Date,
        ): Flow<List<ScheduledPayment>> {
            return state.map { list ->
                list.filter { it.dueDate >= startDate && it.dueDate <= endDate }
            }
        }

        override suspend fun addScheduledPayment(payment: ScheduledPayment): Long {
            state.value = state.value + payment
            return payment.id
        }

        override suspend fun updateScheduledPayment(payment: ScheduledPayment) {
            state.value = state.value.map { if (it.id == payment.id) payment else it }
        }

        override suspend fun deleteScheduledPayment(payment: ScheduledPayment) {
            state.value = state.value.filterNot { it.id == payment.id }
        }

        override suspend fun deleteScheduledPaymentById(id: Long) {
            state.value = state.value.filterNot { it.id == id }
        }

        override suspend fun markAsPaid(
            id: Long,
            isPaid: Boolean,
        ) {
            state.value =
                state.value.map {
                    if (it.id == id) it.copy(isPaid = isPaid) else it
                }
        }

        override suspend fun getScheduledPaymentById(id: Long): ScheduledPayment? {
            return state.value.firstOrNull { it.id == id }
        }

        suspend fun clearAll() {
            state.value = emptyList()
        }
    }
}
