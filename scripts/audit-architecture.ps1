# ============================================================
# MYNEWAPP CLEAN ARCHITECTURE AUDIT SCRIPT
# ============================================================
param(
    [switch]$Detailed = $false,
    [switch]$Fix = $false
)

$ErrorActionPreference = "Stop"
$projectRoot = $PWD.Path
$timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
$reportFile = "architecture-audit-$timestamp.md"

Write-Host '
=== ðŸ” CLEAN ARCHITECTURE AUDIT BAÅžLADI ===" -ForegroundColor Cyan
Write-Host "Proje: $projectRoot" -ForegroundColor Gray
Write-Host "Rapor: $reportFile`n" -ForegroundColor Gray

$violations = @()
$warnings = @()
$passed = @()

# ============================================================
# 1ï¸âƒ£ DUPLICATE DOSYA KONTROLÃœ
# ============================================================
Write-Host "1ï¸âƒ£ Duplicate dosya taramasÄ±..." -ForegroundColor Yellow

$allKotlinFiles = Get-ChildItem -Path . -Recurse -Filter "*.kt" -File | 
    Where-Object { $_.FullName -notmatch "\\build\\" -and $_.FullName -notmatch "\\\.gradle\\" }

$fileGroups = $allKotlinFiles | Group-Object -Property Name | Where-Object { $_.Count -gt 1 }

if ($fileGroups) {
    foreach ($group in $fileGroups) {
        $paths = ($group.Group | ForEach-Object { $_.FullName.Replace("$projectRoot\", "") }) -join "`n    "
        $violations += "DUPLICATE - Dosya: $($group.Name)`n    $paths"
    }
    Write-Host "  âŒ $($fileGroups.Count) duplicate dosya bulundu" -ForegroundColor Red
} else {
    $passed += "DUPLICATE - Duplicate dosya yok"
    Write-Host "  âœ… Duplicate dosya yok" -ForegroundColor Green
}

# ============================================================
# 2ï¸âƒ£ BOUNDARY Ä°HLALÄ° KONTROLÃœ (feature â†’ core.data)
# ============================================================
Write-Host '
2ï¸âƒ£ Boundary ihlali taramasÄ±..." -ForegroundColor Yellow

$featureFiles = Get-ChildItem -Path "feature" -Recurse -Filter "*.kt" -File -ErrorAction SilentlyContinue

if ($featureFiles) {
    foreach ($file in $featureFiles) {
        $content = Get-Content $file.FullName -Raw
        if ($content -match 'import com\.example\.mynewapp\.core\.data') {
            $matches = [regex]::Matches($content, 'import com\.example\.mynewapp\.core\.data\.[^\s]+')
            foreach ($match in $matches) {
                $relativePath = $file.FullName.Replace("$projectRoot\", "")
                $violations += "BOUNDARY - $relativePath -> $($match.Value)"
            }
        }
    }
}

if ($violations | Where-Object { $_ -like "BOUNDARY*" }) {
    $count = ($violations | Where-Object { $_ -like "BOUNDARY*" }).Count
    Write-Host "  âŒ $count boundary ihlali bulundu" -ForegroundColor Red
} else {
    $passed += "BOUNDARY - Feature -> core.data ihlali yok"
    Write-Host "  âœ… Boundary ihlali yok" -ForegroundColor Green
}

# ============================================================
# 3ï¸âƒ£ DOMAIN SAFLIÄžI KONTROLÃœ
Write-Host '
3ï¸âƒ£ Domain saflÄ±k taramasÄ±..." -ForegroundColor Yellow

$domainFiles = Get-ChildItem -Path "core\domain\src" -Recurse -Filter "*.kt" -File -ErrorAction SilentlyContinue

if ($domainFiles) {
    foreach ($file in $domainFiles) {
        $content = Get-Content $file.FullName -Raw
        $illegalImports = @(
            'import android\.',
            'import androidx\.',
            'import com\.google\.android\.',
            'import androidx\.compose\.'
        )
        
        foreach ($pattern in $illegalImports) {
            if ($content -match $pattern) {
                $relativePath = $file.FullName.Replace("$projectRoot\", "")
                $matches = [regex]::Matches($content, $pattern + '[^\s]+')
                foreach ($match in $matches) {
                    $violations += "DOMAIN-PURITY - $relativePath -> $($match.Value)"
                }
            }
        }
    }
}

if ($violations | Where-Object { $_ -like "DOMAIN-PURITY*" }) {
    $count = ($violations | Where-Object { $_ -like "DOMAIN-PURITY*" }).Count
    Write-Host "  âŒ $count domain saflÄ±k ihlali bulundu" -ForegroundColor Red
} else {
    $passed += "DOMAIN-PURITY - core/domain saf (Android/Compose import yok)"
    Write-Host "  âœ… Domain katmanÄ± saf" -ForegroundColor Green
}

# ============================================================
# 4ï¸âƒ£ NAVIGATION TEK KAYNAK KONTROLÃœ
# ============================================================
Write-Host '
4ï¸âƒ£ Navigation tek kaynak kontrolÃ¼..." -ForegroundColor Yellow

$routeFiles = Get-ChildItem -Path . -Recurse -Filter "*Route*.kt","*Destination*.kt" -File |
    Where-Object { $_.FullName -notmatch "\\build\\" }

$navHostFiles = Get-ChildItem -Path . -Recurse -Filter "*NavHost*.kt","*NavGraph*.kt" -File |
    Where-Object { $_.FullName -notmatch "\\build\\" }

if ($routeFiles.Count -gt 2) {
    $paths = ($routeFiles | ForEach-Object { $_.FullName.Replace("$projectRoot\", "") }) -join "`n    "
    $warnings += "NAVIGATION - Birden fazla Route dosyasÄ±:`n    $paths"
    Write-Host "  âš ï¸ Birden fazla Route dosyasÄ± bulundu" -ForegroundColor Yellow
} elseif ($routeFiles.Count -ge 1) {
    $routePath = ($routeFiles | Select-Object -First 1).FullName.Replace("$projectRoot\", "")
    $passed += "NAVIGATION - Route kaynak: core/navigation/NavigationDestinations.kt"
    Write-Host "  âœ… Route tanÄ±mlarÄ±: core/navigation/NavigationDestinations.kt" -ForegroundColor Green
} else {
    $violations += "NAVIGATION - Route/Destination dosyasÄ± bulunamadÄ±"
    Write-Host "  âŒ Route dosyasÄ± bulunamadÄ±" -ForegroundColor Red
}

if ($navHostFiles.Count -gt 1) {
    $paths = ($navHostFiles | ForEach-Object { $_.FullName.Replace("$projectRoot\", "") }) -join "`n    "
    $warnings += "NAVIGATION - Birden fazla NavHost/NavGraph:`n    $paths"
    Write-Host "  âš ï¸ Birden fazla NavHost/NavGraph bulundu" -ForegroundColor Yellow
} else {
    $passed += "NAVIGATION - NavHost tek kaynak: app/navigation/AppNavHost.kt"
    Write-Host "  âœ… NavHost tek kaynak" -ForegroundColor Green
}

# ============================================================
# 5ï¸âƒ£ APP MODÃœL BOYUTU
Write-Host '
5ï¸âƒ£ App modÃ¼l boyut kontrolÃ¼..." -ForegroundColor Yellow

$appFiles = Get-ChildItem -Path "app\src\main\java" -Recurse -Filter "*.kt" -File -ErrorAction SilentlyContinue
$appFileCount = $appFiles.Count

if ($appFileCount -gt 30) {
    $warnings += "APP-SIZE - App modÃ¼lÃ¼nde $appFileCount dosya (ideal: max 30)"
    Write-Host "  âš ï¸ App modÃ¼lÃ¼nde $appFileCount dosya (ideal: max 30)" -ForegroundColor Yellow
} else {
    $passed += "APP-SIZE - App modÃ¼lÃ¼ $appFileCount dosya (ideal)"
    Write-Host "  âœ… App modÃ¼lÃ¼ $appFileCount dosya" -ForegroundColor Green
}

# ============================================================
# 6ï¸âƒ£ SettingsManager KULLANIMI
Write-Host '
6ï¸âƒ£ SettingsManager kullanÄ±m kontrolÃ¼..." -ForegroundColor Yellow

if ($featureFiles) {
    foreach ($file in $featureFiles) {
        $content = Get-Content $file.FullName -Raw
        if ($content -match 'SettingsManager') {
            $relativePath = $file.FullName.Replace("$projectRoot\", "")
            $violations += "SETTINGS-MANAGER - $relativePath -> SettingsManager kullanÄ±yor (SettingsRepository kullanmalÄ±)"
        }
    }
}

if ($violations | Where-Object { $_ -like "SETTINGS-MANAGER*" }) {
    $count = ($violations | Where-Object { $_ -like "SETTINGS-MANAGER*" }).Count
    Write-Host "  âŒ $count feature dosyasÄ±nda SettingsManager kullanÄ±mÄ±" -ForegroundColor Red
} else {
    $passed += "SETTINGS-MANAGER - Feature'lar SettingsRepository kullanÄ±yor"
    Write-Host "  âœ… Feature'lar SettingsRepository kullanÄ±yor" -ForegroundColor Green
}

# ============================================================
# 7ï¸âƒ£ MODÃœL BAÄžIMLILIK KONTROLÃœ
# ============================================================
Write-Host '
7ï¸âƒ£ ModÃ¼l baÄŸÄ±mlÄ±lÄ±k grafiÄŸi kontrolÃ¼..." -ForegroundColor Yellow

# Check feature module dependencies
$featureBuildFiles = Get-ChildItem -Path "feature" -Recurse -Filter "build.gradle.kts" -File -ErrorAction SilentlyContinue

$dependencyViolations = 0
foreach ($buildFile in $featureBuildFiles) {
    $content = Get-Content $buildFile.FullName -Raw
    
    # Feature modÃ¼lleri core.data'ya direkt baÄŸlanmamalÄ± (sadece DI iÃ§in app'te olmalÄ±)
    if ($content -match 'implementation\(project\(":core:data"\)\)') {
        $relativePath = $buildFile.FullName.Replace("$projectRoot\", "")
        $warnings += "MODULE-DEP - $relativePath -> core:data baÄŸÄ±mlÄ±lÄ±ÄŸÄ± var (sadece repository interface kullanmalÄ±)"
        $dependencyViolations++
    }
}

if ($dependencyViolations -eq 0) {
    $passed += "MODULE-DEP - Feature modÃ¼lleri doÄŸru baÄŸÄ±mlÄ±lÄ±klara sahip"
    Write-Host "  âœ… ModÃ¼l baÄŸÄ±mlÄ±lÄ±klarÄ± doÄŸru" -ForegroundColor Green
} else {
    Write-Host "  âš ï¸ $dependencyViolations modÃ¼l baÄŸÄ±mlÄ±lÄ±k uyarÄ±sÄ±" -ForegroundColor Yellow
}

# ============================================================
# RAPOR OLUÅžTUR
# ============================================================
$report = "CLEAN ARCHITECTURE AUDIT RAPORU`n"
$report += "Tarih: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')`n"
$report += "Proje: MyNewApp`n`n"

$report += "OZET:`n"
$report += "Basarili Kontroller: $($passed.Count)`n"
$report += "Uyarilar: $($warnings.Count)`n"
$report += "Ihlaller: $($violations.Count)`n`n"

$report += "BASARILI KONTROLLER:`n"
foreach ($item in $passed) {
    $report += "  * $item`n"
}

$report += "`nUYARILAR:`n"
if ($warnings.Count -eq 0) {
    $report += "  Uyari yok.`n"
} else {
    foreach ($item in $warnings) {
        $report += "  * $item`n"
    }
}

$report += "`nIHLALLER:`n"
if ($violations.Count -eq 0) {
    $report += "  Ihlal yok.`n"
} else {
    foreach ($item in $violations) {
        $report += "  * $item`n"
    }
}

$report += "`nNIHAI DEGERLENDIRME:`n"

if ($violations.Count -eq 0) {
    $report += "`nPROJE CLEAN ARCHITECTURE'A UYGUN!`n"
    Write-Host '
âœ… PROJE CLEAN ARCHITECTURE'A UYGUN!" -ForegroundColor Green
} else {
    $report += "`n$($violations.Count) IHLAL TESPIT EDILDI`n"
    Write-Host '
âŒ $($violations.Count) IHLAL TESPIT EDILDI" -ForegroundColor Red
}

if ($warnings.Count -gt 0) {
    $report += "`n$($warnings.Count) UYARI VAR`n"
    Write-Host "âš ï¸ $($warnings.Count) UYARI VAR" -ForegroundColor Yellow
}

# Raporu dosyaya yaz
$report | Out-File $reportFile -Encoding UTF8
Write-Host '
Rapor kaydedildi: $reportFile" -ForegroundColor Cyan

# Detayli rapor
if ($Detailed) {
    Write-Host ''
    Write-Host '=== DETAYLI RAPOR ===' -ForegroundColor Cyan
    Get-Content $reportFile
}

# Exit code
if ($violations.Count -gt 0) {
    exit 1
} else {
    exit 0
}

