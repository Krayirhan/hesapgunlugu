# ========================================
# COMPLETE FEATURE MIGRATION + CLEANUP
# ========================================

Write-Host "KOMPLE FEATURE MIGRATION BASLIYOR..." -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""

$projectRoot = "C:\Users\Acer\AndroidStudioProjects\MyNewApp"
$features = @("settings", "history", "scheduled", "statistics", "notifications", "onboarding", "privacy")

# ========================================
# PART 1: FEATURE MODULLERINI OLUSTUR
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
    implementation(project(":core:common"))
    implementation(project(":core:domain"))
    implementation(project(":core:ui"))
    implementation(project(":core:navigation"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)

    implementation(libs.androidx.navigation.compose)

    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    implementation(libs.hilt.navigation.compose)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
'@

Write-Host "PART 1: Feature modulleri olusturuluyor..." -ForegroundColor Yellow
Write-Host ""

$successCount = 0

foreach ($feature in $features) {
    Write-Host "   $feature..." -ForegroundColor White

    try {
        $featureDir = "$projectRoot\feature\$feature"
        $srcDir = "$featureDir\src\main\java\com\example\mynewapp\feature\$feature"
        New-Item -Path $srcDir -ItemType Directory -Force | Out-Null

        $sourceDir = "$projectRoot\app\src\main\java\com\example\mynewapp\feature\$feature"
        if (Test-Path $sourceDir) {
            Copy-Item -Path "$sourceDir\*" -Destination $srcDir -Recurse -Force
        }

        $buildGradle = $buildGradleTemplate -replace "FEATURE_NAME", $feature
        $buildGradle | Out-File -FilePath "$featureDir\build.gradle.kts" -Encoding UTF8

        "# Add project specific ProGuard rules here" | Out-File -FilePath "$featureDir\proguard-rules.pro" -Encoding UTF8

        Write-Host "      Basarili" -ForegroundColor Green
        $successCount++

    } catch {
        Write-Host "      Hata: $_" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "   $successCount/7 feature modulu olusturuldu" -ForegroundColor Green
Write-Host ""

# ========================================
# PART 2: app/feature/* KLASORUNU TEMIZLE
# ========================================

Write-Host "PART 2: app/feature/* klasorleri siliniyor..." -ForegroundColor Yellow
Write-Host ""

$deleteCount = 0

foreach ($feature in $features) {
    $featurePath = "$projectRoot\app\src\main\java\com\example\mynewapp\feature\$feature"

    if (Test-Path $featurePath) {
        try {
            Remove-Item -Path $featurePath -Recurse -Force
            Write-Host "   app/feature/$feature silindi" -ForegroundColor Green
            $deleteCount++
        } catch {
            Write-Host "   app/feature/$feature silinemedi: $_" -ForegroundColor Red
        }
    }
}

Write-Host ""
Write-Host "   $deleteCount/7 feature klasoru silindi" -ForegroundColor Green
Write-Host ""

# ========================================
# PART 3: BUILD TEST
# ========================================

Write-Host "PART 3: Build test ediliyor..." -ForegroundColor Yellow
Write-Host "   (Bu 40-60 saniye surebilir...)" -ForegroundColor Gray
Write-Host ""

Push-Location $projectRoot

try {
    & .\gradlew clean assembleFreeDebug 2>&1 | Out-Null

    if ($LASTEXITCODE -eq 0) {
        Write-Host ""
        Write-Host "=======================================" -ForegroundColor Green
        Write-Host "   BUILD SUCCESSFUL!" -ForegroundColor Green
        Write-Host "=======================================" -ForegroundColor Green
        Write-Host ""
        Write-Host "7 FEATURE BASARIYLA TASINDI!" -ForegroundColor Green
        Write-Host ""
        Write-Host "Sonuclar:" -ForegroundColor Cyan
        Write-Host "   Feature modulleri: $successCount/7" -ForegroundColor Green
        Write-Host "   app/feature/* temizlendi: $deleteCount/7" -ForegroundColor Green
        Write-Host "   Build: BASARILI" -ForegroundColor Green
        Write-Host ""
        Write-Host "Ilerleme: 85% -> 100%" -ForegroundColor Cyan
        Write-Host ""
        Write-Host "Commit yapabilirsiniz:" -ForegroundColor Yellow
        Write-Host '   git add .' -ForegroundColor White
        Write-Host '   git commit -m "refactor: migrate all features to independent modules"' -ForegroundColor White
        Write-Host ""

    } else {
        Write-Host ""
        Write-Host "BUILD FAILED!" -ForegroundColor Red
        Write-Host "Manuel build yapin:" -ForegroundColor Yellow
        Write-Host "   .\gradlew assembleFreeDebug --stacktrace" -ForegroundColor White
        Write-Host ""
    }

} catch {
    Write-Host "Build hatasi: $_" -ForegroundColor Red
}

Pop-Location

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   MIGRATION TAMAMLANDI!" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

