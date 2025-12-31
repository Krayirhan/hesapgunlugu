# Jetpack Compose
-dontwarn androidx.compose.**
-keep class androidx.compose.** { *; }

# Keep @Composable functions
-keep @androidx.compose.runtime.Composable class * { *; }

# Material3 components
-keep class androidx.compose.material3.** { *; }

# Keep UI theme and components
-keep class com.example.mynewapp.core.ui.theme.** { *; }
-keep class com.example.mynewapp.core.ui.components.** { *; }
