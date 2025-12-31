<#
Apply Turkish literals to resources for XML files and prepare mapping for Kotlin/Java.
- Scans for Turkish characters in xml/kt/java files.
 - For XML files: replaces text content between tags containing Turkish chars with @string/key and appends the key to the module's res/values/strings.xml
 - For Kotlin/Java: collects occurrences and writes scripts/turkish-mapping.json for manual review (and optional automated replacement disabled by default)

Usage:
  powershell -ExecutionPolicy Bypass -File .\scripts\apply-turkish-resources.ps1

This script is conservative for Kotlin/Java to avoid breaking code. It will auto-update XML resources.
#>

$root = Get-Location
$report = Join-Path $PSScriptRoot 'apply-turkish-report.txt'
if (Test-Path $report) { Remove-Item $report -Force }
Add-Content -Path $report -Value "Apply Turkish resources report - $(Get-Date)\n"

$pattern = '[çÇ????öÖ??üÜ]'
$xmlFiles = Get-ChildItem -Path $PSScriptRoot -Recurse -Include '*.xml' -File -ErrorAction SilentlyContinue
$ktFiles = Get-ChildItem -Path $PSScriptRoot -Recurse -Include '*.kt','*.java' -File -ErrorAction SilentlyContinue

# Load helper to parse and write XML
function Get-Module-FromPath($path) {
    # determine module by relative path: first folder under root
    $rel = Resolve-Path -Relative $path
    $parts = $rel -split '[\\/]'
    if ($parts.Count -gt 1) { return $parts[0] } else { return 'app' }
}

# Helper to add string to module strings.xml
function Add-StringResource($module, $key, $value) {
    $moduleStrPath = Join-Path $PSScriptRoot "$module\src\main\res\values\strings.xml"
    if (-not (Test-Path $moduleStrPath)) {
        # create directory
        $dir = Split-Path $moduleStrPath -Parent
        New-Item -ItemType Directory -Path $dir -Force | Out-Null
        @'
<?xml version="1.0" encoding="utf-8"?>
<resources>
</resources>
'@ | Out-File -FilePath $moduleStrPath -Encoding utf8
    }
    [xml]$xml = Get-Content $moduleStrPath -Raw
    # ensure no duplicate value
    $existing = $xml.resources.string | Where-Object { $_.InnerText -eq $value }
    if ($existing) {
        return $existing.name
    }
    $node = $xml.CreateElement('string')
    $node.SetAttribute('name',$key)
    $node.InnerText = $value
    $xml.resources.AppendChild($node) | Out-Null
    $xml.Save($moduleStrPath)
    return $key
}

$xmlReplacements = @()
$ktMappings = @()

# Process XML files
foreach ($f in $xmlFiles) {
    try {
        $content = Get-Content $f.FullName -Raw -ErrorAction Stop
        if ($content -match $pattern) {
            $orig = $content
            # Replace only text between tags (simple heuristic)
            $new = $content -replace '(?s)>([^<]*[çÇ????öÖ??üÜ][^<]*)<', {
                param($m)
                $text = $m.Groups[1].Value.Trim()
                if ($text -eq '') { return "><" }
                # create key from filename and hash
                $hash = [System.BitConverter]::ToString((New-Object System.Security.Cryptography.MD5CryptoServiceProvider).ComputeHash([System.Text.Encoding]::UTF8.GetBytes($text))).Replace('-','').ToLower()[0..7] -join ''
                $module = Get-Module-FromPath $f.FullName
                $safeKey = ($f.BaseName + '_' + $hash) -replace '[^a-zA-Z0-9_]','_'
                $key = "$safeKey"
                Add-StringResource $module $key $text | Out-Null
                $xmlReplacements += "Replaced in $($f.FullName): '$text' -> @string/$key"
                return ">@string/$key<"
            }
            if ($new -ne $orig) {
                Set-Content -Path $f.FullName -Value $new -Encoding utf8
                Add-Content -Path $report -Value "Updated XML: $($f.FullName)"
            }
        }
    } catch {
        Add-Content -Path $report -Value "Error processing $($f.FullName): $_"
    }
}

# Scan Kotlin/Java for Turkish literals and prepare mapping file
foreach ($f in $ktFiles) {
    $lines = Get-Content $f.FullName -ErrorAction SilentlyContinue
    $changed = $false
    for ($i=0; $i -lt $lines.Length; $i++) {
        $line = $lines[$i]
        if ($line -match '"([^"]*[çÇ????öÖ??üÜ][^"]*)"') {
            $m = [regex]::Matches($line,'"([^"]*[çÇ????öÖ??üÜ][^"]*)"')
            foreach ($match in $m) {
                $text = $match.Groups[1].Value
                $hash = [System.BitConverter]::ToString((New-Object System.Security.Cryptography.MD5CryptoServiceProvider).ComputeHash([System.Text.Encoding]::UTF8.GetBytes($text))).Replace('-','').ToLower()[0..7] -join ''
                $module = Get-Module-FromPath $f.FullName
                $safeKey = ($f.BaseName + '_' + $hash) -replace '[^a-zA-Z0-9_]','_'
                $ktMappings += @{ file = $f.FullName; line = $i+1; text = $text; key = $safeKey; module = $module }
            }
        }
    }
}

# write mappings
$mappingPath = Join-Path $PSScriptRoot 'turkish-mapping.json'
$ktMappings | ConvertTo-Json -Depth 5 | Out-File -FilePath $mappingPath -Encoding utf8
Add-Content -Path $report -Value "XML replacements: $($xmlReplacements.Count)"
Add-Content -Path $report -Value "Kotlin/Java mappings: $($ktMappings.Count) -> $mappingPath"
Add-Content -Path $report -Value "\nDetails:\n"
foreach ($r in $xmlReplacements) { Add-Content -Path $report -Value $r }
Write-Output "Apply script complete. Report: $report"
Write-Output "Kotlin/Java mapping file: $mappingPath"
