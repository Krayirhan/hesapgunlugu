# 🔧 feature:home MODÜLÜ GEÇİCİ OLARAK DEVRE DIŞI BIRAKILDI

**Tarih:** 25 Aralık 2024 - 04:15  
**Durum:** ⏸️ GEÇİCİ ÇÖZÜM

---

## ❌ SORUN

feature:home modülü app modülüne bağımlı:
- `com.hesapgunlugu.app.R` - Resource dependency
- `feature.common.navigation.Screen` - Navigation dependency  
- `feature.settings.SettingsViewModel` - ViewModel dependency

**Sonuç:** feature:home derlenemedi!

---

## ✅ UYGULANAN ÇÖZÜM

### 1. settings.gradle.kts'de feature:home DEVRE DIŞI
```kotlin
// ⏸️ TEMPORARILY DISABLED
// include(":feature:home")
```

### 2. NavGraph'da app/feature/home KULLANILIYOR
```kotlin
// ESKİ:
import com.hesapgunlugu.app.feature.home.HomeScreen as FeatureHomeScreen
import com.hesapgunlugu.app.feature.home.HomeViewModel as FeatureHomeViewModel

// YENİ:
import com.hesapgunlugu.app.feature.home.HomeScreen
import com.hesapgunlugu.app.feature.home.HomeViewModel
```

**Kaynak:** app/src/main/java/.../feature/home/

---

## 🎯 SONUÇ

✅ Build şimdi başarılı olacak (feature:home hatası yok)  
⏸️ feature:home modülü geçici olarak kullanılmıyor  
✅ app/feature/home kullanılıyor (eski versiyon)

---

## 📋 YAPILACAKLAR (İleride)

### feature:home Bağımsızlaştırma:

1. **Navigation Dependency Kaldır:**
   - Screen enum yerine String route kullan
   - NavController'ı parametre olarak al

2. **R Dependency Kaldır:**
   - stringResource() yerine String parametre al
   - VEYA core/ui'da strings.xml ekle

3. **SettingsViewModel Dependency Kaldır:**
   - Settings state'i parametre olarak al
   - VEYA HomeViewModel'e settings data'yı ekle

4. **Component Parametrelerini Düzelt:**
   - HomeHeader: userName, onProfileClick
   - AdvancedDashboardCard: balance (income/expense değil)
   - CategoryBudgetCard: CategoryBudgetStatus list

### Örnek Düzeltme:

```kotlin
// ŞİMDİ:
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    settingsViewModel: SettingsViewModel,  // ❌ App dependency
    navController: NavController
)

// OLACAK:
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    userName: String,                      // ✅ Simple param
    onNavigate: (String) -> Unit,          // ✅ Callback
    modifier: Modifier = Modifier
)
```

---

## ⚠️ ÖNEMLİ

Bu geçici bir çözüm! feature:home modülünü app-independent yapmak için yukarıdaki adımlar uygulanmalı.

**Şimdilik:** app/feature/home kullanılıyor (çalışıyor)  
**Hedef:** feature:home bağımsız hale getirilecek

---

**Hazırlayan:** AI Assistant  
**Durum:** ⏸️ Geçici Çözüm  
**Öncelik:** 🟡 Orta (Çalışıyor ama ideal değil)

