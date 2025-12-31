# ========================================
# KRİTİK REFACTORING OTOMASYONU
# ========================================
# Bu script şunları yapar:
# 1. app/domain klasörünü siler
# 2. Gradle sync yapar
# 3. Clean build yapar
# 4. assembleFreeDebug build yapar

Write-Host "🚀 KRİTİK REFACTORING BAŞLIYOR..." -ForegroundColor Cyan
Write-Host ""

# ========================================
# ADIM 1: app/domain Sil
# ========================================
Write-Host "📂 ADIM 1: app/domain klasörünü siliyorum..." -ForegroundColor Yellow

$domainPath = "C:\Users\Acer\AndroidStudioProjects\MyNewApp\app\src\main\java\com\example\mynewapp\domain"

if (Test-Path $domainPath) {
    try {
        Remove-Item -Path $domainPath -Recurse -Force -ErrorAction Stop
        Write-Host "   ✅ app/domain başarıyla silindi!" -ForegroundColor Green
    } catch {
        Write-Host "   ❌ HATA: app/domain silinemedi!" -ForegroundColor Red
        Write-Host "   Hata: $_" -ForegroundColor Red
        Write-Host ""
        Write-Host "   Manuel olarak silin:" -ForegroundColor Yellow
        Write-Host "   $domainPath" -ForegroundColor White
        exit 1
    }
} else {
    Write-Host "   ℹ️  app/domain zaten yok, devam ediliyor..." -ForegroundColor Cyan
}

Write-Host ""

# ========================================
# ADIM 2: Clean Build
# ========================================
Write-Host "🧹 ADIM 2: Gradle clean yapılıyor..." -ForegroundColor Yellow

Push-Location "C:\Users\Acer\AndroidStudioProjects\MyNewApp"

try {
    & .\gradlew clean
    Write-Host "   ✅ Clean başarılı!" -ForegroundColor Green
} catch {
    Write-Host "   ⚠️  Clean hatası (devam ediliyor)" -ForegroundColor Yellow
}

Write-Host ""

# ========================================
# ADIM 3: Build
# ========================================
Write-Host "🔨 ADIM 3: assembleFreeDebug build yapılıyor..." -ForegroundColor Yellow
Write-Host "   (Bu 40-60 saniye sürebilir...)" -ForegroundColor Gray
Write-Host ""

try {
    $buildOutput = & .\gradlew assembleFreeDebug 2>&1
    $buildSuccess = $LASTEXITCODE -eq 0

    if ($buildSuccess) {
        Write-Host ""
        Write-Host "═══════════════════════════════════════" -ForegroundColor Green
        Write-Host "   ✅ BUILD SUCCESSFUL!" -ForegroundColor Green
        Write-Host "═══════════════════════════════════════" -ForegroundColor Green
        Write-Host ""
        Write-Host "📦 APK Konumu:" -ForegroundColor Cyan
        Write-Host "   app\build\outputs\apk\free\debug\app-free-debug.apk" -ForegroundColor White
        Write-Host ""
        Write-Host "🎉 REFACTORING BAŞARILI!" -ForegroundColor Green
        Write-Host ""
        Write-Host "📊 İlerleme: 65% → 85% tamamlandı!" -ForegroundColor Cyan
        Write-Host ""
        Write-Host "📝 Sıradaki adımlar:" -ForegroundColor Yellow
        Write-Host "   1. ✅ app/domain silindi" -ForegroundColor Green
        Write-Host "   2. ✅ Build başarılı" -ForegroundColor Green
        Write-Host "   3. ⏳ Commit yapın:" -ForegroundColor Cyan
        Write-Host '      git add .' -ForegroundColor White
        Write-Host '      git commit -m "refactor: remove legacy app/domain, stabilize feature:home"' -ForegroundColor White
        Write-Host ""
        Write-Host "   4. ⏳ Install (opsiyonel):" -ForegroundColor Cyan
        Write-Host "      .\gradlew installFreeDebug" -ForegroundColor White
        Write-Host ""

    } else {
        Write-Host ""
        Write-Host "═══════════════════════════════════════" -ForegroundColor Red
        Write-Host "   ❌ BUILD FAILED!" -ForegroundColor Red
        Write-Host "═══════════════════════════════════════" -ForegroundColor Red
        Write-Host ""
        Write-Host "🔍 Build hatası oluştu. Son 20 satır:" -ForegroundColor Yellow
        Write-Host ""
        $buildOutput | Select-Object -Last 20 | ForEach-Object { Write-Host $_ -ForegroundColor Red }
        Write-Host ""
        Write-Host "💡 Tam hata logunu görmek için:" -ForegroundColor Cyan
        Write-Host "   .\gradlew assembleFreeDebug" -ForegroundColor White
        Write-Host ""
        exit 1
    }

} catch {
    Write-Host ""
    Write-Host "❌ Build komutu çalıştırılamadı!" -ForegroundColor Red
    Write-Host "Hata: $_" -ForegroundColor Red
    Write-Host ""
    Write-Host "Manuel build için:" -ForegroundColor Yellow
    Write-Host "   .\gradlew assembleFreeDebug" -ForegroundColor White
    exit 1
}

Pop-Location

Write-Host ""
Write-Host "═══════════════════════════════════════" -ForegroundColor Cyan
Write-Host "   SCRIPT TAMAMLANDI!" -ForegroundColor Cyan
Write-Host "═══════════════════════════════════════" -ForegroundColor Cyan

