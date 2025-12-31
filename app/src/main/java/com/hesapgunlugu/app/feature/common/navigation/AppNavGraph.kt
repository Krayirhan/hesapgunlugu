package com.hesapgunlugu.app.feature.common.navigation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.hesapgunlugu.app.auth.AuthViewModel
import com.hesapgunlugu.app.core.navigation.Route
import com.hesapgunlugu.app.core.navigation.navigateTo
import com.hesapgunlugu.app.core.navigation.navigateToHomeAndClearBackStack
import com.hesapgunlugu.app.feature.history.EditTransactionScreen
import com.hesapgunlugu.app.feature.history.HistoryScreen
import com.hesapgunlugu.app.feature.history.HistoryViewModel
import com.hesapgunlugu.app.feature.history.TransactionDetailScreen
import com.hesapgunlugu.app.feature.home.HomeScreen
import com.hesapgunlugu.app.feature.home.HomeViewModel
import com.hesapgunlugu.app.feature.notifications.NotificationCenterScreen
import com.hesapgunlugu.app.feature.privacy.PrivacyPolicyScreen
import com.hesapgunlugu.app.feature.scheduled.ScheduledScreen
import com.hesapgunlugu.app.feature.scheduled.ScheduledViewModel
import com.hesapgunlugu.app.feature.settings.CategoryManagementScreen
import com.hesapgunlugu.app.feature.settings.DataDeletionScreen
import com.hesapgunlugu.app.feature.settings.SettingsScreen
import com.hesapgunlugu.app.feature.settings.SettingsViewModel
import com.hesapgunlugu.app.feature.settings.ThemeViewModel
import com.hesapgunlugu.app.feature.statistics.StatisticsScreen
import com.hesapgunlugu.app.feature.statistics.StatisticsViewModel
import com.hesapgunlugu.app.core.ui.R as CoreUiR

/**
 * Type-safe navigation graph using Kotlin Serialization
 *
 * Benefits:
 * - Compile-time safety for navigation arguments
 * - No string-based routes
 * - Automatic argument parsing
 * - Better refactoring support
 */
@Suppress("FunctionNaming", "LongMethod")
@Composable
fun AppNavGraph(
    navController: NavHostController,
    innerPadding: PaddingValues,
) {
    NavHost(
        navController = navController,
        startDestination = Route.Home,
        modifier =
            Modifier
                .fillMaxSize()
                .padding(innerPadding),
    ) {
        // Home Screen
        composable<Route.Home> {
            val homeViewModel: HomeViewModel = hiltViewModel()
            val authViewModel: AuthViewModel = hiltViewModel()
            val authState by authViewModel.state.collectAsStateWithLifecycle()
            val signInLauncher =
                rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult(),
                ) { result ->
                    authViewModel.onGoogleSignInResult(result.data)
                }
            HomeScreen(
                homeViewModel = homeViewModel,
                navController = navController,
                userName =
                    authState.displayName
                        ?: authState.email
                        ?: stringResource(CoreUiR.string.auth_default_user),
                isSignedIn = authState.isSignedIn,
                onSignIn = { signInLauncher.launch(authViewModel.getSignInIntent()) },
                onSignOut = { authViewModel.signOut() },
                authError = authState.error,
                isAuthLoading = authState.isLoading,
                onAuthErrorShown = { authViewModel.clearError() },
            )
        }

        // Statistics Screen
        composable<Route.Statistics> {
            val statisticsViewModel: StatisticsViewModel = hiltViewModel()
            StatisticsScreen(
                viewModel = statisticsViewModel,
            )
        }

        // Scheduled Payments Screen
        composable<Route.Scheduled> {
            val scheduledViewModel: ScheduledViewModel = hiltViewModel()
            ScheduledScreen(
                viewModel = scheduledViewModel,
            )
        }

        // History/Calendar Screen
        composable<Route.History> {
            val historyViewModel: HistoryViewModel = hiltViewModel()
            HistoryScreen(
                viewModel = historyViewModel,
                onBackClick = { navController.navigateUp() },
                onTransactionClick = { transactionId ->
                    navController.navigateTo(Route.TransactionDetail(transactionId = transactionId))
                },
            )
        }

        // Settings Screen
        composable<Route.Settings> {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val themeViewModel: ThemeViewModel = hiltViewModel()
            SettingsScreen(
                viewModel = settingsViewModel,
                navController = navController,
                themeViewModel = themeViewModel,
            )
        }

        // Notifications Screen
        composable<Route.Notifications> {
            NotificationCenterScreen(
                onBackClick = { navController.navigateUp() },
            )
        }

        // Privacy Policy Screen
        composable<Route.Privacy> {
            PrivacyPolicyScreen(
                onBackClick = { navController.navigateUp() },
            )
        }

        // Transaction Detail Screen (with type-safe arguments)
        composable<Route.TransactionDetail> {
            TransactionDetailScreen(
                onBackClick = { navController.navigateUp() },
                onEditClick = { transactionId ->
                    navController.navigateTo(Route.EditTransaction(transactionId = transactionId))
                },
            )
        }

        // Category Detail Screen (with type-safe arguments)
        composable<Route.CategoryDetail> { backStackEntry ->
            val args = backStackEntry.toRoute<Route.CategoryDetail>()
            CategoryManagementScreen(
                onBackClick = { navController.navigateUp() },
            )
        }

        // Edit Transaction Screen (with type-safe arguments)
        composable<Route.EditTransaction> {
            EditTransactionScreen(
                onBackClick = { navController.navigateUp() },
            )
        }

        // Category Management Screen
        composable<Route.CategoryManagement> {
            CategoryManagementScreen(
                onBackClick = { navController.navigateUp() },
            )
        }

        // Data Deletion Screen
        composable<Route.DataDeletion> {
            DataDeletionScreen(
                onBackClick = { navController.navigateUp() },
                onDataDeleted = { navController.navigateToHomeAndClearBackStack() },
            )
        }
    }
}
