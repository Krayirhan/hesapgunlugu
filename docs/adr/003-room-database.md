# ADR-003: Room for Local Storage

## Status
Accepted

## Date
2024-12-01

## Context

We needed a local database solution for storing financial transactions. Options:

1. **SharedPreferences** - Key-value storage
2. **SQLite** - Raw SQL
3. **Room** - SQLite abstraction
4. **Realm** - NoSQL mobile database
5. **ObjectBox** - NoSQL database

## Decision

We chose **Room Database** with KSP (Kotlin Symbol Processing).

### Rationale

1. **Jetpack**: Official Android persistence library
2. **Type Safety**: Compile-time SQL validation
3. **Coroutines/Flow**: Native async support
4. **Migration**: Built-in schema migration
5. **Paging 3**: Native integration

### Implementation

```kotlin
@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val title: String,
    val amount: Double,
    val type: String,
    val category: String,
    val date: Long
)

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>
    
    @Insert
    suspend fun insert(transaction: TransactionEntity)
}

@Database(
    entities = [TransactionEntity::class],
    version = 5,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
}
```

### Migration Strategy

```kotlin
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE ...")
    }
}
```

## Consequences

### Positive
- ✅ Compile-time SQL validation
- ✅ Flow integration for reactive updates
- ✅ Paging 3 integration
- ✅ Migration support
- ✅ Testing with in-memory database

### Negative
- ❌ Boilerplate for simple cases
- ❌ Learning curve for complex queries
- ❌ Schema changes require migrations

