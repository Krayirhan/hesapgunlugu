package com.hesapgunlugu.app.benchmark

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BenchmarkSmokeTest {

    @Test
    fun targetContext_isAvailable() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        assertNotNull(context)
    }

    @Test
    fun packageName_isNotEmpty() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        assertNotNull(context.packageName)
    }

    @Test
    fun modulePackage_matchesNamespace() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.hesapgunlugu.app", context.packageName)
    }
}
