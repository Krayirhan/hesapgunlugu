# ========================================
# FEATURE BUILD.GRADLE.KTS OLUŞTURMA
# ========================================

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

Write-Host "📝 build.gradle.kts dosyaları oluşturuluyor..." -ForegroundColor Yellow
Write-Host ""

foreach ($feature in $features) {
    $featureDir = "$projectRoot\feature\$feature"
    $buildGradle = $buildGradleTemplate -replace "FEATURE_NAME", $feature

    $buildGradle | Out-File -FilePath "$featureDir\build.gradle.kts" -Encoding UTF8
    Write-Host "   ✅ feature/$feature/build.gradle.kts" -ForegroundColor Green
}

Write-Host ""
Write-Host "✅ TÜMÜ OLUŞTURULDU!" -ForegroundColor Green

