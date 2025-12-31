# Gradle Cache Temizleme ve Build Script
Write-Host "==================================" -ForegroundColor Cyan
Write-Host "Gradle Cache Temizleniyor..." -ForegroundColor Yellow
Write-Host "==================================" -ForegroundColor Cyan
Write-Host ""

# 1. Gradle daemon'ı durdur
Write-Host "[1/5] Gradle daemon durduruluyor..." -ForegroundColor Green
& .\gradlew.bat --stop
Start-Sleep -Seconds 2

# 2. Gradle cache temizle
$gradleCachePath = "$env:USERPROFILE\.gradle\caches"
if (Test-Path $gradleCachePath) {
    Write-Host "[2/5] Gradle cache temizleniyor: $gradleCachePath" -ForegroundColor Green

    # transforms klasörünü özellikle temizle
    $transformsPath = "$gradleCachePath\transforms-*"
    Get-ChildItem -Path $gradleCachePath -Filter "transforms-*" -Directory | ForEach-Object {
        Write-Host "  - Siliniyor: $($_.FullName)" -ForegroundColor Gray
        Remove-Item -Path $_.FullName -Recurse -Force -ErrorAction SilentlyContinue
    }

    # 8.13 cache'i temizle
    $cache813 = "$gradleCachePath\8.13"
    if (Test-Path $cache813) {
        Write-Host "  - Siliniyor: $cache813" -ForegroundColor Gray
        Remove-Item -Path $cache813 -Recurse -Force -ErrorAction SilentlyContinue
    }

    Write-Host "  Cache temizlendi!" -ForegroundColor Green
} else {
    Write-Host "  Cache klasörü bulunamadı." -ForegroundColor Yellow
}

# 3. Build klasörlerini temizle
Write-Host "[3/5] Build klasörleri temizleniyor..." -ForegroundColor Green
& .\gradlew.bat clean
Start-Sleep -Seconds 2

# 4. .gradle klasörünü temizle (local)
$localGradle = ".gradle"
if (Test-Path $localGradle) {
    Write-Host "[4/5] Local .gradle klasörü temizleniyor..." -ForegroundColor Green
    Remove-Item -Path $localGradle -Recurse -Force -ErrorAction SilentlyContinue
    Write-Host "  Temizlendi!" -ForegroundColor Green
}

# 5. Yeniden build
Write-Host "[5/5] Proje build ediliyor..." -ForegroundColor Green
Write-Host ""
Write-Host "Build komutu: gradlew assembleFreeDebug" -ForegroundColor Cyan
Write-Host ""

& .\gradlew.bat assembleFreeDebug --stacktrace

Write-Host ""
Write-Host "==================================" -ForegroundColor Cyan
Write-Host "İşlem tamamlandı!" -ForegroundColor Green
Write-Host "==================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Sonraki Adımlar:" -ForegroundColor Yellow
Write-Host "1. Eğer hata varsa yukarıdaki çıktıyı kontrol edin" -ForegroundColor White
Write-Host "2. Android Studio'yu yeniden başlatın (File > Invalidate Caches / Restart)" -ForegroundColor White
Write-Host "3. Gradle sync yapın (File > Sync Project with Gradle Files)" -ForegroundColor White
Write-Host ""

Read-Host "Çıkmak için Enter'a basın"

