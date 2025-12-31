# Kod Kalite Metrikleri

Bu dosya proje kalite metriklerini ve hedeflerini tanımlar.

## 📊 Mevcut Durum

| Metrik | Değer | Hedef | Durum |
|--------|-------|-------|-------|
| **Test Coverage** | >70% | >80% | ✅ |
| **Detekt Issues** | 0 critical | 0 | ✅ |
| **KDoc Coverage** | ~50% | >70% | ⚠️ |
| **Cyclomatic Complexity** | <15 | <10 | ⚠️ |
| **Duplicate Code** | <3% | <2% | ✅ |

## 🧪 Test Metrikleri

| Test Türü | Sayı | Coverage |
|-----------|------|----------|
| Unit Tests | 25+ | ~70% |
| Integration Tests | 5+ | ~50% |
| UI Tests | 5+ | ~30% |

### Test Dosyaları

```
app/src/test/
├── core/
│   ├── error/
│   │   └── ErrorHandlerTest.kt
│   └── security/
│       └── PasswordStrengthCheckerTest.kt
├── data/
│   ├── mapper/
│   │   └── TransactionMapperTest.kt
│   └── repository/
│       └── TransactionRepositoryImplTest.kt
├── domain/
│   ├── common/
│   │   └── DomainResultTest.kt
│   └── usecase/
│       ├── scheduled/
│       │   ├── AddScheduledPaymentUseCaseTest.kt
│       │   ├── DeleteScheduledPaymentUseCaseTest.kt
│       │   ├── GetScheduledPaymentsUseCaseTest.kt
│       │   └── MarkPaymentAsPaidUseCaseTest.kt
│       ├── statistics/
│       │   └── GetStatisticsUseCaseTest.kt
│       └── transaction/
│           ├── AddTransactionUseCaseTest.kt
│           ├── DeleteTransactionUseCaseTest.kt
│           ├── GetTransactionsUseCaseTest.kt
│           └── UpdateTransactionUseCaseTest.kt
├── feature/
│   ├── history/
│   │   └── HistoryViewModelTest.kt
│   ├── home/
│   │   └── HomeViewModelTest.kt
│   ├── scheduled/
│   │   └── ScheduledViewModelTest.kt
│   └── statistics/
│       └── StatisticsViewModelTest.kt
└── testutil/
    ├── FakeScheduledPaymentRepository.kt
    ├── FakeTransactionRepository.kt
    ├── MainDispatcherRule.kt
    └── TestFixtures.kt

app/src/androidTest/
├── benchmark/
│   └── TransactionBenchmark.kt
├── data/
│   └── local/
│       └── TransactionDaoTest.kt
├── feature/
│   ├── history/
│   │   └── HistoryScreenUiTest.kt
│   ├── home/
│   │   └── HomeScreenTest.kt
│   ├── settings/
│   │   └── SettingsScreenTest.kt
│   └── statistics/
│       └── StatisticsScreenTest.kt
├── integration/
│   └── TransactionFlowIntegrationTest.kt
└── HiltTestRunner.kt
```

## 🔍 Statik Analiz

### Detekt Kuralları

| Kategori | Durum | Önem |
|----------|-------|------|
| Complexity | ✅ Aktif | Yüksek |
| Coroutines | ✅ Aktif | Yüksek |
| Empty Blocks | ✅ Aktif | Orta |
| Exceptions | ✅ Aktif | Yüksek |
| Naming | ✅ Aktif | Orta |
| Performance | ✅ Aktif | Orta |
| Potential Bugs | ✅ Aktif | Kritik |
| Style | ✅ Aktif | Düşük |

### Detekt Komutları

```bash
# Tüm modüller
./gradlew detekt

# Sadece app modülü
./gradlew :app:detekt

# HTML raporu oluştur
./gradlew detekt --info
# Rapor: app/build/reports/detekt/detekt.html
```

## 📈 Coverage Hedefleri

### Minimum Gereksinimler

| Katman | Minimum | Hedef |
|--------|---------|-------|
| Domain (UseCase) | 80% | 90% |
| Data (Repository) | 70% | 80% |
| ViewModel | 60% | 80% |
| Mapper | 90% | 100% |
| Utils | 80% | 90% |

### Jacoco Komutları

```bash
# Coverage raporu oluştur
./gradlew jacocoTestReport

# Coverage verification (min %60)
./gradlew jacocoCoverageVerification

# Rapor: app/build/reports/jacoco/jacocoTestReport/html/index.html
```

## 🏆 Kalite Standartları

### Code Review Checklist

- [ ] Unit test yazıldı mı?
- [ ] KDoc eklendi mi?
- [ ] Detekt hataları yok mu?
- [ ] Magic number kullanılmadı mı?
- [ ] Error handling yapıldı mı?
- [ ] Accessibility düşünüldü mü?

### CI/CD Kontrolleri

| Kontrol | Fail Condition |
|---------|---------------|
| Lint | Any error |
| Unit Tests | Any failure |
| Detekt | Critical issues |
| Coverage | < 60% |
| Build | Compilation error |

