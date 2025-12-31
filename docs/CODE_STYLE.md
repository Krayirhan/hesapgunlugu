# Code Style Guide

## Kotlin Conventions

This project follows the official [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html) with the following additions.

## Naming Conventions

### Classes
```kotlin
// ✅ Good
class TransactionRepository
class AddTransactionUseCase
class HomeViewModel

// ❌ Bad
class transactionRepository
class AddTransactionUC
class HomeVM
```

### Functions
```kotlin
// ✅ Good
fun getTransactions(): List<Transaction>
fun calculateTotalExpense(): Double
suspend fun addTransaction(transaction: Transaction)

// ❌ Bad
fun GetTransactions(): List<Transaction>
fun calc_total(): Double
suspend fun add(t: Transaction)
```

### Variables
```kotlin
// ✅ Good
val totalAmount: Double
val isLoading: Boolean
val transactionList: List<Transaction>

// ❌ Bad
val total: Double  // Too vague
val loading: Boolean  // Missing 'is' prefix
val tList: List<Transaction>  // Abbreviation
```

### Constants
```kotlin
// ✅ Good
const val DATABASE_NAME = "finance_db"
const val MAX_PIN_ATTEMPTS = 3

// ❌ Bad
const val dbName = "finance_db"
const val maxPinAttempts = 3
```

## File Organization

### Kotlin File Structure
```kotlin
// 1. Package declaration
package com.hesapgunlugu.app.feature.home

// 2. Imports (alphabetically sorted, no wildcards except allowed)
import androidx.compose.runtime.Composable
import com.hesapgunlugu.app.domain.model.Transaction

// 3. Top-level declarations

// 4. Class declaration with KDoc
/**
 * Brief description
 */
class MyClass {
    // 5. Companion object
    companion object {
        const val CONSTANT = "value"
    }
    
    // 6. Properties (public, then private)
    val publicProperty: String
    private val privateProperty: Int
    
    // 7. Init block
    init {
        // initialization
    }
    
    // 8. Public functions
    fun publicFunction() { }
    
    // 9. Private functions
    private fun privateFunction() { }
    
    // 10. Nested classes
    class Nested { }
}
```

## Compose Conventions

### Composable Functions
```kotlin
// ✅ Good - PascalCase, descriptive name
@Composable
fun TransactionItem(
    transaction: Transaction,
    onItemClick: (Transaction) -> Unit,
    modifier: Modifier = Modifier  // Always last
) {
    // Implementation
}

// ❌ Bad
@Composable
fun transactionItem(...)  // lowercase
@Composable
fun TxItem(...)  // abbreviation
```

### State Hoisting
```kotlin
// ✅ Good - State hoisted to caller
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
)

// ❌ Bad - State inside composable
@Composable
fun SearchBar() {
    var query by remember { mutableStateOf("") }
    // ...
}
```

### Modifier Parameter
```kotlin
// ✅ Good - modifier is last parameter with default
@Composable
fun MyComponent(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
)

// ❌ Bad - modifier not last or no default
@Composable
fun MyComponent(
    modifier: Modifier,  // Missing default
    title: String
)
```

## ViewModel Conventions

### State Class
```kotlin
// ✅ Good - Data class with sensible defaults
data class HomeState(
    val transactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val totalBalance: Double = 0.0
)
```

### Events
```kotlin
// ✅ Good - Sealed class for events
sealed class HomeEvent {
    data class ShowError(val message: String) : HomeEvent()
    data class NavigateTo(val route: String) : HomeEvent()
    data object RefreshData : HomeEvent()
}
```

## Repository Conventions

### Result Handling
```kotlin
// ✅ Good - Return Result type
suspend fun addTransaction(transaction: Transaction): Result<Unit>

// ✅ Good - Use runCatching
suspend fun addTransaction(transaction: Transaction): Result<Unit> {
    return runCatching {
        dao.insert(transaction.toEntity())
    }
}
```

## Testing Conventions

### Test Naming
```kotlin
// ✅ Good - Given/When/Then or descriptive
@Test
fun `addTransaction with valid data should return success`()

@Test
fun `addTransaction with blank title should return failure`()

// ❌ Bad - Vague or unclear
@Test
fun testAdd()

@Test
fun test1()
```

### Test Structure
```kotlin
@Test
fun `example test`() = runTest {
    // Given (Arrange)
    val transaction = TestFixtures.createTransaction()
    
    // When (Act)
    val result = useCase(transaction)
    
    // Then (Assert)
    assertTrue(result.isSuccess)
}
```

## Import Conventions

### Allowed Wildcard Imports
```kotlin
import kotlinx.coroutines.*  // ✅ Allowed
import kotlinx.coroutines.flow.*  // ✅ Allowed
import androidx.compose.foundation.layout.*  // ✅ Allowed
import androidx.compose.material3.*  // ✅ Allowed
```

### Forbidden Wildcard Imports
```kotlin
import java.util.*  // ❌ Be specific
import com.hesapgunlugu.app.domain.model.*  // ❌ Be specific
```

