package com.hesapgunlugu.app.core.common.time

import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

interface TimeProvider {
    fun nowMillis(): Long

    fun nowDate(): Date = Date(nowMillis())
}

@Singleton
class SystemTimeProvider
    @Inject
    constructor() : TimeProvider {
        override fun nowMillis(): Long = System.currentTimeMillis()
    }
