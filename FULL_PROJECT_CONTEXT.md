# 📱 HESAP GÜNLÜĞÜ (HesapGunlugu) - KAPSAMLI PROJE DOKÜMANTASYONU

## 🎯 PROMPT OLARAK KULLANIM İÇİN TAM PROJE BAĞLAMI

**Son Güncelleme:** 28 Aralık 2025  
**Versiyon:** 1.0.0  
**Durum:** ✅ Production Ready

---

## 📌 PROJE TANIMI

**HesapGunlugu**, Türkçe "Hesap Günlüğü" anlamına gelen, modern bir **Kişisel Finans Takip Android Uygulamasıdır**. Uygulama, kullanıcıların gelir ve giderlerini takip etmelerini, bütçe yönetimi yapmalarını, finansal istatistiklerini görüntülemelerini ve tekrarlayan ödemelerini planlamalarını sağlar.

| Özellik | Değer |
|---------|-------|
| **Proje Türü** | Native Android Uygulaması |
| **Geliştirme Dili** | Kotlin 2.0.21 |
| **UI Framework** | Jetpack Compose (100% Compose) |
| **Mimari** | Clean Architecture + Multi-Module + MVVM |
| **Minimum SDK** | 26 (Android 8.0 Oreo) |
| **Target SDK** | 36 (Android 15) |
| **Paket Adı** | `com.hesapgunlugu.app` |

---

## 🏗️ MİMARİ YAPI

### Multi-Module Yapısı (25 Modül)

```
HesapGunlugu/
├── 📱 app/                          # Ana uygulama modülü (Composition Root)
│   ├── MainActivity.kt              # Tek Activity - Compose Host
│   ├── MyApplication.kt             # Hilt Application, WorkManager, ACRA
│   ├── di/                          # Hilt Modülleri
│   │   ├── AppModule.kt             # Database, Repository bindings
│   │   ├── CommonModule.kt          # StringProvider, NotificationHelper
│   │   ├── DispatcherModule.kt      # Coroutine Dispatchers
│   │   └── UseCaseModule.kt         # Domain Use Cases
│   ├── widget/                      # Android App Widget
│   │   └── FinanceWidget.kt         # Glance Widget - Bakiye özeti
│   ├── worker/                      # WorkManager Workers
│   │   └── RecurringTransactionWorker.kt
│   └── feature/common/navigation/   # Merkezi Navigation
│       ├── AppNavGraph.kt           # Tek NavHost - Tüm route'lar
│       └── Screen.kt                # Sealed class route tanımları
│
├── 🎯 core/                         # Paylaşılan Altyapı Modülleri (16 modül)
│   ├── common/                      # UiState, StringProvider interface
│   ├── domain/                      # Business Logic Layer
│   │   ├── model/                   # Domain Entities
│   │   │   ├── Transaction.kt       # Gelir/Gider modeli
│   │   │   ├── ScheduledPayment.kt  # Zamanlanmış ödeme
│   │   │   ├── UserSettings.kt      # Kullanıcı ayarları
│   │   │   ├── CategoryTotal.kt     # Kategori bazlı toplam
│   │   │   ├── CategoryBudgetStatus.kt
│   │   │   └── RecurrenceType.kt    # DAILY, WEEKLY, MONTHLY, YEARLY
│   │   ├── repository/              # Repository Interfaces (Contracts)
│   │   │   ├── TransactionRepository.kt
│   │   │   ├── ScheduledPaymentRepository.kt
│   │   │   ├── RecurringRuleRepository.kt
│   │   │   └── SettingsRepository.kt
│   │   └── usecase/                 # Use Cases (İş Mantığı)
│   │       ├── transaction/
│   │       │   ├── AddTransactionUseCase.kt
│   │       │   ├── GetTransactionsUseCase.kt
│   │       │   ├── UpdateTransactionUseCase.kt
│   │       │   └── DeleteTransactionUseCase.kt
│   │       ├── scheduled/
│   │       │   ├── AddScheduledPaymentUseCase.kt
│   │       │   ├── GetScheduledPaymentsUseCase.kt
│   │       │   ├── GetUpcomingPaymentsUseCase.kt
│   │       │   ├── GetRecurringIncomesUseCase.kt
│   │       │   ├── GetRecurringExpensesUseCase.kt
│   │       │   ├── MarkPaymentAsPaidUseCase.kt
│   │       │   └── DeleteScheduledPaymentUseCase.kt
│   │       ├── statistics/
│   │       │   └── GetStatisticsUseCase.kt
│   │       └── settings/
│   │           ├── GetUserSettingsUseCase.kt
│   │           ├── UpdateThemeUseCase.kt
│   │           ├── UpdateMonthlyLimitUseCase.kt
│   │           └── UpdateCategoryBudgetUseCase.kt
│   │
│   ├── data/                        # Data Layer Implementation
│   │   ├── local/
│   │   │   ├── AppDatabase.kt       # Room Database (7 version, 5 entity)
│   │   │   ├── Converters.kt        # Room TypeConverters
│   │   │   ├── DatabaseMigrations.kt
│   │   │   ├── TransactionEntity.kt # Room Entity
│   │   │   ├── TransactionDao.kt    # Room DAO
│   │   │   ├── ScheduledPaymentEntity.kt
│   │   │   ├── ScheduledPaymentDao.kt
│   │   │   ├── NotificationEntity.kt
│   │   │   ├── NotificationDao.kt
│   │   │   ├── SettingsManager.kt   # DataStore Preferences
│   │   │   └── EncryptedSettingsManager.kt
│   │   ├── repository/
│   │   │   ├── TransactionRepositoryImpl.kt
│   │   │   ├── ScheduledPaymentRepositoryImpl.kt
│   │   │   ├── RecurringRuleRepositoryImpl.kt
│   │   │   └── SettingsRepositoryImpl.kt
│   │   ├── mapper/
│   │   │   ├── TransactionMapper.kt # Entity ↔ Domain
│   │   │   └── ScheduledPaymentMapper.kt
│   │   └── paging/
│   │       └── TransactionPagingSource.kt # Paging 3
│   │
│   ├── ui/                          # Paylaşılan UI Katmanı
│   │   ├── theme/
│   │   │   ├── Theme.kt             # Material Design 3, Dynamic Colors
│   │   │   ├── Color.kt             # Renk paleti
│   │   │   ├── Type.kt              # Typography (Poppins font)
│   │   │   ├── Shape.kt             # Shape theming
│   │   │   └── AccessibleTheme.kt   # High contrast mode
│   │   ├── components/              # 40+ Reusable Composable
│   │   │   ├── HomeHeader.kt
│   │   │   ├── DashboardCard.kt
│   │   │   ├── TransactionItem.kt
│   │   │   ├── AddTransactionForm.kt
│   │   │   ├── CategoryBudgetCard.kt
│   │   │   ├── Charts.kt            # Pie, Line, Bar charts
│   │   │   ├── AdvancedCharts.kt
│   │   │   ├── CalendarView.kt
│   │   │   ├── ErrorCard.kt
│   │   │   ├── LoadingErrorStates.kt
│   │   │   ├── SkeletonLoader.kt
│   │   │   └── ...
│   │   ├── accessibility/
│   │   │   ├── AccessibilityExtensions.kt
│   │   │   ├── ColorAccessibility.kt  # WCAG 2.1 AA
│   │   │   └── FontScaling.kt
│   │   └── animations/
│   │       └── Animations.kt
│   │
│   ├── security/                    # Güvenlik Modülü
│   │   ├── SecurityManager.kt       # Merkezi güvenlik
│   │   ├── BiometricAuthManager.kt  # Parmak izi/Yüz tanıma
│   │   ├── PinLockScreen.kt         # PIN kilidi
│   │   ├── PasswordStrengthChecker.kt
│   │   └── RootDetector.kt
│   │
│   ├── backup/                      # Yedekleme
│   │   ├── BackupManager.kt
│   │   ├── BackupEncryption.kt      # AES-256
│   │   └── BackupViewModel.kt
│   │
│   ├── cloud/                       # Bulut Entegrasyonu
│   │   └── GoogleDriveBackupManager.kt
│   │
│   ├── export/                      # Dışa Aktarma
│   │   ├── CsvExportManager.kt
│   │   ├── PdfExportManager.kt
│   │   └── EmailShareManager.kt
│   │
│   ├── notification/                # Bildirimler
│   │   ├── PaymentReminderWorker.kt
│   │   └── RecurringPaymentWorker.kt
│   │
│   ├── navigation/                  # Navigation Helpers
│   │   ├── Navigator.kt
│   │   └── NavigationDestinations.kt
│   │
│   ├── premium/                     # In-App Purchase
│   │   └── BillingManager.kt        # Google Play Billing
│   │
│   ├── performance/                 # Performans İzleme
│   │   └── PerformanceMonitor.kt
│   │
│   ├── feedback/                    # Kullanıcı Geri Bildirimi
│   │   ├── FeedbackManager.kt
│   │   └── AppInfoProvider.kt
│   │
│   ├── error/                       # Hata Yönetimi
│   │   ├── ErrorHandler.kt
│   │   └── GlobalExceptionHandler.kt
│   │
│   ├── debug/                       # Debug Araçları (Debug Only)
│   │   └── DebugMenuDialog.kt
│   │
│   └── util/                        # Yardımcı Fonksiyonlar
│       ├── Constants.kt
│       └── LocalizationUtils.kt
│
├── 🎨 feature/                      # Feature Modülleri (8 modül)
│   ├── home/                        # Ana Ekran (Dashboard)
│   │   ├── HomeScreen.kt
│   │   ├── HomeViewModel.kt
│   │   └── HomeState.kt
│   │
│   ├── statistics/                  # İstatistikler
│   │   ├── StatisticsScreen.kt
│   │   ├── StatisticsViewModel.kt
│   │   └── components/
│   │       ├── CategoryBreakdownCard.kt
│   │       ├── IncomeExpenseBarChart.kt
│   │       ├── TrendAnalysisCard.kt
│   │       └── PeriodSelector.kt
│   │
│   ├── scheduled/                   # Zamanlanmış Ödemeler
│   │   ├── ScheduledScreen.kt
│   │   ├── ScheduledViewModel.kt
│   │   └── components/
│   │       ├── ScheduledPaymentItem.kt
│   │       ├── RecurringItem.kt
│   │       └── RecurringRuleDialog.kt
│   │
│   ├── history/                     # Geçmiş İşlemler
│   │   ├── HistoryScreen.kt
│   │   └── HistoryViewModel.kt
│   │
│   ├── settings/                    # Ayarlar
│   │   ├── SettingsScreen.kt
│   │   ├── SettingsViewModel.kt
│   │   ├── ThemeViewModel.kt
│   │   ├── DataDeletionScreen.kt    # GDPR uyumlu
│   │   └── CategoryManagementScreen.kt
│   │
│   ├── notifications/               # Bildirim Merkezi
│   │   └── NotificationCenterScreen.kt
│   │
│   ├── onboarding/                  # İlk Kullanım Rehberi
│   │   ├── OnboardingScreen.kt
│   │   └── OnboardingManager.kt
│   │
│   └── privacy/                     # Gizlilik Politikası
│       └── PrivacyPolicyScreen.kt
│
├── 📊 baselineprofile/              # Startup Optimizasyonu
│   └── BaselineProfileGenerator.kt
│
├── 📈 benchmark-macro/              # Performans Testleri
│   └── StartupBenchmark.kt
│
├── 📦 gradle/                       # Gradle Wrapper ve Version Catalog
│   ├── wrapper/
│   └── libs.versions.toml           # Merkezi dependency versiyonları
│
├── 🔧 config/                       # Yapılandırma
│   ├── detekt/                      # Static Analysis kuralları
│   └── jacoco/                      # Code Coverage config
│
├── 📚 docs/                         # Dokümantasyon (25+ dosya)
│   ├── ACCESSIBILITY_GUIDE.md
│   ├── APK_SIGNING_GUIDE.md
│   ├── BENCHMARK_GUIDE.md
│   ├── MULTI_MODULE_GUIDE.md
│   ├── PRIVACY_POLICY.md
│   ├── RELEASE_CHECKLIST.md
│   └── ...
│
└── 🚀 scripts/                      # Otomasyon Scriptleri
    ├── audit-architecture.ps1
    ├── validate-architecture.ps1
    └── ...
```

---

## 🔧 TEKNOLOJİ STACK'İ

### Core Technologies

| Teknoloji | Versiyon | Kullanım Amacı |
|-----------|----------|----------------|
| **Kotlin** | 2.0.21 | Ana geliştirme dili |
| **Jetpack Compose** | BOM 2024.09.00 | Declarative UI framework |
| **Material Design 3** | Latest | Modern UI/UX design system |
| **Coroutines** | 1.7.3 | Asenkron programlama |
| **Flow** | 1.7.3 | Reactive streams |

### Jetpack Libraries

| Kütüphane | Versiyon | Kullanım Amacı |
|-----------|----------|----------------|
| **Room** | 2.6.1 | Local SQLite database (KSP ile) |
| **Hilt** | 2.57.2 | Dependency Injection |
| **Navigation Compose** | 2.8.0 | Type-safe navigation |
| **DataStore** | 1.0.0 | Preferences storage |
| **WorkManager** | 2.9.0 | Background tasks |
| **Paging 3** | 3.3.2 | Large dataset pagination |
| **Biometric** | 1.1.0 | Fingerprint/Face ID |
| **Splash Screen** | 1.0.1 | Android 12+ splash API |
| **Glance** | 1.1.0 | App Widget (Compose-like) |
| **Lifecycle** | 2.9.2 | ViewModel, LiveData |

### Testing

| Kütüphane | Versiyon | Kullanım Amacı |
|-----------|----------|----------------|
| **JUnit 4** | 4.13.2 | Unit testing |
| **MockK** | 1.14.7 | Mocking framework |
| **Turbine** | 1.0.0 | Flow testing |
| **Paparazzi** | Latest | Screenshot testing |
| **Espresso** | 3.7.0 | UI testing |
| **Jacoco** | Latest | Code coverage (Target: 80%+) |

### Quality & Performance

| Araç | Versiyon | Kullanım Amacı |
|------|----------|----------------|
| **KSP** | 2.0.21-1.0.27 | Annotation processing (Room) |
| **KAPT** | 2.0.21 | Annotation processing (Hilt) |
| **Detekt** | 1.23.8 | Static code analysis |
| **ProGuard/R8** | Latest | Code shrinking & obfuscation |
| **Baseline Profile** | 1.4.1 | Startup optimization |
| **Timber** | 5.0.1 | Logging |
| **ACRA** | 5.11.3 | Crash reporting (local) |

### Backend & Cloud

| Servis | Versiyon | Kullanım Amacı |
|--------|----------|----------------|
| **Firebase** | BOM 32.7.0 | Analytics, Crashlytics, Performance |
| **Google Drive API** | Latest | Cloud backup |

---

## ✨ UYGULAMA ÖZELLİKLERİ

### 💵 Finansal Yönetim
- ✅ **Gelir/Gider Takibi** - Detaylı işlem kayıtları
- ✅ **13+ Kategori** - Yiyecek, Ulaşım, Eğlence, Fatura, vb.
- ✅ **Bütçe Yönetimi** - Aylık harcama limitleri
- ✅ **Kategori Bazlı Bütçe** - Her kategori için ayrı limit
- ✅ **Zamanlanmış Ödemeler** - Tekrarlayan gelir/gider takibi
- ✅ **İstatistikler ve Grafikler** - Haftalık/Aylık/Yıllık trendler

### 🎨 Modern UI/UX
- ✅ **Material Design 3** - Dynamic colors (Android 12+)
- ✅ **Karanlık/Aydınlık Tema** - Sistem temasına uyum
- ✅ **100% Jetpack Compose** - Declarative UI
- ✅ **Adaptive Layouts** - Telefon/Tablet desteği
- ✅ **Smooth Animations** - Polished kullanıcı deneyimi

### 🔒 Güvenlik
- ✅ **Biometric Authentication** - Parmak izi/Yüz tanıma
- ✅ **PIN Kilidi** - 4-6 haneli PIN koruması
- ✅ **DataStore Encryption** - Hassas veri şifreleme
- ✅ **Screenshot Koruması** - FLAG_SECURE
- ✅ **Brute-force Koruması** - Deneme limiti
- ✅ **Root Detection** - Rooted cihaz uyarısı
- ✅ **ProGuard/R8** - Kod gizleme

### ♿ Erişilebilirlik (WCAG 2.1 AA)
- ✅ **TalkBack Desteği** - Screen reader uyumlu
- ✅ **Content Descriptions** - Tüm UI elementleri
- ✅ **4.5:1 Kontrast Oranı** - Renk erişilebilirliği
- ✅ **Font Scaling** - 0.85x - 2.0x+ ölçekleme
- ✅ **48dp Touch Targets** - Minimum dokunma alanı

### 🌍 Çoklu Dil
- ✅ **Türkçe** (Varsayılan)
- ✅ **İngilizce**

### 📊 İleri Özellikler
- ✅ **App Widget** - Glance ile hızlı bakiye görünümü
- ✅ **CSV/PDF Export** - Rapor dışa aktarma
- ✅ **Email Paylaşım** - Rapor gönderme
- ✅ **Yerel Yedekleme** - Şifreli backup/restore
- ✅ **Google Drive Backup** - Bulut yedekleme
- ✅ **GDPR Uyumlu** - Veri silme ekranı
- ✅ **Arama ve Filtreleme** - Gelişmiş işlem arama

---

## 📦 BUILD VARİANTLARI

### Product Flavors

```kotlin
// Free Flavor (com.hesapgunlugu.app.free)
├─ Max 100 işlem
├─ Reklamlar aktif (placeholder)
└─ Tüm temel özellikler

// Premium Flavor (com.hesapgunlugu.app.premium)
├─ Sınırsız işlem
├─ Reklamsız
└─ Tüm özellikler açık
```

### Build Types

```kotlin
// Debug
├─ Minification kapalı
├─ Test coverage aktif
├─ Debugging araçları
└─ applicationId: .debug

// Staging
├─ Pre-production test
├─ Strict mode aktif
├─ Debug + bazı optimizasyonlar
└─ applicationId: .staging

// Beta
├─ Release benzeri
├─ Crash reporting aktif
├─ Debug logları
└─ applicationId: .beta

// Release
├─ Minification aktif
├─ R8 shrinking
├─ ProGuard rules
├─ Signed APK
└─ applicationId: (clean)
```

---

## 🔄 DEPENDENCY FLOW (Clean Architecture)

```
┌─────────────────────────────────────────────────────────────┐
│                           app                                │
│  (Composition Root, Hilt, Navigation, Workers)              │
└──────────────┬──────────────────────────────────────────────┘
               │
       ┌───────┴────────┐
       │                │
       ▼                ▼
┌─────────────┐   ┌──────────────────┐
│  feature/*  │   │     core/*       │
│(Presentation)│   │    (Shared)      │
└──────┬──────┘   └────────┬─────────┘
       │                   │
       ▼                   ▼
  ┌─────────────────────────────────────┐
  │            core:domain              │
  │   (Pure Kotlin - No Dependencies)   │
  └───────────────┬─────────────────────┘
                  │
                  ▼
  ┌─────────────────────────────────────┐
  │             core:data               │
  │   (Room, DataStore, Repository)     │
  └─────────────────────────────────────┘
```

### Katı Kurallar (Boundary Rules)

| Modül | Görebileceği Modüller | Göremeyeceği Modüller |
|-------|----------------------|----------------------|
| `app` | Tüm modüller | - |
| `feature/*` | `core:domain`, `core:ui`, `core:navigation` | `core:data` ❌ |
| `core:data` | `core:domain` | `feature/*` ❌ |
| `core:domain` | Hiçbiri (pure Kotlin) | Tüm modüller ❌ |
| `core:ui` | `core:domain` | `core:data` ❌ |

---

## 🗄️ VERİTABANI YAPISI

### Room Database (AppDatabase.kt)

| Özellik | Değer |
|---------|-------|
| **Version** | 7 |
| **Entities** | 5 |
| **Export Schema** | true |

### Entities

#### TransactionEntity
```kotlin
@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Double,
    val type: TransactionType,      // INCOME, EXPENSE
    val category: Category,         // 13+ kategori
    val date: Long,                 // Timestamp
    val note: String?,
    val isRecurring: Boolean = false,
    val recurringRuleId: Long? = null
)
```

#### ScheduledPaymentEntity
```kotlin
@Entity(tableName = "scheduled_payments")
data class ScheduledPaymentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val amount: Double,
    val type: TransactionType,
    val category: Category,
    val nextPaymentDate: Long,
    val recurrenceType: RecurrenceType,  // DAILY, WEEKLY, MONTHLY, YEARLY
    val isActive: Boolean = true
)
```

#### RecurringTransactionEntity
```kotlin
@Entity(tableName = "recurring_transactions")
data class RecurringTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val scheduledPaymentId: Long,
    val transactionId: Long,
    val createdDate: Long
)
```

#### RecurringRuleEntity
```kotlin
@Entity(tableName = "recurring_rules")
data class RecurringRuleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val interval: Int,
    val type: RecurrenceType,
    val endDate: Long?,
    val maxOccurrences: Int?
)
```

#### NotificationEntity
```kotlin
@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val message: String,
    val type: NotificationType,
    val createdAt: Long,
    val isRead: Boolean = false
)
```

---

## 🧪 TEST STRATEJİSİ

### Unit Tests (`app/src/test/`)
- **ViewModel Tests:** StateFlow, Use Case interaction
- **Repository Tests:** Mock DAO, Mapper tests
- **Use Case Tests:** Business logic validation
- **Utility Tests:** Extension functions

### Instrumented Tests (`app/src/androidTest/`)
- **UI Tests:** Compose UI testing
- **DAO Tests:** Room database tests (in-memory)
- **Integration Tests:** End-to-end flows
- **Migration Tests:** Database migration verification
- **Accessibility Tests:** TalkBack, contrast

### Benchmark Tests (`benchmark-macro/`)
- **Startup Benchmark:** Cold/Warm/Hot startup
- **Navigation Benchmark:** Screen transition latency
- **Scroll Benchmark:** LazyColumn/Grid FPS

### Coverage Target
- **Unit Tests:** 80%+
- **Integration Tests:** 60%+

---

## 📁 ÖNEMLİ DOSYALAR

| Dosya | Konum | İşlev |
|-------|-------|-------|
| `MainActivity.kt` | `app/` | Tek Activity, Compose Host |
| `MyApplication.kt` | `app/` | Hilt Application, WorkManager init |
| `AppNavGraph.kt` | `app/feature/common/navigation/` | Merkezi Navigation |
| `Screen.kt` | `app/feature/common/navigation/` | Route tanımları (sealed class) |
| `AppModule.kt` | `app/di/` | Ana Hilt modülü |
| `AppDatabase.kt` | `core/data/local/` | Room Database |
| `TransactionRepository.kt` | `core/domain/repository/` | Repository Interface |
| `TransactionRepositoryImpl.kt` | `core/data/repository/` | Repository Implementation |
| `AddTransactionUseCase.kt` | `core/domain/usecase/` | Use Case örneği |
| `Theme.kt` | `core/ui/theme/` | Material 3 Theme |
| `SecurityManager.kt` | `core/security/` | Merkezi güvenlik |
| `libs.versions.toml` | `gradle/` | Version Catalog |
| `build.gradle.kts` | `app/` | App modülü build config |
| `proguard-rules.pro` | `app/` | ProGuard kuralları |

---

## 🚀 PROJE İSTATİSTİKLERİ

| Metrik | Değer |
|--------|-------|
| **Toplam Kotlin Dosyası** | 246 |
| **Toplam Modül** | 25 |
| **Core Modül** | 16 |
| **Feature Modül** | 8 |
| **Test Dosyası** | ~80 |
| **Composable Fonksiyon** | 150+ |
| **Use Case** | 15+ |
| **Repository** | 4 |
| **Room Entity** | 5 |
| **Room DAO** | 5 |
| **WorkManager Worker** | 3 |
| **Hilt Module** | 6 |
| **Satır Kodu (tahmini)** | 25,000+ |

---

## 🔐 GÜVENLİK MİMARİSİ

```
┌─────────────────────────────────────────────────────────────┐
│                    SecurityManager.kt                        │
│         (Merkezi Güvenlik Koordinatörü)                     │
└───────────────────────┬─────────────────────────────────────┘
                        │
        ┌───────────────┼───────────────┐
        │               │               │
        ▼               ▼               ▼
┌───────────────┐ ┌───────────┐ ┌─────────────────┐
│ BiometricAuth │ │ PIN Lock  │ │ BackupEncryption│
│   Manager     │ │  Screen   │ │    (AES-256)    │
└───────────────┘ └───────────┘ └─────────────────┘
        │               │               │
        ▼               ▼               ▼
┌───────────────────────────────────────────────────────────┐
│              EncryptedSettingsManager                      │
│         (EncryptedSharedPreferences)                       │
└───────────────────────────────────────────────────────────┘
```

---

## 📱 NAVIGATION AKIŞI

```
App Launch
    ↓
OnboardingScreen (ilk kullanım)
    ↓
PIN/Biometric Auth (güvenlik aktifse)
    ↓
HomeScreen (Dashboard)
    ├─→ StatisticsScreen
    ├─→ HistoryScreen
    ├─→ ScheduledScreen
    ├─→ NotificationCenterScreen
    └─→ SettingsScreen
            ├─→ ThemeSettings
            ├─→ LanguageSettings
            ├─→ SecuritySettings
            ├─→ BackupSettings
            ├─→ CategoryManagement
            ├─→ DataDeletionScreen
            └─→ PrivacyPolicyScreen
```

---

## 🔨 MODÜL BUILD DOSYALARI

### settings.gradle.kts (Modül Listesi)
```kotlin
include(":app")
include(":baselineprofile")
include(":benchmark-macro")

// Core Modüller
include(":core:common")
include(":core:domain")
include(":core:data")
include(":core:ui")
include(":core:navigation")
include(":core:backup")
include(":core:security")
include(":core:export")
include(":core:util")
include(":core:error")
include(":core:notification")
include(":core:debug")
include(":core:cloud")
include(":core:premium")
include(":core:performance")
include(":core:feedback")

// Feature Modüller
include(":feature:home")
include(":feature:settings")
include(":feature:history")
include(":feature:scheduled")
include(":feature:statistics")
include(":feature:notifications")
include(":feature:onboarding")
include(":feature:privacy")
```

---

## 🏷️ PROJE DURUM

| Özellik | Durum | Skor |
|---------|-------|------|
| **Mimari Sağlık** | ✅ Mükemmel | 97/100 |
| **Boundary Kuralları** | ✅ %100 Uyumlu | 100/100 |
| **Modül Organizasyonu** | ✅ Mükemmel | 100/100 |
| **DI (Hilt) Yapısı** | ✅ Mükemmel | 100/100 |
| **Navigation** | ✅ Çok İyi | 95/100 |
| **Build Konfigürasyonu** | ✅ İyi | 90/100 |
| **Test Coverage** | ✅ 80%+ | - |
| **Production Ready** | ✅ Evet | - |
| **Play Store Ready** | ✅ Evet | - |

---

## 🎯 PROMPT KULLANIM ÖRNEKLERİ

Bu projeyle çalışırken şu prompt yapılarını kullanabilirsiniz:

### 1. Yeni Özellik Ekleme
```
HesapGunlugu projesine [özellik adı] ekle. Clean Architecture prensiplerine 
uygun olarak:
- core:domain'e use case
- core:data'ya repository implementation  
- feature modülüne screen ve viewmodel ekle.
```

### 2. Bug Düzeltme
```
HesapGunlugu projesinde [modül/dosya] içindeki [hata açıklaması] hatasını düzelt.
```

### 3. UI Geliştirme
```
HesapGunlugu'nun [ekran adı] ekranına Material Design 3 uyumlu [komponent] ekle. 
core:ui/components/ altında reusable olsun.
```

### 4. Test Yazma
```
HesapGunlugu'nun [UseCase/ViewModel/Repository] için unit test yaz. 
MockK ve Turbine kullan.
```

### 5. Performans İyileştirme
```
HesapGunlugu'nun [ekran/işlem] performansını optimize et. 
Baseline Profile ve Compose optimizasyonlarını uygula.
```

### 6. Yeni Modül Ekleme
```
HesapGunlugu'na [modül adı] core/feature modülü ekle.
build.gradle.kts, proguard kuralları ve Hilt modülünü dahil et.
```

### 7. Database Migration
```
HesapGunlugu'nun Room database'ine [tablo/kolon] ekle.
Migration strategy ile versiyon 8'e güncelle.
```

---

## 📋 GELİŞTİRME KURALLARI

### Kod Stili
- **Kotlin Coding Conventions** uygulanır
- **Detekt** ile statik analiz
- **4 space indentation**
- **Max line length:** 120 karakter

### Commit Convention
```
feat: Yeni özellik eklendi
fix: Bug düzeltildi
refactor: Kod refactoring
docs: Dokümantasyon güncellendi
test: Test eklendi/güncellendi
chore: Build/tooling değişikliği
```

### Branch Strategy
```
main          → Production
develop       → Development
feature/*     → Yeni özellikler
bugfix/*      → Bug düzeltmeleri
release/*     → Release hazırlık
```

---

## 🔗 İLGİLİ DOSYALAR

| Dosya | Açıklama |
|-------|----------|
| `DETAILED_PROJECT_HIERARCHY.md` | Tüm dosyaların detaylı açıklaması |
| `ARCHITECTURE_AUDIT_FINAL_REPORT.md` | Mimari audit sonuçları |
| `EXECUTIVE_SUMMARY.md` | Yönetici özeti |
| `docs/MULTI_MODULE_GUIDE.md` | Multi-module rehberi |
| `docs/RELEASE_CHECKLIST.md` | Release öncesi kontrol listesi |
| `docs/ACCESSIBILITY_GUIDE.md` | Erişilebilirlik rehberi |

---

## ⚠️ BİLİNEN SORUNLAR VE ÇÖZÜMLER

### 1. Room KSP Serialization Hatası
**Sorun:** `kotlinx.serialization` sürüm çakışması  
**Çözüm:** `kotlinx-serialization-json:1.6.3` kullan veya Room 2.8.4'te kal

### 2. BOM (Byte Order Mark) Hatası
**Sorun:** Gradle dosyalarında UTF-8 BOM karakteri  
**Çözüm:** `fix-bom.ps1` script'ini çalıştır

### 3. KAPT Uyarıları
**Sorun:** Kotlin 2.0 ile KAPT uyumsuzluğu  
**Çözüm:** Hilt için KAPT, Room için KSP kullan (mevcut yapı)

---

**© 2025 HesapGunlugu - Finance Tracker**  
**Mimari:** Clean Architecture + Multi-Module + MVVM  
**Tech Stack:** Kotlin 2.0.21 + Jetpack Compose + Hilt + Room + WorkManager  
**Durum:** ✅ Production Ready

---

*Bu doküman, projenin tüm detaylarını içeren kapsamlı bir referans ve prompt olarak kullanılabilir. Herhangi bir AI asistanına bu bağlamı vererek projeyle ilgili sorular sorabilir veya geliştirme isteklerinde bulunabilirsiniz.*
