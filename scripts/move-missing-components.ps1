# Move Missing Components to core/ui
# Bu script app/feature/common/components'teki eksik 4 dosyayı core/ui'ya taşır

$sourceBase = "C:\Users\Acer\AndroidStudioProjects\MyNewApp\app\src\main\java\com\example\mynewapp\feature\common\components"
$targetBase = "C:\Users\Acer\AndroidStudioProjects\MyNewApp\core\ui\src\main\java\com\example\mynewapp\core\ui\components"

$filesToMove = @(
    "AdvancedCharts.kt",
    "FinancialInsightsCards.kt",
    "LoadingErrorStates.kt",
    "ProCards.kt"
)

Write-Host "EKSIK COMPONENT'LERI TASIYOR..." -ForegroundColor Cyan
Write-Host ""

$successCount = 0

foreach ($file in $filesToMove) {
    $sourcePath = Join-Path $sourceBase $file
    $targetPath = Join-Path $targetBase $file

    if (Test-Path $sourcePath) {
        try {
            # Oku
            $content = Get-Content $sourcePath -Raw

            # Package name değiştir
            $content = $content -replace 'package com\.example\.mynewapp\.feature\.common\.components', 'package com.hesapgunlugu.app.core.ui.components'

            # Yaz
            $content | Out-File -FilePath $targetPath -Encoding UTF8 -NoNewline

            Write-Host "   $file tasindi" -ForegroundColor Green
            $successCount++

        } catch {
            Write-Host "   $file HATA: $_" -ForegroundColor Red
        }
    } else {
        Write-Host "   $file bulunamadi" -ForegroundColor Yellow
    }
}

Write-Host ""
Write-Host "$successCount/4 dosya basariyla tasindi!" -ForegroundColor Green
Write-Host ""
Write-Host "Simdi app/feature/common/components'teki duplicate'leri silin:" -ForegroundColor Yellow
Write-Host "   Remove-Item '$sourceBase\*' -Force" -ForegroundColor White

