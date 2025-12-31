# BOM Temizleme Script'i
# UTF-8 BOM karakterini dosyalardan temizler

$files = @(
    "C:\Users\Acer\AndroidStudioProjects\MyNewApp\feature\settings\build.gradle.kts",
    "C:\Users\Acer\AndroidStudioProjects\MyNewApp\feature\statistics\build.gradle.kts",
    "C:\Users\Acer\AndroidStudioProjects\MyNewApp\feature\scheduled\build.gradle.kts",
    "C:\Users\Acer\AndroidStudioProjects\MyNewApp\feature\privacy\build.gradle.kts",
    "C:\Users\Acer\AndroidStudioProjects\MyNewApp\feature\onboarding\build.gradle.kts",
    "C:\Users\Acer\AndroidStudioProjects\MyNewApp\feature\notifications\build.gradle.kts"
)

foreach ($file in $files) {
    if (Test-Path $file) {
        Write-Host "Temizleniyor: $file" -ForegroundColor Yellow

        # Dosyayı UTF-8 (BOM yok) olarak oku ve yaz
        $content = Get-Content $file -Raw -Encoding UTF8
        $utf8NoBom = New-Object System.Text.UTF8Encoding $false
        [System.IO.File]::WriteAllText($file, $content, $utf8NoBom)

        Write-Host "  ✅ Temizlendi" -ForegroundColor Green
    } else {
        Write-Host "  ⚠️  Dosya bulunamadı: $file" -ForegroundColor Red
    }
}

Write-Host "`n✅ Tüm dosyalar temizlendi!" -ForegroundColor Green
Write-Host "Gradle Sync yapabilirsiniz." -ForegroundColor Cyan

