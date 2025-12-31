# 🔧 feature:home BAĞIMSIZLAŞTIRMA PLANI

**Sorun:** feature:home modülü app modülüne bağımlı (Screen, SettingsViewModel, R)

## 🎯 ÇÖZÜM STRATEJİSİ

### Seçenek 1: feature:home'ı Basitleştir (KOLAY) ✅
- Screen kullanımlarını String route'lara dönüştür
- R.string kullanımlarını hardcoded string yap  
- SettingsViewModel bağımlılığını kaldır
- Navigation callback'leri parametre olarak al

### Seçenek 2: Gerekli Dependency'leri Ekle (ORTA)
- feature:home → app dependency ekle (anti-pattern!)
- VEYA core/navigation modülü oluştur

### Seçenek 3: feature:home'ı Şimdilik Devre Dışı Bırak (GEÇİCİ)
- app/feature/home'u kullan
- feature/home'u sonra düzelt

## ✅ SEÇİLEN: Seçenek 1 - Basitleştirme

feature:home'ı tamamen bağımsız hale getireceğiz.

