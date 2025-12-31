# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Firebase Auth
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses
-keep class com.google.firebase.auth.** { *; }
-keep class com.google.android.gms.auth.** { *; }
-keep class com.google.android.gms.common.** { *; }

# Google Sign-In
-keep class com.google.android.gms.auth.api.signin.** { *; }
-keep class com.google.android.gms.auth.api.credentials.** { *; }
-dontwarn com.google.android.gms.**

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Preserve the line number information for debugging stack traces
-keepattributes SourceFile,LineNumberTable

# Hide the original source file name
-renamesourcefileattribute SourceFile

# ==================== ROOM DATABASE ====================
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *
-dontwarn androidx.room.paging.**

# ==================== HILT ====================
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ComponentSupplier { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# ==================== GSON ====================
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# ==================== DATA MODELS ====================
-keep class com.hesapgunlugu.app.domain.model.** { *; }
-keep class com.hesapgunlugu.app.data.local.entity.** { *; }

# ==================== KOTLIN COROUTINES ====================
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}
-dontwarn kotlinx.coroutines.**

# ==================== KOTLIN ====================
-dontwarn kotlin.**
-keep class kotlin.Metadata { *; }
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

# ==================== TIMBER ====================
# Remove Timber logs in release
-assumenosideeffects class timber.log.Timber* {
    public static void v(...);
    public static void d(...);
    public static void i(...);
    public static void w(...);
    public static void e(...);
    public static void wtf(...);
}

# ==================== DATASTORE ====================
-keep class androidx.datastore.** { *; }
-keepclassmembers class * extends com.google.protobuf.GeneratedMessageLite {
    <fields>;
}

# ==================== SECURITY & ENCRYPTION ====================
# Keep security related classes but obfuscate implementation details
-keep class com.hesapgunlugu.app.core.security.SecurityManager {
    public <methods>;
}

# SECURITY: Obfuscate sensitive method names and classes
# This makes reverse engineering more difficult
-repackageclasses 'o'
-allowaccessmodification
-overloadaggressively

# SECURITY: Remove debug info from release builds
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# SECURITY: Obfuscate BuildConfig (hides API keys in dex)
-keepclassmembers class com.hesapgunlugu.app.BuildConfig {
    # Keep only version info visible
    public static final java.lang.String VERSION_NAME;
    public static final int VERSION_CODE;
    # Obfuscate everything else (API keys, etc.)
}

# ==================== CRASH REPORTING (ACRA) ====================
-keep class org.acra.** { *; }
-keep class * implements org.acra.config.Configuration { *; }
-dontwarn org.acra.**

# ==================== OKHTTP & RETROFIT (SSL PINNING) ====================
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# Retrofit
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# OkHttp Platform used only on JVM and when Conscrypt dependency is available
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**

# ==================== GDPR COMPLIANCE ====================
# Keep GDPR manager public API but obfuscate internals
-keep class com.hesapgunlugu.app.core.privacy.GdprComplianceManager {
    public <methods>;
}

# ==================== NATIVE METHODS ====================
-keepclasseswithmembernames class * {
    native <methods>;
}

# ==================== SECURITY HARDENING ====================
# String encryption (makes it harder to extract sensitive strings from APK)
# Note: This requires additional ProGuard/R8 rules or third-party tools

# Remove source file names (makes stack traces less useful to attackers)
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

# Remove unused resources (reduces APK size and attack surface)
# This is done by R8 automatically with isShrinkResources = true

# Obfuscate package names (makes reverse engineering harder)
-flattenpackagehierarchy 'com.hesapgunlugu.obfuscated'

# ==================== END OF SECURITY RULES ====================
-keep class com.hesapgunlugu.app.core.security.PinVerificationResult { *; }
-keep class com.hesapgunlugu.app.core.security.PinValidationResult { *; }
-keep class com.hesapgunlugu.app.core.security.BiometricAuthenticator {
    public <methods>;
}
-keep class com.hesapgunlugu.app.core.backup.BackupEncryption {
    public <methods>;
}

# Keep encryption algorithms
-keep class javax.crypto.** { *; }
-keep class java.security.** { *; }
-dontwarn javax.crypto.**

# ==================== ANNOTATION PROCESSING (Runtime not needed) ====================
-dontwarn javax.annotation.processing.**
-dontwarn com.google.auto.service.processor.**

# ==================== WORKMANAGER ====================
-keep class * extends androidx.work.Worker
-keep class * extends androidx.work.ListenableWorker {
    public <init>(...);
}
-keep class androidx.work.** { *; }

# ==================== COMPOSE ====================
-dontwarn androidx.compose.**
-keep class androidx.compose.** { *; }

# ==================== GENERAL ====================
# Keep generic signatures for reflection
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep Parcelables
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}