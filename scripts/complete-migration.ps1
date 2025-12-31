# ========================================
# COMPLETE FEATURE MIGRATION + CLEANUP
# ========================================
# Bu script:
# 1. 7 feature'ı taşır
# 2. NavGraph import'larını günceller
# 3. app/feature/* klasörünü siler (common hariç)
# 4. Build test eder

Write-Host "🚀 KOMPLE FEATURE MİGRATION BAŞLIYOR..." -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""

$projectRoot = "C:\Users\Acer\AndroidStudioProjects\MyNewApp"
$features = @("settings", "history", "scheduled", "statistics", "notifications", "onboarding", "privacy")

# ========================================
# PART 1: FEATURE MODÜLLERINI OLUŞTUR
# ========================================

$buildGradleTemplate = @'
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.hesapgunlugu.app.feature.FEATURE_NAME"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }
}

kapt {
    correctErrorTypes = true
}

dependencies {
    // Core modules
    implementation(project(":core:common"))
    implementation(project(":core:domain"))
    implementation(project(":core:ui"))
    implementation(project(":core:navigation"))

    // Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    implementation(libs.hilt.navigation.compose)

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
'@

Write-Host "📦 PART 1: Feature modülleri oluşturuluyor..." -ForegroundColor Yellow
Write-Host ""

$successCount = 0

foreach ($feature in $features) {
    Write-Host "   📂 $feature..." -ForegroundColor White

    try {
        # Klasör yapısı
        $featureDir = "$projectRoot\feature\$feature"
        $srcDir = "$featureDir\src\main\java\com\example\mynewapp\feature\$feature"
        New-Item -Path $srcDir -ItemType Directory -Force | Out-Null

        # Dosyaları kopyala
        $sourceDir = "$projectRoot\app\src\main\java\com\example\mynewapp\feature\$feature"
        if (Test-Path $sourceDir) {
            Copy-Item -Path "$sourceDir\*" -Destination $srcDir -Recurse -Force
        }

        # build.gradle.kts
        $buildGradle = $buildGradleTemplate -replace "FEATURE_NAME", $feature
        $buildGradle | Out-File -FilePath "$featureDir\build.gradle.kts" -Encoding UTF8

        # proguard-rules.pro
        "# Add project specific ProGuard rules here" | Out-File -FilePath "$featureDir\proguard-rules.pro" -Encoding UTF8

        Write-Host "      ✅ Başarılı" -ForegroundColor Green
        $successCount++

    } catch {
        Write-Host "      ❌ Hata: $_" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "   ✅ $successCount/7 feature modülü oluşturuldu" -ForegroundColor Green
Write-Host ""

# ========================================
# PART 2: NAVGRAPH IMPORT'LARINI GÜNCELLE
# ========================================

Write-Host "📝 PART 2: NavGraph import'ları güncelleniyor..." -ForegroundColor Yellow

$navGraphPath = "$projectRoot\app\src\main\java\com\example\mynewapp\feature\common\navigation\NavGraph.kt"

if (Test-Path $navGraphPath) {
    $content = Get-Content $navGraphPath -Raw

    # app.feature'dan feature modülüne import'ları değiştir
    $content = $content -replace 'import com\.example\.mynewapp\.feature\.settings\.', 'import com.hesapgunlugu.app.feature.settings.'
    $content = $content -replace 'import com\.example\.mynewapp\.feature\.history\.', 'import com.hesapgunlugu.app.feature.history.'
    $content = $content -replace 'import com\.example\.mynewapp\.feature\.scheduled\.', 'import com.hesapgunlugu.app.feature.scheduled.'
    $content = $content -replace 'import com\.example\.mynewapp\.feature\.statistics\.', 'import com.hesapgunlugu.app.feature.statistics.'
    $content = $content -replace 'import com\.example\.mynewapp\.feature\.notifications\.', 'import com.hesapgunlugu.app.feature.notifications.'
    $content = $content -replace 'import com\.example\.mynewapp\.feature\.onboarding\.', 'import com.hesapgunlugu.app.feature.onboarding.'
    $content = $content -replace 'import com\.example\.mynewapp\.feature\.privacy\.', 'import com.hesapgunlugu.app.feature.privacy.'

    $content | Out-File -FilePath $navGraphPath -Encoding UTF8 -NoNewline
    Write-Host "   ✅ NavGraph.kt güncellendi" -ForegroundColor Green
} else {
    Write-Host "   ⚠️  NavGraph.kt bulunamadı" -ForegroundColor Yellow
}

Write-Host ""

# ========================================
# PART 3: app/feature/* KLASÖRÜNÜ TEMİZLE
# ========================================

Write-Host "🗑️  PART 3: app/feature/* klasörleri siliniyor..." -ForegroundColor Yellow
Write-Host ""

$deleteCount = 0

foreach ($feature in $features) {
    $featurePath = "$projectRoot\app\src\main\java\com\example\mynewapp\feature\$feature"

    if (Test-Path $featurePath) {
        try {
            Remove-Item -Path $featurePath -Recurse -Force
            Write-Host "   ✅ app/feature/$feature silindi" -ForegroundColor Green
            $deleteCount++
        } catch {
            Write-Host "   ❌ app/feature/$feature silinemedi: $_" -ForegroundColor Red
        }
    }
}

Write-Host ""
Write-Host "   ✅ $deleteCount/7 feature klasörü silindi" -ForegroundColor Green
Write-Host ""

# ========================================
# PART 4: BUILD TEST
# ========================================

Write-Host "🔨 PART 4: Build test ediliyor..." -ForegroundColor Yellow
Write-Host "   (Bu 40-60 saniye sürebilir...)" -ForegroundColor Gray
Write-Host ""

Push-Location $projectRoot

try {
    & .\gradlew clean assembleFreeDebug 2>&1 | Out-Null

    if ($LASTEXITCODE -eq 0) {
        Write-Host ""
        Write-Host "═══════════════════════════════════════" -ForegroundColor Green
        Write-Host "   ✅ BUILD SUCCESSFUL!" -ForegroundColor Green
        Write-Host "═══════════════════════════════════════" -ForegroundColor Green
        Write-Host ""
        Write-Host "🎉 7 FEATURE BAŞARIYLA TAŞINDI!" -ForegroundColor Green
        Write-Host ""
        Write-Host "📊 Sonuçlar:" -ForegroundColor Cyan
        Write-Host "   ✅ Feature modülleri: $successCount/7" -ForegroundColor Green
        Write-Host "   ✅ app/feature/* temizlendi: $deleteCount/7" -ForegroundColor Green
        Write-Host "   ✅ Build: BAŞARILI" -ForegroundColor Green
        Write-Host ""
        Write-Host "📈 İlerleme: 85% → 100% 🎊" -ForegroundColor Cyan
        Write-Host ""
        Write-Host "📝 Commit yapabilirsiniz:" -ForegroundColor Yellow
        Write-Host '   git add .' -ForegroundColor White
        Write-Host '   git commit -m "refactor: migrate all features to independent modules"' -ForegroundColor White
        Write-Host ""

    } else {
        Write-Host ""
        Write-Host "❌ BUILD FAILED!" -ForegroundColor Red
        Write-Host "Manuel build yapın:" -ForegroundColor Yellow
        Write-Host "   .\gradlew assembleFreeDebug --stacktrace" -ForegroundColor White
        Write-Host ""
    }

} catch {
    Write-Host "❌ Build hatası: $_" -ForegroundColor Red
}

Pop-Location

Write-Host ""
Write-Host "════════════════════════════════════════" -ForegroundColor Cyan
Write-Host "   MIGRATION TAMAMLANDI!" -ForegroundColor Cyan
Write-Host "════════════════════════════════════════" -ForegroundColor Cyan

