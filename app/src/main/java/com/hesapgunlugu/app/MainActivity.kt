package com.hesapgunlugu.app

import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hesapgunlugu.app.core.navigation.Route
import com.hesapgunlugu.app.core.security.IntegrityCheckResult
import com.hesapgunlugu.app.core.security.PinLockScreen
import com.hesapgunlugu.app.core.security.PlayIntegrityManager
import com.hesapgunlugu.app.core.security.RootDetector
import com.hesapgunlugu.app.core.security.SecurityViewModel
import com.hesapgunlugu.app.core.ui.theme.AccessibleTheme
import com.hesapgunlugu.app.core.ui.theme.HesapGunluguTheme
import com.hesapgunlugu.app.core.util.LocalizationUtils
import com.hesapgunlugu.app.feature.common.navigation.AppNavGraph
import com.hesapgunlugu.app.feature.common.navigation.Screen
import com.hesapgunlugu.app.feature.onboarding.OnboardingManager
import com.hesapgunlugu.app.feature.onboarding.OnboardingScreen
import com.hesapgunlugu.app.feature.settings.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    @Inject
    lateinit var onboardingManager: OnboardingManager

    @Inject
    lateinit var playIntegrityManager: PlayIntegrityManager

    @Inject
    lateinit var rootDetector: RootDetector

    private var keepSplashScreen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install Splash Screen before super.onCreate
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { keepSplashScreen }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeViewModel: ThemeViewModel = hiltViewModel()
            val securityViewModel: SecurityViewModel = hiltViewModel()
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsStateWithLifecycle()
            val securityState by securityViewModel.state.collectAsStateWithLifecycle()
            var integrityState by remember { mutableStateOf<IntegrityGateState>(IntegrityGateState.Checking) }

            // Splash screen'i kapat
            LaunchedEffect(Unit) {
                keepSplashScreen = false
            }

            LaunchedEffect(Unit) {
                integrityState =
                    if (BuildConfig.DEBUG) {
                        IntegrityGateState.Allowed
                    } else {
                        if (rootDetector.isDeviceRooted() || rootDetector.isEmulator()) {
                            IntegrityGateState.Blocked("Device integrity check failed.")
                        } else {
                            when (val result = playIntegrityManager.checkIntegrity()) {
                                is IntegrityCheckResult.Success -> IntegrityGateState.Allowed
                                is IntegrityCheckResult.Failed -> IntegrityGateState.Blocked(result.reason)
                            }
                        }
                    }
            }

            // Screenshot koruması - PIN ekranı gösterildiğinde aktif et
            LaunchedEffect(securityState.showPinScreen) {
                if (securityState.showPinScreen) {
                    window.setFlags(
                        WindowManager.LayoutParams.FLAG_SECURE,
                        WindowManager.LayoutParams.FLAG_SECURE,
                    )
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                }
            }

            // Onboarding durumu
            var showOnboarding by remember {
                mutableStateOf(!onboardingManager.isOnboardingCompleted())
            }

            val lifecycleOwner = LocalLifecycleOwner.current
            val activity = LocalActivity.current as FragmentActivity

            // Initialize shared prefs and current language state
            val prefs = activity.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            var currentLanguage by remember {
                mutableStateOf(
                    prefs.getString("language_code", "tr") ?: "tr",
                )
            }

            // Uygulama arka plana gittiğinde kilitle
            DisposableEffect(lifecycleOwner) {
                val observer =
                    LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_STOP) {
                            securityViewModel.onAppBackgrounded()
                        }
                    }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }

            // Dil değişikliğini izle
            DisposableEffect(Unit) {
                val listener =
                    android.content.SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
                        if (key == "language_code") {
                            val newLanguage = prefs.getString("language_code", "tr") ?: "tr"
                            if (newLanguage != currentLanguage) {
                                currentLanguage = newLanguage
                                // Configuration'ı güncelle - bu tüm string kaynakları yeniler
                                LocalizationUtils.updateConfiguration(this@MainActivity, newLanguage)
                            }
                        }
                    }
                prefs.registerOnSharedPreferenceChangeListener(listener)
                onDispose {
                    prefs.unregisterOnSharedPreferenceChangeListener(listener)
                }
            }

            // currentLanguage değiştiğinde Composable'ları yeniden oluştur
            key(currentLanguage) {
                AccessibleTheme(darkTheme = isDarkTheme) {
                    HesapGunluguTheme(darkTheme = isDarkTheme) {
                        when {
                            integrityState is IntegrityGateState.Checking -> {
                                IntegrityCheckingScreen()
                            }
                            integrityState is IntegrityGateState.Blocked -> {
                                IntegrityBlockedScreen(
                                    reason = (integrityState as IntegrityGateState.Blocked).reason,
                                )
                            }
                            // İlk kullanımda onboarding göster
                            showOnboarding -> {
                                OnboardingScreen(
                                    onFinish = {
                                        onboardingManager.setOnboardingCompleted()
                                        showOnboarding = false
                                    },
                                )
                            }
                            // PIN ekranı göster
                            securityState.showPinScreen && !securityState.isLoading -> {
                                PinLockScreen(
                                    onPinEntered = { pin ->
                                        securityViewModel.verifyPin(pin)
                                    },
                                    onBiometricClick = {
                                        securityViewModel.authenticateWithBiometric(activity)
                                    },
                                    onForgotPin = {
                                        // PIN sıfırlama - SecurityViewModel üzerinden
                                        securityViewModel.resetPin()
                                        securityViewModel.setAppLockEnabled(false)
                                    },
                                    showBiometric = securityState.isBiometricEnabled && securityViewModel.canUseBiometric(),
                                    error = securityState.pinError,
                                )
                            }
                            // Ana içerik
                            else -> {
                                MainContent(themeViewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}

private sealed class IntegrityGateState {
    data object Checking : IntegrityGateState()

    data object Allowed : IntegrityGateState()

    data class Blocked(val reason: String) : IntegrityGateState()
}

@Composable
private fun IntegrityCheckingScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun IntegrityBlockedScreen(reason: String) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(24.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Security check failed",
                style = MaterialTheme.typography.headlineSmall,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = reason,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun MainContent(themeViewModel: ThemeViewModel) {
    val navController = rememberNavController()

    // 5 Tab Navigasyon - Type-safe routes
    val items =
        listOf(
            Screen.Home,
            Screen.Statistics,
            Screen.Scheduled,
            Screen.Calendar,
            Screen.Settings,
        )

    // Route mapping for type-safe navigation
    val routeMap =
        mapOf(
            Screen.Home to Route.Home,
            Screen.Statistics to Route.Statistics,
            Screen.Scheduled to Route.Scheduled,
            Screen.Calendar to Route.History,
            Screen.Settings to Route.Settings,
        )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                tonalElevation = 0.dp,
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { screen ->
                    val targetRoute = routeMap[screen]
                    // Type-safe route comparison using route class name
                    val isSelected =
                        when (targetRoute) {
                            is Route.Home ->
                                currentDestination?.route == "com.hesapgunlugu.app.core.navigation.Route.Home" ||
                                    currentDestination?.route?.endsWith("Home") == true
                            is Route.Statistics ->
                                currentDestination?.route == "com.hesapgunlugu.app.core.navigation.Route.Statistics" ||
                                    currentDestination?.route?.endsWith("Statistics") == true
                            is Route.Scheduled ->
                                currentDestination?.route == "com.hesapgunlugu.app.core.navigation.Route.Scheduled" ||
                                    currentDestination?.route?.endsWith("Scheduled") == true
                            is Route.History ->
                                currentDestination?.route == "com.hesapgunlugu.app.core.navigation.Route.History" ||
                                    currentDestination?.route?.endsWith("History") == true
                            is Route.Settings ->
                                currentDestination?.route == "com.hesapgunlugu.app.core.navigation.Route.Settings" ||
                                    currentDestination?.route?.endsWith("Settings") == true
                            else -> false
                        }

                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                contentDescription = stringResource(screen.titleRes),
                                modifier = Modifier.size(24.dp),
                            )
                        },
                        label = {
                            Text(
                                text = stringResource(screen.titleRes),
                                fontSize = 10.sp,
                                maxLines = 1,
                            )
                        },
                        selected = isSelected,
                        onClick = {
                            // Prevent re-navigation to the same screen
                            if (!isSelected) {
                                val route = routeMap[screen] ?: Route.Home
                                navController.navigate(route) {
                                    // Pop up to the start destination of the graph to
                                    // avoid building up a large stack of destinations
                                    // on the back stack as users select items
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    // Avoid multiple copies of the same destination when
                                    // reselecting the same item
                                    launchSingleTop = true
                                    // Restore state when reselecting a previously selected item
                                    restoreState = true
                                }
                            }
                        },
                        colors =
                            NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                    )
                }
            }
        },
    ) { innerPadding ->
        AppNavGraph(
            navController = navController,
            innerPadding = innerPadding,
        )
    }
}
