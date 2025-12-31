# Use Case Migration Script
# This script updates all imports across the project

$projectRoot = "c:\Users\Acer\AndroidStudioProjects\MyNewApp"

Write-Host "🔄 Updating imports from app/domain/usecase to core/domain/usecase..." -ForegroundColor Cyan

# Find all .kt files in app module
$files = Get-ChildItem -Path "$projectRoot\app\src" -Recurse -Filter "*.kt"

$totalFiles = $files.Count
$currentFile = 0

foreach ($file in $files) {
    $currentFile++
    $content = Get-Content $file.FullName -Raw
    
    # Check if file contains old imports
    if ($content -match "com\.example\.mynewapp\.domain\.usecase") {
        Write-Host "[$currentFile/$totalFiles] Updating $($file.Name)..." -ForegroundColor Yellow
        
        # Replace imports
        $updated = $content -replace 'import com\.example\.mynewapp\.domain\.usecase', 'import com.hesapgunlugu.app.core.domain.usecase'
        
        Set-Content -Path $file.FullName -Value $updated -NoNewline
        Write-Host "  ✅ Updated" -ForegroundColor Green
    } else {
        Write-Host "[$currentFile/$totalFiles] Skipping $($file.Name)" -ForegroundColor Gray
    }
}

Write-Host "`n✅ Migration complete!" -ForegroundColor Green
Write-Host "📝 Please verify the changes and build the project." -ForegroundColor Cyan
