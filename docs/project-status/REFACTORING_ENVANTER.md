# 📋 MODÜL REFACTORING ENVANTER RAPORU

**Tarih:** 25 Aralık 2024  
**Amaç:** Clean Architecture sınırlarını zorlamak

---

## 🔍 MEVCUT DURUM ANALİZİ

### 1️⃣ app/feature/ içinde OLAN feature'lar (SİLİNECEK)
```
app/src/main/java/.../feature/
├── common/          → core/ui veya feature/common-ui'a taşınacak
├── history/         → feature/history modülü oluşturulacak
├── home/            → SİLİNECEK (feature/home zaten var)
├── notifications/   → feature/notifications oluşturulacak
├── onboarding/      → feature/onboarding oluşturulacak
├── privacy/         → feature/privacy oluşturulacak
├── scheduled/       → feature/scheduled oluşturulacak
├── settings/        → feature/settings oluşturulacak
└── statistics/      → feature/statistics oluşturulacak
```

### 2️⃣ feature/ modüllerinde ZATEN VAR
```
feature/
└── home/            → ✅ Zaten var (canonical kaynak)
```

### 3️⃣ app/domain/ (LEGACY - SİLİNECEK)
```
app/domain/
├── common/          → ?
├── model/           → core/domain/model'e taşındı mı kontrol et
└── repository/      → core/domain/repository'de zaten var
```

### 4️⃣ Navigation
```
Navigation route'ları:
- app/feature/common/navigation/Screen.kt
- app/feature/common/navigation/NavGraph.kt
- app/feature/common/navigation/BottomNavBar.kt

→ core/navigation modülüne taşınmalı veya app'te kalmalı (nav host)
```

---

## 🎯 REFACTORING PLANI

### Faz 1: Home Pilot Taşıma (1 saat)
1. ✅ feature/home zaten var
2. ❌ app/feature/home SİL
3. ✅ Navigation düzelt
4. ✅ Build test

### Faz 2: Legacy Domain Temizlik (30 dakika)
1. app/domain kullanımlarını bul
2. core/domain'e geçir
3. app/domain klasörünü SİL

### Faz 3: Common Components Taşıma (1 saat)
1. app/feature/common/components → core/ui/components
2. app/feature/common/navigation → app veya core/navigation

### Faz 4: Diğer Feature'ları Taşı (3 saat)
1. Settings (en basit)
2. History
3. Scheduled
4. Statistics
5. Notifications
6. Onboarding
7. Privacy

### Faz 5: DI Temizlik (30 dakika)
1. app/di modüllerini kontrol et
2. core/data DI oluştur
3. Gereksiz app/di sil

### Faz 6: Final Temizlik (30 dakika)
1. app/feature klasörü SİL
2. app/domain SİL
3. Build + Test
4. Dokümantasyon güncelle

---

## ✅ BAŞARI KRİTERLERİ

- [ ] app/feature/ klasörü yok
- [ ] app/domain/ klasörü yok
- [ ] Her feature kendi modülünde
- [ ] Domain sadece core/domain'de
- [ ] Data sadece core/data'da
- [ ] UI components core/ui'da
- [ ] Build başarılı
- [ ] Testler geçiyor
- [ ] Sıfır duplicate kod

---

## 📊 TAHMINI SÜRE

- **Toplam:** 6-7 saat
- **Kritik path:** Legacy domain temizlik
- **Risk:** Navigation bağımlılıkları

---

**Sonraki Aksiyon:** Faz 1 başlat - Home pilot taşıma

