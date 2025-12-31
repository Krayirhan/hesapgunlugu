package com.hesapgunlugu.app.core.navigation

import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.navOptions
import timber.log.Timber

/*
 * Type-safe navigation extensions for NavController
 *
 * These extensions provide compile-time safety when navigating between screens,
 * eliminating string-based routes and reducing navigation-related bugs.
 *
 * ## Usage
 * ```kotlin
 * // Simple navigation
 * navController.navigateTo(Route.Settings)
 *
 * // Navigation with arguments
 * navController.navigateTo(Route.TransactionDetail(transactionId = 123))
 *
 * // Navigation with options
 * navController.navigateTo(Route.Home) {
 *     popUpTo(Route.Onboarding) { inclusive = true }
 * }
 * ```
 */

/**
 * Navigate to a route in a type-safe manner
 *
 * @param route The destination route (must be a sealed class member of Route)
 * @param builder Optional navigation options builder
 */
inline fun <reified T : Route> NavController.navigateTo(
    route: T,
    noinline builder: NavOptionsBuilder.() -> Unit = {},
) {
    try {
        navigate(route, navOptions(builder))
        Timber.d("Navigation: ${route::class.simpleName}")
    } catch (e: Exception) {
        Timber.e(e, "Navigation failed to ${route::class.simpleName}")
    }
}

/**
 * Navigate back with optional result
 *
 * @param route Optional destination to pop up to
 * @param inclusive Whether to include the destination in the pop
 */
inline fun <reified T : Route> NavController.navigateBackTo(
    route: T,
    inclusive: Boolean = false,
) {
    popBackStack(route, inclusive)
}

/**
 * Navigate back without route
 */
fun NavController.navigateBack(): Boolean {
    return navigateUp()
}

/**
 * Navigate and clear back stack up to route
 *
 * @param destination The new destination
 * @param popUpToRoute Clear stack up to this route
 * @param inclusive Include popUpToRoute in the clear
 */
inline fun <reified D : Route, reified P : Route> NavController.navigateAndClearBackStack(
    destination: D,
    popUpToRoute: P,
    inclusive: Boolean = true,
) {
    navigate(
        destination,
        navOptions {
            popUpTo<P> {
                this.inclusive = inclusive
            }
            launchSingleTop = true
        },
    )
}

/**
 * Navigate to home and clear entire back stack
 */
fun NavController.navigateToHomeAndClearBackStack() {
    navigate(
        Route.Home,
        navOptions {
            popUpTo(0) { inclusive = true }
            launchSingleTop = true
        },
    )
}

/**
 * Navigate to a tab destination (Home, Statistics, Scheduled, History, Settings)
 * This mimics bottom navigation behavior - saves and restores state properly.
 *
 * Use this when navigating to main tab destinations from within a screen
 * to ensure proper back stack management and bottom navigation sync.
 *
 * @param route The tab destination route
 */
inline fun <reified T : Route> NavController.navigateToTab(route: T) {
    try {
        val startDestinationId = graph.findStartDestination().id
        navigate(
            route,
            navOptions {
                popUpTo(startDestinationId) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            },
        )
        Timber.d("Tab Navigation: ${route::class.simpleName}")
    } catch (e: Exception) {
        Timber.e(e, "Tab navigation failed to ${route::class.simpleName}")
    }
}

/**
 * Safe navigation that checks if route can be navigated to
 */
inline fun <reified T : Route> NavController.safeNavigateTo(
    route: T,
    noinline builder: NavOptionsBuilder.() -> Unit = {},
) {
    try {
        val currentRoute = currentDestination?.route
        if (currentRoute != route::class.qualifiedName) {
            navigate(route, navOptions(builder))
        } else {
            Timber.d("Already at destination: ${route::class.simpleName}")
        }
    } catch (e: Exception) {
        Timber.e(e, "Safe navigation failed")
    }
}

/**
 * Navigate with result callback support
 *
 * ## Usage
 * ```kotlin
 * // Navigate and wait for result
 * navController.navigateForResult<String, Route.Settings>(Route.Settings, lifecycleOwner) { result ->
 *     result?.let { handleResult(it) }
 * }
 * ```
 */
inline fun <T, reified R : Route> NavController.navigateForResult(
    route: R,
    lifecycleOwner: LifecycleOwner,
    key: String = "result",
    crossinline onResult: (T?) -> Unit,
) {
    val savedStateHandle = currentBackStackEntry?.savedStateHandle
    savedStateHandle?.getLiveData<T>(key)?.observe(lifecycleOwner) { result: T ->
        onResult(result)
        savedStateHandle.remove<T>(key)
    }
    navigate(route)
}

/**
 * Set navigation result for previous screen
 */
fun <T> NavController.setNavigationResult(
    key: String = "result",
    result: T,
) {
    previousBackStackEntry?.savedStateHandle?.set(key, result)
}
