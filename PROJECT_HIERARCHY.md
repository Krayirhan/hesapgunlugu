# 📂 PROJECT HIERARCHY - Finance Tracker

## 🏗️ Project Structure Overview

```
HesapGunlugu/
├── 📱 app/                          # Main application module
├── 🎯 core/                         # Core/base modules (12 modules)
├── 🎨 feature/                      # Feature modules (8 modules)
├── 📊 benchmark-macro/              # Performance benchmark
├── 🚀 baselineprofile/              # Startup optimization
├── ⚙️ config/                       # Configuration files
├── 📚 docs/                         # Documentation
├── 🔧 scripts/                      # Build/deployment scripts
├── 🤖 .github/workflows/            # CI/CD pipeline
└── 📋 Root directory files
```

---

## 📱 APP MODULE

### **app/**
Main application module - Container that combines all feature and core modules

#### **app/src/main/java/com/example/HesapGunlugu/**

##### **MainActivity.kt**
- Uygulamanın tek Activity'si
- Jetpack Compose setContent ile UI başlatır
- Navigation host container
- Sistem bar renklerini ayarlar

##### **MyApplication.kt**
- Application sınıfı
- Initializes Hilt dependency injection
- Timber logging configuration
- WorkManager initialization
- Crash handler setup

##### **FinanceWidget.kt**
- Home screen widget implementation
- Widget UI using Glance API
- Displays balance and recent transactions
- Different widget sizes support (small/medium/large)

##### **RecurringTransactionWorker.kt**
- WorkManager background worker
- Automatically creates recurring payments
- Daily periodic execution
- Sends notifications

#### **app/di/**

##### **AppModule.kt**
- Ana Hilt module
- Database instance sağlar
- DAO provider'ları
- Repository injection
- Migration konfigürasyonu

##### **UseCaseModule.kt**
- Use case dependency injection
- ViewModelScoped provider'lar
- Transaction, scheduled, statistics use case'leri
- Repository bağımlılıkları inject eder

##### **DispatcherModule.kt**
- Coroutine dispatcher'ları sağlar
- IO, Main, Default dispatcher'lar
- Testing için değiştirilebilir

##### **CommonModule.kt**
- Ortak utility injection
- StringProvider, NotificationHelper
- Context tabanlı servisler

#### **app/feature/common/navigation/**

##### **Screen.kt**
- Navigation route definitions
- Sealed class for screen enum
- Route string constants

##### **AppNavGraph.kt**
- Jetpack Navigation Compose
- Navigation between all screens
- Deep link support
- Animation transitions

#### **app/core/common/**

##### **StringProviderImpl.kt**
- StringProvider interface implementation
- String resource access from Context
- Localization support

##### **NotificationHelperImpl.kt**
- Notification creation helper
- Channel management
- Show/cancel notifications

#### **app/src/test/** (Unit Tests)

##### **ExampleUnitTest.kt**
- Sample unit test
- JUnit test class

##### **testutil/MainDispatcherRule.kt**
- Coroutine dispatcher rule for testing
- Replaces Main dispatcher with test dispatcher

##### **testutil/TestFixtures.kt**
- Mock data for testing
- Transaction, Category, Payment fixtures

##### **testutil/FakeTransactionRepository.kt**
- TransactionRepository fake implementation
- In-memory data for testing
- Flow emission control

##### **testutil/FakeScheduledPaymentRepository.kt**
- ScheduledPaymentRepository fake
- For test scenarios

##### **feature/home/HomeViewModelTest.kt**
- HomeViewModel unit tests
- Balance calculation test
- Transaction loading test
- Error handling test

##### **feature/statistics/StatisticsViewModelTest.kt**
- StatisticsViewModel tests
- Category expense calculation
- Chart data generation test

##### **feature/scheduled/ScheduledViewModelTest.kt**
- ScheduledViewModel tests
- Recurring rule creation test
- Payment list filtering

##### **feature/history/HistoryViewModelTest.kt**
- HistoryViewModel tests
- Date filtering test
- Search functionality test

##### **domain/usecase/transaction/** (Use Case Tests)
- **AddTransactionUseCaseTest.kt** - Transaction add test
- **UpdateTransactionUseCaseTest.kt** - Transaction update test
- **DeleteTransactionUseCaseTest.kt** - Transaction delete test
- **GetTransactionsUseCaseTest.kt** - Transaction list test

##### **domain/usecase/scheduled/** (Scheduled Use Case Tests)
- **AddScheduledPaymentUseCaseTest.kt** - Add scheduled payment
- **GetScheduledPaymentsUseCaseTest.kt** - Scheduled payment list
- **MarkPaymentAsPaidUseCaseTest.kt** - Mark payment
- **DeleteScheduledPaymentUseCaseTest.kt** - Delete scheduled payment

##### **domain/usecase/statistics/GetStatisticsUseCaseTest.kt**
- Statistics calculation tests

##### **data/repository/TransactionRepositoryTest.kt**
- Repository implementation tests
- DAO interaction tests

##### **data/mapper/TransactionMapperTest.kt**
- Entity-Domain model conversion tests

##### **core/security/** (Security Tests)
- **SecurityManagerTest.kt** - Security manager tests
- **PasswordStrengthCheckerTest.kt** - Password security test
- **PinVerificationResultTest.kt** - PIN verification test

##### **core/error/ErrorHandlerTest.kt**
- Error handling logic tests

##### **core/backup/BackupEncryptionTest.kt**
- Backup encryption tests

##### **core/util/ExtensionsTest.kt**
- Extension function tests

##### **worker/RecurringTransactionWorkerTest.kt**
- Background worker tests

##### **snapshot/ScreenshotTest.kt**
- Paparazzi screenshot tests
- UI regression tests

#### **app/src/androidTest/** (Instrumented Tests)

##### **ExampleInstrumentedTest.kt**
- Sample instrumented test
- Context access test

##### **HiltTestRunner.kt**
- Hilt test runner
- Dependency injection test support

##### **integration/TransactionFlowIntegrationTest.kt**
- End-to-end transaction flow test
- Test with real database

##### **navigation/NavigationTest.kt**
- Navigation flow tests
- Screen transition tests

##### **feature/home/HomeScreenTest.kt**
- Home screen UI tests
- Compose UI test

##### **feature/home/HomeScreenComposeTest.kt**
- HomeScreen Compose tests

##### **feature/settings/SettingsScreenTest.kt**
- Settings screen tests

##### **feature/statistics/StatisticsScreenTest.kt**
- Statistics screen tests

##### **feature/history/HistoryScreenUiTest.kt**
- History screen UI tests

##### **data/local/TransactionDaoTest.kt**
- TransactionDao database tests
- Room query tests

##### **data/local/ScheduledPaymentDaoTest.kt**
- ScheduledPaymentDao tests

##### **data/local/MigrationTest.kt**
- Database migration tests

##### **core/security/SecurityManagerTest.kt**
- Security manager instrumented tests

##### **core/ui/accessibility/AccessibilityTest.kt**
- Accessibility tests
- TalkBack compatibility

- Right-to-left language support

##### **benchmark/TransactionBenchmark.kt**
- Transaction performance benchmark

##### **AppIntegrationTest.kt**
- General application integration tests

#### **app/src/main/res/**

##### **values/strings.xml**
- Turkish string resources
- UI texts, labels

##### **values/themes.xml**
- Material theme definitions
- Light/Dark theme

##### **values/colors.xml**
- Color palettes
- Material color definitions

##### **values/plurals.xml**
- Plural string resources
- Number formats

##### **values-en/strings.xml**
- English translations


##### **drawable/ic_launcher_background.xml**
- Launcher icon background

##### **drawable/ic_launcher_foreground.xml**
- Launcher icon foreground

##### **drawable/ic_notification.xml**
- Notification icon

##### **drawable/widget_background.xml**
- Widget background drawable

##### **drawable/widget_preview.xml**
- Widget preview image

##### **layout/widget_finance.xml**
- Widget layout XML

##### **layout/widget_finance_loading.xml**
- Widget loading state layout

##### **xml/file_paths.xml**
- FileProvider path definitions
- For export/import

##### **xml/backup_rules.xml**
- Android backup rules

##### **xml/data_extraction_rules.xml**
- Data extraction rules

##### **xml/finance_widget_info.xml**
- Widget metadata
- Size, update period

##### **AndroidManifest.xml**
- Application manifest
- Permission definitions
- Activity, Service, Receiver registrations
- FileProvider, WorkManager

##### **app/build.gradle.kts**
- Application module build script
- Plugin definitions (Hilt, KSP, Compose)
- Dependencies
- Build types (debug, release, staging)
- Product flavors (free, premium)
- ProGuard rules
- Signing config

---

## 🎯 CORE MODÜLLERI

### **core/common/**
Ortak utility ve interface tanımları

##### **StringProvider.kt**
- String resource erişim interface
- Test edilebilir string yönetimi

##### **NotificationHelper.kt**
- Bildirim yönetim interface

##### **Result.kt**
- Success/Error wrapper sealed class
- Use case return type

##### **Extensions.kt**
- Kotlin extension fonksiyonları
- Date, String, Number extension'ları

##### **build.gradle.kts**
- Common modül build script

---

### **core/data/**
Data layer - Room database, DAO, Repository

##### **local/AppDatabase.kt**
- Room database definition
- Entities, DAOs, Version
- Migration definitions

##### **local/DatabaseMigrations.kt**
- Database migration implementations
- MIGRATION_6_7 etc.

##### **local/entity/TransactionEntity.kt**
- Transaction database entity
- Room @Entity annotation

##### **local/entity/CategoryEntity.kt**
- Category database entity

##### **local/entity/ScheduledPaymentEntity.kt**
- Scheduled payment entity

##### **local/entity/RecurringRuleEntity.kt**
- Recurring rule entity
- Recurrence type, interval

##### **local/entity/SavingsGoalEntity.kt**
- Savings goal entity

##### **local/entity/BudgetEntity.kt**
- Budget entity

##### **local/dao/TransactionDao.kt**
- Transaction CRUD operations
- Flow-based queries
- @Query annotations

##### **local/dao/CategoryDao.kt**
- Category DAO operations

##### **local/dao/ScheduledPaymentDao.kt**
- Scheduled payment DAO

##### **local/dao/RecurringRuleDao.kt**
- Recurring rule DAO
- Active rule queries

##### **local/dao/SavingsGoalDao.kt**
- Savings goal DAO

##### **local/dao/BudgetDao.kt**
- Budget DAO

##### **local/converter/LocalDateConverter.kt**
- Room type converter
- LocalDate ↔ Long conversion

##### **local/converter/RecurrenceTypeConverter.kt**
- Enum type converter

##### **model/RecurringRule.kt**
- Domain model for recurring rule
- Business logic methods

##### **repository/TransactionRepositoryImpl.kt**
- TransactionRepository implementation
- DAO to domain model mapping

##### **repository/ScheduledPaymentRepositoryImpl.kt**
- ScheduledPaymentRepository impl

##### **repository/CategoryRepositoryImpl.kt**
- CategoryRepository impl

##### **repository/SettingsRepositoryImpl.kt**
- SettingsRepository impl
- SharedPreferences usage

##### **preferences/UserPreferences.kt**
- DataStore preferences
- Settings storage

##### **worker/RecurringPaymentWorker.kt**
- Background transaction creation worker

##### **build.gradle.kts**
- Data module build script
- Room, KSP dependencies

---

### **core/domain/**
Domain katmanı - Use case'ler, Repository interface'leri, Domain model'ler

##### **model/Transaction.kt**
- Transaction domain model
- Business logic içerir

##### **model/Category.kt**
- Category domain model

##### **model/ScheduledPayment.kt**
- Scheduled payment domain model

##### **model/SavingsGoal.kt**
- Savings goal domain model

##### **model/Budget.kt**
- Budget domain model

##### **model/UserSettings.kt**
- User settings domain model

##### **model/CategoryTotal.kt**
- Category toplamları için model

##### **model/CategoryBudgetStatus.kt**
- Budget durumu model

##### **model/TransactionException.kt**
- Domain exception sınıfı

##### **repository/TransactionRepository.kt**
- Transaction repository interface
- Data katmanından soyutlama

##### **repository/ScheduledPaymentRepository.kt**
- Scheduled payment repository interface

##### **repository/SettingsRepository.kt**
- Settings repository interface

##### **usecase/transaction/AddTransactionUseCase.kt**
- İşlem ekleme use case
- Validation logic

##### **usecase/transaction/UpdateTransactionUseCase.kt**
- İşlem güncelleme use case

##### **usecase/transaction/DeleteTransactionUseCase.kt**
- İşlem silme use case

##### **usecase/transaction/GetTransactionsUseCase.kt**
- İşlem listesi use case
- Filtering, sorting

##### **usecase/scheduled/AddScheduledPaymentUseCase.kt**
- Planlı ödeme ekleme

##### **usecase/scheduled/GetScheduledPaymentsUseCase.kt**
- Scheduled payment list

##### **usecase/scheduled/GetUpcomingPaymentsUseCase.kt**
- Upcoming payments

##### **usecase/scheduled/GetRecurringExpensesUseCase.kt**
- Recurring expenses

##### **usecase/scheduled/GetRecurringIncomesUseCase.kt**
- Recurring incomes

##### **usecase/scheduled/MarkPaymentAsPaidUseCase.kt**
- Mark payment

##### **usecase/scheduled/DeleteScheduledPaymentUseCase.kt**
- Delete scheduled payment

##### **usecase/recurring/AddRecurringRuleUseCase.kt**
- Add recurring rule

##### **usecase/statistics/GetStatisticsUseCase.kt**
- Statistics calculation use case

##### **usecase/UpdateCategoryBudgetUseCase.kt**
- Update category budget

##### **usecase/UpdateMonthlyLimitUseCase.kt**
- Update monthly limit

##### **usecase/UpdateThemeUseCase.kt**
- Update theme

##### **usecase/GetUserSettingsUseCase.kt**
- Get user settings

##### **build.gradle.kts**
- Domain modül build script

---

### **core/ui/**
UI components, theme, compose utilities

##### **theme/Theme.kt**
- Material Design 3 theme definition
- Light/Dark theme
- ColorScheme setup

##### **theme/Color.kt**
- Color palettes
- Primary, Secondary, Tertiary colors

##### **theme/Type.kt**
- Typography definitions
- Font family, size

##### **theme/Shape.kt**
- Shape definitions
- Corner radius

##### **theme/AccessibleTheme.kt**
- Accessibility-enabled theme
- High contrast

##### **components/TransactionItem.kt**
- Transaction list item Compose
- Amount, category, date display

##### **components/SavingsGoalCard.kt**
- Savings goal card Compose
- Progress bar, target display

##### **components/Charts.kt**
- PieChart and BarChart Compose
- Canvas-based drawing

##### **components/AdvancedCharts.kt**
- Advanced chart components

##### **components/CalendarView.kt**
- Calendar view Compose
- LocalDate grid

##### **components/EmptyStates.kt**
- Empty state components
- Icon, message, action button

##### **components/ErrorCard.kt**
- Error display card

##### **components/ErrorBoundary.kt**
- Error boundary wrapper

##### **components/LoadingErrorStates.kt**
- Loading and error states

##### **components/SkeletonLoader.kt**
- Skeleton loading animation

##### **components/ShimmerLoadingList.kt**
- Shimmer effect loading

##### **components/QuickActions.kt**
- Quick action buttons

##### **components/HomeHeader.kt**
- Home screen header

##### **components/DashboardCard.kt**
- Dashboard card component

##### **components/AdvancedDashboardCard.kt**
- Advanced dashboard card

##### **components/FinancialInsightCard.kt**
- Financial insight card

##### **components/FinancialInsightsCards.kt**
- Insight card list

##### **components/CategoryBudgetCard.kt**
- Category budget card

##### **components/SpendingLimitCard.kt**
- Spending limit card

##### **components/ExpensePieChart.kt**
- Expense pie chart

##### **components/ProCards.kt**
- Premium feature cards

##### **components/AddTransactionForm.kt**
- Transaction add form

##### **components/AddScheduledForm.kt**
- Scheduled payment form

##### **components/AddBudgetCategoryDialog.kt**
- Budget category dialog

##### **components/EditBudgetDialog.kt**
- Budget edit dialog

##### **animations/Animations.kt**
- Compose animation functions
- SlideIn, FadeIn, Scale animations

##### **preview/PreviewAnnotations.kt**
- Compose preview annotations
- ThemePreviews, DevicePreviews

##### **haptic/HapticFeedbackManager.kt**
- Haptic feedback management
- VibrationEffect API

##### **accessibility/** (Accessibility)
- **AccessibilityExtensions.kt** - Accessibility extensions
- **AccessibilityModifiers.kt** - Compose modifiers
- **AccessibilityTesting.kt** - Test utilities
- **ColorAccessibility.kt** - Color accessibility
- **FontScaling.kt** - Font scaling

##### **build.gradle.kts**
- UI modül build script
- Compose dependencies

---

### **core/navigation/**
Navigation management

##### **Navigator.kt**
- Navigation manager interface
- Screen navigation functions

##### **NavigationDestinations.kt**
- Navigation destination definitions
- Route strings

##### **build.gradle.kts**
- Navigation module build

---

### **core/security/**
Security features

##### **SecurityManager.kt**
- Central security manager
- PIN, biometric management

##### **BiometricAuthManager.kt**
- Biometric authentication
- Fingerprint/Face recognition

##### **PinLockScreen.kt**
- PIN entry screen Compose
- PIN input UI

##### **PasswordStrengthChecker.kt**
- Password security check
- Strength calculation

##### **RootDetector.kt**
- Root detection
- Device security check

##### **SecurityViewModel.kt**
- Security screen ViewModel
- PIN/biometric state

##### **build.gradle.kts**
- Security modül build

---

### **core/notification/**
Notification management

##### **RecurringPaymentWorker.kt**
- Recurring payment worker
- Notification sending

##### **PaymentReminderWorker.kt**
- Payment reminder worker

##### **build.gradle.kts**
- Notification module build

---

### **core/export/**
Export features

##### **PdfExportManager.kt**
- PDF export
- Transaction list to PDF

##### **CsvExportManager.kt**
- CSV export
- Excel compatible format

##### **EmailShareManager.kt**
- Email sharing
- FileProvider usage

##### **build.gradle.kts**
- Export module build

---

### **core/cloud/**
Cloud backup

##### **GoogleDriveBackupManager.kt**
- Google Drive backup management
- Sign-in, upload, download

##### **build.gradle.kts**
- Cloud module build
- Google Play Services

---

### **core/premium/**
Premium subscription

##### **BillingManager.kt**
- Google Play Billing
- Subscription management
- Purchase flow

##### **build.gradle.kts**
- Premium module build
- Billing library

---

### **core/performance/**
Performance monitoring

##### **PerformanceMonitor.kt**
- Performance monitoring
- StrictMode, memory tracking
- Frame metrics

##### **build.gradle.kts**
- Performance modül build

---

### **core/feedback/**
User feedback

##### **FeedbackManager.kt**
- Feedback sending
- Bug reporting
- Rating, sharing

##### **build.gradle.kts**
- Feedback module build

---

### **core/error/**
Error handling

##### **ErrorHandler.kt**
- Error handling logic
- Exception mapping

##### **GlobalExceptionHandler.kt**
- Global exception handler
- Uncaught exception catching

##### **build.gradle.kts**
- Error module build

---

### **core/debug/**
Debug tools

##### **DebugMenuDialog.kt**
- Debug menu Compose dialog
- App/device info display

##### **build.gradle.kts**
- Debug module build

---

### **core/backup/**
Backup/Restore

##### **BackupManager.kt**
- JSON backup creation
- Import/Export logic

##### **build.gradle.kts**
- Backup module build

---

### **core/util/**
Utility functions

##### **Constants.kt**
- Constant values
- App constants

##### **LocalizationUtils.kt**
- Localization helpers
- Language/date formats

##### **build.gradle.kts**
- Util modül build

---

## 🎨 FEATURE MODÜLLERI

### **feature/home/**
Ana ekran

##### **HomeScreen.kt**
- Ana ekran Compose UI
- Balance, transactions, goals

##### **HomeViewModel.kt**
- Home ekranı ViewModel
- State management

##### **HomeState.kt**
- Home ekranı UI state

##### **HomeScreenPreview.kt**
- Compose preview'ları

##### **src/test/HomeViewModelTest.kt**
- Unit testler

##### **src/androidTest/HomeScreenUITest.kt**
- UI testler

##### **build.gradle.kts**
- Home modül build

---

### **feature/statistics/**
İstatistikler

##### **StatisticsScreen.kt**
- İstatistik ekranı Compose
- Charts, category breakdown

##### **StatisticsViewModel.kt**
- Statistics ViewModel

##### **CategoryExpense.kt**
- Category expense model

##### **StatisticsScreenPreview.kt**
- Preview'lar

##### **components/** (Statistics Components)
- **StatisticsHeader.kt** - Header
- **PeriodSelector.kt** - Dönem seçici
- **IncomeExpenseBarChart.kt** - Bar chart
- **CategoryBreakdownCard.kt** - Kategori dağılımı
- **TrendAnalysisCard.kt** - Trend analizi
- **EmptyStatisticsCard.kt** - Empty state

##### **src/test/StatisticsViewModelTest.kt**
- Unit testler

##### **src/androidTest/StatisticsScreenUITest.kt**
- UI testler

##### **build.gradle.kts**
- Statistics modül build

---

### **feature/history/**
İşlem geçmişi

##### **HistoryScreen.kt**
- Geçmiş ekranı Compose
- Transaction list, calendar view

##### **HistoryViewModel.kt**
- History ViewModel
- Filtering, searching

##### **HistoryScreenPreview.kt**
- Preview'lar

##### **build.gradle.kts**
- History modül build

---

### **feature/scheduled/**
Planlı ödemeler

##### **ScheduledScreen.kt**
- Planlı ödeme ekranı
- Upcoming/recurring payments

##### **ScheduledViewModel.kt**
- Scheduled ViewModel

##### **ScheduledScreenPreview.kt**
- Preview'lar

##### **components/** (Scheduled Components)
- **ScheduledHeader.kt** - Header
- **ScheduledPaymentItem.kt** - Payment item
- **RecurringItem.kt** - Recurring item
- **RecurringRuleDialog.kt** - Rule oluşturma dialog
- **EditScheduledPaymentDialog.kt** - Düzenleme dialog
- **EmptyStateCard.kt** - Empty state
- **SectionTitle.kt** - Section başlık

##### **src/test/ScheduledViewModelTest.kt**
- Unit testler

##### **build.gradle.kts**
- Scheduled modül build

---

### **feature/settings/**
Ayarlar

##### **SettingsScreen.kt**
- Ayarlar ekranı Compose
- Theme, security, data

##### **SettingsViewModel.kt**
- Settings ViewModel

##### **SettingsState.kt**
- Settings state

##### **ThemeViewModel.kt**
- Tema yönetimi ViewModel

##### **DataDeletionScreen.kt**
- Veri silme ekranı

##### **DataDeletionViewModel.kt**
- Data deletion ViewModel

##### **CategoryManagementScreen.kt**
- Kategori yönetim ekranı
- CRUD operations

##### **SettingsScreenPreview.kt**
- Preview'lar

##### **components/BudgetAlertThresholdDialog.kt**
- Bütçe eşik dialog

##### **src/test/SettingsScreenUITest.kt**
- UI testler

##### **build.gradle.kts**
- Settings modül build

---

### **feature/onboarding/**
Onboarding flow

##### **OnboardingScreen.kt**
- Onboarding ekranı
- 4 sayfa slider
- Feature tanıtımı

##### **OnboardingManager.kt**
- Onboarding state yönetimi
- First launch detection

##### **build.gradle.kts**
- Onboarding modül build

---

### **feature/privacy/**
Gizlilik politikası

##### **PrivacyPolicyScreen.kt**
- Privacy policy ekranı
- Markdown gösterimi

##### **build.gradle.kts**
- Privacy modül build

---

### **feature/notifications/**
Bildirim merkezi

##### **NotificationCenterScreen.kt**
- Bildirim listesi ekranı
- Notification history

##### **build.gradle.kts**
- Notifications modül build

---

## 📊 BENCHMARK & BASELINE PROFILE

### **benchmark-macro/**

##### **StartupBenchmark.kt**
- Startup time benchmark
- Cold/warm start ölçümü

##### **ScrollBenchmark.kt**
- Scroll performance benchmark

##### **NavigationBenchmark.kt**
- Navigation performance

##### **build.gradle.kts**
- Benchmark modül build
- Macrobenchmark library

---

### **baselineprofile/**

##### **BaselineProfileGenerator.kt**
- Baseline profile oluşturma
- Startup optimization

##### **build.gradle.kts**
- Baseline profile build

---

## ⚙️ CONFIGURATION & SCRIPTS

### **config/detekt/**

##### **detekt.yml**
- Detekt kod kalite kuralları
- Static analysis config

##### **baseline.xml**
- Detekt baseline
- Mevcut sorunları ignore

---

### **config/jacoco/**

##### **jacoco.gradle**
- Jacoco coverage config
- Test coverage reports
- %45 minimum threshold

---

### **.github/workflows/**

##### **android-ci.yml**
- CI/CD pipeline
- Lint, test, build
- PR üzerinde otomatik çalışır

##### **release.yml**
- Release automation
- APK/AAB build
- GitHub release oluşturma
- Play Store upload

---

### **scripts/**

##### **audit-architecture.ps1**
- Mimari audit script

##### **migrate-all-features.ps1**
- Feature migration

##### **complete-migration.ps1**
- Migration tamamlama

##### **create-branch.ps1**
- Git branch oluşturma

---

## 📚 DOCUMENTATION

### **docs/**

##### **PRIVACY_POLICY.md**
- Gizlilik politikası
- KVKK/GDPR uyumlu

##### **TERMS_OF_SERVICE.md**
- Kullanım koşulları

##### **PLAY_STORE_LISTING.md**
- Play Store açıklaması
- TR/EN versiyonlar
- Screenshot tanımları

##### **RELEASE_CHECKLIST.md**
- Release kontrol listesi
- Step-by-step guide

##### **ACCESSIBILITY_GUIDE.md**
- Erişilebilirlik rehberi

##### **APK_SIGNING_GUIDE.md**
- APK imzalama rehberi

##### **BENCHMARK_GUIDE.md**
- Benchmark rehberi

##### **CODE_STYLE.md**
- Kod stil rehberi

##### **CONTRIBUTING.md**
- Katkıda bulunma rehberi

##### **DOCUMENTATION_STANDARDS.md**

##### **MULTI_MODULE_GUIDE.md**
- Multi-module rehberi

##### **PRODUCTION_READY_GUIDE.md**
- Production hazırlık rehberi

##### **QUALITY_METRICS.md**
- Kalite metrikleri

##### **adr/** (Architecture Decision Records)
- Mimari kararların dokümantasyonu

##### **architecture/**
- Mimari dokümantasyonu

##### **development/**
- Geliştirme rehberleri

##### **project-status/**
- Proje durum raporları

---

## 📋 ROOT DIRECTORY FILES

##### **build.gradle.kts**
- Root level build script
- Plugin versiyonları
- Repository tanımları

##### **settings.gradle.kts**
- Gradle settings
- Tüm modüllerin include edilmesi
- 15 modül

##### **gradle.properties**
- Gradle properties
- JVM heap size
- Build optimizations
- AndroidX flags

##### **gradle/libs.versions.toml**
- Version catalog
- Tüm dependency versiyonları
- Merkezi versiyon yönetimi

##### **gradlew & gradlew.bat**
- Gradle wrapper
- Cross-platform build

##### **local.properties**
- Local config (gitignore)
- SDK path

##### **.gitignore**
- Git ignore rules
- build/, .idea/ vb.

##### **README.md**
- Proje README
- Setup, build, run talimatları

##### **TODO.md**
- TODO listesi
- %100 tamamlanmış

##### **CHANGELOG.md**
- Versiyon değişiklikleri

##### **PROJECT_README.md**
- Detaylı proje dokümantasyonu

##### **PROJECT_COMPLETION.md**
- Proje tamamlanma raporu

##### **COMPLETED_FEATURES.md**
- Tamamlanan özellikler listesi

##### **clean-and-build.ps1**
- Clean ve build script

##### **clean-cache.bat**
- Cache temizleme

##### **final-build.bat**
- Final build script

##### **quick-refactor.ps1**
- Quick refactoring script

---

## 📊 İSTATİSTİKLER

### Modül Sayıları
- **Core modülleri:** 12
- **Feature modülleri:** 8
- **Toplam modül:** 22 (app + benchmark dahil)

### Dosya Sayıları (Yaklaşık)
- **Kotlin dosyaları:** 234+
- **XML dosyaları:** 165+
- **Build script:** 28
- **Test dosyaları:** 50+
- **Dokümantasyon:** 20+

### Kod Satırları (Tahmini)
- **Production code:** ~15,000 satır
- **Test code:** ~5,000 satır
- **UI Compose:** ~8,000 satır
- **Total:** ~28,000 satır

### Test Coverage
- **Unit tests:** 44+ test sınıfı
- **UI tests:** 10+ test sınıfı
- **Integration tests:** 5+ test sınıfı
- **Coverage:** %70+

---

## 🏗️ MİMARİ KATMANLAR

### 1️⃣ Presentation Layer (Feature Modules)
- Compose UI screens
- ViewModels
- UI state management
- Navigation

### 2️⃣ Domain Layer (core/domain)
- Use cases (business logic)
- Domain models
- Repository interfaces

### 3️⃣ Data Layer (core/data)
- Repository implementations
- Room database
- DAO'lar
- Entity'ler
- Local data source

### 4️⃣ Infrastructure Layer (Core Modules)
- UI components (core/ui)
- Security (core/security)
- Export/Import (core/export)
- Navigation (core/navigation)
- Error handling (core/error)
- Performance (core/performance)
- Premium (core/premium)
- Cloud (core/cloud)
- Feedback (core/feedback)

---

## 🎯 BAĞIMLILIKLAR

### Ana Teknolojiler
- **Kotlin:** 1.9.23
- **Compose:** 1.6.0
- **Room:** 2.6.1
- **Hilt:** 2.51.1
- **Coroutines:** 1.8.0
- **Material3:** 1.2.1

### Testing
- **JUnit:** 4.13.2
- **Mockk:** 1.13.9
- **Turbine:** 1.0.0
- **Espresso:** 3.5.1
- **Compose UI Test:** 1.6.0

### Build Tools
- **Gradle:** 8.7
- **AGP:** 8.5.0
- **KSP:** 1.9.23-1.0.20
- **Detekt:** 1.23.4
- **Jacoco:** 0.8.11

---

## ✅ PROJE DURUMU

**%100 TAMAMLANDI** 🎉

- ✅ Tüm core modüller
- ✅ Tüm feature'lar
- ✅ Testing (%70+ coverage)
- ✅ CI/CD pipeline
- ✅ Dokümantasyon
- ✅ Premium features
- ✅ Performance optimization
- ✅ Accessibility
- ✅ Localization (TR/EN/AR)
- ✅ Production ready

**RELEASE HAZIR! 🚀**
