package com.hesapgunlugu.app.core.navigation

import androidx.navigation.NavHostController

/**
 * Navigator interface for centralized navigation logic
 *
 * This abstraction allows ViewModels and other components to navigate
 * without directly depending on NavController.
 */
interface Navigator {
    fun navigateTo(destination: NavigationDestination)

    fun navigateToWithArgs(route: String)

    fun navigateUp()

    fun navigateBack()

    fun popBackStackTo(
        destination: NavigationDestination,
        inclusive: Boolean = false,
    )
}

/**
 * Default implementation of Navigator
 */
class NavigatorImpl(private val navController: NavHostController) : Navigator {
    override fun navigateTo(destination: NavigationDestination) {
        navController.navigate(destination.route)
    }

    override fun navigateToWithArgs(route: String) {
        navController.navigate(route)
    }

    override fun navigateUp() {
        navController.navigateUp()
    }

    override fun navigateBack() {
        navController.popBackStack()
    }

    override fun popBackStackTo(
        destination: NavigationDestination,
        inclusive: Boolean,
    ) {
        navController.popBackStack(destination.route, inclusive)
    }
}

/**
 * Extension function to simplify navigation
 */
fun Navigator.navigateToTransactionDetail(transactionId: Long) {
    navigateToWithArgs(NavigationDestination.TransactionDetail.createRoute(transactionId))
}

fun Navigator.navigateToEditTransaction(transactionId: Long) {
    navigateToWithArgs(NavigationDestination.EditTransaction.createRoute(transactionId))
}

fun Navigator.navigateToScheduledDetail(scheduledId: Long) {
    navigateToWithArgs(NavigationDestination.ScheduledDetail.createRoute(scheduledId))
}

fun Navigator.navigateToEditScheduled(scheduledId: Long) {
    navigateToWithArgs(NavigationDestination.EditScheduled.createRoute(scheduledId))
}
