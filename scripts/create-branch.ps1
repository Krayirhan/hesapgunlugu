# Create Refactoring Branch
# Bu script refactor branch'i oluşturur

Write-Host "GIT BRANCH OLUSTURULUYOR..." -ForegroundColor Cyan
Write-Host ""

Push-Location "C:\Users\Acer\AndroidStudioProjects\MyNewApp"

try {
    # Mevcut branch kontrol
    $currentBranch = git rev-parse --abbrev-ref HEAD 2>&1
    Write-Host "Mevcut branch: $currentBranch" -ForegroundColor Yellow

    # Yeni branch oluştur
    git checkout -b refactor/module-boundaries 2>&1

    if ($LASTEXITCODE -eq 0) {
        Write-Host ""
        Write-Host "BASARILI!" -ForegroundColor Green
        Write-Host "Yeni branch: refactor/module-boundaries" -ForegroundColor Green
        Write-Host ""
        Write-Host "Tum degisiklikleri commit edebilirsiniz:" -ForegroundColor Yellow
        Write-Host '   git add .' -ForegroundColor White
        Write-Host '   git commit -m "refactor: complete clean architecture migration"' -ForegroundColor White
    } else {
        Write-Host ""
        Write-Host "Branch zaten var veya hata olustu" -ForegroundColor Red
        Write-Host "Manuel olusturun:" -ForegroundColor Yellow
        Write-Host "   git checkout -b refactor/module-boundaries" -ForegroundColor White
    }

} catch {
    Write-Host "Hata: $_" -ForegroundColor Red
}

Pop-Location

