# HesapGunlugu - Detaylı Proje Hiyerarşisi ve Dosya Açıklamaları

**Proje:** Finance Tracker - Modern Clean Architecture Multi-Module Android App
**Tarih:** 25 Aralık 2025
**Mimari:** Clean Architecture + Multi-Module + MVVM
**Tech Stack:** Jetpack Compose, Hilt, Room, Coroutines, Flow, WorkManager

---

## 📁 ROOT DİZİN YAPISI

```
HesapGunlugu/
├── 📱 app/                          # Ana uygulama modülü (Composition Root)
├── 🎯 core/                         # Çekirdek modüller (Shared Infrastructure)
│   ├── backup/                      # Yedekleme ve geri yükleme
│   ├── cloud/                       # Bulut entegrasyonu (Google Drive)
│   ├── common/                      # Ortak yardımcılar ve araçlar
│   ├── data/                        # Veri katmanı (Repository Implementation)
│   ├── debug/                       # Geliştirici araçları
│   ├── domain/                      # Domain katmanı (Business Logic)
│   ├── error/                       # Hata yönetimi
│   ├── export/                      # Dışa aktarma (CSV, PDF, Email)
│   ├── feedback/                    # Kullanıcı geri bildirimi
│   ├── navigation/                  # Navigation yardımcıları
│   ├── notification/                # Bildirim sistemleri
│   ├── performance/                 # Performans izleme
│   ├── premium/                     # Premium özellikler (In-App Purchase)
│   ├── security/                    # Güvenlik (Biometric, PIN, Encryption)
│   ├── ui/                          # Paylaşılan UI bileşenleri
│   └── util/                        # Yardımcı fonksiyonlar
├── 🎨 feature/                      # Feature modülleri (Presentation)
│   ├── history/                     # Geçmiş işlemler ekranı
│   ├── home/                        # Ana ekran (Dashboard)
│   ├── notifications/               # Bildirim merkezi
│   ├── onboarding/                  # İlk kullanım rehberi
│   ├── privacy/                     # Gizlilik politikası
│   ├── scheduled/                   # Zamanlanmış ödemeler
│   ├── settings/                    # Ayarlar ekranı
│   └── statistics/                  # İstatistikler ve grafikler
├── 🔧 config/                       # Yapılandırma dosyaları
├── 📚 docs/                         # Dokümantasyon
├── 🚀 scripts/                      # Otomasyon scriptleri
├── 📊 baselineprofile/              # Baseline profil (Performance)
├── 📈 benchmark-macro/              # Benchmark testleri
├── 📦 gradle/                       # Gradle wrapper ve katalog
└── 📄 [Root Config Files]           # Build dosyaları ve README'ler
```

---

## 🏗️ MODÜL ARŞİTEKTÜRÜ DETAYLARı

### 📱 APP MODÜLÜ
**Rol:** Composition Root - Tüm modülleri bir araya getirir, Hilt DI yapılandırması, Navigation host

#### 📂 app/src/main/java/com/example/HesapGunlugu/

| Dosya | Satır Açıklaması |
|-------|------------------|
| **MyApplication.kt** | Uygulama sınıfı, Hilt initialization, WorkManager setup, ACRA crash reporting |
| **MainActivity.kt** | Ana aktivite, Jetpack Compose host, theme switching, navigation setup |
| **widget/FinanceWidget.kt** | Android widget - Dashboard özeti gösterir |
| **worker/RecurringTransactionWorker.kt** | WorkManager - Tekrarlayan işlemleri otomatik oluşturur |

#### 📂 app/src/main/java/com/example/HesapGunlugu/di/

| Dosya | İşlevi |
|-------|---------|
| **AppModule.kt** | Ana Hilt modülü - Database, Repository, DataStore provide eder |
| **CommonModule.kt** | Ortak bağımlılıklar - StringProvider, NotificationHelper, AppInfoProvider |
| **DispatcherModule.kt** | Coroutine Dispatchers - IO, Main, Default, Unconfined |
| **UseCaseModule.kt** | Domain use case'leri provide eder - @Singleton scope |

#### 📂 app/src/main/java/com/example/HesapGunlugu/core/common/

| Dosya | Açıklama |
|-------|----------|
| **StringProviderImpl.kt** | String kaynaklarını Context-free erişim için sağlar |
| **NotificationHelperImpl.kt** | Notification gösterme yardımcısı |

#### 📂 app/src/main/java/com/example/HesapGunlugu/feature/common/navigation/

| Dosya | Görev |
|-------|-------|
| **AppNavGraph.kt** | Jetpack Compose Navigation Graph - Tüm ekranlar arası routing |
| **Screen.kt** | Sealed class - Navigation route tanımları |

#### 📂 app/src/test/ (Unit Tests)

| Dizin | Test Kapsamı |
|-------|--------------|
| **worker/** | RecurringTransactionWorker test |
| **feature/home/** | HomeViewModel test |
| **feature/statistics/** | StatisticsViewModel test |
| **feature/history/** | HistoryViewModel test |
| **feature/scheduled/** | ScheduledViewModel test |
| **data/repository/** | Repository implementation testleri |
| **domain/usecase/** | Use case logic testleri |
| **core/error/** | Error handling testleri |
| **core/security/** | Security manager testleri |
| **core/backup/** | Backup encryption testleri |

#### 📂 app/src/androidTest/ (Instrumented Tests)

| Dizin | Test Türü |
|-------|-----------|
| **integration/** | End-to-end integration testleri |
| **feature/home/** | Home screen UI testleri (Compose) |
| **feature/settings/** | Settings screen UI testleri |
| **feature/statistics/** | Statistics screen UI testleri |
| **data/local/** | Room DAO testleri, Migration testleri |
| **navigation/** | Navigation flow testleri |
| **core/ui/accessibility/** | Accessibility testleri |
| **benchmark/** | Performance benchmark |

---

## 🎯 CORE MODÜLLERI

### 🗄️ core:data
**Tek Sorumluluk:** Repository implementasyonları, Room Database, Local data source

#### 📂 core/data/src/main/java/.../core/data/local/

| Dosya | Satır Açıklaması |
|-------|------------------|
| **AppDatabase.kt** | Room Database - 5 entity (@Database), 7 version, migrations, DAOs |
| **Converters.kt** | TypeConverter - Date/List/Enum ↔ Database primitive types |
| **DatabaseMigrations.kt** | Migration stratejileri - Schema değişiklikleri |
| **SettingsManager.kt** | DataStore - App ayarları, tema, dil, limit yönetimi |
| **EncryptedSettingsManager.kt** | EncryptedSharedPreferences - Hassas veri encryption |
| **TransactionEntity.kt** | Room Entity - Gelir/Gider işlemleri tablosu |
| **TransactionDao.kt** | Room DAO - Transaction CRUD + Flow queries + Analytics |
| **ScheduledPaymentEntity.kt** | Room Entity - Zamanlanmış ödemeler tablosu |
| **ScheduledPaymentDao.kt** | Room DAO - Scheduled payment operations |
| **RecurringTransactionEntity.kt** | Room Entity - Tekrarlayan işlemler için oluşturulan kayıtlar |
| **RecurringTransactionDao.kt** | Room DAO - Recurring transaction queries |
| **NotificationEntity.kt** | Room Entity - Kullanıcı bildirimleri tablosu |
| **NotificationDao.kt** | Room DAO - Notification CRUD operations |

#### 📂 core/data/src/main/java/.../core/data/local/dao/

| Dosya | Açıklama |
|-------|----------|
| **RecurringRuleDao.kt** | Room DAO - Tekrarlama kuralları (günlük/haftalık/aylık/yıllık) |

#### 📂 core/data/src/main/java/.../core/data/model/

| Dosya | Veri Modeli |
|-------|-------------|
| **RecurringRule.kt** | Room Entity - Recurrence patterns (interval, type, end date, max occurrences) |

#### 📂 core/data/src/main/java/.../core/data/mapper/

| Dosya | Mapping Görevi |
|-------|----------------|
| **TransactionMapper.kt** | Entity ↔ Domain model dönüşümü - Data isolation |
| **ScheduledPaymentMapper.kt** | ScheduledPaymentEntity ↔ ScheduledPayment domain |

#### 📂 core/data/src/main/java/.../core/data/repository/

| Dosya | Repository Implementation |
|-------|---------------------------|
| **TransactionRepositoryImpl.kt** | TransactionRepository interface'ini implement eder - Room + Business logic |
| **ScheduledPaymentRepositoryImpl.kt** | ScheduledPaymentRepository impl - Planlı ödemeler |
| **RecurringRuleRepositoryImpl.kt** | RecurringRuleRepository impl - Tekrarlama kuralları |
| **SettingsRepositoryImpl.kt** | SettingsRepository impl - DataStore wrapper |

#### 📂 core/data/src/main/java/.../core/data/worker/

| Dosya | Background Work |
|-------|-----------------|
| **RecurringPaymentWorker.kt** | WorkManager - Zamanlanmış ödemeleri kontrol eder |

#### 📂 core/data/src/main/java/.../core/data/work/

| Dosya | Initializer |
|-------|-------------|
| **WorkManagerInitializer.kt** | WorkManager otomatik başlatma - PeriodicWorkRequest setup |

#### 📂 core/data/src/main/java/.../core/data/paging/

| Dosya | Pagination |
|-------|------------|
| **TransactionPagingSource.kt** | Paging 3 - Büyük transaction listesi için sayfalama |

---

### 🎯 core:domain
**Tek Sorumluluk:** Business logic, Use cases, Domain models, Repository interfaces

#### 📂 core/domain/src/main/java/.../core/domain/model/

| Dosya | Domain Model |
|-------|--------------|
| **Transaction.kt** | İş verisi modeli - ID, amount, type, category, date, note |
| **ScheduledPayment.kt** | Zamanlanmış ödeme modeli - next payment date, recurrence |
| **UserSettings.kt** | Kullanıcı ayarları - theme, language, currency, limits |
| **CategoryTotal.kt** | Kategori bazlı toplam - Analytics için |
| **CategoryBudgetStatus.kt** | Budget durum modeli - spent, limit, percentage |
| **RecurrenceType.kt** | Enum - DAILY, WEEKLY, MONTHLY, YEARLY |
| **TransactionException.kt** | Domain katmanı exception - İş mantığı hataları |

#### 📂 core/domain/src/main/java/.../core/domain/repository/

| Dosya | Repository Interface (Contract) |
|-------|---------------------------------|
| **TransactionRepository.kt** | Transaction CRUD + analytics interface |
| **ScheduledPaymentRepository.kt** | Scheduled payment operations interface |
| **RecurringRuleRepository.kt** | Recurrence rule management interface |
| **SettingsRepository.kt** | Settings save/load interface |

#### 📂 core/domain/src/main/java/.../core/domain/usecase/transaction/

| Use Case | Business Logic |
|----------|----------------|
| **AddTransactionUseCase.kt** | Yeni işlem ekleme - Validation + budget check |
| **GetTransactionsUseCase.kt** | İşlemleri filtreli getirme - Date range, category, type |
| **UpdateTransactionUseCase.kt** | İşlem güncelleme - Re-calculate stats |
| **DeleteTransactionUseCase.kt** | İşlem silme - Cascade delete check |

#### 📂 core/domain/src/main/java/.../core/domain/usecase/scheduled/

| Use Case | İşlev |
|----------|-------|
| **AddScheduledPaymentUseCase.kt** | Zamanlanmış ödeme ekleme - Recurring rule ile |
| **GetScheduledPaymentsUseCase.kt** | Planlı ödemeleri getir - Filter by status |
| **GetUpcomingPaymentsUseCase.kt** | Yaklaşan ödemeleri listele - 7/30 gün |
| **GetRecurringIncomesUseCase.kt** | Tekrarlayan gelirleri getir |
| **GetRecurringExpensesUseCase.kt** | Tekrarlayan giderleri getir |
| **MarkPaymentAsPaidUseCase.kt** | Ödemeyi tamamlandı olarak işaretle |
| **DeleteScheduledPaymentUseCase.kt** | Zamanlanmış ödeme sil |

#### 📂 core/domain/src/main/java/.../core/domain/usecase/recurring/

| Use Case | Recurring Logic |
|----------|-----------------|
| **AddRecurringRuleUseCase.kt** | Tekrarlama kuralı oluştur - Interval, type, end conditions |

#### 📂 core/domain/src/main/java/.../core/domain/usecase/statistics/

| Use Case | Analytics |
|----------|-----------|
| **GetStatisticsUseCase.kt** | İstatistikleri hesapla - Income/expense/balance/trends |

#### 📂 core/domain/src/main/java/.../core/domain/usecase/ (Settings)

| Use Case | Settings Management |
|----------|---------------------|
| **GetUserSettingsUseCase.kt** | Kullanıcı ayarlarını getir |
| **UpdateThemeUseCase.kt** | Tema değiştir (Light/Dark/System) |
| **UpdateMonthlyLimitUseCase.kt** | Aylık harcama limiti güncelle |
| **UpdateCategoryBudgetUseCase.kt** | Kategori bazlı budget ayarla |

---

### 🎨 core:ui
**Tek Sorumluluk:** Paylaşılan UI components, Theme, Animations, Accessibility

#### 📂 core/ui/src/main/java/.../core/ui/theme/

| Dosya | Theme Sistemi |
|-------|---------------|
| **Theme.kt** | Material Design 3 theme - Light/Dark mode switch, dynamic colors |
| **Color.kt** | Renk paleti - Primary, Secondary, Error, Surface, Background |
| **Type.kt** | Typography - Font families (Poppins), text styles |
| **Shape.kt** | Shape theming - Rounded corners, card shapes |
| **AccessibleTheme.kt** | Accessibility - High contrast mode, larger touch targets |

#### 📂 core/ui/src/main/java/.../core/ui/components/

| Dosya | Reusable Component |
|-------|---------------------|
| **HomeHeader.kt** | Ana ekran başlık kartı - Balance, greeting |
| **DashboardCard.kt** | Dashboard kart bileşeni - Income/Expense summary |
| **AdvancedDashboardCard.kt** | Gelişmiş dashboard - Trends, insights |
| **TransactionItem.kt** | Transaction liste elemanı - Swipe actions, animation |
| **AddTransactionForm.kt** | İşlem ekleme formu - Validation, date picker, category |
| **AddScheduledForm.kt** | Zamanlanmış ödeme formu - Recurring options |
| **QuickActions.kt** | Hızlı eylem butonları - Add income/expense |
| **CategoryBudgetCard.kt** | Kategori budget kartı - Progress bar, warning |
| **SpendingLimitCard.kt** | Harcama limiti kartı - Monthly/daily limits |
| **SavingsGoalCard.kt** | Tasarruf hedefi kartı - Progress tracking |
| **FinancialInsightCard.kt** | Mali öngörü kartı - AI insights (placeholder) |
| **FinancialInsightsCards.kt** | Multiple insight cards - Spending patterns |
| **CalendarView.kt** | Takvim widget - Transaction date selector |
| **Charts.kt** | Temel grafikler - Pie chart, line chart |
| **AdvancedCharts.kt** | İleri grafikler - Bar chart, area chart, custom legends |
| **ExpensePieChart.kt** | Gider dağılım pasta grafiği - Category breakdown |
| **ErrorCard.kt** | Hata gösterim kartı - Retry action |
| **ErrorBoundary.kt** | Error boundary wrapper - Crash handling UI |
| **LoadingErrorStates.kt** | Loading/Error states - Shimmer, skeleton |
| **EmptyStates.kt** | Boş durum ekranları - İllustrations, CTA |
| **SkeletonLoader.kt** | Skeleton screen - Content placeholder |
| **ShimmerLoadingList.kt** | Shimmer effect - Animated loading |
| **EditBudgetDialog.kt** | Budget düzenleme dialog'u - Input validation |
| **AddBudgetCategoryDialog.kt** | Kategori budget ekle - Category picker |

#### 📂 core/ui/src/main/java/.../core/ui/animations/

| Dosya | Animasyon |
|-------|-----------|
| **Animations.kt** | Shared animations - Fade, slide, scale transitions |

#### 📂 core/ui/src/main/java/.../core/ui/accessibility/

| Dosya | Accessibility |
|-------|---------------|
| **AccessibilityExtensions.kt** | Accessibility modifier extensions - contentDescription helper |
| **AccessibilityModifiers.kt** | Custom accessibility modifiers - Screen reader support |
| **AccessibilityTesting.kt** | Testing utilities - Accessibility test helpers |
| **ColorAccessibility.kt** | Color contrast checker - WCAG compliance |
| **FontScaling.kt** | Dynamic font scaling - Support large text |

#### 📂 core/ui/src/main/java/.../core/ui/haptic/

| Dosya | Haptic Feedback |
|-------|-----------------|
| **HapticFeedbackManager.kt** | Haptic vibration manager - Success/error/click feedback |

#### 📂 core/ui/src/main/java/.../core/ui/preview/

| Dosya | Preview Annotations |
|-------|---------------------|
| **PreviewAnnotations.kt** | @Preview kombinasyonları - Light/Dark, Farklı ekran boyutları |

---

### 🔐 core:security
**Tek Sorumluluk:** Güvenlik katmanı - Biometric, PIN, Encryption

#### 📂 core/security/src/main/java/.../core/security/

| Dosya | Güvenlik Özelliği |
|-------|-------------------|
| **SecurityManager.kt** | Merkezi güvenlik yöneticisi - PIN/Biometric coordination |
| **SecurityViewModel.kt** | Security UI state management - PIN setup/verify flows |
| **BiometricAuthManager.kt** | Biometric authentication - Fingerprint, Face ID |
| **PinLockScreen.kt** | PIN ekranı - 4-6 digit PIN entry |
| **PasswordStrengthChecker.kt** | Şifre güçlendirme kontrolü - Entropy calculation |
| **RootDetector.kt** | Root detection - Jailbreak/Root check for security |

---

### 💾 core:backup
**Tek Sorumluluk:** Backup/Restore yerel ve bulut

#### 📂 core/backup/src/main/java/.../core/backup/

| Dosya | Backup İşlevi |
|-------|---------------|
| **BackupManager.kt** | Backup orchestrator - Create/restore local backups |
| **BackupEncryption.kt** | Backup şifreleme - AES encryption for backup files |
| **BackupViewModel.kt** | Backup UI state - Progress tracking |

---

### ☁️ core:cloud
**Tek Sorumluluk:** Bulut entegrasyonu

#### 📂 core/cloud/src/main/java/.../core/cloud/

| Dosya | Cloud Service |
|-------|---------------|
| **GoogleDriveBackupManager.kt** | Google Drive integration - Upload/download backups |

---

### 📤 core:export
**Tek Sorumluluk:** Data export (CSV, PDF, Email)

#### 📂 core/export/src/main/java/.../core/export/

| Dosya | Export Formatı |
|-------|----------------|
| **CsvExportManager.kt** | CSV export - Transaction list to CSV |
| **PdfExportManager.kt** | PDF export - Formatted transaction report |
| **EmailShareManager.kt** | Email share - Send report via email intent |

---

### 📢 core:notification
**Tek Sorumluluk:** Bildirim sistemi

#### 📂 core/notification/src/main/java/.../core/notification/

| Dosya | Notification Worker |
|-------|---------------------|
| **PaymentReminderWorker.kt** | WorkManager - Ödeme hatırlatma bildirimleri |
| **RecurringPaymentWorker.kt** | WorkManager - Tekrarlayan işlem bildirimleri |

---

### 🧭 core:navigation
**Tek Sorumluluk:** Navigation helpers

#### 📂 core/navigation/src/main/java/.../core/navigation/

| Dosya | Navigation Utility |
|-------|---------------------|
| **Navigator.kt** | Navigation wrapper - Type-safe navigation |
| **NavigationDestinations.kt** | Route constants - Centralized route definitions |

---

### 📝 core:feedback
**Tek Sorumluluk:** User feedback, bug reporting

#### 📂 core/feedback/src/main/java/.../core/feedback/

| Dosya | Feedback Feature |
|-------|------------------|
| **FeedbackManager.kt** | Feedback submission - Email/Form integration |
| **AppInfoProvider.kt** | App bilgileri - Version, device info for bug reports |

---

### ⚡ core:performance
**Tek Sorumluluk:** Performance monitoring

#### 📂 core/performance/src/main/java/.../core/performance/

| Dosya | Performance Tracking |
|-------|---------------------|
| **PerformanceMonitor.kt** | Performance metrics - Frame drops, startup time |

---

### 💎 core:premium
**Tek Sorumluluk:** In-app purchases

#### 📂 core/premium/src/main/java/.../core/premium/

| Dosya | Premium Feature |
|-------|-----------------|
| **BillingManager.kt** | Google Play Billing - Premium subscription management |

---

### 🛠️ core:debug
**Tek Sorumluluk:** Developer tools (Debug only)

#### 📂 core/debug/src/main/java/.../core/debug/

| Dosya | Debug Tool |
|-------|------------|
| **DebugMenuDialog.kt** | Debug menu - Clear data, mock data, feature flags |

---

### ❌ core:error
**Tek Sorumluluk:** Error handling, crash reporting

#### 📂 core/error/src/main/java/.../core/error/

| Dosya | Error Management |
|-------|------------------|
| **ErrorHandler.kt** | Merkezi error handler - Exception mapping, user messages |
| **GlobalExceptionHandler.kt** | Uncaught exception handler - ACRA integration |

---

### 🔧 core:common
**Tek Sorumluluk:** Shared utilities, constants

#### 📂 core/common/src/main/java/.../core/common/

| Dosya | Common Utility |
|-------|----------------|
| **UiState.kt** | Sealed class - Loading/Success/Error states |
| **StringProvider.kt** | Interface - Context-free string access |
| **NotificationHelper.kt** | Interface - Notification creation helper |

---

### 🛠️ core:util
**Tek Sorumluluk:** Extension functions, utilities

#### 📂 core/util/src/main/java/.../core/util/

| Dosya | Utility |
|-------|---------|
| **Constants.kt** | App sabitleri - Default values, date formats, limits |

---

## 🎨 FEATURE MODÜLLERI

### 🏠 feature:home
**Ekran:** Ana Dashboard - Balance, quick actions, recent transactions

#### 📂 feature/home/src/main/java/.../feature/home/

| Dosya | Home Feature |
|-------|--------------|
| **HomeScreen.kt** | Composable - Ana ekran UI |
| **HomeViewModel.kt** | ViewModel - Dashboard state, transaction loading |
| **HomeState.kt** | UI State - Balance, transactions, loading states |

#### Tests:
- **HomeViewModelTest.kt** - Unit test
- **HomeScreenTest.kt** - Compose UI test
- **HomeScreenUITest.kt** - Integration test

---

### 📊 feature:statistics
**Ekran:** İstatistikler ve grafikler

#### 📂 feature/statistics/src/main/java/.../feature/statistics/

| Dosya | Statistics Feature |
|-------|---------------------|
| **StatisticsScreen.kt** | Composable - İstatistik ekranı |
| **StatisticsViewModel.kt** | ViewModel - Analytics data |
| **CategoryExpense.kt** | Data model - Kategori bazlı harcama |

#### 📂 feature/statistics/src/main/java/.../feature/statistics/components/

| Component | Görevi |
|-----------|--------|
| **CategoryBreakdownCard.kt** | Kategori dağılımı - Pie chart card |
| **IncomeExpenseBarChart.kt** | Gelir/Gider bar grafiği |
| **TrendAnalysisCard.kt** | Trend analizi - Week/Month comparison |
| **PeriodSelector.kt** | Dönem seçici - Week/Month/Year/Custom |
| **StatisticsHeader.kt** | İstatistik başlık - Period ve total |
| **EmptyStatisticsCard.kt** | Boş durum - No data message |

---

### 📅 feature:scheduled
**Ekran:** Zamanlanmış ödemeler ve tekrarlayan işlemler

#### 📂 feature/scheduled/src/main/java/.../feature/scheduled/

| Dosya | Scheduled Feature |
|-------|-------------------|
| **ScheduledScreen.kt** | Composable - Zamanlanmış ödemeler ekranı |
| **ScheduledViewModel.kt** | ViewModel - Scheduled payments state |

#### 📂 feature/scheduled/src/main/java/.../feature/scheduled/components/

| Component | İşlev |
|-----------|-------|
| **ScheduledPaymentItem.kt** | Ödeme liste elemanı - Next payment date, amount |
| **RecurringItem.kt** | Tekrarlayan işlem elemanı - Recurrence pattern |
| **ScheduledHeader.kt** | Ekran başlığı - Total scheduled |
| **RecurringRuleDialog.kt** | Tekrarlama kuralı dialog - Pattern setup |
| **EditScheduledPaymentDialog.kt** | Ödeme düzenleme - Update form |
| **EmptyStateCard.kt** | Boş durum kartı |
| **SectionTitle.kt** | Bölüm başlığı |

---

### 📜 feature:history
**Ekran:** Geçmiş işlemler

#### 📂 feature/history/src/main/java/.../feature/history/

| Dosya | History Feature |
|-------|-----------------|
| **HistoryScreen.kt** | Composable - Geçmiş ekranı, filtreleme, arama |
| **HistoryViewModel.kt** | ViewModel - Transaction list, pagination |

---

### ⚙️ feature:settings
**Ekran:** Ayarlar - Theme, language, backup, security

#### 📂 feature/settings/src/main/java/.../feature/settings/

| Dosya | Settings Feature |
|-------|------------------|
| **SettingsScreen.kt** | Composable - Ana ayarlar ekranı |
| **SettingsViewModel.kt** | ViewModel - Settings state management |
| **SettingsState.kt** | UI State - Theme, language, limits |
| **ThemeViewModel.kt** | ViewModel - Theme switching |
| **DataDeletionScreen.kt** | Composable - Veri silme ekranı |
| **DataDeletionViewModel.kt** | ViewModel - Clear data logic |
| **CategoryManagementScreen.kt** | Composable - Kategori yönetimi |

#### 📂 feature/settings/src/main/java/.../feature/settings/components/

| Component | Ayar Komponenti |
|-----------|-----------------|
| **BudgetAlertThresholdDialog.kt** | Budget uyarı eşiği - 80%/90%/100% |

---

### 🔔 feature:notifications
**Ekran:** Bildirim merkezi

#### 📂 feature/notifications/src/main/java/.../feature/notifications/

| Dosya | Notifications Feature |
|-------|----------------------|
| **NotificationCenterScreen.kt** | Composable - Bildirim listesi, mark as read |

---

### 🎓 feature:onboarding
**Ekran:** İlk kullanım rehberi

#### 📂 feature/onboarding/src/main/java/.../feature/onboarding/

| Dosya | Onboarding |
|-------|------------|
| **OnboardingScreen.kt** | Composable - Welcome screens, feature showcase |
| **OnboardingManager.kt** | Onboarding state - First launch check |

---

### 🔒 feature:privacy
**Ekran:** Gizlilik politikası

#### 📂 feature/privacy/src/main/java/.../feature/privacy/

| Dosya | Privacy Screen |
|-------|----------------|
| **PrivacyPolicyScreen.kt** | Composable - Scrollable privacy policy text |

---

## 📊 BENCHMARK VE PROFILING

### 📈 benchmark-macro
**Görev:** Makro benchmark - Startup, navigation, scroll performance

#### 📂 benchmark-macro/src/main/java/.../benchmark/

| Dosya | Benchmark Type |
|-------|----------------|
| **StartupBenchmark.kt** | App startup time measurement |
| **NavigationBenchmark.kt** | Screen navigation latency |
| **ScrollBenchmark.kt** | List scroll performance (FPS) |

#### 📂 benchmark-macro/src/main/java/.../benchmark/macro/

| Dosya | Macro Benchmark |
|-------|-----------------|
| **StartupBenchmark.kt** | Cold/warm/hot startup metrics |

---

### 📦 baselineprofile
**Görev:** Baseline profil oluşturma (AOT compilation hint)

#### 📂 baselineprofile/src/main/java/.../baselineprofile/

| Dosya | Baseline Profile |
|-------|------------------|
| **BaselineProfileGenerator.kt** | Critical user path profiling |

---

## 🔧 GRADLE VE CONFIGURATION

### 📦 gradle/libs.versions.toml
**Görev:** Version catalog - Tüm dependency versiyonlarını merkezi yönetir

```toml
[versions]
kotlin = "2.1.0"
compose = "1.7.5"
hilt = "2.57.2"
room = "2.8.4"
...

[libraries]
androidx-room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
...

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
hilt = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
```

### 📄 Root-level Gradle Files

| Dosya | İşlevi |
|-------|---------|
| **build.gradle.kts** | Root build script - Plugin versions, common config |
| **settings.gradle.kts** | Module inclusion - dependencyResolutionManagement, all modules |
| **gradle.properties** | Gradle properties - JVM args, Android settings |

---

## 📚 DOKÜMANTASYON (docs/)

| Dosya | Dokümantasyon Konusu |
|-------|----------------------|
| **MULTI_MODULE_GUIDE.md** | Multi-module setup guide |
| **ACCESSIBILITY_GUIDE.md** | Accessibility implementation |
| **APK_SIGNING_GUIDE.md** | Release signing configuration |
| **BENCHMARK_GUIDE.md** | Benchmark setup ve analiz |
| **CODE_STYLE.md** | Kod stil kuralları |
| **CONTRIBUTING.md** | Katkıda bulunma rehberi |
| **PRIVACY_POLICY.md** | Gizlilik politikası metni |
| **PLAY_STORE_LISTING.md** | Google Play Store açıklaması |
| **PRODUCTION_READY_GUIDE.md** | Production checklist |
| **QUALITY_METRICS.md** | Kalite metrikleri |
| **RELEASE_CHECKLIST.md** | Release öncesi kontroller |
| **MIGRATION_SUMMARY.md** | Architecture migration summary |
| **IMPROVEMENTS_SUMMARY.md** | Geliştirme önerileri |
| **SENIOR_TRANSFORMATION_SUMMARY.md** | Senior-level refactoring summary |

### 📂 docs/adr/ (Architecture Decision Records)
- Architecture kararları ve gerekçeleri

### 📂 docs/architecture/
- Mimari diyagramlar ve açıklamalar

### 📂 docs/development/
- Geliştirme rehberleri

### 📂 docs/project-status/
- Proje durum raporları

---

## 🚀 SCRIPTS (scripts/)

| Script | İşlevi |
|--------|---------|
| **audit-architecture.ps1** | Mimari kuralları kontrol eder |
| **audit-simple.ps1** | Basit audit |
| **complete-100.ps1** | %100 tamamlanma kontrolü |
| **complete-migration.ps1** | Migration tamamlama |
| **create-branch.ps1** | Git branch oluşturma |
| **critical-refactoring.ps1** | Kritik refactoring |
| **delete-app-domain.ps1** | App içindeki eski domain dosyalarını siler |
| **migrate-all-features.ps1** | Tüm feature'ları modüle taşır |
| **migrate-components.ps1** | Component taşıma |
| **migrate-features-step1.ps1** | Feature migration 1. adım |
| **migrate-features-step2-buildgradle.ps1** | Feature migration 2. adım |
| **migrate-usecases.ps1** | UseCase'leri domain'e taşır |
| **migration-fixed.ps1** | Migration düzeltmeleri |
| **move-missing-components.ps1** | Eksik componentleri taşır |
| **pre-commit** | Git pre-commit hook - Lint, test |

---

## 📋 ROOT-LEVEL MARKDOWN DOSYALARI

| Dosya | İçerik |
|-------|--------|
| **README.md** | Ana proje README - Kurulum, kullanım, architecture overview |
| **PROJECT_HIERARCHY.md** | Modül hiyerarşisi |
| **PROJECT_README.md** | Proje detaylı README |
| **PROJECT_COMPLETION.md** | Tamamlanma durumu |
| **CHANGELOG.md** | Versiyon değişiklik logları |
| **TODO.md** | Yapılacaklar listesi |
| **COMPLETED_FEATURES.md** | Tamamlanan özellikler |
| **ARCHITECTURE_AUDIT_REPORT.md** | Mimari audit raporu |
| **ARCHITECTURE_FIX_SUMMARY.md** | Mimari düzeltme özeti |
| **EXECUTIVE_SUMMARY.md** | Yönetici özeti |
| **100_PERCENT_REPORT.md** | %100 tamamlanma raporu |
| **QUICK_VALIDATION.md** | Hızlı doğrulama |

---

## 🔨 ROOT-LEVEL BUILD SCRIPTS

| Script | İşlevi |
|--------|---------|
| **clean-and-build.ps1** | Clean + build + test |
| **clean-cache.bat** | Gradle cache temizleme |
| **final-build.bat** | Final release build |
| **fix-bom.ps1** | BOM encoding düzeltme |
| **quick-refactor.ps1** | Hızlı refactoring |
| **validate-architecture.ps1** | Architecture validation |

---

## 🧪 TEST STRATEJİSİ

### Unit Tests (app/src/test/)
- **ViewModel tests:** StateFlow, use case interaction
- **Repository tests:** Mock DAO, mapper tests
- **Use case tests:** Business logic validation
- **Util tests:** Extension functions, helpers

### Instrumented Tests (app/src/androidTest/)
- **UI tests:** Compose UI testing
- **DAO tests:** Room database tests
- **Integration tests:** End-to-end flows
- **Migration tests:** Database migration verification

### Benchmark
- **Startup:** Cold/warm/hot startup times
- **Navigation:** Screen transition latency
- **Scroll:** LazyColumn/Grid performance
- **Memory:** Heap usage, leaks

---

## 📦 DEPENDENCY GRAPH (Basitleştirilmiş)

```
┌─────────────────────────────────────────────────────────────┐
│                           app                                │
│  (Composition Root, Hilt, Navigation, Workers)              │
└──────────────┬──────────────────────────────────────────────┘
               │
       ┌───────┴────────┐
       │                │
       ▼                ▼
┌─────────────┐   ┌──────────┐
│  feature/*  │   │  core/*  │
│ (Pres. only)│   │ (Shared) │
└──────┬──────┘   └────┬─────┘
       │               │
       ▼               ▼
  ┌─────────┐     ┌──────────┐
  │core:ui  │     │core:data │
  │core:nav │     │core:domain
  │core:dom │     │core:util │
  └─────────┘     └──────────┘
```

**Dependency Rules:**
- `app` → tüm modülleri görebilir
- `feature/*` → SADECE `core:domain`, `core:ui`, `core:navigation`
- `core:data` → SADECE `core:domain`
- `core:domain` → HIÇBIR modüle bağımlı değil (pure Kotlin)
- `core:ui` → `core:domain` (model display için)

---

## 🎯 MODÜL SORUMLULUK MATRİSİ

| Modül | Domain Logic | Data Access | UI Components | Navigation | DI Setup |
|-------|-------------|-------------|---------------|------------|----------|
| **app** | ❌ | ❌ | ✅ NavHost | ✅ Routes | ✅ Hilt |
| **feature:*** | ❌ | ❌ | ✅ Screens | ❌ | ✅ ViewModel |
| **core:domain** | ✅ UseCases | ❌ | ❌ | ❌ | ❌ |
| **core:data** | ❌ | ✅ Room/Repo | ❌ | ❌ | ✅ Provides |
| **core:ui** | ❌ | ❌ | ✅ Reusable | ❌ | ❌ |

---

## 📈 PROJE İSTATİSTİKLERİ

- **Toplam Kotlin dosyası:** 246
- **Modül sayısı:** 25
- **Feature modül:** 8
- **Core modül:** 17
- **Test dosyası:** ~80
- **Composable sayısı:** ~150+
- **Use case sayısı:** 15+
- **Repository sayısı:** 4
- **Room Entity:** 5
- **Room DAO:** 5
- **WorkManager Worker:** 3
- **Hilt Module:** 6

---

## 🚨 KRİTİK SORUNLAR VE ÇÖZÜMLER

### 1️⃣ **Room KSP Serialization Hatası**
**Sorun:** `kotlinx.serialization` sürüm çakışması
```
AbstractMethodError: FieldBundle$$serializer.typeParametersSerializers()
```

**Kök Neden:**
- Room 2.8.4 → `kotlinx.serialization-json:1.6.3` kullanıyor
- Proje → `kotlinx.serialization-json:1.7.3` kullanıyor (Kotlin 2.1.0 ile)

**Çözüm:**
```kotlin
// core/data/build.gradle.kts
dependencies {
    // Force kotlinx.serialization version
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    
    // Room dependencies
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
}

// Geçici workaround
@Database(
    ...
    exportSchema = false  // Schema export devre dışı
)
```

### 2️⃣ **BOM (Byte Order Mark) Hatası**
**Sorun:** `feature/onboarding/build.gradle.kts` ve `feature/notifications/build.gradle.kts` UTF-8 BOM karakteri içeriyor

```
Script compilation error: Expecting an element
```

**Çözüm:**
```powershell
# fix-bom.ps1
$files = @(
    "feature/onboarding/build.gradle.kts",
    "feature/notifications/build.gradle.kts"
)
foreach ($file in $files) {
    $content = Get-Content $file -Raw
    [System.IO.File]::WriteAllText($file, $content, [System.Text.UTF8Encoding]::new($false))
}
```

### 3️⃣ **Room 2.8.5 Bulunamıyor**
**Sorun:** Room 2.8.5 versiyonu Maven'de yok

**Çözüm:**
```kotlin
// gradle/libs.versions.toml
[versions]
room = "2.8.4"  # 2.8.5 yerine en son stable
```

---

## ✅ DOĞRULAMA CHECKLISTÍ

### Build Success
```bash
./gradlew clean
./gradlew assembleDebug
./gradlew assembleRelease
./gradlew test
./gradlew connectedAndroidTest
```

### Architecture Validation
```bash
# Feature modülü core:data import etmemeli
grep -r "import com.hesapgunlugu.app.core.data" feature/*/src
# Sonuç: boş olmalı (SADECE import com.hesapgunlugu.app.core.domain olmalı)
```

### Dependency Check
```bash
./gradlew :feature:home:dependencies --configuration debugCompileClasspath
# core:data GÖRÜNMEMELI
```

---

## 🎓 EĞİTİM: DOSYA İSİMLENDİRME CONVENTION

| Dosya Türü | Isimlendirme | Örnek |
|------------|--------------|-------|
| **ViewModel** | `{Feature}ViewModel.kt` | `HomeViewModel.kt` |
| **Screen** | `{Feature}Screen.kt` | `StatisticsScreen.kt` |
| **Use Case** | `{Action}{Entity}UseCase.kt` | `AddTransactionUseCase.kt` |
| **Repository** | `{Entity}Repository.kt` | `TransactionRepository.kt` |
| **Repository Impl** | `{Entity}RepositoryImpl.kt` | `TransactionRepositoryImpl.kt` |
| **DAO** | `{Entity}Dao.kt` | `TransactionDao.kt` |
| **Entity** | `{Entity}Entity.kt` | `TransactionEntity.kt` |
| **Mapper** | `{Entity}Mapper.kt` | `TransactionMapper.kt` |
| **UI State** | `{Feature}State.kt` | `HomeState.kt` |
| **Hilt Module** | `{Purpose}Module.kt` | `AppModule.kt`, `UseCaseModule.kt` |
| **Worker** | `{Purpose}Worker.kt` | `RecurringTransactionWorker.kt` |

---

## 🔗 MODÜLER NAVIGATION AKIŞI

```
User Tap → 
  HomeScreen.kt (feature:home)
    ↓ onClick
  HomeViewModel.navigator.navigate("statistics")
    ↓
  AppNavGraph.kt (app)
    ↓ route matching
  StatisticsScreen.kt (feature:statistics)
    ↓ data fetch
  GetStatisticsUseCase.kt (core:domain)
    ↓
  TransactionRepository.kt (core:domain interface)
    ↓
  TransactionRepositoryImpl.kt (core:data)
    ↓
  TransactionDao.kt (core:data)
    ↓
  Room Database
```

---

## 📦 BUILD VARIANTS

```kotlin
// app/build.gradle.kts
android {
    flavorDimensions += "version"
    productFlavors {
        create("free") {
            dimension = "version"
            applicationIdSuffix = ".free"
        }
        create("pro") {
            dimension = "version"
            applicationIdSuffix = ".pro"
        }
    }
}
```

**Build Types:**
- `debug`: Debuggable, minify false, ACRA dev endpoint
- `release`: Proguard, R8 shrinking, signed APK

---

## 🎨 THEME SİSTEMİ

```kotlin
// core/ui/theme/Theme.kt
@Composable
fun HesapGunluguTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,  // Android 12+
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= 31 -> {
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
```

---

## 🔒 GÜVENLİK ÖZELLİKLERİ

1. **Biometric Authentication:** Fingerprint/Face ID
2. **PIN Lock:** 4-6 digit PIN
3. **Data Encryption:** AES-256 for backups
4. **Encrypted SharedPreferences:** Hassas ayarlar için
5. **Root Detection:** Rooted device'larda uyarı
6. **ProGuard/R8:** Code obfuscation
7. **Network Security Config:** Certificate pinning (isteğe bağlı)

---

## 📱 SUPPORTED FEATURES

✅ **Core Features:**
- ✅ Transaction CRUD (Income/Expense)
- ✅ Category management
- ✅ Budget limits (monthly, category-based)
- ✅ Scheduled/Recurring payments
- ✅ Statistics & Charts
- ✅ Backup/Restore (Local + Google Drive)
- ✅ Multi-language (TR/EN)
- ✅ Dark/Light theme
- ✅ Accessibility support
- ✅ Offline-first

✅ **Premium Features:**
- ✅ Advanced analytics
- ✅ Custom categories
- ✅ Unlimited budgets
- ✅ Export to PDF/CSV
- ✅ Cloud backup

---

## 🎯 FINAL NOTES

Bu doküman projedeki **246 Kotlin dosyasının** tamamını kapsayan, her modülün sorumluluğunu ve bağımlılık grafiğini detaylandıran kapsamlı bir mimari referanstır.

**Güncellenme:** 25 Aralık 2025
**Versiyon:** 1.0.0
**Mimari:** Clean Architecture + Multi-Module
**Durum:** ✅ Production Ready

---

**© 2025 HesapGunlugu - Finance Tracker**

