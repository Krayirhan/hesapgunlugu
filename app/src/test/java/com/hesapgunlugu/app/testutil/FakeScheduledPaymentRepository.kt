package com.hesapgunlugu.app.testutil

import com.hesapgunlugu.app.core.domain.model.ScheduledPayment
import com.hesapgunlugu.app.core.domain.repository.ScheduledPaymentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.util.Date

class FakeScheduledPaymentRepository : ScheduledPaymentRepository {
    private val payments = MutableStateFlow<List<ScheduledPayment>>(emptyList())

    var shouldReturnError = false
    var errorToReturn: Exception = Exception("Test error")

    override fun getAllScheduledPayments(): Flow<List<ScheduledPayment>> = payments

    override fun getRecurringIncomes(): Flow<List<ScheduledPayment>> =
        payments.map { list ->
            list.filter { it.isRecurring && it.isIncome }
        }

    override fun getRecurringExpenses(): Flow<List<ScheduledPayment>> =
        payments.map { list ->
            list.filter { it.isRecurring && !it.isIncome }
        }

    override fun getUpcomingPayments(
        startDate: Date,
        endDate: Date,
    ): Flow<List<ScheduledPayment>> =
        payments.map { list ->
            list.filter { it.dueDate >= startDate && it.dueDate <= endDate }
                .sortedBy { it.dueDate }
        }

    override suspend fun addScheduledPayment(payment: ScheduledPayment): Long {
        if (shouldReturnError) {
            throw errorToReturn
        }
        val currentList = payments.value.toMutableList()
        val newId = (currentList.maxOfOrNull { it.id } ?: 0) + 1
        currentList.add(payment.copy(id = newId))
        payments.value = currentList
        return newId
    }

    override suspend fun updateScheduledPayment(payment: ScheduledPayment) {
        if (shouldReturnError) {
            throw errorToReturn
        }
        payments.value =
            payments.value.map {
                if (it.id == payment.id) payment else it
            }
    }

    override suspend fun deleteScheduledPayment(payment: ScheduledPayment) {
        if (shouldReturnError) {
            throw errorToReturn
        }
        payments.value = payments.value.filter { it.id != payment.id }
    }

    override suspend fun deleteScheduledPaymentById(id: Long) {
        if (shouldReturnError) {
            throw errorToReturn
        }
        payments.value = payments.value.filter { it.id != id }
    }

    override suspend fun markAsPaid(
        id: Long,
        isPaid: Boolean,
    ) {
        if (shouldReturnError) {
            throw errorToReturn
        }
        payments.value =
            payments.value.map {
                if (it.id == id) it.copy(isPaid = isPaid) else it
            }
    }

    override suspend fun getScheduledPaymentById(id: Long): ScheduledPayment? {
        return payments.value.find { it.id == id }
    }

    fun setPayments(list: List<ScheduledPayment>) {
        payments.value = list
    }

    fun addPaymentSync(payment: ScheduledPayment) {
        val currentList = payments.value.toMutableList()
        currentList.add(payment)
        payments.value = currentList
    }

    fun clear() {
        payments.value = emptyList()
        shouldReturnError = false
    }

    fun getPaymentCount(): Int = payments.value.size
}
