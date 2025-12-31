# Keep all domain models - they're data transfer objects
-keep class com.example.mynewapp.core.domain.model.** { *; }

# Keep use case interfaces and implementations
-keep class com.example.mynewapp.core.domain.usecase.** { *; }

# Keep repository interfaces
-keep interface com.example.mynewapp.core.domain.repository.** { *; }

# Preserve sealed classes for when expressions
-keep class * extends kotlin.coroutines.Continuation
