# Mimari Doğrulama Script'i
# Windows PowerShell
# Çalıştırma: .\validate-architecture.ps1

Write-Host "`n==================================================" -ForegroundColor Cyan
Write-Host "🏗️  MİMARİ DOĞRULAMA BAŞLIYOR..." -ForegroundColor Cyan
Write-Host "==================================================`n" -ForegroundColor Cyan

$violations = @()
$warnings = @()
$passed = @()

# ============================================================
# 1️⃣ FEATURE → core:data BOUNDARY KONTROLÜ
# ============================================================
Write-Host "1️⃣  Feature → core:data boundary kontrolü..." -ForegroundColor Yellow

$featureDataImports = Select-String -Path "feature\**\*.kt" -Pattern "import com.hesapgunlugu.app.core.data" -Recurse -ErrorAction SilentlyContinue

if ($featureDataImports) {
    $violations += "BOUNDARY İHLALİ - Feature modülleri core:data import ediyor:"
    foreach ($import in $featureDataImports) {
        $filePath = $import.Path.Replace("$PWD\", "")
        $violations += "  ❌ $filePath (satır $($import.LineNumber))"
        Write-Host "  ❌ $filePath" -ForegroundColor Red
    }
} else {
    $passed += "Feature modülleri core:data import etmiyor ✅"
    Write-Host "  ✅ Feature modülleri temiz" -ForegroundColor Green
}

# ============================================================
# 2️⃣ core:domain → core:data BOUNDARY KONTROLÜ (main source)
# ============================================================
Write-Host "`n2️⃣  core:domain → core:data boundary kontrolü (main source)..." -ForegroundColor Yellow

$domainDataImports = Select-String -Path "core\domain\src\main\**\*.kt" -Pattern "import com.hesapgunlugu.app.core.data" -Recurse -ErrorAction SilentlyContinue

if ($domainDataImports) {
    $violations += "BOUNDARY İHLALİ - core:domain main source core:data import ediyor:"
    foreach ($import in $domainDataImports) {
        $filePath = $import.Path.Replace("$PWD\", "")
        $violations += "  ❌ $filePath (satır $($import.LineNumber))"
        Write-Host "  ❌ $filePath" -ForegroundColor Red
    }
} else {
    $passed += "core:domain main source core:data import etmiyor ✅"
    Write-Host "  ✅ core:domain temiz" -ForegroundColor Green
}

# ============================================================
# 3️⃣ NAVIGATION TEK KAYNAK KONTROLÜ
# ============================================================
Write-Host "`n3️⃣  Navigation tek kaynak kontrolü..." -ForegroundColor Yellow

$navGraphFiles = Get-ChildItem -Path . -Recurse -Filter "*NavHost*.kt","*NavGraph*.kt" -File -ErrorAction SilentlyContinue |
    Where-Object { $_.FullName -notmatch "\\build\\" }

if ($navGraphFiles.Count -gt 1) {
    $warnings += "NAVIGATION UYARI - Birden fazla NavGraph dosyası bulundu:"
    foreach ($file in $navGraphFiles) {
        $filePath = $file.FullName.Replace("$PWD\", "")
        $warnings += "  ⚠️  $filePath"
        Write-Host "  ⚠️  $filePath" -ForegroundColor Yellow
    }
} elseif ($navGraphFiles.Count -eq 1) {
    $navPath = $navGraphFiles[0].FullName.Replace("$PWD\", "")
    $passed += "Navigation tek kaynak: $navPath ✅"
    Write-Host "  ✅ $navPath" -ForegroundColor Green
} else {
    $violations += "NAVIGATION HATA - NavGraph dosyası bulunamadı"
    Write-Host "  ❌ NavGraph bulunamadı" -ForegroundColor Red
}

# ============================================================
# 4️⃣ app MODÜLÜNDEKİ LEGACY domain/data KONTROLÜ
# ============================================================
Write-Host "`n4️⃣  app modülünde legacy domain/data kontrolü..." -ForegroundColor Yellow

$legacyDirs = Get-ChildItem -Path "app\src\main\java\com\example\mynewapp" -Directory -Recurse -ErrorAction SilentlyContinue |
    Where-Object { $_.Name -match "^(domain|data|repository)$" -and $_.FullName -notmatch "feature\\common\\navigation" }

if ($legacyDirs) {
    $violations += "LEGACY KALINTI - app içinde domain/data klasörleri bulundu:"
    foreach ($dir in $legacyDirs) {
        $dirPath = $dir.FullName.Replace("$PWD\", "")
        $violations += "  ❌ $dirPath"
        Write-Host "  ❌ $dirPath" -ForegroundColor Red
    }
} else {
    $passed += "app modülü temiz (legacy domain/data yok) ✅"
    Write-Host "  ✅ app modülü temiz" -ForegroundColor Green
}

# ============================================================
# 5️⃣ VIEWMODEL → SETTINGSMANAGER DOĞRUDAN KULLANIMI
# ============================================================
Write-Host "`n5️⃣  ViewModel → SettingsManager doğrudan kullanım kontrolü..." -ForegroundColor Yellow

$settingsManagerUsage = Select-String -Path "feature\**\*ViewModel.kt" -Pattern "SettingsManager" -Recurse -ErrorAction SilentlyContinue

if ($settingsManagerUsage) {
    $violations += "BOUNDARY İHLALİ - ViewModel'lar SettingsManager (data layer) kullanıyor:"
    foreach ($usage in $settingsManagerUsage) {
        $filePath = $usage.Path.Replace("$PWD\", "")
        $violations += "  ❌ $filePath (satır $($usage.LineNumber)): $($usage.Line.Trim())"
        Write-Host "  ❌ $filePath" -ForegroundColor Red
    }
} else {
    $passed += "ViewModel'lar SettingsRepository (domain) kullanıyor ✅"
    Write-Host "  ✅ ViewModel'lar temiz" -ForegroundColor Green
}

# ============================================================
# 6️⃣ HILT MODULE KONTROLÜ
# ============================================================
Write-Host "`n6️⃣  Hilt modül kontrolü..." -ForegroundColor Yellow

$hiltModules = Select-String -Path "app\src\main\java\com\example\mynewapp\di\*.kt" -Pattern "@Module" -ErrorAction SilentlyContinue

if ($hiltModules) {
    $moduleCount = ($hiltModules | Select-Object -Unique Path).Count
    $passed += "Hilt modül sayısı: $moduleCount ✅"
    Write-Host "  ✅ $moduleCount Hilt modülü bulundu" -ForegroundColor Green
    foreach ($module in ($hiltModules | Select-Object -Unique Path)) {
        $moduleName = [System.IO.Path]::GetFileNameWithoutExtension($module.Path)
        Write-Host "     - $moduleName" -ForegroundColor Gray
    }
} else {
    $warnings += "UYARI - Hilt modülü bulunamadı (app/di/*.kt)"
    Write-Host "  ⚠️  Hilt modülü bulunamadı" -ForegroundColor Yellow
}

# ============================================================
# 📊 ÖZET RAPOR
# ============================================================
Write-Host "`n==================================================`n" -ForegroundColor Cyan

if ($violations.Count -eq 0 -and $warnings.Count -eq 0) {
    Write-Host "✅ ✅ ✅  TÜM KONTROLLERDEN GEÇTİ!  ✅ ✅ ✅`n" -ForegroundColor Green
    Write-Host "Mimari Sağlık Skoru: 100/100 🎉`n" -ForegroundColor Green
} else {
    if ($violations.Count -gt 0) {
        Write-Host "❌ BOUNDARY İHLALLERİ BULUNDU ($($violations.Count) adet)`n" -ForegroundColor Red
        foreach ($v in $violations) {
            Write-Host $v -ForegroundColor Red
        }
        Write-Host ""
    }

    if ($warnings.Count -gt 0) {
        Write-Host "⚠️  UYARILAR ($($warnings.Count) adet)`n" -ForegroundColor Yellow
        foreach ($w in $warnings) {
            Write-Host $w -ForegroundColor Yellow
        }
        Write-Host ""
    }
}

Write-Host "✅ BAŞARILI KONTROLER ($($passed.Count) adet)`n" -ForegroundColor Green
foreach ($p in $passed) {
    Write-Host "  $p" -ForegroundColor Green
}

Write-Host "`n==================================================`n" -ForegroundColor Cyan

if ($violations.Count -eq 0) {
    Write-Host "✅ MİMARİ DOĞRULAMA BAŞARILI!" -ForegroundColor Green
    exit 0
} else {
    Write-Host "❌ MİMARİ DOĞRULAMA BAŞARISIZ!" -ForegroundColor Red
    Write-Host "Lütfen yukarıdaki ihlalleri düzeltin.`n" -ForegroundColor Red
    exit 1
}

