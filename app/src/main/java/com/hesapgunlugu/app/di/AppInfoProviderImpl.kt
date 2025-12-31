package com.hesapgunlugu.app.di

import com.hesapgunlugu.app.BuildConfig
import com.hesapgunlugu.app.core.feedback.AppInfoProvider
import javax.inject.Inject

/**
 * Implementation of AppInfoProvider that provides app's BuildConfig info
 */
class AppInfoProviderImpl
    @Inject
    constructor() : AppInfoProvider {
        override val versionName: String = BuildConfig.VERSION_NAME
        override val versionCode: Int = BuildConfig.VERSION_CODE
        override val buildType: String = BuildConfig.BUILD_TYPE
    }
