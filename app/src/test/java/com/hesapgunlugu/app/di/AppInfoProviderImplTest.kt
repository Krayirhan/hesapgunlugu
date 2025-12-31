package com.hesapgunlugu.app.di

import com.hesapgunlugu.app.BuildConfig
import org.junit.Assert.assertEquals
import org.junit.Test

class AppInfoProviderImplTest {
    @Test
    fun providesBuildConfigValues() {
        val provider = AppInfoProviderImpl()
        assertEquals(BuildConfig.VERSION_NAME, provider.versionName)
        assertEquals(BuildConfig.VERSION_CODE, provider.versionCode)
        assertEquals(BuildConfig.BUILD_TYPE, provider.buildType)
    }
}
