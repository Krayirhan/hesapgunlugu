# ================================================================
# RELEASE KEYSTORE OLUŞTURMA SCRİPTİ
# ================================================================
# Bu script interaktif olarak release keystore oluşturur
# DİKKAT: Şifreleri güvenli bir yerde saklayın!
# ================================================================

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   RELEASE KEYSTORE OLUSTURMA WIZARD   " -ForegroundColor Cyan  
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Keystore yolu
$keystorePath = "$PSScriptRoot\release-keystore.jks"

# Zaten var mı kontrol et
if (Test-Path $keystorePath) {
    Write-Host "⚠️  UYARI: Keystore zaten mevcut: $keystorePath" -ForegroundColor Yellow
    $overwrite = Read-Host "Üzerine yazmak istiyor musunuz? (E/H)"
    if ($overwrite -ne "E" -and $overwrite -ne "e") {
        Write-Host "İşlem iptal edildi." -ForegroundColor Red
        exit
    }
    Remove-Item $keystorePath -Force
}

# Java/keytool yolunu bul
$javaHome = $null
$possiblePaths = @(
    "$env:JAVA_HOME\bin\keytool.exe",
    "C:\Program Files\Android\Android Studio\jbr\bin\keytool.exe",
    "C:\Program Files\Java\jdk-17\bin\keytool.exe",
    "C:\Program Files\Java\jdk-11\bin\keytool.exe"
)

foreach ($path in $possiblePaths) {
    if (Test-Path $path) {
        $keytoolPath = $path
        break
    }
}

if (-not $keytoolPath) {
    Write-Host "❌ HATA: keytool bulunamadı!" -ForegroundColor Red
    Write-Host "Java JDK veya Android Studio kurulu olduğundan emin olun." -ForegroundColor Yellow
    exit 1
}

Write-Host "✓ Keytool bulundu: $keytoolPath" -ForegroundColor Green
Write-Host ""

# Bilgileri al
Write-Host "📝 Keystore Bilgilerini Girin:" -ForegroundColor Yellow
Write-Host "   (Bu bilgiler sertifikada görünecek)" -ForegroundColor Gray
Write-Host ""

$alias = Read-Host "Key Alias [mynewapp-release]"
if ([string]::IsNullOrEmpty($alias)) { $alias = "mynewapp-release" }

$storePass = Read-Host "Keystore Şifresi (en az 6 karakter)" -AsSecureString
$storePassPlain = [Runtime.InteropServices.Marshal]::PtrToStringAuto([Runtime.InteropServices.Marshal]::SecureStringToBSTR($storePass))

$keyPass = Read-Host "Key Şifresi (Enter = aynı şifre)" -AsSecureString
$keyPassPlain = [Runtime.InteropServices.Marshal]::PtrToStringAuto([Runtime.InteropServices.Marshal]::SecureStringToBSTR($keyPass))
if ([string]::IsNullOrEmpty($keyPassPlain)) { $keyPassPlain = $storePassPlain }

Write-Host ""
Write-Host "📋 Sertifika Bilgileri:" -ForegroundColor Yellow

$cn = Read-Host "Ad Soyad (CN)"
$ou = Read-Host "Organizasyon Birimi (OU) [Development]"
if ([string]::IsNullOrEmpty($ou)) { $ou = "Development" }

$o = Read-Host "Şirket/Organizasyon (O)"
$l = Read-Host "Şehir (L) [Istanbul]"
if ([string]::IsNullOrEmpty($l)) { $l = "Istanbul" }

$st = Read-Host "Eyalet/İl (ST) [Istanbul]"
if ([string]::IsNullOrEmpty($st)) { $st = "Istanbul" }

$c = Read-Host "Ülke Kodu (C) [TR]"
if ([string]::IsNullOrEmpty($c)) { $c = "TR" }

$dname = "CN=$cn, OU=$ou, O=$o, L=$l, ST=$st, C=$c"

Write-Host ""
Write-Host "🔧 Keystore oluşturuluyor..." -ForegroundColor Yellow

# Keystore oluştur
$keytoolArgs = @(
    "-genkeypair",
    "-v",
    "-keystore", $keystorePath,
    "-keyalg", "RSA",
    "-keysize", "2048",
    "-validity", "10000",
    "-alias", $alias,
    "-storepass", $storePassPlain,
    "-keypass", $keyPassPlain,
    "-dname", $dname
)

try {
    & $keytoolPath @keytoolArgs 2>&1 | Out-Null
    
    if (Test-Path $keystorePath) {
        Write-Host ""
        Write-Host "✅ KEYSTORE BAŞARIYLA OLUŞTURULDU!" -ForegroundColor Green
        Write-Host "   Konum: $keystorePath" -ForegroundColor White
        Write-Host ""
        
        # local.properties için bilgi
        Write-Host "📝 local.properties'e eklenecek satırlar:" -ForegroundColor Cyan
        Write-Host "----------------------------------------" -ForegroundColor Gray
        Write-Host "signing.storeFile=../signing/release-keystore.jks"
        Write-Host "signing.storePassword=$storePassPlain"
        Write-Host "signing.keyAlias=$alias"
        Write-Host "signing.keyPassword=$keyPassPlain"
        Write-Host "----------------------------------------" -ForegroundColor Gray
        Write-Host ""
        
        # Otomatik local.properties güncelleme
        $localProps = "$PSScriptRoot\..\local.properties"
        $addToLocal = Read-Host "Bu bilgileri local.properties'e eklemek ister misiniz? (E/H)"
        
        if ($addToLocal -eq "E" -or $addToLocal -eq "e") {
            $signingConfig = @"

# Release Signing Configuration (Added by create-keystore.ps1)
signing.storeFile=../signing/release-keystore.jks
signing.storePassword=$storePassPlain
signing.keyAlias=$alias
signing.keyPassword=$keyPassPlain
"@
            Add-Content -Path $localProps -Value $signingConfig
            Write-Host "✅ local.properties güncellendi!" -ForegroundColor Green
        }
        
        Write-Host ""
        Write-Host "⚠️  ÖNEMLİ HATIRLATMALAR:" -ForegroundColor Yellow
        Write-Host "   1. Keystore'u yedekleyin (USB, Cloud vault)" -ForegroundColor White
        Write-Host "   2. Şifreleri güvenli bir yerde saklayın" -ForegroundColor White
        Write-Host "   3. Keystore'u Git'e ASLA commit etmeyin" -ForegroundColor White
        Write-Host "   4. CI/CD için GitHub Secrets kullanın" -ForegroundColor White
        Write-Host ""
        
    } else {
        Write-Host "❌ Keystore oluşturulamadı!" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ Hata: $_" -ForegroundColor Red
}
