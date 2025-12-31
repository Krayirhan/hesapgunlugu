$report = Join-Path $PSScriptRoot 'unicode-check-report.txt'
if (Test-Path $report) { Remove-Item $report -Force }
$pattern = '\\u([0-9A-Fa-f]{0,3})([^0-9A-Fa-f]|$)'
$files = Get-ChildItem -Path $PSScriptRoot -Recurse -Include '*/res/values/*.xml','*/res/values*/strings*.xml' -File -ErrorAction SilentlyContinue
foreach ($f in $files) {
    $content = Get-Content $f.FullName -Raw -ErrorAction SilentlyContinue
    if ($null -eq $content) { continue }
    $matches = [regex]::Matches($content, $pattern)
    foreach ($m in $matches) {
        Add-Content -Path $report -Value "File: $($f.FullName) -> Invalid unicode escape near: '$($m.Value)'"
    }
}
Write-Output "Done. Report: $report"