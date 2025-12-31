package com.hesapgunlugu.app.core.feedback

/**
 * Interface to provide app information from the app module
 * This breaks the circular dependency between core:feedback and app
 */
interface AppInfoProvider {
    val versionName: String
    val versionCode: Int
    val buildType: String
}
