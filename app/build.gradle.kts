import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    // Room için KSP
    alias(libs.plugins.ksp)

    // ✅ Hilt için KAPT (alias kullanıyorsun, id("org.jetbrains.kotlin.kapt") YAZMA)
    alias(libs.plugins.kotlin.kapt)

    // ✅ Hilt plugin
    alias(libs.plugins.hilt.android)

    // Code Coverage
    jacoco

    // Detekt for code quality - using version catalog
    alias(libs.plugins.detekt)

    // Screenshot Testing
    id("app.cash.paparazzi")

    // Firebase
    id("com.google.gms.google-services") version "4.4.2"
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.firebase.performance)
}

// Load secrets from secrets.properties (if exists)
val secretsPropertiesFile = rootProject.file("secrets.properties")
val secretsProperties = Properties()
if (secretsPropertiesFile.exists()) {
    secretsProperties.load(secretsPropertiesFile.inputStream())
}

// Helper function to get secret or environment variable
fun getSecret(
    key: String,
    default: String = "",
): String {
    return secretsProperties.getProperty(key)
        ?: System.getenv(key)
        ?: default
}

android {
    namespace = "com.hesapgunlugu.app"
    compileSdk = 36
    val versionCodeOverride = (project.findProperty("versionCode") as? String)?.toIntOrNull()
    val versionNameOverride = project.findProperty("versionName") as? String

    defaultConfig {
        applicationId = "com.hesapgunlugu.app"
        minSdk = 26
        targetSdk = 36
        versionCode = versionCodeOverride ?: 10000 // Format: MAJOR(1) + MINOR(00) + PATCH(00) = 1.0.0
        versionName = versionNameOverride ?: "1.0.0"
        testInstrumentationRunner = "com.hesapgunlugu.app.HiltTestRunner"

        // Vector drawable support
        vectorDrawables {
            useSupportLibrary = true
        }

        // SECURITY: API Keys via BuildConfig (NOT hardcoded)
        // These will be obfuscated by R8 in release builds
        buildConfigField("String", "FIREBASE_API_KEY", "\"${getSecret("FIREBASE_API_KEY")}\"")
        buildConfigField("String", "FIREBASE_PROJECT_ID", "\"${getSecret("FIREBASE_PROJECT_ID")}\"")
        buildConfigField("String", "SENTRY_DSN", "\"${getSecret("SENTRY_DSN")}\"")
        buildConfigField("String", "ACRA_EMAIL", "\"${getSecret("ACRA_EMAIL")}\"")

        // Feature flags via BuildConfig
        buildConfigField("Boolean", "ENABLE_CRASH_REPORTING", "${getSecret("ENABLE_CRASH_REPORTING", "true")}")
        buildConfigField("Boolean", "ENABLE_ANALYTICS", "${getSecret("ENABLE_ANALYTICS", "true")}")
    }

    // Product Flavors
    flavorDimensions += "version"
    productFlavors {
        create("free") {
            dimension = "version"
            applicationIdSuffix = ".free"
            versionNameSuffix = "-free"
            buildConfigField("Boolean", "IS_PREMIUM", "false")
            buildConfigField("int", "MAX_TRANSACTIONS", "100")
            buildConfigField("Boolean", "ADS_ENABLED", "true")
        }
        create("premium") {
            dimension = "version"
            applicationIdSuffix = ".premium"
            versionNameSuffix = "-premium"
            buildConfigField("Boolean", "IS_PREMIUM", "true")
            buildConfigField("int", "MAX_TRANSACTIONS", "Integer.MAX_VALUE")
            buildConfigField("Boolean", "ADS_ENABLED", "false")
        }
    }

    // Signing configuration
    signingConfigs {
        create("release") {
            val keystorePropertiesFile = rootProject.file("local.properties")
            if (keystorePropertiesFile.exists()) {
                val keystoreProperties = Properties()
                keystoreProperties.load(keystorePropertiesFile.inputStream())

                val storeFilePath = keystoreProperties.getProperty("signing.storeFile")
                if (!storeFilePath.isNullOrBlank()) {
                    storeFile = file(storeFilePath)
                    storePassword = keystoreProperties.getProperty("signing.storePassword") ?: ""
                    keyAlias = keystoreProperties.getProperty("signing.keyAlias") ?: ""
                    keyPassword = keystoreProperties.getProperty("signing.keyPassword") ?: ""
                }
            } else {
                // CI/CD environment variables
                val envKeystore = System.getenv("KEYSTORE_FILE")
                if (!envKeystore.isNullOrBlank()) {
                    storeFile = file(envKeystore)
                    storePassword = System.getenv("KEYSTORE_PASSWORD")
                    keyAlias = System.getenv("KEY_ALIAS")
                    keyPassword = System.getenv("KEY_PASSWORD")
                }
            }
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            buildConfigField("String", "BUILD_ENV", "\"DEBUG\"")
        }

        // Staging build type for testing
        create("staging") {
            initWith(getByName("debug"))
            isMinifyEnabled = false
            applicationIdSuffix = ".staging"
            versionNameSuffix = "-staging"
            buildConfigField("String", "BUILD_ENV", "\"STAGING\"")
            // Enable strict mode for testing
            buildConfigField("Boolean", "STRICT_MODE", "true")
        }

        // Beta build type for internal testing
        create("beta") {
            initWith(getByName("release"))
            applicationIdSuffix = ".beta"
            versionNameSuffix = "-beta"
            buildConfigField("String", "BUILD_ENV", "\"BETA\"")
            // Beta-specific configurations
            buildConfigField("Boolean", "ENABLE_CRASH_REPORTING", "true")
            buildConfigField("Boolean", "ENABLE_ANALYTICS", "true")
            buildConfigField("Boolean", "DEBUG_LOGS", "true")
            signingConfig = signingConfigs.getByName("release")
        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            buildConfigField("String", "BUILD_ENV", "\"RELEASE\"")
            buildConfigField("Boolean", "STRICT_MODE", "false")

            // Apply signing configuration
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"

        // Compose Compiler Metrics & Reports
        val metricsDestination = layout.buildDirectory.get().asFile.resolve("compose_metrics")
        val reportsDestination = layout.buildDirectory.get().asFile.resolve("compose_reports")
        freeCompilerArgs +=
            listOf(
                "-P",
                "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=$metricsDestination",
                "-P",
                "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=$reportsDestination",
            )
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    // Lint Configuration
    lint {
        disable += setOf("MissingTranslation")
        abortOnError = false
        checkReleaseBuilds = true
        warningsAsErrors = false
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }

    packaging {
        resources {
            excludes +=
                setOf(
                    "META-INF/DEPENDENCIES",
                    "META-INF/LICENSE",
                    "META-INF/LICENSE.txt",
                    "META-INF/license.txt",
                    "META-INF/NOTICE",
                    "META-INF/NOTICE.txt",
                    "META-INF/notice.txt",
                    "META-INF/ASL2.0",
                    "META-INF/*.kotlin_module",
                )
        }
    }
}

// Room schema export is in core:data module only
// (app module doesn't define Room entities, only provides DB instance)

kapt {
    correctErrorTypes = true
}

dependencies {
    // Core modules
    implementation(project(":core:common"))
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":core:ui"))
    implementation(project(":core:navigation"))
    implementation(project(":core:error"))
    implementation(project(":core:notification"))
    implementation(project(":core:util"))
    implementation(project(":core:backup"))
    implementation(project(":core:security"))
    implementation(project(":core:export"))
    implementation(project(":core:feedback"))

    // Feature modules
    // ✅ AKTIF - feature:home modülü
    implementation(project(":feature:home"))
    implementation(project(":feature:settings"))
    implementation(project(":feature:history"))
    implementation(project(":feature:scheduled"))
    implementation(project(":feature:statistics"))
    implementation(project(":feature:notifications"))
    implementation(project(":feature:onboarding"))
    implementation(project(":feature:privacy"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Splash Screen
    implementation(libs.splashscreen)

    // App Widget - Using RemoteViews (no additional library needed)

    // Charts - Using custom Canvas implementation (no external library needed)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.navigation.compose)

    // Firebase Auth & Google Sign-In (optional login)
    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.android.gms:play-services-auth:21.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // Room (KSP)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // SQLCipher - encrypted Room database (required for DatabaseEncryption)
    implementation(libs.sqlcipher.android)

    // Hilt (KAPT) - version catalog
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    implementation(libs.hilt.navigation.compose)

    // DataStore & Gson
    implementation(libs.datastore.preferences)
    implementation(libs.gson)

    // Biometric Authentication
    implementation(libs.biometric)

    // Logging
    implementation(libs.timber)

    // Crash Reporting
    implementation(libs.acra.core)
    implementation(libs.acra.dialog)
    implementation(libs.acra.mail)

    // WorkManager
    implementation(libs.work.runtime)
    implementation(libs.hilt.work)
    kapt(libs.hilt.work.compiler)

    // Paging 3
    implementation(libs.paging.runtime)
    implementation(libs.paging.compose)

    // Memory Leak Detection - Active for debug builds
    // LeakCanary automatically detects memory leaks and shows notifications
    // Completely excluded from release builds (no runtime overhead)
    debugImplementation(libs.leakcanary)

    // Baseline Profile for startup optimization
    implementation(libs.profileinstaller)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.performance)

    // Tests
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.arch.core.testing)

    // Android Tests
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.turbine)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.mockk)

    // Hilt Testing - using existing hilt.android libs
    androidTestImplementation(libs.hilt.android)
    kaptAndroidTest(libs.hilt.android.compiler)
    testImplementation(libs.hilt.android)
    kaptTest(libs.hilt.android.compiler)

    // Room Testing - using existing room libs
    testImplementation(libs.androidx.room.ktx)

    // Benchmark Testing
    androidTestImplementation(libs.benchmark.macro.junit4)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

// Apply Jacoco configuration
apply(from = "${rootProject.projectDir}/config/jacoco/jacoco.gradle")

// Detekt configuration
detekt {
    buildUponDefaultConfig = true
    config.setFrom("${rootProject.projectDir}/config/detekt/detekt.yml")
    baseline = file("${rootProject.projectDir}/config/detekt/baseline.xml")
}
