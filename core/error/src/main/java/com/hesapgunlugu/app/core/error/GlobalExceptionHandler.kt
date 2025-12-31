package com.hesapgunlugu.app.core.error

import android.content.Context
import android.util.Log

object GlobalExceptionHandler {
    @Volatile
    private var initialized = false

    fun initialize(context: Context) {
        if (initialized) return
        initialized = true

        val previous = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                Log.e("GlobalExceptionHandler", "Uncaught exception in thread=${thread.name}", throwable)
            } catch (_: Throwable) {
                // Ignore any logging failures.
            }
            previous?.uncaughtException(thread, throwable)
        }
    }
}
