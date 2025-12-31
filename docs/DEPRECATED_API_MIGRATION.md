# Deprecated API Migration Guide

## Overview
Bu doküman, projede kalan `java.util.Date` ve `java.util.Locale` kullanımlarının modern `java.time.*` ve `androidx.core.os.LocaleListCompat` API'lerine nasıl migrate edileceğini açıklar.

## Completed Migrations ✅

### 1. Core Utilities
- ✅ **LocalizationUtils.kt**: `formatDate()` artık `Instant` kullanıyor
- ✅ **Converters.kt**: Room type converters `Instant` kullanıyor
- ✅ **PrivacyPolicyScreen.kt**: `LocalDate` + `DateTimeFormatter` kullanıyor
- ✅ **Transaction.kt**: `timestamp` field + deprecated `date` property
- ✅ **TransactionMapper.kt**: Instant<->Long mapping

## Remaining Migrations ⏳

### Core Domain Layer
Aşağıdaki dosyalarda `java.util.Date` kullanımı devam ediyor. Bu dosyalar domain models olduğu için dikkatli migrate edilmeli:

#### High Priority
1. **ScheduledPayment.kt** (3. satır)
   - `nextDueDate: Date` → `nextDueDateMillis: Long` veya `Instant`
   - Domain model olduğu için tüm kullanıcıları güncellenmel

2. **GetUpcomingPaymentsUseCase.kt** (7. satır)
   - Date comparisons → Instant comparisons

3. **RecurringRuleRepository.kt** (4. satır)
   - Repository interfaces'te Date parametreleri

4. **ScheduledPaymentRepository.kt** (5. satır)
   - Repository interfaces'te Date parametreleri

5. **TransactionRepository.kt** (7. satır)
   - Repository interfaces'te Date parametreleri

### Core Data Layer
Repository implementations ve DAOs:

1. **ScheduledPaymentRepositoryImpl.kt** (9. satır)
2. **RecurringRuleRepositoryImpl.kt** (9. satır)
3. **TransactionRepositoryImpl.kt** (13. satır)
4. **ScheduledPaymentDao.kt** (5. satır)
5. **ScheduledPaymentMapper.kt** (5. satır)
6. **TransactionPagingSource.kt** (10. satır)
7. **NotificationEntity.kt** (6. satır)

### Core UI Layer
Compose UI components (zaten Locale.getDefault() kullanıyor, ama import temizliği yapılabilir):

1. **AddTransactionForm.kt** (32-33. satır)
2. **TransactionItem.kt** (34-35. satır)
3. **AdvancedDashboardCard.kt** (21. satır)
4. **SpendingLimitCard.kt** (14. satır)

### Core Export
1. **CsvExportManager.kt** (19-20. satır)
   - CSV date formatting için DateTimeFormatter kullan

### Feature Layer
Feature modules (lower priority, UI layer):

1. **feature/history/HistoryScreen.kt** (51-52. satır)
2. **feature/statistics/** - Çeşitli statistics components
3. **feature/scheduled/** - Scheduled payment components

### Test Files
1. **ExtensionsTest.kt** (6. satır)
2. **RtlLayoutTest.kt** (13. satır)

## Migration Strategy

### 1. Domain Models (Öncelik: Yüksek)
```kotlin
// BEFORE
data class ScheduledPayment(
    val nextDueDate: Date
)

// AFTER - Option 1: Instant
data class ScheduledPayment(
    val nextDueDate: Instant
)

// AFTER - Option 2: Timestamp (daha basit)
data class ScheduledPayment(
    val nextDueDateMillis: Long
) {
    val nextDueDate: Instant get() = Instant.ofEpochMilli(nextDueDateMillis)
}
```

### 2. Repository Interfaces
```kotlin
// BEFORE
interface TransactionRepository {
    fun getTransactionsByDate(startDate: Date, endDate: Date): Flow<List<Transaction>>
}

// AFTER
interface TransactionRepository {
    fun getTransactionsByDate(startMillis: Long, endMillis: Long): Flow<List<Transaction>>
}
```

### 3. UI Components
```kotlin
// BEFORE
import java.util.Date
import java.util.Locale

val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

// AFTER
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.ZoneId

val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
    .withZone(ZoneId.systemDefault())
```

### 4. CSV Export
```kotlin
// BEFORE
import java.text.SimpleDateFormat
import java.util.Date

val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)

// AFTER
import java.time.Instant
import java.time.format.DateTimeFormatter

val dateStr = DateTimeFormatter.ISO_LOCAL_DATE
    .withZone(ZoneId.systemDefault())
    .format(instant)
```

## Benefits of Migration

### Performance
- `java.time` API'leri thread-safe (immutable)
- Daha iyi memory footprint
- Modern JVM optimizations

### Type Safety
- Stronger type system (`LocalDate`, `LocalDateTime`, `Instant` ayrı)
- Compile-time zone awareness
- Null-safety with Kotlin

### API Clarity
- Fluent API (`instant.plusDays(7)`)
- Clear naming (`LocalDate` vs `Date`)
- Better documentation

## Testing Strategy

1. Unit testleri güncellenip çalıştırılmalı
2. Integration testler verify edilmeli
3. UI testlerinde date picker/display kontrol edilmeli
4. CSV export/import regression testi yapılmalı

## Rollback Plan

Eğer migration sorun çıkarırsa:
1. Domain models'e deprecated properties ekle (Transaction.kt gibi)
2. Extension functions ile backward compat sağla
3. Gradual migration uygula (feature by feature)

## Completion Checklist

- [ ] Domain models migrate edildi
- [ ] Repository interfaces güncellendi
- [ ] Repository implementations güncellendi
- [ ] DAOs güncellendi
- [ ] Use cases güncellendi
- [ ] UI components güncellendi
- [ ] CSV export/import güncellendi
- [ ] Test files güncellendi
- [ ] Deprecated imports temizlendi
- [ ] All tests passing
- [ ] Manual QA yapıldı
- [ ] Documentation updated

## Timeline

- **Phase 1 (Immediate)**: Domain models + repositories
- **Phase 2 (Next sprint)**: UI components
- **Phase 3 (Lower priority)**: Test files + feature modules

## Notes

- `java.util.Locale` kullanımı çoğu yerde `Locale.getDefault()` şeklinde ve zararsız
- Compose'da `LocalConfiguration.current.locales` tercih edilebilir
- `java.util.Date` kullanımları tamamen kaldırılmalı (deprecated ve thread-unsafe)
