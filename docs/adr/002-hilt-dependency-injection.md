# ADR-002: Hilt for Dependency Injection

## Status
Accepted

## Date
2024-12-01

## Context

We needed a dependency injection solution for our Android application. The options were:

1. **Manual DI** - Constructor injection without framework
2. **Dagger 2** - Compile-time DI
3. **Hilt** - Dagger wrapper for Android
4. **Koin** - Service locator pattern

## Decision

We chose **Hilt** for dependency injection.

### Rationale

1. **Android-first**: Built specifically for Android
2. **Jetpack Integration**: Works with ViewModel, WorkManager, etc.
3. **Compile-time Safety**: Errors caught at build time
4. **Less Boilerplate**: Compared to pure Dagger
5. **Google Recommended**: Official Android guidance

### Implementation

```kotlin
// Application
@HiltAndroidApp
class MyApplication : Application()

// ViewModel
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val useCase: GetTransactionsUseCase
) : ViewModel()

// Module
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase
}
```

## Consequences

### Positive
- ✅ Type-safe DI
- ✅ Easy ViewModel injection
- ✅ WorkManager integration
- ✅ Testing support with HiltTestRunner
- ✅ IDE support

### Negative
- ❌ Build time increase
- ❌ Learning curve for Dagger concepts
- ❌ Kapt required (slower than KSP)

