# Contributing Guide

Thank you for your interest in contributing to this project!

## Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- JDK 17
- Git

### Setup
1. Clone the repository
2. Open in Android Studio
3. Sync Gradle
4. Run the app

## Development Workflow

### 1. Create a Branch
```bash
# Feature
git checkout -b feature/add-search

# Bugfix
git checkout -b fix/date-parsing-error

# Refactor
git checkout -b refactor/clean-up-repository
```

### 2. Make Changes
- Follow [Code Style Guide](CODE_STYLE.md)
- Write tests for new features
- Update documentation

### 3. Test Your Changes
```bash
# Run unit tests
./gradlew testDebugUnitTest

# Run instrumented tests
./gradlew connectedDebugAndroidTest

# Run all checks
./gradlew check
```

### 4. Commit Your Changes
Follow [Conventional Commits](https://www.conventionalcommits.org/):

```bash
# Feature
git commit -m "feat: add transaction search functionality"

# Fix
git commit -m "fix: resolve date parsing issue in Turkish locale"

# Docs
git commit -m "docs: update README with new features"

# Refactor
git commit -m "refactor: extract mapper classes"

# Test
git commit -m "test: add unit tests for AddTransactionUseCase"

# Chore
git commit -m "chore: update dependencies"
```

### 5. Create Pull Request
- Use descriptive title
- Reference related issues
- Add screenshots for UI changes
- Request review

## Code Quality

### Minimum Requirements
- ✅ All tests pass
- ✅ No Detekt issues
- ✅ Code coverage maintained (>60%)
- ✅ Documentation for public APIs
- ✅ Accessibility considered

### Before Submitting
```bash
# Run full check
./gradlew check

# Generate coverage report
./gradlew jacocoTestReport

# Run Detekt
./gradlew detekt
```

## Architecture Guidelines

### Adding New Features

1. **Create Domain Model** (if needed)
   ```
   domain/model/NewFeature.kt
   ```

2. **Create Use Case**
   ```
   domain/usecase/newfeature/NewFeatureUseCase.kt
   ```

3. **Update Repository** (if needed)
   ```
   domain/repository/NewFeatureRepository.kt
   data/repository/NewFeatureRepositoryImpl.kt
   ```

4. **Create UI**
   ```
   feature/newfeature/
   ├── NewFeatureScreen.kt
   ├── NewFeatureViewModel.kt
   └── components/
   ```

5. **Add Tests**
   ```
   test/.../NewFeatureUseCaseTest.kt
   test/.../NewFeatureViewModelTest.kt
   androidTest/.../NewFeatureScreenTest.kt
   ```

6. **Update Navigation** (if needed)

### File Naming

| Type | Convention | Example |
|------|------------|---------|
| Screen | `*Screen.kt` | `HomeScreen.kt` |
| ViewModel | `*ViewModel.kt` | `HomeViewModel.kt` |
| UseCase | `*UseCase.kt` | `AddTransactionUseCase.kt` |
| Repository | `*Repository.kt` | `TransactionRepository.kt` |
| Test | `*Test.kt` | `HomeViewModelTest.kt` |

## Review Process

### What Reviewers Look For
- Code follows style guide
- Tests are comprehensive
- No breaking changes
- Documentation updated
- Accessibility maintained

### Response Time
- Initial review: 1-2 business days
- Follow-up reviews: 1 business day

## Questions?

Open an issue with the `question` label.

