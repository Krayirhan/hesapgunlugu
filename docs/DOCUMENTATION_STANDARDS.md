# Documentation Standards

## KDoc Guidelines

This document defines the documentation standards for the project.

## Required Documentation

### 1. Public Classes
All public classes must have KDoc with:
- Brief description
- @param for constructor parameters (if applicable)
- @see for related classes

```kotlin
/**
 * Manages user transactions including income and expenses.
 *
 * This repository handles all CRUD operations for transactions
 * and provides Flow-based reactive data streams.
 *
 * @param dao The Room DAO for database operations
 * @see Transaction
 * @see TransactionDao
 */
class TransactionRepositoryImpl @Inject constructor(
    private val dao: TransactionDao
) : TransactionRepository
```

### 2. Public Functions
All public functions must have KDoc with:
- Brief description
- @param for each parameter
- @return for return value
- @throws for exceptions (if applicable)

```kotlin
/**
 * Adds a new transaction to the database.
 *
 * Validates the transaction before saving and emits
 * appropriate events on success or failure.
 *
 * @param transaction The transaction to add
 * @return Result indicating success or failure
 * @throws IllegalArgumentException if transaction data is invalid
 */
suspend fun addTransaction(transaction: Transaction): Result<Unit>
```

### 3. Use Cases
All use cases must document:
- Business purpose
- Input/output
- Side effects

```kotlin
/**
 * Use Case: Add Transaction
 *
 * Validates and adds a new financial transaction.
 *
 * ## Business Rules
 * - Title must not be blank
 * - Amount must be positive
 * - Category must be valid
 *
 * ## Side Effects
 * - Updates total balance
 * - May trigger budget notifications
 *
 * @param transaction The transaction to add
 * @return Result<Unit> Success or failure with error message
 */
class AddTransactionUseCase
```

### 4. ViewModels
ViewModels should document:
- Purpose
- State description
- Available actions

```kotlin
/**
 * ViewModel for Home Screen
 *
 * Manages the state for the main dashboard including:
 * - Transaction list
 * - Balance calculations
 * - Budget status
 *
 * ## State
 * @see HomeState for the UI state model
 *
 * ## Actions
 * - addTransaction: Add new transaction
 * - deleteTransaction: Remove transaction
 * - refresh: Reload data
 */
@HiltViewModel
class HomeViewModel
```

## Comment Standards

### TODO Comments
Use for planned improvements:
```kotlin
// TODO: Add pagination support for large datasets
```

### FIXME Comments
Use for known issues that need fixing:
```kotlin
// FIXME: Date parsing fails for certain locales
```

### NOTE Comments
Use for important implementation details:
```kotlin
// NOTE: This calculation assumes monthly budget cycle
```

## File Headers

Each file should have a brief header if it contains complex logic:

```kotlin
/**
 * Transaction Repository Implementation
 *
 * Provides data access for financial transactions with:
 * - CRUD operations
 * - Paging support
 * - Aggregation queries
 *
 * @author YourName
 * @since 1.0.0
 */
```

## Accessibility Documentation

Document accessibility features:

```kotlin
/**
 * Transaction Item Composable
 *
 * ## Accessibility
 * - Supports TalkBack with full transaction description
 * - Uses semantic properties for screen readers
 * - Provides content descriptions for icons
 */
@Composable
fun TransactionItem(...)
```

