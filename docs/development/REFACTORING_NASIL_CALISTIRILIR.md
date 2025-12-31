# 🚀 KRİTİK REFACTORING - ÇALIŞTIRMA REHBERİ

## 🎯 AMAÇ
Bu script'ler şunları yapar:
1. ✅ `app/domain` klasörünü siler
2. ✅ Gradle clean yapar
3. ✅ Build test eder (assembleFreeDebug)

---

## 📦 2 SEÇENEK VAR

### ✨ SEÇENEK 1: Detaylı Script (ÖNERİLEN)

**Özellikler:**
- ✅ Adım adım gösterir
- ✅ Hataları raporlar
- ✅ Başarı/başarısızlık mesajları
- ✅ APK konumunu gösterir

**Çalıştırma:**
```powershell
.\scripts\critical-refactoring.ps1
```

---

### ⚡ SEÇENEK 2: Hızlı Script

**Özellikler:**
- ⚡ Tek satırda çalışır
- 🎯 Minimum output

**Çalıştırma:**
```powershell
.\quick-refactor.ps1
```

---

## 🔧 MANUEL ÇALIŞTIRMA

Eğer script çalışmazsa manuel olarak:

```powershell
# 1. app/domain sil
Remove-Item -Path "app\src\main\java\com\example\HesapGunlugu\domain" -Recurse -Force

# 2. Clean
.\gradlew clean

# 3. Build
.\gradlew assembleFreeDebug
```

---

## ⚠️ EXECUTION POLICY HATASI ALIRSAN

Eğer şu hatayı alırsan:
```
cannot be loaded because running scripts is disabled on this system
```

**Çözüm:**
```powershell
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

Sonra script'i tekrar çalıştır.

---

## ✅ BAŞARILI OLUNCA GÖRECEKLER

```
═══════════════════════════════════════
   ✅ BUILD SUCCESSFUL!
═══════════════════════════════════════

📦 APK Konumu:
   app\build\outputs\apk\free\debug\app-free-debug.apk

🎉 REFACTORING BAŞARILI!

📊 İlerleme: 65% → 85% tamamlandı!
```

---

## 🎯 BAŞARILI OLUNCA YAPILACAKLAR

### 1. Commit Yap
```bash
git add .
git commit -m "refactor: remove legacy app/domain, stabilize feature:home"
```

### 2. Install (Opsiyonel)
```bash
.\gradlew installFreeDebug
```

### 3. Test Et
Uygulamayı telefonunuzda/emulatörde açın ve kontrol edin.

---

## ❌ BUILD BAŞARISIZ OLURSA

Script hata satırlarını gösterecek. Hatayı bana bildirin!

**Tam log için:**
```bash
.\gradlew assembleFreeDebug --stacktrace
```

---

## 📊 NE DEĞİŞTİ?

**Öncesi:**
```
app/
├── domain/              ❌ Legacy kod
│   ├── model/
│   ├── repository/
│   └── common/
└── feature/
    ├── home/            ❌ app'te
    ├── settings/
    └── ...
```

**Sonrası:**
```
app/
└── feature/
    ├── common/          ✅ Sadece navigation
    ├── settings/
    └── ...

feature/
└── home/                ✅ Bağımsız modül

core/
├── domain/              ✅ Tek kaynak
├── data/
└── ui/
```

---

## 🚀 HEMEN ÇALIŞTIR!

**Önerilen:**
```powershell
.\scripts\critical-refactoring.ps1
```

**Hızlı:**
```powershell
.\quick-refactor.ps1
```

---

**Not:** Bu işlem geri alınamaz! Ama zaten `app/domain` kullanılmıyor, güvenle silebilirsiniz. 🎯

