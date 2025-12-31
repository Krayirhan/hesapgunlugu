# ================================================================
# PACKAGE NAME REFACTORING SCRIPT
# com.hesapgunlugu.app → com.hesapgunlugu.app
# ================================================================

$ErrorActionPreference = "Stop"

$OldPackage = "com.hesapgunlugu.app"
$NewPackage = "com.hesapgunlugu.app"
$OldPath = "com\example\mynewapp"
$NewPath = "com\hesapgunlugu\app"
$OldAppName = "MyNewApp"
$NewAppName = "HesapGunlugu"
$OldDisplayName = "My New App"
$NewDisplayName = "Hesap Günlüğü"

$ProjectRoot = "C:\Users\Acer\AndroidStudioProjects\MyNewApp"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  PACKAGE NAME REFACTORING SCRIPT" -ForegroundColor Cyan
Write-Host "  $OldPackage → $NewPackage" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Step 1: Update all build.gradle.kts files
Write-Host "📦 Step 1: Updating build.gradle.kts files..." -ForegroundColor Green

$gradleFiles = Get-ChildItem -Path $ProjectRoot -Recurse -Filter "build.gradle.kts" | Where-Object { $_.FullName -notlike "*\build\*" }

foreach ($file in $gradleFiles) {
    $content = Get-Content $file.FullName -Raw -Encoding UTF8
    if ($content -match $OldPackage) {
        $newContent = $content -replace [regex]::Escape($OldPackage), $NewPackage
        Set-Content -Path $file.FullName -Value $newContent -Encoding UTF8 -NoNewline
        Write-Host "  ✓ $($file.FullName)" -ForegroundColor Gray
    }
}

# Step 2: Update settings.gradle.kts
Write-Host "`n📦 Step 2: Updating settings.gradle.kts..." -ForegroundColor Green

$settingsFile = "$ProjectRoot\settings.gradle.kts"
$content = Get-Content $settingsFile -Raw -Encoding UTF8
$content = $content -replace [regex]::Escape($OldDisplayName), $NewDisplayName
Set-Content -Path $settingsFile -Value $content -Encoding UTF8 -NoNewline
Write-Host "  ✓ settings.gradle.kts" -ForegroundColor Gray

# Step 3: Update all Kotlin files (package declarations and imports)
Write-Host "`n📦 Step 3: Updating Kotlin files..." -ForegroundColor Green

$ktFiles = Get-ChildItem -Path $ProjectRoot -Recurse -Filter "*.kt" | Where-Object { $_.FullName -notlike "*\build\*" }

foreach ($file in $ktFiles) {
    $content = Get-Content $file.FullName -Raw -Encoding UTF8
    if ($content -match $OldPackage) {
        $newContent = $content -replace [regex]::Escape($OldPackage), $NewPackage
        Set-Content -Path $file.FullName -Value $newContent -Encoding UTF8 -NoNewline
        Write-Host "  ✓ $($file.Name)" -ForegroundColor Gray
    }
}

# Step 4: Update XML files (AndroidManifest, resources)
Write-Host "`n📦 Step 4: Updating XML files..." -ForegroundColor Green

$xmlFiles = Get-ChildItem -Path $ProjectRoot -Recurse -Filter "*.xml" | Where-Object { $_.FullName -notlike "*\build\*" }

foreach ($file in $xmlFiles) {
    $content = Get-Content $file.FullName -Raw -Encoding UTF8
    if ($content -match $OldPackage) {
        $newContent = $content -replace [regex]::Escape($OldPackage), $NewPackage
        Set-Content -Path $file.FullName -Value $newContent -Encoding UTF8 -NoNewline
        Write-Host "  ✓ $($file.Name)" -ForegroundColor Gray
    }
}

# Step 5: Update ProGuard files
Write-Host "`n📦 Step 5: Updating ProGuard files..." -ForegroundColor Green

$proguardFiles = Get-ChildItem -Path $ProjectRoot -Recurse -Filter "proguard-rules.pro" | Where-Object { $_.FullName -notlike "*\build\*" }

foreach ($file in $proguardFiles) {
    $content = Get-Content $file.FullName -Raw -Encoding UTF8
    if ($content -match $OldPackage) {
        $newContent = $content -replace [regex]::Escape($OldPackage), $NewPackage
        Set-Content -Path $file.FullName -Value $newContent -Encoding UTF8 -NoNewline
        Write-Host "  ✓ $($file.FullName)" -ForegroundColor Gray
    }
}

# Step 6: Update Markdown/Documentation files
Write-Host "`n📦 Step 6: Updating documentation..." -ForegroundColor Green

$mdFiles = Get-ChildItem -Path $ProjectRoot -Recurse -Include "*.md", "*.txt" | Where-Object { $_.FullName -notlike "*\build\*" }

foreach ($file in $mdFiles) {
    $content = Get-Content $file.FullName -Raw -Encoding UTF8
    $updated = $false
    
    if ($content -match $OldPackage) {
        $content = $content -replace [regex]::Escape($OldPackage), $NewPackage
        $updated = $true
    }
    if ($content -match $OldAppName) {
        $content = $content -replace $OldAppName, $NewAppName
        $updated = $true
    }
    
    if ($updated) {
        Set-Content -Path $file.FullName -Value $content -Encoding UTF8 -NoNewline
        Write-Host "  ✓ $($file.Name)" -ForegroundColor Gray
    }
}

# Step 7: Update PowerShell scripts
Write-Host "`n📦 Step 7: Updating scripts..." -ForegroundColor Green

$psFiles = Get-ChildItem -Path $ProjectRoot -Recurse -Filter "*.ps1" | Where-Object { $_.FullName -notlike "*\build\*" -and $_.Name -ne "refactor-package-name.ps1" }

foreach ($file in $psFiles) {
    $content = Get-Content $file.FullName -Raw -Encoding UTF8
    if ($content -match $OldPackage) {
        $newContent = $content -replace [regex]::Escape($OldPackage), $NewPackage
        Set-Content -Path $file.FullName -Value $newContent -Encoding UTF8 -NoNewline
        Write-Host "  ✓ $($file.Name)" -ForegroundColor Gray
    }
}

# Step 8: Rename directory structure
Write-Host "`n📦 Step 8: Renaming directory structure..." -ForegroundColor Yellow
Write-Host "  ⚠️  This requires manual folder renaming or Android Studio refactoring" -ForegroundColor Yellow
Write-Host ""
Write-Host "  Folders to rename:" -ForegroundColor White
Write-Host "  OLD: src/main/java/com/example/mynewapp" -ForegroundColor Red
Write-Host "  NEW: src/main/java/com/hesapgunlugu/app" -ForegroundColor Green
Write-Host ""

# Find all directories that need renaming
$dirsToRename = Get-ChildItem -Path $ProjectRoot -Recurse -Directory | Where-Object { 
    $_.FullName -like "*\src\*\java\com\example\mynewapp*" -and 
    $_.FullName -notlike "*\build\*" 
} | Sort-Object { $_.FullName.Length } -Descending

Write-Host "  Found $($dirsToRename.Count) directories to rename" -ForegroundColor Cyan

$renameNow = Read-Host "`nRename directories now? (E/H)"

if ($renameNow -eq "E" -or $renameNow -eq "e") {
    foreach ($dir in $dirsToRename) {
        $oldDirPath = $dir.FullName
        $newDirPath = $oldDirPath -replace [regex]::Escape($OldPath), $NewPath
        
        # Create new directory structure
        $newParent = Split-Path $newDirPath -Parent
        if (!(Test-Path $newParent)) {
            New-Item -ItemType Directory -Path $newParent -Force | Out-Null
        }
        
        # Move contents
        if (Test-Path $oldDirPath) {
            $files = Get-ChildItem -Path $oldDirPath -File
            foreach ($f in $files) {
                $destPath = Join-Path $newDirPath $f.Name
                $destDir = Split-Path $destPath -Parent
                if (!(Test-Path $destDir)) {
                    New-Item -ItemType Directory -Path $destDir -Force | Out-Null
                }
                Move-Item -Path $f.FullName -Destination $destPath -Force
                Write-Host "  ✓ Moved: $($f.Name)" -ForegroundColor Gray
            }
        }
    }
    
    # Clean up old directories
    Write-Host "`n  Cleaning up old directories..." -ForegroundColor Yellow
    $oldDirs = Get-ChildItem -Path $ProjectRoot -Recurse -Directory | Where-Object { 
        $_.FullName -like "*\java\com\example*" -and 
        $_.FullName -notlike "*\build\*" 
    } | Sort-Object { $_.FullName.Length } -Descending
    
    foreach ($dir in $oldDirs) {
        if ((Get-ChildItem -Path $dir.FullName -Recurse -File).Count -eq 0) {
            Remove-Item -Path $dir.FullName -Recurse -Force -ErrorAction SilentlyContinue
            Write-Host "  ✓ Removed empty: $($dir.Name)" -ForegroundColor Gray
        }
    }
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "  REFACTORING COMPLETE!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "Next steps:" -ForegroundColor Yellow
Write-Host "1. Open project in Android Studio" -ForegroundColor White
Write-Host "2. File → Sync Project with Gradle Files" -ForegroundColor White
Write-Host "3. Build → Clean Project" -ForegroundColor White
Write-Host "4. Build → Rebuild Project" -ForegroundColor White
Write-Host ""
Write-Host "If there are errors, use Android Studio's refactoring:" -ForegroundColor Yellow
Write-Host "Right-click package → Refactor → Rename" -ForegroundColor White
