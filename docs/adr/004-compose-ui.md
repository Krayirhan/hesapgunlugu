# ADR-004: Jetpack Compose for UI

## Status
Accepted

## Date
2024-12-01

## Context

We needed to choose a UI framework for our Android application:

1. **XML Views** - Traditional Android UI
2. **Jetpack Compose** - Declarative UI toolkit
3. **Flutter** - Cross-platform (rejected - Android-only focus)

## Decision

We chose **Jetpack Compose** with Material 3.

### Rationale

1. **Modern**: Google's recommended UI toolkit
2. **Declarative**: Simpler state management
3. **Kotlin-first**: Full Kotlin integration
4. **Less Code**: Reduced boilerplate
5. **Preview**: IDE preview support
6. **Animations**: Built-in animation APIs

### Implementation

```kotlin
@Composable
fun TransactionItem(
    transaction: Transaction,
    onItemClick: (Transaction) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onItemClick(transaction) }
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Text(text = transaction.title)
            Spacer(modifier = Modifier.weight(1f))
            Text(text = transaction.amount.formatCurrency())
        }
    }
}

@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun TransactionItemPreview() {
    HesapGunluguTheme {
        TransactionItem(
            transaction = previewTransaction,
            onItemClick = {}
        )
    }
}
```

### State Management

```kotlin
@HiltViewModel
class HomeViewModel : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()
}

@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    HomeContent(
        state = state,
        onAction = viewModel::onAction
    )
}
```

## Consequences

### Positive
- ✅ Less code, more readable
- ✅ Powerful theming with Material 3
- ✅ Built-in accessibility support
- ✅ Easy animations
- ✅ Preview support
- ✅ Reusable components

### Negative
- ❌ Learning curve for View developers
- ❌ Recomposition complexity
- ❌ Some features still maturing
- ❌ Larger APK size (slightly)

