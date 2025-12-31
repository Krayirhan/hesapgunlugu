# 🚀 RELEASE ÖNCESİ HIZLI BAŞLANGIÇ

Bu döküman, uygulamayı Play Store'a yüklemeden önce yapılması gereken 3 kritik adımı içerir.

---

## 1️⃣ FIREBASE KURULUMU (google-services.json)

### Adımlar:

1. **Firebase Console'a gidin:** https://console.firebase.google.com

2. **Yeni proje oluşturun** veya mevcut projeyi seçin

3. **Android uygulaması ekleyin:**
   - Package name: `com.hesapgunlugu.app` (veya `com.hesapgunlugu.app.free` / `.premium`)
   - App nickname: HesapGunlugu Finance Tracker
   - SHA-1 (opsiyonel, Google Sign-In için gerekli)

4. **google-services.json dosyasını indirin**

5. **Dosyayı kopyalayın:**
   ```
   app/google-services.json
   ```

6. **Doğrulama:**
   ```powershell
   Test-Path "app\google-services.json"  # True olmalı
   ```

### Firebase Özellikleri (Otomatik aktif):
- ✅ Crashlytics (crash reporting)
- ✅ Analytics (kullanım istatistikleri)
- ✅ Performance Monitoring

---

## 2️⃣ KEYSTORE OLUŞTURMA

### Yöntem A: Script ile (Önerilen)

```powershell
cd signing
.\create-keystore.ps1
```

Script interaktif olarak:
- Keystore şifresi sorar
- Sertifika bilgilerini alır
- `release-keystore.jks` oluşturur
- `local.properties`'i günceller

### Yöntem B: Android Studio ile

1. **Build** → **Generate Signed Bundle/APK**
2. **APK** seçin → **Next**
3. **Create new...** butonuna tıklayın
4. Bilgileri girin:
   | Alan | Değer |
   |------|-------|
   | Key store path | `signing/release-keystore.jks` |
   | Password | Güçlü şifre (12+ karakter) |
   | Alias | `HesapGunlugu-release` |
   | Validity | 25 years |

5. **local.properties**'e ekleyin:
   ```properties
   signing.storeFile=../signing/release-keystore.jks
   signing.storePassword=YOUR_PASSWORD
   signing.keyAlias=HesapGunlugu-release
   signing.keyPassword=YOUR_KEY_PASSWORD
   ```

### ⚠️ ÖNEMLİ:
- Keystore'u **YEDEKLE** (USB, cloud vault)
- Şifreleri **GÜVENLİ SAKLA** (1Password, LastPass)
- Keystore kaybı = Uygulama güncellenemez!

---

## 3️⃣ MANUEL TEST (3+ Cihaz)

### Test Cihazları Seçimi:

| Kategori | Örnek Cihazlar | Android |
|----------|----------------|---------|
| Düşük | Samsung A10, Xiaomi Redmi 9A | 9-10 |
| Orta | Samsung A52, Pixel 4a | 11-12 |
| Yüksek | Samsung S23, Pixel 8 | 13-14 |

### Hızlı Test Listesi:

```
□ Fresh install çalışıyor
□ Uygulama açılıyor (< 2 saniye)
□ İşlem ekleme çalışıyor
□ İşlem silme çalışıyor
□ Filtreleme çalışıyor
□ Grafikler gösteriliyor
□ Backup/restore çalışıyor
□ PIN kilidi çalışıyor
□ Dark mode düzgün
□ Rotasyonda veri korunuyor
```

### Detaylı Test:
Bkz: [docs/MANUAL_TEST_CHECKLIST.md](docs/MANUAL_TEST_CHECKLIST.md)

---

## 📋 RELEASE CHECKLIST

```
FIREBASE:
□ Firebase projesi oluşturuldu
□ google-services.json app/ klasöründe
□ Crashlytics aktif (Firebase Console'da)
□ Analytics aktif

KEYSTORE:
□ release-keystore.jks oluşturuldu
□ local.properties güncellendi
□ Keystore yedeklendi
□ Şifreler güvenli yerde

TEST:
□ En az 3 farklı cihazda test edildi
□ Critical bug yok
□ Performans kabul edilebilir
□ Accessibility test edildi

BUILD:
□ Release APK oluşturuldu
□ ProGuard hatası yok
□ APK size < 15MB
□ Tüm ekranlar çalışıyor
```

---

## 🏁 RELEASE BUILD OLUŞTURMA

Tüm adımlar tamamlandıktan sonra:

```powershell
# Release APK oluştur
./gradlew assembleFreeRelease assemblePremiumRelease

# Veya AAB (Play Store için önerilen)
./gradlew bundleFreeRelease bundlePremiumRelease
```

Çıktılar:
- `app/build/outputs/apk/free/release/app-free-release.apk`
- `app/build/outputs/apk/premium/release/app-premium-release.apk`
- `app/build/outputs/bundle/freeRelease/app-free-release.aab`
- `app/build/outputs/bundle/premiumRelease/app-premium-release.aab`

---

## 🆘 SORUN GİDERME

### google-services.json hatası
```
File google-services.json is missing
```
**Çözüm:** Firebase Console'dan indirip `app/` klasörüne kopyalayın.

### Keystore şifre hatası
```
Keystore was tampered with, or password was incorrect
```
**Çözüm:** `local.properties`'teki şifreleri kontrol edin.

### Release build imzalanmıyor
```
SigningConfig "release" is missing required property
```
**Çözüm:** `local.properties`'te tüm signing.* satırlarının doğru olduğundan emin olun.

---

## 📞 DESTEK

Sorun yaşarsanız:
1. `docs/` klasöründeki dökümanları inceleyin
2. `RELEASE_CHECKLIST.md` kontrol edin
3. GitHub Issues açın
