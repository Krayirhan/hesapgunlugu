plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.kapt")
}

android {
    namespace = "com.hesapgunlugu.app.core.premium"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "BILLING_BACKEND_URL",
            "\"${project.findProperty("BILLING_BACKEND_URL") ?: ""}\"",
        )

        buildConfigField(
            "String",
            "BILLING_BACKEND_API_KEY",
            "\"${project.findProperty("BILLING_BACKEND_API_KEY") ?: ""}\"",
        )

        buildConfigField(
            "String",
            "BILLING_BACKEND_HOST",
            "\"${project.findProperty("BILLING_BACKEND_HOST") ?: ""}\"",
        )

        buildConfigField(
            "String",
            "BILLING_BACKEND_PINS",
            "\"${project.findProperty("BILLING_BACKEND_PINS") ?: ""}\"",
        )
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        buildConfig = true
    }
}

kapt {
    correctErrorTypes = true
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.core)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    // Google Play Billing
    implementation("com.android.billingclient:billing-ktx:6.1.0")

    // Retrofit & OkHttp for backend verification
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.gson)

    implementation(libs.timber)
}
