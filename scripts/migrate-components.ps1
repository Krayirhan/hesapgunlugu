# Component Migration Script
# app/feature/common/components → core/ui/components

$sourcePath = "C:\Users\Acer\AndroidStudioProjects\MyNewApp\app\src\main\java\com\example\mynewapp\feature\common\components"
$targetPath = "C:\Users\Acer\AndroidStudioProjects\MyNewApp\core\ui\src\main\java\com\example\mynewapp\core\ui\components"

# Component dosyaları (FinancialInsightsCards.kt hariç - zaten feature/home'da var)
$components = @(
    "AddBudgetCategoryDialog.kt",
    "AddScheduledForm.kt",
    "AddTransactionForm.kt",
    "AdvancedCharts.kt",
    "AdvancedDashboardCard.kt",
    "CategoryBudgetCard.kt",
    "DashboardCard.kt",
    "EditBudgetDialog.kt",
    "ExpensePieChart.kt",
    "HomeHeader.kt",
    "LoadingErrorStates.kt",
    "ProCards.kt",
    "QuickActions.kt",
    "SpendingLimitCard.kt",
    "TransactionItem.kt"
)

Write-Host "🚀 Component Migration Başlıyor..." -ForegroundColor Cyan
Write-Host ""

foreach ($component in $components) {
    $sourceFile = Join-Path $sourcePath $component
    $targetFile = Join-Path $targetPath $component

    if (Test-Path $sourceFile) {
        Write-Host "📦 Taşınıyor: $component" -ForegroundColor Yellow

        # Dosyayı oku
        $content = Get-Content $sourceFile -Raw

        # Package'ı değiştir
        $content = $content -replace 'package com\.example\.mynewapp\.feature\.common\.components', 'package com.hesapgunlugu.app.core.ui.components'

        # Hedef dosyaya yaz
        Set-Content -Path $targetFile -Value $content

        Write-Host "  ✅ Taşındı: core/ui/components/$component" -ForegroundColor Green
    } else {
        Write-Host "  ⚠️ Bulunamadı: $component" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "🎉 Migration tamamlandı!" -ForegroundColor Green
Write-Host ""
Write-Host "📝 Sonraki adımlar:" -ForegroundColor Cyan
Write-Host "1. Import'ları güncelle (7 dosya)" -ForegroundColor White
Write-Host "2. app/feature/common/components klasörünü sil" -ForegroundColor White
Write-Host "3. Gradle Sync + Clean + Rebuild" -ForegroundColor White
Write-Host "4. Build test et" -ForegroundColor White
Write-Host ""

# Import güncelleme gerekli dosyaları listele
Write-Host "🔍 Import güncellemesi gereken dosyalar:" -ForegroundColor Yellow
$filesToUpdate = @(
    "feature\home\src\main\java\com\example\mynewapp\feature\home\HomeScreen.kt",
    "app\src\main\java\com\example\mynewapp\feature\home\HomeScreen.kt",
    "app\src\main\java\com\example\mynewapp\feature\history\HistoryScreen.kt",
    "app\src\main\java\com\example\mynewapp\feature\scheduled\ScheduledScreen.kt"
)

foreach ($file in $filesToUpdate) {
    $fullPath = "C:\Users\Acer\AndroidStudioProjects\MyNewApp\$file"
    if (Test-Path $fullPath) {
        Write-Host "  📄 $file" -ForegroundColor White
    }
}

