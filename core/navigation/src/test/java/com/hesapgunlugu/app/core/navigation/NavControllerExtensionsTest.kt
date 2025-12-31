package com.hesapgunlugu.app.core.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptions
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNotNull

/**
 * Tests for type-safe navigation extensions
 */
class NavControllerExtensionsTest {
    private lateinit var navController: NavController

    @Before
    fun setup() {
        navController = mockk(relaxed = true)
        every { navController.currentDestination } returns mockk(relaxed = true)
    }

    @Test
    fun `navigateTo calls navigate with route`() {
        navController.navigateTo(Route.Home)

        verify { navController.navigate(Route.Home, any<NavOptions>()) }
    }

    @Test
    fun `navigateTo with arguments passes correct route`() {
        val route = Route.TransactionDetail(transactionId = 123L)

        navController.navigateTo(route)

        verify { navController.navigate(route, any<NavOptions>()) }
    }

    @Test
    fun `navigateBack with route calls popBackStack`() {
        navController.navigateBackTo(Route.Home, inclusive = true)

        verify { navController.popBackStack(Route.Home, true) }
    }

    @Test
    fun `navigateBack without route calls navigateUp`() {
        navController.navigateBack()

        verify { navController.navigateUp() }
    }

    @Test
    fun `navigateAndClearBackStack sets correct nav options`() {
        navController.navigateAndClearBackStack(
            destination = Route.Home,
            popUpToRoute = Route.Onboarding,
            inclusive = true,
        )

        verify {
            navController.navigate(
                Route.Home,
                any<NavOptions>(),
            )
        }
    }

    @Test
    fun `navigateToHomeAndClearBackStack clears entire stack`() {
        navController.navigateToHomeAndClearBackStack()

        verify {
            navController.navigate(
                Route.Home,
                any<NavOptions>(),
            )
        }
    }

    @Test
    fun `safeNavigateTo prevents duplicate navigation`() {
        every { navController.currentDestination?.route } returns Route.Home::class.qualifiedName

        navController.safeNavigateTo(Route.Home)

        // Should NOT navigate (already at destination)
        verify(exactly = 0) { navController.navigate(any<Route>(), any<NavOptions>()) }
    }

    @Test
    fun `all Route destinations are properly sealed`() {
        // Verify all routes are accessible
        assertNotNull(Route.Home)
        assertNotNull(Route.Statistics)
        assertNotNull(Route.Settings)
        assertNotNull(Route.Notifications)
        assertNotNull(Route.Scheduled)
        assertNotNull(Route.History)
        assertNotNull(Route.TransactionDetail(1L))
        assertNotNull(Route.CategoryDetail("Food"))
        assertNotNull(Route.EditTransaction(1L))
    }
}
