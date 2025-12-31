package com.hesapgunlugu.app.core.navigation

/**
 * Navigation destinations for the app
 *
 * Centralized navigation routes to avoid hardcoded strings across the app.
 * Each destination represents a screen in the application.
 */
sealed class NavigationDestination(val route: String) {
    // Main navigation destinations
    data object Home : NavigationDestination("home")

    data object Transactions : NavigationDestination("transactions")

    data object Scheduled : NavigationDestination("scheduled")

    data object Statistics : NavigationDestination("statistics")

    data object Settings : NavigationDestination("settings")

    // Detail screens
    data object TransactionDetail : NavigationDestination("transaction/{transactionId}") {
        fun createRoute(transactionId: Long) = "transaction/$transactionId"
    }

    data object AddTransaction : NavigationDestination("add_transaction")

    data object EditTransaction : NavigationDestination("edit_transaction/{transactionId}") {
        fun createRoute(transactionId: Long) = "edit_transaction/$transactionId"
    }

    data object ScheduledDetail : NavigationDestination("scheduled/{scheduledId}") {
        fun createRoute(scheduledId: Long) = "scheduled/$scheduledId"
    }

    data object AddScheduled : NavigationDestination("add_scheduled")

    data object EditScheduled : NavigationDestination("edit_scheduled/{scheduledId}") {
        fun createRoute(scheduledId: Long) = "edit_scheduled/$scheduledId"
    }

    // Settings sub-screens
    data object CategorySettings : NavigationDestination("settings/categories")

    data object SecuritySettings : NavigationDestination("settings/security")

    data object BackupSettings : NavigationDestination("settings/backup")

    data object AboutSettings : NavigationDestination("settings/about")

    // Onboarding
    data object Onboarding : NavigationDestination("onboarding")
}

/**
 * Navigation arguments
 */
object NavigationArgs {
    const val TRANSACTION_ID = "transactionId"
    const val SCHEDULED_ID = "scheduledId"
}

/**
 * Bottom navigation items
 */
val bottomNavigationItems =
    listOf(
        NavigationDestination.Home,
        NavigationDestination.Transactions,
        NavigationDestination.Scheduled,
        NavigationDestination.Statistics,
        NavigationDestination.Settings,
    )
