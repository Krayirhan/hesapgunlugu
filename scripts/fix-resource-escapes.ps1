# Replace escaped apostrophes (\') in resource XML files which can cause AAPT errors
$files = Get-ChildItem -Path $PSScriptRoot -Recurse -Include '*\res\values\*.xml' -File -ErrorAction SilentlyContinue
foreach ($f in $files) {
    try {
        $text = Get-Content $f.FullName -Raw -ErrorAction Stop
        if ($text -match "\\'") {
            $new = $text -replace "\\'","'"
            Set-Content -Path $f.FullName -Value $new -Encoding utf8
            Write-Output "Fixed: $($f.FullName)"
        }
    } catch {
        Write-Output "Error processing $($f.FullName): $_"
    }
}
Write-Output "Done."
