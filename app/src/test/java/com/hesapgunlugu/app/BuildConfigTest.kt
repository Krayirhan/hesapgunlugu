package com.hesapgunlugu.app

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BuildConfigTest {
    @Test
    fun versionName_isNotEmpty() {
        assertFalse(BuildConfig.VERSION_NAME.isNullOrBlank())
    }

    @Test
    fun buildType_isNotEmpty() {
        assertFalse(BuildConfig.BUILD_TYPE.isNullOrBlank())
    }

    @Test
    fun versionCode_isPositive() {
        assertTrue(BuildConfig.VERSION_CODE > 0)
    }
}
