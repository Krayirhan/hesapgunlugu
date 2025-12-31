# Mimari Dokümantasyon

Bu klasör projenin mimari yapısı ve hiyerarşisi hakkında bilgi içerir.

## İçerik

### Proje Hiyerarşisi
- **PROJE_DOSYA_HIYERARSISI.md** - Detaylı dosya hiyerarşisi ve açıklamalar
- **PROJECT_HIERARCHY.md** - Genel proje yapısı
- **PROJECT_HIERARCHY_DETAILED.md** - Detaylı hiyerarşi
- **FULL_HIERARCHY.txt** - Tüm dosyaların listesi

### Mimari Değerlendirme
- **architecture-audit-20251225_025618.txt** - Mimari denetim raporu
- **SENIOR_EVALUATION_REPORT.md** - Senior seviye değerlendirme
- **SENIOR_LEVEL_IMPROVEMENTS.md** - İyileştirme önerileri

### Geliştirmeler
- **README_IMPROVEMENTS.md** - README geliştirme önerileri

## Mimari Prensipler

Proje **Clean Architecture** + **Multi-Module** yapısını kullanır:

```
app (UI Layer)
  ↓
feature modules (Feature Layer)
  ↓
core modules (Domain + Data + UI Components)
```

> **Detaylı bilgi için:** `adr/` klasöründeki Architecture Decision Records dosyalarına bakın.
