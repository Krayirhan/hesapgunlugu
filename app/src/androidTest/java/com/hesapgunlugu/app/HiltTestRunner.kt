package com.hesapgunlugu.app

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

/**
 * Custom Hilt Test Runner
 *
 * Instrumented testlerde Hilt'in çalışabilmesi için gerekli.
 * AndroidManifest'te testInstrumentationRunner olarak kullanılır.
 */
class HiltTestRunner : AndroidJUnitRunner() {
    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?,
    ): Application {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}
