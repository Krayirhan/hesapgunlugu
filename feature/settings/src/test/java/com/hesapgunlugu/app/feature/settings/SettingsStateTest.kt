package com.hesapgunlugu.app.feature.settings

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsStateTest {
    @Test
    fun defaultNotificationsEnabled_isTrue() {
        val state = SettingsState()
        assertTrue(state.notificationsEnabled)
    }

    @Test
    fun defaultCurrencyCode_isTry() {
        val state = SettingsState()
        assertEquals("TRY", state.currencyCode)
    }

    @Test
    fun defaultBudgetAlertThreshold_isEighty() {
        val state = SettingsState()
        assertEquals(80, state.budgetAlertThreshold)
    }
}
