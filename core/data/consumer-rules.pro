# Room Database
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Keep all DAO interfaces
-keep interface * extends androidx.room.Dao {
    *;
}

# Hilt - Keep injected classes
-keepclasseswithmembers class * {
    @dagger.hilt.android.lifecycle.HiltViewModel <init>(...);
}
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-dontwarn kotlinx.coroutines.flow.**

# Keep data classes used in Room
-keepclassmembers class com.example.mynewapp.core.data.local.entity.** {
    *;
}

# Keep all model classes
-keep class com.example.mynewapp.core.domain.model.** { *; }
