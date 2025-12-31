# ADR-001: Clean Architecture

## Status
Accepted

## Date
2024-12-01

## Context

We needed to choose an architecture pattern for our Android finance tracking application. The key requirements were:

1. **Testability**: Easy to unit test business logic
2. **Maintainability**: Easy to understand and modify
3. **Scalability**: Can grow with new features
4. **Separation of Concerns**: Clear boundaries between layers

Options considered:
- MVC (Model-View-Controller)
- MVP (Model-View-Presenter)
- MVVM (Model-View-ViewModel)
- Clean Architecture with MVVM

## Decision

We decided to implement **Clean Architecture** with **MVVM** pattern for the presentation layer.

### Layer Structure

```
app/
├── core/          # Shared utilities, UI components
├── data/          # Data sources, repositories implementation
├── di/            # Dependency injection modules
├── domain/        # Business logic, use cases, interfaces
├── feature/       # Feature modules (home, settings, etc.)
└── worker/        # Background workers
```

### Layer Dependencies

```
Presentation (feature/) → Domain → Data
         ↓                  ↓         ↓
    ViewModel           UseCase   Repository
         ↓                  ↓         ↓
      UI State          Entities    DAO/API
```

### Key Principles

1. **Dependency Rule**: Inner layers don't know about outer layers
2. **Interfaces in Domain**: Repository interfaces defined in domain
3. **Use Cases**: Single responsibility business operations
4. **Data Mapping**: Entities mapped between layers

## Consequences

### Positive
- ✅ Highly testable - each layer can be tested in isolation
- ✅ Maintainable - clear separation of concerns
- ✅ Flexible - easy to swap implementations
- ✅ Scalable - new features follow the same pattern
- ✅ Team-friendly - developers can work on different layers

### Negative
- ❌ More boilerplate code
- ❌ Steeper learning curve for new developers
- ❌ More files and folders to manage
- ❌ Mapping overhead between layers

### Mitigations
- Use extension functions for mapping
- Create templates for new features
- Document patterns in ADRs

