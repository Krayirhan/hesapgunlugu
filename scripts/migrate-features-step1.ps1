# ========================================
# 7 FEATURE OTOMATIK TAŞIMA SCRIPTI
# ========================================
# Bu script 7 feature'ı bağımsız modüllere taşır

Write-Host "🚀 7 FEATURE TAŞIMA İŞLEMİ BAŞLIYOR..." -ForegroundColor Cyan
Write-Host ""

$projectRoot = "C:\Users\Acer\AndroidStudioProjects\MyNewApp"
$features = @("settings", "history", "scheduled", "statistics", "notifications", "onboarding", "privacy")

foreach ($feature in $features) {
    Write-Host "📦 $feature feature'ı taşınıyor..." -ForegroundColor Yellow

    # 1. Modül klasörü oluştur
    $featureDir = "$projectRoot\feature\$feature"
    $srcDir = "$featureDir\src\main\java\com\example\mynewapp\feature\$feature"

    New-Item -Path $srcDir -ItemType Directory -Force | Out-Null

    # 2. Dosyaları kopyala
    $sourceDir = "$projectRoot\app\src\main\java\com\example\mynewapp\feature\$feature"
    if (Test-Path $sourceDir) {
        Copy-Item -Path "$sourceDir\*" -Destination $srcDir -Recurse -Force
        Write-Host "   ✅ Dosyalar kopyalandı" -ForegroundColor Green
    } else {
        Write-Host "   ⚠️  Kaynak klasör bulunamadı: $sourceDir" -ForegroundColor Yellow
    }
}

Write-Host ""
Write-Host "✅ KLASÖRLER OLUŞTURULDU!" -ForegroundColor Green
Write-Host ""
Write-Host "Sıradaki: build.gradle.kts dosyaları oluşturulacak..." -ForegroundColor Cyan

