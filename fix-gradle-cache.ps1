# Gradle Cache Fix Script
# Bu script Gradle cache sorunlarını çözer

Write-Host "============================================" -ForegroundColor Cyan
Write-Host "Gradle Cache Onarım Scripti" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

# 1. Gradle Daemon'u durdur
Write-Host "[1/6] Gradle Daemon durduruluyor..." -ForegroundColor Yellow
try {
    & .\gradlew.bat --stop 2>$null
} catch { }

# Java proceslerini durdur
Get-Process -Name "java" -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue
Start-Sleep -Seconds 2

# 2. Proje build klasörlerini temizle
Write-Host "[2/6] Proje build klasörleri temizleniyor..." -ForegroundColor Yellow
$projectRoot = $PSScriptRoot

$foldersToDelete = @(
    "$projectRoot\build",
    "$projectRoot\app\build",
    "$projectRoot\.gradle"
)

# Core modülleri
Get-ChildItem -Path "$projectRoot\core" -Directory -ErrorAction SilentlyContinue | ForEach-Object {
    $foldersToDelete += "$($_.FullName)\build"
}

# Feature modülleri
Get-ChildItem -Path "$projectRoot\feature" -Directory -ErrorAction SilentlyContinue | ForEach-Object {
    $foldersToDelete += "$($_.FullName)\build"
}

# Diğer modüller
$foldersToDelete += "$projectRoot\baselineprofile\build"
$foldersToDelete += "$projectRoot\benchmark-macro\build"

foreach ($folder in $foldersToDelete) {
    if (Test-Path $folder) {
        Write-Host "  Siliniyor: $folder" -ForegroundColor Gray
        Remove-Item -Path $folder -Recurse -Force -ErrorAction SilentlyContinue
    }
}

# 3. Gradle transforms cache'i temizle
Write-Host "[3/6] Gradle transforms cache temizleniyor..." -ForegroundColor Yellow
$gradleCachePath = "$env:USERPROFILE\.gradle\caches"
Get-ChildItem -Path $gradleCachePath -Directory -ErrorAction SilentlyContinue | ForEach-Object {
    $transformsPath = Join-Path $_.FullName "transforms"
    if (Test-Path $transformsPath) {
        Write-Host "  Siliniyor: $transformsPath" -ForegroundColor Gray
        Remove-Item -Path $transformsPath -Recurse -Force -ErrorAction SilentlyContinue
    }
}

# 4. Gradle 8.13 cache'i tamamen temizle
Write-Host "[4/6] Gradle 8.13 cache tamamen temizleniyor..." -ForegroundColor Yellow
$gradle813Cache = "$env:USERPROFILE\.gradle\caches\8.13"
if (Test-Path $gradle813Cache) {
    Write-Host "  Siliniyor: $gradle813Cache" -ForegroundColor Gray
    Remove-Item -Path $gradle813Cache -Recurse -Force -ErrorAction SilentlyContinue
}

# 5. Kotlin/Kapt cache'i temizle
Write-Host "[5/6] Kotlin/Kapt metadata temizleniyor..." -ForegroundColor Yellow
$metadataFolders = Get-ChildItem -Path "$env:USERPROFILE\.gradle\caches\modules-2" -Directory -Filter "metadata-*" -ErrorAction SilentlyContinue
foreach ($folder in $metadataFolders) {
    Write-Host "  Siliniyor: $($folder.FullName)" -ForegroundColor Gray
    Remove-Item -Path $folder.FullName -Recurse -Force -ErrorAction SilentlyContinue
}

# 6. Jarlar cache'i temizle (leakcanary sorunu için)
Write-Host "[6/6] Jars cache temizleniyor..." -ForegroundColor Yellow
$jarCaches = Get-ChildItem -Path "$env:USERPROFILE\.gradle\caches\jars-*" -Directory -ErrorAction SilentlyContinue
foreach ($folder in $jarCaches) {
    Write-Host "  Siliniyor: $($folder.FullName)" -ForegroundColor Gray
    Remove-Item -Path $folder.FullName -Recurse -Force -ErrorAction SilentlyContinue
}

Write-Host ""
Write-Host "============================================" -ForegroundColor Green
Write-Host "TAMAMLANDI!" -ForegroundColor Green
Write-Host "============================================" -ForegroundColor Green
Write-Host ""
Write-Host "Şimdi şu adımları takip edin:" -ForegroundColor Yellow
Write-Host "1. Android Studio'yu tamamen kapatın" -ForegroundColor White
Write-Host "2. Android Studio'yu yeniden açın" -ForegroundColor White
Write-Host "3. 'File > Sync Project with Gradle Files' yapın" -ForegroundColor White
Write-Host "4. Build > Clean Project, sonra Build > Rebuild Project" -ForegroundColor White
Write-Host ""
Write-Host "Devam etmek için bir tuşa basın..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")

