package com.hesapgunlugu.app.feature.statistics

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StatisticsStateTest {
    @Test
    fun defaultState_isLoadingTrue() {
        val state = StatisticsState()
        assertTrue(state.isLoading)
    }

    @Test
    fun defaultWeekLabels_hasSevenDays() {
        val state = StatisticsState()
        assertEquals(7, state.weekLabels.size)
    }

    @Test
    fun defaultWeeklyIncome_hasSevenValues() {
        val state = StatisticsState()
        assertEquals(7, state.weeklyIncome.size)
    }
}
