# ADR-005: Coroutines & Flow for Async Operations

## Status
Accepted

## Date
2024-12-01

## Context

We needed an asynchronous programming solution:

1. **Callbacks** - Traditional async
2. **RxJava** - Reactive streams
3. **Coroutines + Flow** - Kotlin native async

## Decision

We chose **Kotlin Coroutines with Flow**.

### Rationale

1. **Kotlin-native**: First-class language support
2. **Structured Concurrency**: Automatic cancellation
3. **Flow**: Cold streams for reactive data
4. **Integration**: Room, Retrofit, Compose support
5. **Simpler**: Less boilerplate than RxJava

### Implementation

```kotlin
// Repository
class TransactionRepositoryImpl @Inject constructor(
    private val dao: TransactionDao
) : TransactionRepository {
    
    override fun getAllTransactions(): Flow<List<Transaction>> {
        return dao.getAllTransactions().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override suspend fun addTransaction(transaction: Transaction): Result<Unit> {
        return runCatching {
            dao.insert(transaction.toEntity())
        }
    }
}

// ViewModel
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase
) : ViewModel() {
    
    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()
    
    init {
        viewModelScope.launch {
            getTransactionsUseCase()
                .catch { e -> _state.update { it.copy(error = e.message) } }
                .collect { transactions ->
                    _state.update { it.copy(transactions = transactions) }
                }
        }
    }
}

// Compose
@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    // UI
}
```

### Dispatcher Injection

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DispatcherModule {
    
    @Provides
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}

class MyRepository @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend fun fetchData() = withContext(ioDispatcher) {
        // IO operation
    }
}
```

## Consequences

### Positive
- ✅ Structured concurrency
- ✅ Automatic cancellation with viewModelScope
- ✅ Flow operators (map, filter, combine)
- ✅ StateFlow for UI state
- ✅ Easy testing with TestDispatcher

### Negative
- ❌ Learning curve for reactive concepts
- ❌ Debugging can be complex
- ❌ Cold vs Hot streams confusion

