<#
PowerShell script to normalize file encodings to UTF-8 (no BOM) and attempt to fix files
that were saved with the wrong code page (common for Turkish: CP1254).

Usage:
  powershell -ExecutionPolicy Bypass -File .\scripts\fix-encodings.ps1

This script will:
 - Scan source files (*.xml, *.kt, *.java, *.gradle.kts, *.properties, *.txt, *.md)
 - If a file contains the Unicode replacement character U+FFFD (?), it will try to
   reinterpret the raw bytes as Windows-1254 (code page 1254) and write the file
   back as UTF-8 (no BOM) if the reinterpretation removes replacement characters.
 - Remove UTF-8 BOM from files, writing them as UTF-8 without BOM.
 - Produce a report at scripts/encoding-fix-report.txt listing fixed files and
   files that still need manual review.
#>

$reportPath = Join-Path -Path $PSScriptRoot -ChildPath "encoding-fix-report.txt"
if (Test-Path $reportPath) { Remove-Item $reportPath -Force }

$searchExtensions = @('*.xml','*.kt','*.java','*.gradle.kts','*.properties','*.txt','*.md')
$files = @()
foreach ($ext in $searchExtensions) {
    $files += Get-ChildItem -Path $PSScriptRoot -Recurse -Include $ext -File -ErrorAction SilentlyContinue
}

$fixed = @()
$needsManual = @()
$bomRemoved = @()

# Helper utf8 no BOM encoder
$utf8NoBom = New-Object System.Text.UTF8Encoding($false)
$cp1254 = [System.Text.Encoding]::GetEncoding(1254)

foreach ($file in $files) {
    try {
        $bytes = [System.IO.File]::ReadAllBytes($file.FullName)
        if ($bytes.Length -eq 0) { continue }

        # Detect and remove UTF-8 BOM if present
        if ($bytes.Length -ge 3 -and $bytes[0] -eq 0xEF -and $bytes[1] -eq 0xBB -and $bytes[2] -eq 0xBF) {
            # decode as utf8 then write without BOM
            $text = [System.Text.Encoding]::UTF8.GetString($bytes, 3, $bytes.Length - 3)
            [System.IO.File]::WriteAllText($file.FullName, $text, $utf8NoBom)
            $bomRemoved += $file.FullName
            # reload bytes
            $bytes = [System.IO.File]::ReadAllBytes($file.FullName)
        }

        # Decode as utf8
        $textUtf8 = [System.Text.Encoding]::UTF8.GetString($bytes)

        if ($textUtf8.Contains([char]0xFFFD)) {
            # contains replacement char; try windows-1254 interpretation
            $text1254 = $cp1254.GetString($bytes)
            if (-not $text1254.Contains([char]0xFFFD)) {
                # write as UTF8 no BOM
                [System.IO.File]::WriteAllText($file.FullName, $text1254, $utf8NoBom)
                $fixed += $file.FullName
            } else {
                $needsManual += $file.FullName
            }
        } else {
            # No replacement char in utf8; ensure file is saved as utf8 no BOM
            # If original bytes are not equal to utf8 bytes without BOM, rewrite
            $utf8BytesNoBom = $utf8NoBom.GetBytes($textUtf8)
            $same = $true
            if ($utf8BytesNoBom.Length -ne $bytes.Length) { $same = $false } else {
                for ($i=0; $i -lt $bytes.Length; $i++) { if ($bytes[$i] -ne $utf8BytesNoBom[$i]) { $same = $false; break } }
            }
            if (-not $same) {
                [System.IO.File]::WriteAllText($file.FullName, $textUtf8, $utf8NoBom)
                $fixed += $file.FullName
            }
        }
    } catch {
        $needsManual += $file.FullName
    }
}

Add-Content -Path $reportPath -Value "Encoding fix report - $(Get-Date)"
Add-Content -Path $reportPath -Value "\nFiles fixed or normalized:"
foreach ($f in $fixed) { Add-Content -Path $reportPath -Value $f }
Add-Content -Path $reportPath -Value "\nFiles where BOM was removed:"
foreach ($f in $bomRemoved) { Add-Content -Path $reportPath -Value $f }
Add-Content -Path $reportPath -Value "\nFiles needing manual review (contain replacement chars or errors):"
foreach ($f in $needsManual) { Add-Content -Path $reportPath -Value $f }

Add-Content -Path $reportPath -Value "\nSummary:"
Add-Content -Path $reportPath -Value "Fixed: $($fixed.Count)"
Add-Content -Path $reportPath -Value "BOM removed: $($bomRemoved.Count)"
Add-Content -Path $reportPath -Value "Manual review: $($needsManual.Count)"

Write-Output "Encoding fix complete. Report at: $reportPath"
if ($needsManual.Count -gt 0) { Write-Output "Some files need manual review. See report." }
