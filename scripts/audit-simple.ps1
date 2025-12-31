param([switch]$Detailed)
$projectRoot = $PWD.Path
$timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
$reportFile = "architecture-audit-$timestamp.txt"
$violations = @()
$warnings = @()
$passed = @()

Write-Host ""
Write-Host "CLEAN ARCHITECTURE AUDIT STARTED" -ForegroundColor Cyan

# 1 - Duplicate check
Write-Host "1 - Duplicate file scan..." -ForegroundColor Yellow
$allKotlinFiles = Get-ChildItem -Path . -Recurse -Filter "*.kt" -File | 
    Where-Object { $_.FullName -notmatch "\\build\\" -and $_.FullName -notmatch "\\\.gradle\\" }
$fileGroups = $allKotlinFiles | Group-Object -Property Name | Where-Object { $_.Count -gt 1 }

if ($fileGroups) {
    foreach ($group in $fileGroups) {
        $paths = ($group.Group | ForEach-Object { $_.FullName.Replace("$projectRoot\", "") }) -join ", "
        $violations += "DUPLICATE: $($group.Name) - $paths"
    }
    Write-Host "  ERROR: $($fileGroups.Count) duplicates found" -ForegroundColor Red
} else {
    $passed += "DUPLICATE: No duplicate files"
    Write-Host "  OK: No duplicates" -ForegroundColor Green
}

# 2 - Boundary violations
Write-Host "2 - Boundary violation scan..." -ForegroundColor Yellow
$featureFiles = Get-ChildItem -Path "feature" -Recurse -Filter "*.kt" -File -ErrorAction SilentlyContinue
if ($featureFiles) {
    foreach ($file in $featureFiles) {
        $content = Get-Content $file.FullName -Raw
        if ($content -match 'import com\.example\.mynewapp\.core\.data') {
            $relativePath = $file.FullName.Replace("$projectRoot\", "")
            $violations += "BOUNDARY: $relativePath imports core.data"
        }
    }
}

if ($violations | Where-Object { $_ -like "BOUNDARY*" }) {
    Write-Host "  ERROR: Boundary violations found" -ForegroundColor Red
} else {
    $passed += "BOUNDARY: No feature to core.data imports"
    Write-Host "  OK: No boundary violations" -ForegroundColor Green
}

# 3 - Domain purity
Write-Host "3 - Domain purity scan..." -ForegroundColor Yellow
$domainFiles = Get-ChildItem -Path "core\domain\src" -Recurse -Filter "*.kt" -File -ErrorAction SilentlyContinue
if ($domainFiles) {
    foreach ($file in $domainFiles) {
        $content = Get-Content $file.FullName -Raw
        if ($content -match 'import android\.' -or $content -match 'import androidx\.') {
            $relativePath = $file.FullName.Replace("$projectRoot\", "")
            $violations += "DOMAIN-PURITY: $relativePath has Android imports"
        }
    }
}

if ($violations | Where-Object { $_ -like "DOMAIN-PURITY*" }) {
    Write-Host "  ERROR: Domain purity violations" -ForegroundColor Red
} else {
    $passed += "DOMAIN-PURITY: Domain layer is pure"
    Write-Host "  OK: Domain is pure" -ForegroundColor Green
}

# Summary
Write-Host ""
Write-Host "SUMMARY:" -ForegroundColor Cyan
Write-Host "  Passed: $($passed.Count)" -ForegroundColor Green
Write-Host "  Warnings: $($warnings.Count)" -ForegroundColor Yellow
Write-Host "  Violations: $($violations.Count)" -ForegroundColor Red

$report = "AUDIT REPORT`n"
$report += "Passed: $($passed.Count)`n"
$report += "Warnings: $($warnings.Count)`n"
$report += "Violations: $($violations.Count)`n`n"
foreach ($p in $passed) { $report += "OK: $p`n" }
foreach ($v in $violations) { $report += "ERROR: $v`n" }
$report | Out-File $reportFile

Write-Host ""
Write-Host "Report saved: $reportFile" -ForegroundColor Cyan

if ($violations.Count -eq 0) {
    Write-Host "RESULT: CLEAN ARCHITECTURE COMPLIANT" -ForegroundColor Green
    exit 0
} else {
    Write-Host "RESULT: $($violations.Count) VIOLATIONS FOUND" -ForegroundColor Red
    exit 1
}
