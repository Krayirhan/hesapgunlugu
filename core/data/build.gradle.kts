import java.util.Properties

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.kapt")
    id("com.google.devtools.ksp")
    alias(libs.plugins.room)
}

room {
    schemaDirectory("$projectDir/schemas")
}

android {
    namespace = "com.hesapgunlugu.app.core.data"
    compileSdk = 36

    // Load secrets from secrets.properties (if exists)
    val secretsPropertiesFile = rootProject.file("secrets.properties")
    val secretsProperties = Properties()
    if (secretsPropertiesFile.exists()) {
        secretsProperties.load(secretsPropertiesFile.inputStream())
    }

    fun getSecret(
        key: String,
        default: String = "",
    ): String {
        return secretsProperties.getProperty(key)
            ?: System.getenv(key)
            ?: default
    }

    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        buildConfigField(
            "String",
            "BACKEND_BASE_URL",
            "\"${getSecret("BACKEND_BASE_URL")}\"",
        )
        buildConfigField(
            "String",
            "BACKEND_API_KEY",
            "\"${getSecret("BACKEND_API_KEY")}\"",
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
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
        buildConfig = true
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:domain"))
    implementation(project(":core:util"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    // WorkManager with Hilt
    implementation(libs.work.runtime)
    implementation(libs.hilt.work)
    kapt(libs.hilt.work.compiler)

    // Room - Compatible with Kotlin 2.0.21 and KSP 2.0.21-1.0.27
    // Room 2.6.1 is the stable version that works with Kotlin 2.x
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Paging
    implementation(libs.paging.runtime)

    // DataStore
    implementation(libs.datastore.preferences)
    implementation(libs.gson)

    // Security - Encrypted DataStore
    implementation(libs.security.crypto)

    // SQLCipher - encrypted Room database
    implementation(libs.sqlcipher.android)

    // Retrofit for API
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)

    // Firebase Auth (backend token for validation)
    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")

    // Logging
    implementation(libs.timber)

    // Unit Tests
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.kotlin.test)
}

extra["coverageMinimum"] = "0.90"
extra["branchCoverageMinimum"] = "0.80"
apply(from = "${rootProject.projectDir}/config/jacoco/jacoco.gradle.kts")
