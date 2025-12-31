# 📁 PROJE DOSYA HİYERAŞİSİ VE AÇIKLAMALARI

**Proje:** HesapGunlugu (Finans Takip Uygulaması)  
**Tarih:** 25 Aralık 2024  
**Mimari:** Clean Architecture + Multi-Module

---

## 📂 ROOT DİZİN

```
HesapGunlugu/
├── 📄 .gitignore                          → Git ignore kuralları
├── 📄 build.gradle.kts                    → Root build script (plugin versiyonları)
├── 📄 settings.gradle.kts                 → Gradle modül ayarları (dependencyResolutionManagement)
├── 📄 gradle.properties                   → Gradle build özellikleri (JVM heap, Kotlin, AGP)
├── 📄 gradlew                             → Gradle wrapper script (Unix/Mac)
├── 📄 gradlew.bat                         → Gradle wrapper script (Windows)
├── 📄 local.properties                    → Local SDK path (git'e commit edilmez)
│
├── 📁 .github/                            → GitHub Actions CI/CD (gelecek için hazır)
├── 📁 .gradle/                            → Gradle cache ve temp dosyalar
├── 📁 .idea/                              → IntelliJ/Android Studio IDE ayarları
├── 📁 .kotlin/                            → Kotlin compiler cache
├── 📁 build/                              → Build output dosyaları
│
├── 📁 gradle/
│   ├── 📄 libs.versions.toml              → Version Catalog (tüm dependency versiyonları)
│   └── 📁 wrapper/
│       ├── 📄 gradle-wrapper.jar          → Gradle wrapper binary
│       └── 📄 gradle-wrapper.properties   → Gradle wrapper config
│
├── 📁 scripts/
│   ├── 📄 migrate-usecases.ps1            → Use case migration script
│   ├── 📄 pre-commit                      → Git pre-commit hook
│   ├── 📄 clean-and-build.ps1             → Cache temizleme ve build script
│   ├── 📄 clean-cache.bat                 → Gradle cache temizleme (Windows)
│   └── 📄 final-build.bat                 → Final build script
│
├── 📁 config/
│   └── 📁 detekt/
│       └── 📄 detekt.yml                  → Detekt static analysis kuralları
│
├── 📁 docs/
│   ├── 📄 ACCESSIBILITY_GUIDE.md          → Erişilebilirlik rehberi (TalkBack, semantics)
│   ├── 📄 APK_SIGNING_GUIDE.md            → APK imzalama ve keystore oluşturma
│   ├── 📄 BENCHMARK_GUIDE.md              → Performance benchmark kullanımı
│   ├── 📄 CODE_STYLE.md                   → Kod yazım standartları
│   ├── 📄 CONTRIBUTING.md                 → Katkıda bulunma rehberi
│   ├── 📄 DOCUMENTATION_STANDARDS.md      → Dokümantasyon standartları
│   ├── 📄 IMPROVEMENTS_SUMMARY.md         → Geliştirme özeti
│   ├── 📄 MIGRATION_SUMMARY.md            → Migration geçmişi
│   ├── 📄 MULTI_MODULE_GUIDE.md           → Multi-module mimari açıklaması
│   ├── 📄 PRODUCTION_READY_GUIDE.md       → Production deployment rehberi
│   ├── 📄 QUALITY_METRICS.md              → Kalite metrikleri ve hedefler
│   ├── 📄 SENIOR_TRANSFORMATION_SUMMARY.md → Senior-level dönüşüm raporu
│   └── 📁 adr/                            → Architecture Decision Records
│       ├── 📄 001-clean-architecture.md   → Clean Architecture kararı
│       ├── 📄 002-hilt-dependency-injection.md → Hilt DI kararı
│       ├── 📄 003-room-database.md        → Room Database kararı
│       ├── 📄 004-compose-ui.md           → Jetpack Compose UI kararı
│       ├── 📄 005-coroutines-flow.md      → Coroutines & Flow kararı
│       ├── 📄 006-no-firebase.md          → Firebase kullanmama kararı
│       └── 📄 README.md                   → ADR nedir açıklaması
│
├── 📄 README.md                           → Proje ana README dosyası
├── 📄 CHANGELOG.md                        → Versiyon değişiklik geçmişi
├── 📄 TODO.md                             → Yapılacaklar listesi
├── 📄 YAPILACAKLAR_LISTESI.md             → Detaylı yapılacaklar (Türkçe)
├── 📄 PROJECT_README.md                   → Proje detaylı açıklaması
├── 📄 PROJECT_FINAL_STATUS.md             → Son durum raporu
├── 📄 QUICK_SUMMARY.md                    → Hızlı özet
├── 📄 BASELINE_PROFILE_INFO.md            → Baseline Profile açıklaması
├── 📄 BUILD_FIXES_APPLIED.md              → Uygulanan build düzeltmeleri
├── 📄 BUILD_FIX_SUMMARY.md                → Build düzeltme özeti
├── 📄 BUILD_GUIDE.md                      → Build rehberi
├── 📄 DUPLICATE_BINDING_FIX.md            → Duplicate binding hatası çözümü
├── 📄 HOMEVIEWMODEL_FIX.md                → HomeViewModel düzeltmeleri
├── 📄 LAST_SESSION_CHANGES.md             → Son oturum değişiklikleri
├── 📄 PROBLEM_RESOLVED.md                 → Çözülen problemler
├── 📄 README_IMPROVEMENTS.md              → README geliştirmeleri
├── 📄 SENIOR_EVALUATION_REPORT.md         → Senior seviye değerlendirme
├── 📄 SENIOR_LEVEL_IMPROVEMENTS.md        → Senior seviye geliştirmeler
├── 📄 5_SAYFA_OZELLIK_LISTESI.md          → 5 ana sayfa özellik listesi
├── 📄 YENI_OZELLIKLER_RAPORU.md           → Yeni özellikler raporu
├── 📄 HOMESCREEN_CHECKLIST.md             → HomeScreen checklist
└── 📄 HOMESCREEN_DETAYLI_GELISTIRME.md    → HomeScreen detaylı geliştirme raporu
```

---

## 📱 APP MODÜLÜ

```
app/
├── 📄 build.gradle.kts                    → App modülü build script (dependencies, flavors, buildTypes)
├── 📄 proguard-rules.pro                  → ProGuard obfuscation kuralları
│
├── 📁 schemas/
│   └── 📁 com.hesapgunlugu.app.data.local.AppDatabase/
│       ├── 📄 1.json                      → Database schema v1 (export edilmiş)
│       ├── 📄 2.json                      → Database schema v2
│       └── 📄 3.json                      → Database schema v3
│
├── 📁 src/
│   ├── 📁 androidTest/
│   │   └── 📁 java/com/example/HesapGunlugu/
│   │       ├── 📁 data/
│   │       │   ├── 📁 dao/
│   │       │   │   ├── 📄 TransactionDaoTest.kt          → TransactionDao instrumented test
│   │       │   │   ├── 📄 CategoryDaoTest.kt             → CategoryDao instrumented test
│   │       │   │   └── 📄 ScheduledPaymentDaoTest.kt     → ScheduledPaymentDao test
│   │       │   └── 📁 repository/
│   │       │       ├── 📄 TransactionRepositoryTest.kt   → Repository instrumented test
│   │       │       └── 📄 SettingsManagerTest.kt         → SettingsManager test
│   │       ├── 📁 feature/
│   │       │   └── 📁 home/
│   │       │       ├── 📄 HomeScreenTest.kt              → HomeScreen UI test (Compose Test)
│   │       │       └── 📄 HomeScreenComposeTest.kt       → HomeScreen components test
│   │       └── 📄 ExampleInstrumentedTest.kt             → Örnek instrumented test
│   │
│   ├── 📁 test/
│   │   └── 📁 java/com/example/HesapGunlugu/
│   │       ├── 📁 data/
│   │       │   └── 📁 repository/
│   │       │       └── 📄 TransactionRepositoryImplTest.kt → Repository unit test
│   │       ├── 📁 domain/
│   │       │   └── 📁 usecase/
│   │       │       ├── 📄 AddTransactionUseCaseTest.kt    → Use case unit test
│   │       │       └── 📄 GetTransactionsUseCaseTest.kt   → Use case unit test
│   │       ├── 📁 snapshot/
│   │       │   └── 📄 ScreenshotTest.kt                   → Paparazzi snapshot test
│   │       └── 📄 ExampleUnitTest.kt                      → Örnek unit test
│   │
│   └── 📁 main/
│       ├── 📄 AndroidManifest.xml         → Ana manifest dosyası (permissions, activities, providers)
│       │
│       ├── 📁 res/
│       │   ├── 📁 drawable/               → Vector drawable'lar
│       │   │   ├── 📄 ic_launcher_background.xml          → Launcher arka plan
│       │   │   ├── 📄 ic_launcher_foreground.xml          → Launcher ön plan
│       │   │   ├── 📄 ic_home.xml                         → Home icon
│       │   │   ├── 📄 ic_statistics.xml                   → İstatistik icon
│       │   │   ├── 📄 ic_scheduled.xml                    → Planlı işlem icon
│       │   │   ├── 📄 ic_history.xml                      → Geçmiş icon
│       │   │   └── 📄 ic_settings.xml                     → Ayarlar icon
│       │   │
│       │   ├── 📁 mipmap-xxxhdpi/          → Launcher icons (farklı yoğunluklar)
│       │   ├── 📁 mipmap-xxhdpi/
│       │   ├── 📁 mipmap-xhdpi/
│       │   ├── 📁 mipmap-hdpi/
│       │   ├── 📁 mipmap-mdpi/
│       │   └── 📁 mipmap-anydpi-v26/
│       │       ├── 📄 ic_launcher.xml                     → Adaptive icon
│       │       └── 📄 ic_launcher_round.xml               → Round adaptive icon
│       │   │
│       │   ├── 📁 values/
│       │   │   ├── 📄 strings.xml                         → String kaynakları (Türkçe)
│       │   │   ├── 📄 colors.xml                          → Renk tanımlamaları
│       │   │   ├── 📄 themes.xml                          → Tema tanımlamaları
│       │   │   └── 📄 dimens.xml                          → Boyut tanımlamaları
│       │   │
│       │   ├── 📁 values-en/
│       │   │   └── 📄 strings.xml                         → String kaynakları (İngilizce)
│       │   │
│       │   ├── 📁 values-night/
│       │   │   └── 📄 themes.xml                          → Dark tema
│       │   │
│       │   └── 📁 xml/
│       │       ├── 📄 backup_rules.xml                    → Backup kuralları (Android 12+)
│       │       ├── 📄 data_extraction_rules.xml           → Data extraction kuralları
│       │       ├── 📄 file_paths.xml                      → FileProvider paths
│       │       └── 📄 shortcuts.xml                       → App shortcuts (gelecek için)
│       │
│       └── 📁 java/com/example/HesapGunlugu/
│           ├── 📄 MyApplication.kt        → Application class (Hilt, ACRA, LeakCanary)
│           ├── 📄 MainActivity.kt         → Ana Activity (Compose setContent, Navigation)
│           │
│           ├── 📁 di/                     → Dependency Injection (Hilt modules)
│           │   ├── 📄 AppModule.kt        → App-level dependencies (Context, StringProvider)
│           │   ├── 📄 CommonModule.kt     → Common utilities binding
│           │   ├── 📄 DatabaseModule.kt   → Room Database provision
│           │   ├── 📄 RepositoryModule.kt → Repository implementations binding
│           │   └── 📄 UseCaseModule.kt    → Use case provision
│           │
│           ├── 📁 core/                   → Core utilities (app içinde)
│           │   ├── 📁 backup/
│           │   │   ├── 📄 BackupManager.kt                → JSON export/import
│           │   │   ├── 📄 BackupViewModel.kt              → Backup ViewModel
│           │   │   └── 📄 BackupEvent.kt                  → Backup events
│           │   │
│           │   ├── 📁 security/
│           │   │   ├── 📄 BiometricManager.kt             → Biometric authentication
│           │   │   ├── 📄 EncryptionManager.kt            → AES-256 encryption
│           │   │   ├── 📄 PinManager.kt                   → PIN lock manager
│           │   │   ├── 📄 SecurityViewModel.kt            → Security ViewModel
│           │   │   └── 📄 SecurityState.kt                → Security state
│           │   │
│           │   ├── 📁 ui/
│           │   │   └── 📁 accessibility/
│           │   │       └── 📄 AccessibilityExtensions.kt  → Accessibility extension fonksiyonlar
│           │   │
│           │   └── 📁 utils/
│           │       ├── 📄 DateUtils.kt                    → Tarih formatları
│           │       ├── 📄 CurrencyUtils.kt                → Para birimi formatları
│           │       └── 📄 ExceptionHandler.kt             → Global exception handler
│           │
│           ├── 📁 domain/                 → Domain katmanı (app içinde - legacy)
│           │   └── 📁 model/
│           │       ├── 📄 Transaction.kt                  → Transaction entity (legacy)
│           │       └── 📄 Category.kt                     → Category entity (legacy)
│           │
│           ├── 📁 widget/                 → Home Screen Widget
│           │   ├── 📄 BalanceWidget.kt                    → Widget provider
│           │   ├── 📄 BalanceWidgetReceiver.kt            → Widget update receiver
│           │   └── 📄 BalanceGlanceWidget.kt              → Glance widget (modern)
│           │
│           ├── 📁 worker/                 → Background Workers
│           │   ├── 📄 ScheduledPaymentWorker.kt           → Planlı işlem hatırlatıcı
│           │   └── 📄 BudgetAlertWorker.kt                → Bütçe uyarı worker
│           │
│           └── 📁 feature/               → Feature modülleri (app içinde)
│               │
│               ├── 📁 common/
│               │   ├── 📁 components/    → Paylaşılan Compose components
│               │   │   ├── 📄 AddTransactionForm.kt       → İşlem ekleme formu
│               │   │   ├── 📄 AddScheduledForm.kt         → Planlı işlem formu
│               │   │   ├── 📄 AdvancedDashboardCard.kt    → Gelişmiş dashboard kartı
│               │   │   ├── 📄 BalanceCard.kt              → Bakiye kartı
│               │   │   ├── 📄 CategoryBudgetCard.kt       → Kategori bütçe kartı
│               │   │   ├── 📄 CategoryPicker.kt           → Kategori seçici
│               │   │   ├── 📄 DashboardCard.kt            → Dashboard kartı
│               │   │   ├── 📄 ErrorCard.kt                → Hata kartı
│               │   │   ├── 📄 ExpensePieChart.kt          → Harcama pie chart
│               │   │   ├── 📄 FinancialInsightsCards.kt   → Finansal analiz kartları (YENİ)
│               │   │   ├── 📄 HomeHeader.kt               → Ana sayfa başlık
│               │   │   ├── 📄 QuickActionsRow.kt          → Hızlı işlemler satırı
│               │   │   ├── 📄 ShimmerLoadingList.kt       → Skeleton loading
│               │   │   ├── 📄 SpendingLimitCard.kt        → Harcama limiti kartı
│               │   │   ├── 📄 TransactionItem.kt          → İşlem liste item'ı
│               │   │   ├── 📄 CategoryBudgetDialog.kt     → Bütçe dialog'u
│               │   │   └── 📄 EditBudgetDialog.kt         → Bütçe düzenleme dialog'u
│               │   │
│               │   └── 📁 navigation/
│               │       ├── 📄 Screen.kt                   → Navigation route tanımları
│               │       ├── 📄 NavGraph.kt                 → Navigation graph
│               │       └── 📄 BottomNavBar.kt             → Bottom navigation bar
│               │
│               ├── 📁 home/              → Ana Sayfa
│               │   ├── 📄 HomeScreen.kt                   → Home Screen UI
│               │   ├── 📄 HomeViewModel.kt                → Home ViewModel (business logic)
│               │   └── 📄 HomeState.kt                    → Home UI state
│               │
│               ├── 📁 statistics/        → İstatistikler Sayfası
│               │   ├── 📄 StatisticsScreen.kt             → İstatistik ekranı
│               │   ├── 📄 StatisticsViewModel.kt          → İstatistik ViewModel
│               │   ├── 📄 StatisticsState.kt              → İstatistik state
│               │   └── 📁 components/
│               │       ├── 📄 CategoryBreakdownCard.kt    → Kategori dağılımı
│               │       ├── 📄 IncomeExpenseBarChart.kt    → Gelir-gider bar chart
│               │       ├── 📄 IncomeExpenseSummaryCards.kt → Özet kartları
│               │       ├── 📄 PeriodSelector.kt           → Dönem seçici
│               │       ├── 📄 StatisticsHeader.kt         → Header
│               │       ├── 📄 TrendAnalysisCard.kt        → Trend analizi
│               │       └── 📄 EmptyStatisticsCard.kt      → Boş durum
│               │
│               ├── 📁 scheduled/         → Planlı İşlemler Sayfası
│               │   ├── 📄 ScheduledScreen.kt              → Planlı işlem ekranı
│               │   ├── 📄 ScheduledViewModel.kt           → ViewModel
│               │   ├── 📄 ScheduledState.kt               → State
│               │   ├── 📄 UiEvent.kt                      → UI events
│               │   └── 📁 components/
│               │       ├── 📄 EmptyStateCard.kt           → Boş durum kartı
│               │       ├── 📄 RecurringItem.kt            → Tekrarlayan işlem item
│               │       ├── 📄 ScheduledHeader.kt          → Header
│               │       ├── 📄 ScheduledPaymentItem.kt     → Planlı ödeme item
│               │       ├── 📄 ScheduledSummaryCards.kt    → Özet kartları
│               │       └── 📄 SectionTitle.kt             → Bölüm başlığı
│               │
│               ├── 📁 history/           → Geçmiş/Takvim Sayfası
│               │   ├── 📄 HistoryScreen.kt                → Geçmiş ekranı
│               │   ├── 📄 HistoryViewModel.kt             → ViewModel
│               │   ├── 📄 HistoryState.kt                 → State
│               │   └── 📁 components/
│               │       ├── 📄 CalendarView.kt             → Takvim görünümü
│               │       ├── 📄 FilterChips.kt              → Filtre chip'leri
│               │       ├── 📄 HistoryHeader.kt            → Header
│               │       └── 📄 TransactionList.kt          → İşlem listesi
│               │
│               ├── 📁 settings/          → Ayarlar Sayfası
│               │   ├── 📄 SettingsScreen.kt               → Ayarlar ekranı
│               │   ├── 📄 SettingsViewModel.kt            → Settings ViewModel
│               │   ├── 📄 SettingsState.kt                → Settings state
│               │   ├── 📄 ThemeViewModel.kt               → Theme ViewModel
│               │   └── 📁 components/
│               │       ├── 📄 SettingsHeader.kt           → Header
│               │       ├── 📄 SettingSection.kt           → Ayar bölümü
│               │       ├── 📄 SettingItem.kt              → Ayar item
│               │       └── 📄 SettingSwitch.kt            → Switch item
│               │
│               ├── 📁 notifications/     → Bildirim Merkezi
│               │   ├── 📄 NotificationCenterScreen.kt     → Bildirim ekranı
│               │   └── 📄 NotificationItem.kt             → Bildirim item
│               │
│               ├── 📁 onboarding/        → İlk Açılış
│               │   ├── 📄 OnboardingScreen.kt             → Onboarding ekranı
│               │   ├── 📄 OnboardingViewModel.kt          → ViewModel
│               │   └── 📁 components/
│               │       └── 📄 OnboardingPage.kt           → Onboarding sayfa
│               │
│               └── 📁 privacy/           → Gizlilik
│                   ├── 📄 PrivacyPolicyScreen.kt          → Gizlilik politikası
│                   └── 📄 TermsScreen.kt                  → Kullanım şartları
```

---

## 🏗️ CORE MODÜLLERI

### 📦 core/common

```
core/common/
├── 📄 build.gradle.kts                    → Common modül build script
│
└── 📁 src/main/java/com/example/HesapGunlugu/core/common/
    ├── 📄 Constants.kt                    → Uygulama sabitleri
    ├── 📄 StringProvider.kt               → String kaynaklarına erişim interface
    ├── 📄 StringProviderImpl.kt           → StringProvider implementasyonu
    ├── 📄 NotificationHelper.kt           → Bildirim yardımcısı
    └── 📄 Result.kt                       → Result wrapper (Success/Error)
```

### 📦 core/data

```
core/data/
├── 📄 build.gradle.kts                    → Data modül build script
│
└── 📁 src/main/java/com/example/HesapGunlugu/core/data/
    ├── 📁 local/
    │   ├── 📄 AppDatabase.kt              → Room Database tanımı (Transaction, Category, ScheduledPayment)
    │   ├── 📄 Converters.kt               → Type converters (Date, List<String>)
    │   ├── 📄 DatabaseMigrations.kt       → Database migration stratejileri
    │   ├── 📄 SettingsManager.kt          → DataStore-based ayarlar yönetimi
    │   ├── 📄 EncryptedSettingsManager.kt → Şifreli ayarlar yönetimi
    │   │
    │   └── 📁 dao/
    │       ├── 📄 TransactionDao.kt       → Transaction CRUD operasyonları
    │       ├── 📄 CategoryDao.kt          → Category CRUD
    │       └── 📄 ScheduledPaymentDao.kt  → ScheduledPayment CRUD
    │
    ├── 📁 repository/
    │   ├── 📄 TransactionRepositoryImpl.kt → Transaction repository implementasyonu
    │   ├── 📄 CategoryRepositoryImpl.kt    → Category repository impl
    │   └── 📄 ScheduledPaymentRepositoryImpl.kt → ScheduledPayment repository impl
    │
    └── 📁 paging/
        └── 📄 TransactionPagingSource.kt  → Paging 3 data source
```

### 📦 core/domain

```
core/domain/
├── 📄 build.gradle.kts                    → Domain modül build script
│
└── 📁 src/main/java/com/example/HesapGunlugu/core/domain/
    ├── 📁 model/
    │   ├── 📄 Transaction.kt              → Transaction domain model
    │   ├── 📄 Category.kt                 → Category domain model
    │   ├── 📄 ScheduledPayment.kt         → ScheduledPayment domain model
    │   ├── 📄 CategoryBudgetStatus.kt     → Kategori bütçe durumu
    │   ├── 📄 TransactionType.kt          → Enum: INCOME, EXPENSE
    │   ├── 📄 RecurrenceType.kt           → Enum: DAILY, WEEKLY, MONTHLY, YEARLY
    │   └── 📄 TransactionException.kt     → Domain exceptions
    │
    ├── 📁 repository/
    │   ├── 📄 TransactionRepository.kt    → Transaction repository interface
    │   ├── 📄 CategoryRepository.kt       → Category repository interface
    │   └── 📄 ScheduledPaymentRepository.kt → ScheduledPayment repository interface
    │
    └── 📁 usecase/
        ├── 📁 transaction/
        │   ├── 📄 GetTransactionsUseCase.kt       → İşlemleri getir
        │   ├── 📄 AddTransactionUseCase.kt        → İşlem ekle
        │   ├── 📄 UpdateTransactionUseCase.kt     → İşlem güncelle
        │   ├── 📄 DeleteTransactionUseCase.kt     → İşlem sil
        │   └── 📄 GetTransactionByIdUseCase.kt    → ID'ye göre getir
        │
        ├── 📁 category/
        │   ├── 📄 GetCategoriesUseCase.kt         → Kategorileri getir
        │   └── 📄 AddCategoryUseCase.kt           → Kategori ekle
        │
        └── 📁 scheduled/
            ├── 📄 GetScheduledPaymentsUseCase.kt  → Planlı işlemleri getir
            ├── 📄 AddScheduledPaymentUseCase.kt   → Planlı işlem ekle
            ├── 📄 DeleteScheduledPaymentUseCase.kt → Planlı işlem sil
            └── 📄 MarkPaymentAsPaidUseCase.kt     → Ödendi olarak işaretle
```

### 📦 core/ui

```
core/ui/
├── 📄 build.gradle.kts                    → UI modül build script
│
└── 📁 src/main/java/com/example/HesapGunlugu/core/ui/
    ├── 📁 theme/
    │   ├── 📄 Color.kt                    → Renk tanımlamaları
    │   ├── 📄 Theme.kt                    → Material 3 tema
    │   ├── 📄 Type.kt                     → Typography tanımları
    │   └── 📄 Shape.kt                    → Shape tanımları
    │
    ├── 📁 components/
    │   ├── 📄 ErrorBoundary.kt            → Error handling wrapper
    │   ├── 📄 LoadingIndicator.kt         → Loading göstergesi
    │   └── 📄 EmptyState.kt               → Boş durum component
    │
    └── 📁 accessibility/
        └── 📄 AccessibilityExtensions.kt  → Accessibility modifiers
```

### 📦 core/navigation

```
core/navigation/
├── 📄 build.gradle.kts                    → Navigation modül build script
├── 📄 proguard-rules.pro                  → ProGuard kuralları
│
└── 📁 src/main/java/com/example/HesapGunlugu/core/navigation/
    ├── 📄 Navigator.kt                    → Navigation interface
    └── 📄 NavigationExtensions.kt         → Navigation extension'lar
```

---

## 🎯 FEATURE MODÜLLERI

### 📦 feature/home

```
feature/home/
├── 📄 build.gradle.kts                    → Home modül build script
├── 📄 proguard-rules.pro                  → ProGuard kuralları
│
└── 📁 src/main/java/com/example/HesapGunlugu/feature/home/
    ├── 📄 HomeScreen.kt                   → Home Screen UI (modül versiyonu)
    ├── 📄 HomeViewModel.kt                → Home ViewModel (modül versiyonu)
    ├── 📄 HomeState.kt                    → Home State (modül versiyonu)
    │
    └── 📁 (feature/common/components kopyası için hazır)
        └── 📄 FinancialInsightsCards.kt   → Finansal analiz kartları
```

---

## 🔬 BENCHMARK & PROFILING

### 📦 baselineprofile

```
baselineprofile/
├── 📄 build.gradle.kts                    → Baseline profile build script
│
└── 📁 src/main/
    └── 📄 BaselineProfileGenerator.kt     → Baseline profile generator (startup optimization)
```

### 📦 benchmark-macro

```
benchmark-macro/
├── 📄 build.gradle.kts                    → Benchmark build script
│
└── 📁 src/main/
    ├── 📄 StartupBenchmark.kt             → Uygulama başlatma benchmark
    ├── 📄 ScrollBenchmark.kt              → Scroll performance benchmark
    └── 📄 NavigationBenchmark.kt          → Navigation performance benchmark
```

---

## 📊 TOPLAM DOSYA İSTATİSTİKLERİ

```
📁 Toplam Modül Sayısı: 11
   ├── app (ana modül)
   ├── core/common
   ├── core/data
   ├── core/domain
   ├── core/ui
   ├── core/navigation
   ├── feature/home
   ├── baselineprofile
   └── benchmark-macro

📄 Toplam Kotlin Dosyası: ~150+
📄 Toplam Test Dosyası: ~25+
📄 Toplam Dokümantasyon: ~30+
📄 Toplam XML Dosyası: ~40+

📊 Toplam Satır Sayısı: ~15,000+ satır kod
```

---

## 🎯 DOSYA AMAÇLARI ÖZETİ

### Mimari Katmanlar

**Presentation Layer (UI):**
- `*Screen.kt` → Compose UI ekranları
- `*ViewModel.kt` → Business logic ve state yönetimi
- `*State.kt` → UI state tanımları
- `*Event.kt` → UI event tanımları
- `components/` → Reusable Compose components

**Domain Layer (Business Logic):**
- `model/` → Domain entities (business objects)
- `repository/` → Repository interfaces
- `usecase/` → Business use cases (tek sorumluluk)

**Data Layer (Data Access):**
- `local/` → Room Database, DataStore
- `dao/` → Data Access Objects
- `repository/` → Repository implementations
- `paging/` → Paging sources

**DI Layer:**
- `di/` → Hilt modules (dependency injection)

**Infrastructure:**
- `worker/` → Background tasks (WorkManager)
- `widget/` → Home screen widgets
- `backup/` → Export/Import functionality
- `security/` → Encryption, authentication

---

## 📝 ÖZEL DOSYA TÜRLER ACIKLAMASı

- **build.gradle.kts** → Gradle build yapılandırması (Kotlin DSL)
- **proguard-rules.pro** → Code obfuscation ve shrinking kuralları
- **AndroidManifest.xml** → App manifest (permissions, components)
- **libs.versions.toml** → Centralized dependency versioning (Version Catalog)
- **.kt** → Kotlin source code
- **.xml** → XML resources (layouts, strings, colors, themes)
- **.json** → Database schemas, configuration files
- **.md** → Markdown documentation
- **.yml** → YAML configuration (detekt, CI/CD)
- **.properties** → Properties files (gradle, local)
- **.pro** → ProGuard rules
- **.jar** → Java Archive (Gradle wrapper)
- **.ps1** → PowerShell scripts
- **.bat** → Batch scripts (Windows)

---

**Son Güncelleme:** 25 Aralık 2024  
**Toplam Dosya:** 300+  
**Mimari:** Clean Architecture + Multi-Module  
**UI Framework:** Jetpack Compose  
**DI Framework:** Hilt  
**Database:** Room + DataStore  
**Async:** Coroutines + Flow

✅ **Proje tam dokümante edildi!**

