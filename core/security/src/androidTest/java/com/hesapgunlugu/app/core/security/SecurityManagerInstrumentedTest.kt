package com.hesapgunlugu.app.core.security

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hesapgunlugu.app.core.common.time.TimeProvider
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SecurityManagerInstrumentedTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private lateinit var timeProvider: FakeTimeProvider
    private lateinit var securityManager: SecurityManager

    @Before
    fun setup() =
        runBlocking {
            timeProvider = FakeTimeProvider(1_700_000_000_000)
            securityManager = SecurityManager(context, timeProvider)
            securityManager.clearAllSecurityData()
        }

    @Test
    fun setPin_thenVerify_success() =
        runBlocking {
            securityManager.setPin("2468")
            val result = securityManager.verifyPin("2468")
            assertTrue(result is PinVerificationResult.Success)
        }

    @Test
    fun verifyPin_wrongAttempts_lockout_thenExpires() =
        runBlocking {
            securityManager.setPin("2468")

            repeat(3) {
                securityManager.verifyPin("0000")
            }

            val lockedOut = securityManager.verifyPin("2468")
            assertTrue(lockedOut is PinVerificationResult.LockedOut)

            timeProvider.advanceMillis(31_000)
            val success = securityManager.verifyPin("2468")
            assertTrue(success is PinVerificationResult.Success)
        }

    private class FakeTimeProvider(private var currentMillis: Long) : TimeProvider {
        override fun nowMillis(): Long = currentMillis

        fun advanceMillis(deltaMillis: Long) {
            currentMillis += deltaMillis
        }
    }
}
