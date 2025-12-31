# WorkManager
-keep class * extends androidx.work.Worker
-keep class * extends androidx.work.CoroutineWorker {
    public <init>(android.content.Context,androidx.work.WorkerParameters);
}

# Hilt Workers
-keep @androidx.hilt.work.HiltWorker class * {
    <init>(...);
}

# Keep notification models
-keep class com.example.mynewapp.core.notification.** { *; }
