# Google Play Billing Library
-keep class com.android.billingclient.api.** { *; }

# Keep billing models and states
-keep class com.example.mynewapp.core.premium.** { *; }

# Prevent obfuscation of purchase verification
-keep class * implements com.example.mynewapp.core.premium.BillingRepository {
    public *;
}
