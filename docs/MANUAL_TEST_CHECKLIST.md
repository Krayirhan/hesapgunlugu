# 🧪 Manual Test Checklist - Release Öncesi

**Proje:** HesapGunlugu Finance Tracker  
**Versiyon:** 1.0.0  
**Test Tarihi:** ____/____/2025  
**Tester:** ________________________

---

## 📱 Test Cihazları (En az 3 farklı cihaz)

| # | Cihaz Model | Android Versiyon | Ekran Boyutu | Test Durumu |
|---|-------------|------------------|--------------|-------------|
| 1 | _____________ | Android ___ | ___" | ⬜ Geçti / ⬜ Kaldı |
| 2 | _____________ | Android ___ | ___" | ⬜ Geçti / ⬜ Kaldı |
| 3 | _____________ | Android ___ | ___" | ⬜ Geçti / ⬜ Kaldı |

### Önerilen Test Kombinasyonları:
- **Düşük:** Android 8/9, 5" ekran, 2GB RAM
- **Orta:** Android 11/12, 6" ekran, 4GB RAM  
- **Yüksek:** Android 13/14, 6.5"+ ekran, 8GB+ RAM

---

## 1️⃣ KURULUM TESTLERİ

| Test | Beklenen | Cihaz 1 | Cihaz 2 | Cihaz 3 |
|------|----------|---------|---------|---------|
| Fresh install | Sorunsuz kurulum | ⬜ | ⬜ | ⬜ |
| Upgrade install | Veri korunur | ⬜ | ⬜ | ⬜ |
| Uninstall/reinstall | Temiz başlangıç | ⬜ | ⬜ | ⬜ |
| İzin dialogs | Doğru gösterilir | ⬜ | ⬜ | ⬜ |

---

## 2️⃣ ONBOARDING TESTLERİ

| Test | Beklenen | Cihaz 1 | Cihaz 2 | Cihaz 3 |
|------|----------|---------|---------|---------|
| İlk açılış | Onboarding gösterilir | ⬜ | ⬜ | ⬜ |
| Skip butonu | Ana ekrana geçer | ⬜ | ⬜ | ⬜ |
| Tüm adımlar | Smooth geçiş | ⬜ | ⬜ | ⬜ |

---

## 3️⃣ ANA EKRAN (HOME) TESTLERİ

| Test | Beklenen | Cihaz 1 | Cihaz 2 | Cihaz 3 |
|------|----------|---------|---------|---------|
| Bakiye gösterimi | Doğru hesaplama | ⬜ | ⬜ | ⬜ |
| Gelir/Gider özeti | Doğru rakamlar | ⬜ | ⬜ | ⬜ |
| Son işlemler listesi | 5 işlem gösterilir | ⬜ | ⬜ | ⬜ |
| Pull-to-refresh | Veri yenilenir | ⬜ | ⬜ | ⬜ |
| FAB butonu | İşlem ekleme açılır | ⬜ | ⬜ | ⬜ |

---

## 4️⃣ İŞLEM EKLEME/DÜZENLEME

| Test | Beklenen | Cihaz 1 | Cihaz 2 | Cihaz 3 |
|------|----------|---------|---------|---------|
| Gelir ekleme | Başarıyla kaydedilir | ⬜ | ⬜ | ⬜ |
| Gider ekleme | Başarıyla kaydedilir | ⬜ | ⬜ | ⬜ |
| Kategori seçimi | Tüm kategoriler çalışır | ⬜ | ⬜ | ⬜ |
| Tarih seçimi | DatePicker çalışır | ⬜ | ⬜ | ⬜ |
| Miktar validasyonu | Negatif değer reddedilir | ⬜ | ⬜ | ⬜ |
| İşlem düzenleme | Değişiklik kaydedilir | ⬜ | ⬜ | ⬜ |
| İşlem silme | Onay sonrası silinir | ⬜ | ⬜ | ⬜ |

---

## 5️⃣ GEÇMİŞ (HISTORY) TESTLERİ

| Test | Beklenen | Cihaz 1 | Cihaz 2 | Cihaz 3 |
|------|----------|---------|---------|---------|
| Tüm işlemler listesi | Scroll çalışır | ⬜ | ⬜ | ⬜ |
| Filtreleme (Gelir) | Sadece gelirler | ⬜ | ⬜ | ⬜ |
| Filtreleme (Gider) | Sadece giderler | ⬜ | ⬜ | ⬜ |
| Sıralama (Tarih) | Doğru sıralama | ⬜ | ⬜ | ⬜ |
| Sıralama (Tutar) | Doğru sıralama | ⬜ | ⬜ | ⬜ |
| Arama | Doğru sonuçlar | ⬜ | ⬜ | ⬜ |

---

## 6️⃣ İSTATİSTİKLER TESTLERİ

| Test | Beklenen | Cihaz 1 | Cihaz 2 | Cihaz 3 |
|------|----------|---------|---------|---------|
| Pie chart | Kategoriler gösterilir | ⬜ | ⬜ | ⬜ |
| Bar chart | Aylık trend gösterilir | ⬜ | ⬜ | ⬜ |
| Tarih aralığı seçimi | Grafikler güncellenir | ⬜ | ⬜ | ⬜ |
| Veri olmadan | Boş state gösterilir | ⬜ | ⬜ | ⬜ |

---

## 7️⃣ ZAMANLANMIŞ ÖDEMELER

| Test | Beklenen | Cihaz 1 | Cihaz 2 | Cihaz 3 |
|------|----------|---------|---------|---------|
| Ödeme ekleme | Başarıyla kaydedilir | ⬜ | ⬜ | ⬜ |
| Tekrar seçenekleri | Tümü çalışır | ⬜ | ⬜ | ⬜ |
| Bildirim hatırlatması | Zamanında gelir | ⬜ | ⬜ | ⬜ |
| Ödeme tamamlama | Statü güncellenir | ⬜ | ⬜ | ⬜ |

---

## 8️⃣ AYARLAR TESTLERİ

| Test | Beklenen | Cihaz 1 | Cihaz 2 | Cihaz 3 |
|------|----------|---------|---------|---------|
| Tema değişikliği | Anında uygulanır | ⬜ | ⬜ | ⬜ |
| Dil değişikliği | Tüm metinler güncellenir | ⬜ | ⬜ | ⬜ |
| Para birimi | Doğru sembol | ⬜ | ⬜ | ⬜ |
| Bütçe ayarlama | Kaydedilir | ⬜ | ⬜ | ⬜ |
| Bildirim ayarları | Çalışır | ⬜ | ⬜ | ⬜ |

---

## 9️⃣ GÜVENLİK TESTLERİ

| Test | Beklenen | Cihaz 1 | Cihaz 2 | Cihaz 3 |
|------|----------|---------|---------|---------|
| PIN oluşturma | 4-6 haneli kabul | ⬜ | ⬜ | ⬜ |
| PIN doğrulama | Doğru PIN geçer | ⬜ | ⬜ | ⬜ |
| Yanlış PIN (3x) | Lockout aktif | ⬜ | ⬜ | ⬜ |
| Biometric (varsa) | Parmak izi çalışır | ⬜ | ⬜ | ⬜ |
| App lock | Background'dan dönüşte | ⬜ | ⬜ | ⬜ |

---

## 🔟 BACKUP/RESTORE TESTLERİ

| Test | Beklenen | Cihaz 1 | Cihaz 2 | Cihaz 3 |
|------|----------|---------|---------|---------|
| JSON export | Dosya oluşur | ⬜ | ⬜ | ⬜ |
| JSON import | Veri geri yüklenir | ⬜ | ⬜ | ⬜ |
| Şifreli backup | Şifre sorulur | ⬜ | ⬜ | ⬜ |
| CSV export | Dosya açılabilir | ⬜ | ⬜ | ⬜ |
| PDF export | Rapor oluşur | ⬜ | ⬜ | ⬜ |

---

## 1️⃣1️⃣ PERFORMANS TESTLERİ

| Test | Beklenen | Cihaz 1 | Cihaz 2 | Cihaz 3 |
|------|----------|---------|---------|---------|
| Cold start | < 2 saniye | ⬜ ___s | ⬜ ___s | ⬜ ___s |
| Warm start | < 1 saniye | ⬜ ___s | ⬜ ___s | ⬜ ___s |
| Liste scroll | 60fps, jank yok | ⬜ | ⬜ | ⬜ |
| 100+ işlem | Hızlı yanıt | ⬜ | ⬜ | ⬜ |
| Hafıza kullanımı | < 150MB | ⬜ ___MB | ⬜ ___MB | ⬜ ___MB |

---

## 1️⃣2️⃣ EDGE CASE TESTLERİ

| Test | Beklenen | Cihaz 1 | Cihaz 2 | Cihaz 3 |
|------|----------|---------|---------|---------|
| Offline kullanım | Tüm özellikler çalışır | ⬜ | ⬜ | ⬜ |
| Düşük pil | Normal çalışma | ⬜ | ⬜ | ⬜ |
| Rotasyon | Veri korunur | ⬜ | ⬜ | ⬜ |
| Sistem kill & restore | State korunur | ⬜ | ⬜ | ⬜ |
| İnterrupt (arama) | Veri kaybı yok | ⬜ | ⬜ | ⬜ |
| Font scaling (200%) | UI düzgün | ⬜ | ⬜ | ⬜ |
| Dark mode | Tüm ekranlar OK | ⬜ | ⬜ | ⬜ |

---

## 1️⃣3️⃣ ERİŞİLEBİLİRLİK TESTLERİ

| Test | Beklenen | Cihaz 1 | Cihaz 2 | Cihaz 3 |
|------|----------|---------|---------|---------|
| TalkBack navigasyon | Tüm elementler okunur | ⬜ | ⬜ | ⬜ |
| Touch target | 48dp minimum | ⬜ | ⬜ | ⬜ |
| Kontrast | Okunaklı metin | ⬜ | ⬜ | ⬜ |

---

## 📝 BULUNAN HATALAR

| # | Açıklama | Cihaz | Severity | Çözüldü? |
|---|----------|-------|----------|----------|
| 1 | | | ⬜ Low ⬜ Med ⬜ High ⬜ Critical | ⬜ |
| 2 | | | ⬜ Low ⬜ Med ⬜ High ⬜ Critical | ⬜ |
| 3 | | | ⬜ Low ⬜ Med ⬜ High ⬜ Critical | ⬜ |
| 4 | | | ⬜ Low ⬜ Med ⬜ High ⬜ Critical | ⬜ |
| 5 | | | ⬜ Low ⬜ Med ⬜ High ⬜ Critical | ⬜ |

---

## ✅ SONUÇ

| Kriter | Durum |
|--------|-------|
| Critical bug sayısı | ⬜ 0 (RELEASE OK) / ⬜ 1+ (BLOCK) |
| High bug sayısı | ___ adet |
| Toplam test | ___/75 geçti |
| Release onayı | ⬜ ONAYLANDI / ⬜ REDDEDİLDİ |

**Test eden:** ________________________  
**Tarih:** ____/____/2025  
**İmza:** ________________________
