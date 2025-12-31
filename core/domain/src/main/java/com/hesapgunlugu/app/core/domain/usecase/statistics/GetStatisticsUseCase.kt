package com.hesapgunlugu.app.core.domain.usecase.statistics

import com.hesapgunlugu.app.core.domain.model.Transaction
import com.hesapgunlugu.app.core.domain.model.TransactionType
import com.hesapgunlugu.app.core.domain.repository.TransactionRepository
import com.hesapgunlugu.app.core.domain.schedule.PlannedOccurrence
import com.hesapgunlugu.app.core.domain.usecase.scheduled.GetPlannedOccurrencesUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

data class StatisticsData(
    val totalIncome: Double,
    val totalExpense: Double,
    val plannedIncome: Double,
    val plannedExpense: Double,
    val weeklyIncome: List<Float>,
    val weeklyExpense: List<Float>,
    val plannedWeeklyIncome: List<Float>,
    val plannedWeeklyExpense: List<Float>,
    val categoryExpenses: Map<String, Double>,
    val currentMonthExpense: Double,
    val previousMonthExpense: Double,
)

class GetStatisticsUseCase
    @Inject
    constructor(
        private val transactionRepository: TransactionRepository,
        private val getPlannedOccurrencesUseCase: GetPlannedOccurrencesUseCase,
    ) {
        operator fun invoke(period: String = "Weekly"): Flow<StatisticsData> {
            val now = Calendar.getInstance()
            val periodStart = calculatePeriodStart(period)
            val periodEnd = now.time

            return combine(
                transactionRepository.getAllTransactions(),
                getPlannedOccurrencesUseCase(periodStart, periodEnd),
            ) { transactions, planned ->
                calculateStatistics(transactions, planned, periodStart, periodEnd)
            }
        }

        private fun calculateStatistics(
            transactions: List<Transaction>,
            plannedOccurrences: List<PlannedOccurrence>,
            periodStart: Date,
            periodEnd: Date,
        ): StatisticsData {
            val calendar = Calendar.getInstance()

            val periodTransactions =
                transactions.filter { tx ->
                    tx.date.time >= periodStart.time && tx.date.time <= periodEnd.time
                }

            val totalIncome =
                periodTransactions
                    .filter { it.type == TransactionType.INCOME }
                    .sumOf { it.amount }
            val totalExpense =
                periodTransactions
                    .filter { it.type == TransactionType.EXPENSE }
                    .sumOf { it.amount }

            val plannedIncome =
                plannedOccurrences
                    .filter { it.payment.isIncome }
                    .sumOf { it.payment.amount }
            val plannedExpense =
                plannedOccurrences
                    .filter { !it.payment.isIncome }
                    .sumOf { it.payment.amount }

            val currentMonth = calendar.get(Calendar.MONTH)
            val currentYear = calendar.get(Calendar.YEAR)

            val thisMonthTransactions =
                transactions.filter { tx ->
                    val txCal = Calendar.getInstance().apply { time = tx.date }
                    txCal.get(Calendar.MONTH) == currentMonth && txCal.get(Calendar.YEAR) == currentYear
                }

            calendar.add(Calendar.MONTH, -1)
            val lastMonth = calendar.get(Calendar.MONTH)
            val lastMonthYear = calendar.get(Calendar.YEAR)

            val lastMonthTransactions =
                transactions.filter { tx ->
                    val txCal = Calendar.getInstance().apply { time = tx.date }
                    txCal.get(Calendar.MONTH) == lastMonth && txCal.get(Calendar.YEAR) == lastMonthYear
                }

            val weekRange = weekRange()
            val weekStart = weekRange.first
            val weekEnd = weekRange.second

            val thisWeekTransactions =
                transactions.filter { tx ->
                    tx.date >= weekStart && tx.date <= weekEnd
                }
            val thisWeekPlanned =
                plannedOccurrences.filter { occurrence ->
                    occurrence.date >= weekStart && occurrence.date <= weekEnd
                }

            val weeklyIncome = MutableList(7) { 0f }
            val weeklyExpense = MutableList(7) { 0f }
            val plannedWeeklyIncome = MutableList(7) { 0f }
            val plannedWeeklyExpense = MutableList(7) { 0f }

            thisWeekTransactions.forEach { tx ->
                val dayOfWeek = toWeekIndex(tx.date)
                if (tx.type == TransactionType.INCOME) {
                    weeklyIncome[dayOfWeek] += tx.amount.toFloat()
                } else {
                    weeklyExpense[dayOfWeek] += tx.amount.toFloat()
                }
            }

            thisWeekPlanned.forEach { occurrence ->
                val dayOfWeek = toWeekIndex(occurrence.date)
                if (occurrence.payment.isIncome) {
                    plannedWeeklyIncome[dayOfWeek] += occurrence.payment.amount.toFloat()
                } else {
                    plannedWeeklyExpense[dayOfWeek] += occurrence.payment.amount.toFloat()
                }
            }

            val categoryExpenses =
                periodTransactions
                    .filter { it.type == TransactionType.EXPENSE }
                    .groupBy { it.category }
                    .mapValues { it.value.sumOf { tx -> tx.amount } }

            val lastMonthExpense =
                lastMonthTransactions
                    .filter { it.type == TransactionType.EXPENSE }
                    .sumOf { it.amount }

            val thisMonthExpense =
                thisMonthTransactions
                    .filter { it.type == TransactionType.EXPENSE }
                    .sumOf { it.amount }

            return StatisticsData(
                totalIncome = totalIncome,
                totalExpense = totalExpense,
                plannedIncome = plannedIncome,
                plannedExpense = plannedExpense,
                weeklyIncome = weeklyIncome,
                weeklyExpense = weeklyExpense,
                plannedWeeklyIncome = plannedWeeklyIncome,
                plannedWeeklyExpense = plannedWeeklyExpense,
                categoryExpenses = categoryExpenses,
                currentMonthExpense = thisMonthExpense,
                previousMonthExpense = lastMonthExpense,
            )
        }

        private fun calculatePeriodStart(period: String): Date {
            return when (period) {
                "Weekly" ->
                    Calendar.getInstance().apply {
                        firstDayOfWeek = Calendar.MONDAY
                        set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }.time
                "Monthly" ->
                    Calendar.getInstance().apply {
                        set(Calendar.DAY_OF_MONTH, 1)
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }.time
                "Yearly" ->
                    Calendar.getInstance().apply {
                        set(Calendar.DAY_OF_YEAR, 1)
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }.time
                else ->
                    Calendar.getInstance().apply {
                        firstDayOfWeek = Calendar.MONDAY
                        set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }.time
            }
        }

        private fun weekRange(): Pair<Date, Date> {
            val weekCalendar = Calendar.getInstance()
            weekCalendar.firstDayOfWeek = Calendar.MONDAY
            weekCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            weekCalendar.set(Calendar.HOUR_OF_DAY, 0)
            weekCalendar.set(Calendar.MINUTE, 0)
            weekCalendar.set(Calendar.SECOND, 0)
            weekCalendar.set(Calendar.MILLISECOND, 0)
            val weekStart = weekCalendar.time

            weekCalendar.add(Calendar.DAY_OF_WEEK, 6)
            weekCalendar.set(Calendar.HOUR_OF_DAY, 23)
            weekCalendar.set(Calendar.MINUTE, 59)
            weekCalendar.set(Calendar.SECOND, 59)
            weekCalendar.set(Calendar.MILLISECOND, 999)
            val weekEnd = weekCalendar.time
            return weekStart to weekEnd
        }

        private fun toWeekIndex(date: Date): Int {
            val txCal = Calendar.getInstance().apply { time = date }
            return when (txCal.get(Calendar.DAY_OF_WEEK)) {
                Calendar.MONDAY -> 0
                Calendar.TUESDAY -> 1
                Calendar.WEDNESDAY -> 2
                Calendar.THURSDAY -> 3
                Calendar.FRIDAY -> 4
                Calendar.SATURDAY -> 5
                Calendar.SUNDAY -> 6
                else -> 0
            }
        }
    }
