package com.hesapgunlugu.app.feature.history

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hesapgunlugu.app.core.domain.model.Transaction
import com.hesapgunlugu.app.core.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TransactionDetailState(
    val isLoading: Boolean = true,
    val transaction: Transaction? = null,
    @StringRes val errorRes: Int? = null,
)

sealed class TransactionDetailEvent {
    data object Updated : TransactionDetailEvent()

    data object Deleted : TransactionDetailEvent()

    data class Error(
        @StringRes val messageRes: Int,
    ) : TransactionDetailEvent()
}

@HiltViewModel
class TransactionDetailViewModel
    @Inject
    constructor(
        private val repository: TransactionRepository,
        savedStateHandle: SavedStateHandle,
    ) : ViewModel() {
        private val transactionId: Long = savedStateHandle["transactionId"] ?: 0L

        val state: StateFlow<TransactionDetailState> =
            repository.getAllTransactions()
                .map { transactions ->
                    val transaction = transactions.firstOrNull { it.id.toLong() == transactionId }
                    if (transaction == null) {
                        TransactionDetailState(
                            isLoading = false,
                            transaction = null,
                            errorRes = R.string.transaction_not_found,
                        )
                    } else {
                        TransactionDetailState(
                            isLoading = false,
                            transaction = transaction,
                            errorRes = null,
                        )
                    }
                }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TransactionDetailState())

        private val _event = MutableSharedFlow<TransactionDetailEvent>()
        val event: SharedFlow<TransactionDetailEvent> = _event.asSharedFlow()

        fun updateTransaction(transaction: Transaction) {
            viewModelScope.launch {
                val result = repository.updateTransaction(transaction)
                if (result.isSuccess) {
                    _event.emit(TransactionDetailEvent.Updated)
                } else {
                    _event.emit(TransactionDetailEvent.Error(R.string.error_update_transaction))
                }
            }
        }

        fun deleteTransaction(transaction: Transaction) {
            viewModelScope.launch {
                val result = repository.deleteTransaction(transaction)
                if (result.isSuccess) {
                    _event.emit(TransactionDetailEvent.Deleted)
                } else {
                    _event.emit(TransactionDetailEvent.Error(R.string.error_delete_transaction))
                }
            }
        }
    }
