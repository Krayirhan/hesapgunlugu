<#
Scan repository for Turkish letters inside string literals in Kotlin/Java/Xml/Gradle files.
Writes a report to scripts/turkish-literals-report.txt with occurrences.
Usage:
  powershell -ExecutionPolicy Bypass -File .\scripts\find-turkish-literals.ps1
#>
$root = Get-Location
$report = Join-Path $PSScriptRoot 'turkish-literals-report.txt'
if (Test-Path $report) { Remove-Item $report -Force }
$enc = [System.Text.Encoding]::UTF8
Add-Content -Path $report -Value "Turkish literals scan report - $(Get-Date)\n"
$pattern = '[çÇ????öÖ??üÜ]'
$exts = @('*.kt','*.java','*.xml','*.gradle.kts','*.properties','*.txt')
foreach ($ext in $exts) {
    $files = Get-ChildItem -Path $PSScriptRoot -Recurse -Include $ext -File -ErrorAction SilentlyContinue
    foreach ($f in $files) {
        $lines = Get-Content -Path $f.FullName -ErrorAction SilentlyContinue
        for ($i=0; $i -lt $lines.Length; $i++) {
            $line = $lines[$i]
            if ($line -match $pattern) {
                # for Kotlin/Java detect string literal occurrences
                $isLiteral = $false
                if ($f.Extension -in '.kt','.java','.gradle.kts') {
                    if ($line -match '"([^"]*[çÇ????öÖ??üÜ][^"]*)"') { $isLiteral = $true }
                }
                if ($f.Extension -eq '.xml') {
                    # check text content between > <
                    if ($line -match '>([^<]*[çÇ????öÖ??üÜ][^<]*)<') { $isLiteral = $true }
                }
                if ($isLiteral) {
                    $entry = "File: $($f.FullName) | Line: $($i+1) | Text: $line"
                    Add-Content -Path $report -Value $entry
                }
            }
        }
    }
}
Write-Output "Scan complete. Report at: $report"
