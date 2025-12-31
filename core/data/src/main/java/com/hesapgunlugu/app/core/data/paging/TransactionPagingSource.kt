package com.hesapgunlugu.app.core.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.hesapgunlugu.app.core.data.local.TransactionDao
import com.hesapgunlugu.app.core.data.mapper.TransactionMapper
import com.hesapgunlugu.app.core.domain.model.Transaction
import com.hesapgunlugu.app.core.domain.model.TransactionType
import timber.log.Timber
import java.util.Date

/**
 * PagingSource for transactions with pagination support
 * Loads data in pages to avoid loading all transactions at once
 */
class TransactionPagingSource(
    private val dao: TransactionDao,
    private val searchQuery: String? = null,
    private val typeFilter: TransactionType? = null,
    private val startDate: Date? = null,
    private val endDate: Date? = null,
) : PagingSource<Int, Transaction>() {
    companion object {
        private const val STARTING_PAGE_INDEX = 0
        const val PAGE_SIZE = 20
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Transaction> {
        val page = params.key ?: STARTING_PAGE_INDEX
        val offset = page * PAGE_SIZE

        return try {
            val entities =
                when {
                    // Search query with filters
                    !searchQuery.isNullOrBlank() && typeFilter != null -> {
                        dao.searchTransactionsWithTypePaged(
                            query = "%$searchQuery%",
                            type = typeFilter.name,
                            limit = PAGE_SIZE,
                            offset = offset,
                        )
                    }
                    // Search query only
                    !searchQuery.isNullOrBlank() -> {
                        dao.searchTransactionsPaged(
                            query = "%$searchQuery%",
                            limit = PAGE_SIZE,
                            offset = offset,
                        )
                    }
                    // Type filter only
                    typeFilter != null -> {
                        dao.getTransactionsByTypePaged(
                            type = typeFilter.name,
                            limit = PAGE_SIZE,
                            offset = offset,
                        )
                    }
                    // Date range filter
                    startDate != null && endDate != null -> {
                        dao.getTransactionsByDateRangePaged(
                            startDate = startDate.time,
                            endDate = endDate.time,
                            limit = PAGE_SIZE,
                            offset = offset,
                        )
                    }
                    // All transactions
                    else -> {
                        dao.getAllTransactionsPaged(
                            limit = PAGE_SIZE,
                            offset = offset,
                        )
                    }
                }

            val transactions = entities.map { TransactionMapper.toDomain(it) }

            Timber.d("Loaded page $page with ${transactions.size} transactions")

            LoadResult.Page(
                data = transactions,
                prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1,
                nextKey = if (transactions.isEmpty()) null else page + 1,
            )
        } catch (exception: Exception) {
            Timber.e(exception, "Error loading transactions page $page")
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Transaction>): Int? {
        // Try to find the page key of the closest page to the most recently accessed index
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
