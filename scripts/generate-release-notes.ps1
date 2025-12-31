param(
    [string]$ChangelogPath = "CHANGELOG.md"
)

if (!(Test-Path $ChangelogPath)) {
    Write-Error "Changelog not found: $ChangelogPath"
    exit 1
}

$content = Get-Content $ChangelogPath
$start = $content | Select-String -Pattern '^## \[' | Where-Object { $_.Line -notmatch '^\#\# \[Unreleased\]' } | Select-Object -First 1

if (-not $start) {
    Write-Error "No version section found in changelog."
    exit 1
}

$startIndex = $start.LineNumber - 1
$endIndex = ($content | Select-String -Pattern '^## \[' | Where-Object { $_.Line -ne $start.Line } | Select-Object -First 1).LineNumber - 2

if ($endIndex -lt $startIndex) {
    $endIndex = $content.Length - 1
}

$notes = $content[$startIndex..$endIndex]
$notes | ForEach-Object { $_.TrimEnd() }
