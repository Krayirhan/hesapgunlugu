$report = Join-Path $PSScriptRoot 'backslash-report.txt'
if (Test-Path $report) { Remove-Item $report -Force }
$files = Get-ChildItem -Path $PSScriptRoot -Recurse -Include '*.xml','*.kt','*.java','*.gradle.kts' -File -ErrorAction SilentlyContinue
foreach ($f in $files) {
    $lines = Get-Content $f.FullName -ErrorAction SilentlyContinue
    for ($i=0; $i -lt $lines.Length; $i++) {
        if ($lines[$i] -match '\\') {
            Add-Content -Path $report -Value "File: $($f.FullName) Line: $($i+1) -> $($lines[$i])"
        }
    }
}
Write-Output "Backslash scan complete. Report: $report"