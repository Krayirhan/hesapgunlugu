# 📦 FAZ 3: COMMON COMPONENTS TAŞIMA PLANI

**Tarih:** 25 Aralık 2024  
**Hedef:** app/feature/common → core/ui veya app'te kalacak

---

## 🔍 MEVCUT DURUM

### app/feature/common/components/ (16 dosya)
```
app/feature/common/components/
├── AddBudgetCategoryDialog.kt      → core/ui/components ✅
├── AddScheduledForm.kt             → core/ui/components ✅
├── AddTransactionForm.kt           → core/ui/components ✅
├── AdvancedCharts.kt               → core/ui/components ✅
├── AdvancedDashboardCard.kt        → core/ui/components ✅
├── CategoryBudgetCard.kt           → core/ui/components ✅
├── DashboardCard.kt                → core/ui/components ✅
├── EditBudgetDialog.kt             → core/ui/components ✅
├── ExpensePieChart.kt              → core/ui/components ✅
├── FinancialInsightsCards.kt       → core/ui/components ✅
├── HomeHeader.kt                   → core/ui/components ✅
├── LoadingErrorStates.kt           → core/ui/components ✅
├── ProCards.kt                     → core/ui/components ✅
├── QuickActions.kt                 → core/ui/components ✅
├── SpendingLimitCard.kt            → core/ui/components ✅
└── TransactionItem.kt              → core/ui/components ✅
```

### app/feature/common/navigation/ (3 dosya)
```
app/feature/common/navigation/
├── Screen.kt                       → APP'TE KALACAK (root nav host'ta kullanılıyor)
├── NavGraph.kt                     → APP'TE KALACAK (root nav host)
└── BottomNavBar.kt                 → APP'TE KALACAK (app-level UI)
```

---

## 🎯 KARAR

### ✅ TAŞINACAK (core/ui/components)
- Tüm components/ altındaki 16 dosya
- Bunlar pure UI component'leri, tüm feature'lar kullanabilir
- Bağımlılık: domain model, ViewModel yok - sadece @Composable

### ⛔ KALACAK (app/feature/common/navigation)
- Screen.kt - Route tanımları (app-level)
- NavGraph.kt - Root navigation host (MainActivity'de kullanılıyor)
- BottomNavBar.kt - App-level bottom nav (tüm ekranları bağlıyor)

**Alternatif:** Navigation'ı core/navigation'a taşı ama şimdilik app'te kalması daha pratik.

---

## 📋 TAŞIMA ADIMLARI

### 1. core/ui/components klasörü oluştur
```bash
mkdir -p core/ui/src/main/java/com/example/HesapGunlugu/core/ui/components
```

### 2. Dosyaları kopyala (16 dosya)
Her dosya için:
1. İçeriği oku
2. Package'ı `com.hesapgunlugu.app.core.ui.components` olarak değiştir
3. core/ui/components altına yaz
4. Import'ları kontrol et

### 3. Import'ları güncelle
Tüm feature'larda:
```kotlin
// ESKİ
import com.hesapgunlugu.app.feature.common.components.*

// YENİ
import com.hesapgunlugu.app.core.ui.components.*
```

### 4. app/feature/common/components sil
Safe delete ile kontrol ederek sil.

---

## ⏱️ TAHMİNİ SÜRE

- Dosya kopyalama: 30 dakika
- Import güncelleme: 20 dakika
- Test + düzeltme: 10 dakika
- **Toplam:** 1 saat

---

## ✅ BAŞARI KRİTERLERİ

- [ ] core/ui/components altında 16 component var
- [ ] Tüm import'lar core.ui.components'ten geliyor
- [ ] app/feature/common/components silindi
- [ ] app/feature/common/navigation kaldı (bu doğru)
- [ ] Build başarılı
- [ ] Hiç duplicate yok

---

**Sonraki Aksiyon:** Component dosyalarını tek tek kopyalamaya başla

