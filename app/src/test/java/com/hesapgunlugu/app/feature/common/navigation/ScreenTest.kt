package com.hesapgunlugu.app.feature.common.navigation

import com.hesapgunlugu.app.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ScreenTest {
    @Test
    fun homeScreen_hasExpectedRouteAndTitle() {
        assertEquals("home", Screen.Home.route)
        assertEquals(R.string.nav_home, Screen.Home.titleRes)
        assertNotNull(Screen.Home.selectedIcon)
        assertNotNull(Screen.Home.unselectedIcon)
    }

    @Test
    fun statisticsScreen_hasExpectedRouteAndTitle() {
        assertEquals("statistics", Screen.Statistics.route)
        assertEquals(R.string.nav_statistics, Screen.Statistics.titleRes)
    }

    @Test
    fun scheduledScreen_hasExpectedRouteAndTitle() {
        assertEquals("scheduled", Screen.Scheduled.route)
        assertEquals(R.string.nav_scheduled, Screen.Scheduled.titleRes)
    }

    @Test
    fun calendarScreen_hasExpectedRouteAndTitle() {
        assertEquals("calendar", Screen.Calendar.route)
        assertEquals(R.string.calendar, Screen.Calendar.titleRes)
    }

    @Test
    fun settingsScreen_hasExpectedRouteAndTitle() {
        assertEquals("settings", Screen.Settings.route)
        assertEquals(R.string.nav_settings, Screen.Settings.titleRes)
    }

    @Test
    fun notificationsScreen_hasExpectedRouteAndTitle() {
        assertEquals("notifications", Screen.Notifications.route)
        assertEquals(R.string.notifications, Screen.Notifications.titleRes)
    }

    @Test
    fun privacyScreen_hasExpectedRouteAndTitle() {
        assertEquals("privacy", Screen.Privacy.route)
        assertEquals(R.string.privacy_policy, Screen.Privacy.titleRes)
    }
}
