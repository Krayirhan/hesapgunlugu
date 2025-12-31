pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Hesap Günlügü"
include(":app")
// Baseline Profile for startup optimization
include(":baselineprofile")
// multi-module scaffolding
include(":core:common")
include(":core:domain")
include(":core:data")
include(":core:ui")
include(":core:navigation")
include(":core:backup")
include(":core:security")
include(":core:export")
include(":core:util")
include(":core:error")
include(":core:notification")
include(":core:debug")
include(":core:cloud")
include(":core:premium")
include(":core:performance")
include(":core:feedback")
// feature modules
// âœ… AKTIF - feature:home modÃ¼lÃ¼
include(":feature:home")
include(":feature:settings")
include(":feature:history")
include(":feature:scheduled")
include(":feature:statistics")
include(":feature:notifications")
include(":feature:onboarding")
include(":feature:privacy")
// macro benchmark module
include(":benchmark-macro")


