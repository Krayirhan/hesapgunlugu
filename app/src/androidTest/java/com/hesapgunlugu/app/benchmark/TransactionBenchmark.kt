package com.hesapgunlugu.app.benchmark

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hesapgunlugu.app.core.data.local.TransactionEntity
import com.hesapgunlugu.app.core.data.mapper.TransactionMapper
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Performance Benchmark Tests
 *
 * Measures performance of critical operations.
 * Run with: ./gradlew :app:connectedBenchmarkAndroidTest
 */
@RunWith(AndroidJUnit4::class)
class TransactionBenchmark {
    @get:Rule
    val benchmarkRule = BenchmarkRule()

    @Test
    fun benchmark_mapSingleTransaction() {
        val entity =
            TransactionEntity(
                id = 1,
                title = "Test Transaction",
                amount = 100.0,
                type = "EXPENSE",
                category = "Market",
                emoji = "🛒",
                date = System.currentTimeMillis(),
            )

        benchmarkRule.measureRepeated {
            TransactionMapper.toDomain(entity)
        }
    }

    @Test
    fun benchmark_mapTransactionList() {
        val entities =
            (1..100).map { i ->
                TransactionEntity(
                    id = i,
                    title = "Transaction $i",
                    amount = i * 10.0,
                    type = if (i % 2 == 0) "INCOME" else "EXPENSE",
                    category = "Category ${i % 5}",
                    emoji = "🛒",
                    date = System.currentTimeMillis(),
                )
            }

        benchmarkRule.measureRepeated {
            TransactionMapper.toDomainList(entities)
        }
    }

    @Test
    fun benchmark_mapLargeTransactionList() {
        val entities =
            (1..1000).map { i ->
                TransactionEntity(
                    id = i,
                    title = "Transaction $i",
                    amount = i * 10.0,
                    type = if (i % 2 == 0) "INCOME" else "EXPENSE",
                    category = "Category ${i % 10}",
                    emoji = "🛒",
                    date = System.currentTimeMillis(),
                )
            }

        benchmarkRule.measureRepeated {
            TransactionMapper.toDomainList(entities)
        }
    }
}
