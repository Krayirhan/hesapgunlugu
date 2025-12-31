# ========================================
# MASTER FEATURE MIGRATION SCRIPT
# ========================================
# Bu script 7 feature'ı otomatik olarak taşır

Write-Host "🚀 7 FEATURE TAŞIMA İŞLEMİ BAŞLIYOR..." -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""

$projectRoot = "C:\Users\Acer\AndroidStudioProjects\MyNewApp"
$features = @("settings", "history", "scheduled", "statistics", "notifications", "onboarding", "privacy")

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

$successCount = 0
$errorCount = 0

foreach ($feature in $features) {
    Write-Host "📦 $feature TAŞINIYOR..." -ForegroundColor Yellow

    try {
        # 1. Modül klasör yapısı oluştur
        $featureDir = "$projectRoot\feature\$feature"
        $srcDir = "$featureDir\src\main\java\com\example\mynewapp\feature\$feature"

        New-Item -Path $srcDir -ItemType Directory -Force | Out-Null
        Write-Host "   ✅ Klasör yapısı oluşturuldu" -ForegroundColor Green

        # 2. Dosyaları kopyala
        $sourceDir = "$projectRoot\app\src\main\java\com\example\mynewapp\feature\$feature"
        if (Test-Path $sourceDir) {
            Copy-Item -Path "$sourceDir\*" -Destination $srcDir -Recurse -Force
            Write-Host "   ✅ Dosyalar kopyalandı" -ForegroundColor Green
        } else {
            Write-Host "   ⚠️  Kaynak klasör bulunamadı" -ForegroundColor Yellow
        }

        # 3. build.gradle.kts oluştur
        $buildGradle = $buildGradleTemplate -replace "FEATURE_NAME", $feature
        $buildGradle | Out-File -FilePath "$featureDir\build.gradle.kts" -Encoding UTF8
        Write-Host "   ✅ build.gradle.kts oluşturuldu" -ForegroundColor Green

        # 4. proguard-rules.pro oluştur
        $proguardContent = "# Add project specific ProGuard rules here"
        $proguardContent | Out-File -FilePath "$featureDir\proguard-rules.pro" -Encoding UTF8
        Write-Host "   ✅ proguard-rules.pro oluşturuldu" -ForegroundColor Green

        Write-Host "   🎉 $feature BAŞARILI!" -ForegroundColor Green
        $successCount++

    } catch {
        Write-Host "   ❌ HATA: $_" -ForegroundColor Red
        $errorCount++
    }

    Write-Host ""
}

Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "📊 ÖZET:" -ForegroundColor Cyan
Write-Host "   ✅ Başarılı: $successCount" -ForegroundColor Green
Write-Host "   ❌ Hatalı: $errorCount" -ForegroundColor Red
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""

if ($successCount -eq 7) {
    Write-Host "🎉 TÜM FEATURE'LAR BAŞARIYLA TAŞINDI!" -ForegroundColor Green
    Write-Host ""
    Write-Host "📝 Sıradaki adımlar:" -ForegroundColor Yellow
    Write-Host "   1. app/build.gradle.kts'e feature dependency'leri eklenecek" -ForegroundColor White
    Write-Host "   2. NavGraph import'ları güncellenecek" -ForegroundColor White
    Write-Host "   3. app/feature/* klasörü silinecek" -ForegroundColor White
    Write-Host "   4. Build test edilecek" -ForegroundColor White
    Write-Host ""
} else {
    Write-Host "⚠️  Bazı feature'lar taşınamadı. Lütfen hataları kontrol edin." -ForegroundColor Yellow
}

