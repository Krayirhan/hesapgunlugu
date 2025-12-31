# ========================================
# APP/DOMAIN SİLME SCRIPTI
# ========================================
# Bu script app/domain legacy klasörünü siler

Write-Host "🗑️  app/domain klasörünü siliyorum..." -ForegroundColor Yellow

$domainPath = "C:\Users\Acer\AndroidStudioProjects\MyNewApp\app\src\main\java\com\example\mynewapp\domain"

if (Test-Path $domainPath) {
    Remove-Item -Path $domainPath -Recurse -Force
    Write-Host "✅ app/domain silindi!" -ForegroundColor Green
} else {
    Write-Host "⚠️  app/domain zaten yok!" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "✅ ADIM 1 TAMAMLANDI!" -ForegroundColor Green
Write-Host "Sıradaki: StringProvider duplicate binding düzeltme" -ForegroundColor Cyan

