package com.hesapgunlugu.app.core.security

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hesapgunlugu.app.core.common.time.TimeProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * SecurityManager için Unit Testler
 */
@RunWith(AndroidJUnit4::class)
class SecurityManagerTest {
    private lateinit var securityManager: SecurityManager
    private lateinit var context: Context
    private lateinit var timeProvider: FakeTimeProvider

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        timeProvider = FakeTimeProvider(1_700_000_000_000)
        securityManager = SecurityManager(context, timeProvider)
    }

    @After
    fun tearDown() =
        runBlocking {
            // Test sonrası temizlik
            securityManager.removePin()
        }

    @Test
    fun `validatePinStrength should reject short PIN`() {
        val result = securityManager.validatePinStrength("123")
        assertFalse(result.isValid)
        assertEquals("PIN en az 4 haneli olmalıdır", result.errorMessage)
    }

    @Test
    fun `validatePinStrength should reject long PIN`() {
        val result = securityManager.validatePinStrength("123456789")
        assertFalse(result.isValid)
        assertEquals("PIN en fazla 8 haneli olabilir", result.errorMessage)
    }

    @Test
    fun `validatePinStrength should reject non-digit PIN`() {
        val result = securityManager.validatePinStrength("12ab")
        assertFalse(result.isValid)
        assertEquals("PIN sadece rakamlardan oluşmalıdır", result.errorMessage)
    }

    @Test
    fun `validatePinStrength should reject repetitive PIN`() {
        val result = securityManager.validatePinStrength("1111")
        assertFalse(result.isValid)
        assertEquals("PIN tekrarlayan rakamlar içeremez (örn: 1111)", result.errorMessage)
    }

    @Test
    fun `validatePinStrength should reject sequential ascending PIN`() {
        val result = securityManager.validatePinStrength("1234")
        assertFalse(result.isValid)
        assertEquals("PIN ardışık rakamlar içeremez (örn: 1234)", result.errorMessage)
    }

    @Test
    fun `validatePinStrength should reject sequential descending PIN`() {
        val result = securityManager.validatePinStrength("4321")
        assertFalse(result.isValid)
        assertEquals("PIN ardışık rakamlar içeremez (örn: 1234)", result.errorMessage)
    }

    @Test
    fun `validatePinStrength should reject common weak PINs`() {
        val result = securityManager.validatePinStrength("0000")
        assertFalse(result.isValid)
        assertEquals("Bu PIN çok yaygın, daha güçlü bir PIN seçin", result.errorMessage)
    }

    @Test
    fun `validatePinStrength should accept strong PIN`() {
        val result = securityManager.validatePinStrength("2468")
        assertTrue(result.isValid)
        assertEquals("PIN geçerli", result.errorMessage)
    }

    @Test
    fun `setPin should successfully set valid PIN`() =
        runBlocking {
            val pin = "2580"
            securityManager.setPin(pin)

            val hasPinSet = securityManager.hasPinSet.first()
            assertTrue(hasPinSet)

            val isAppLockEnabled = securityManager.isAppLockEnabled.first()
            assertTrue(isAppLockEnabled)
        }

    @Test(expected = IllegalArgumentException::class)
    fun `setPin should throw exception for invalid PIN`() =
        runBlocking {
            securityManager.setPin("1234") // Sequential PIN
        }

    @Test
    fun `verifyPin should succeed for correct PIN`() =
        runBlocking {
            val pin = "2580"
            securityManager.setPin(pin)

            val result = securityManager.verifyPin(pin)
            assertTrue(result is PinVerificationResult.Success)
        }

    @Test
    fun `verifyPin should fail for incorrect PIN`() =
        runBlocking {
            val correctPin = "2580"
            val wrongPin = "1357"

            securityManager.setPin(correctPin)
            val result = securityManager.verifyPin(wrongPin)

            assertTrue(result is PinVerificationResult.Failed)
            if (result is PinVerificationResult.Failed) {
                assertEquals(2, result.remainingAttempts) // 3 - 1 = 2
            }
        }

    @Test
    fun `verifyPin should lockout after max failed attempts`() =
        runBlocking {
            val correctPin = "2580"
            securityManager.setPin(correctPin)

            // 3 kez yanlış deneme
            repeat(3) {
                securityManager.verifyPin("0000")
            }

            // 4. deneme kilitlenme döndürmeli
            val result = securityManager.verifyPin("0000")
            assertTrue(result is PinVerificationResult.LockedOut)

            if (result is PinVerificationResult.LockedOut) {
                assertTrue(result.remainingSeconds > 0)
                assertTrue(result.remainingSeconds <= 30)
            }
        }

    @Test
    fun `verifyPin should reset failed attempts after successful login`() =
        runBlocking {
            val correctPin = "2580"
            securityManager.setPin(correctPin)

            // 2 kez yanlış deneme
            securityManager.verifyPin("0000")
            securityManager.verifyPin("0000")

            // Doğru PIN ile giriş
            val successResult = securityManager.verifyPin(correctPin)
            assertTrue(successResult is PinVerificationResult.Success)

            // Kalan deneme hakkı sıfırlanmalı
            val remainingAttempts = securityManager.getRemainingAttempts()
            assertEquals(3, remainingAttempts)
        }

    @Test
    fun `setBiometricEnabled should update preference`() =
        runBlocking {
            securityManager.setBiometricEnabled(true)
            val isEnabled = securityManager.isBiometricEnabled.first()
            assertTrue(isEnabled)

            securityManager.setBiometricEnabled(false)
            val isDisabled = securityManager.isBiometricEnabled.first()
            assertFalse(isDisabled)
        }

    @Test
    fun `setAuthenticated should update session state`() =
        runBlocking {
            securityManager.setAuthenticated(true)
            val isAuth = securityManager.isAuthenticated.first()
            assertTrue(isAuth)

            securityManager.setAuthenticated(false)
            val isNotAuth = securityManager.isAuthenticated.first()
            assertFalse(isNotAuth)
        }

    @Test
    fun `removePin should clear PIN and disable app lock`() =
        runBlocking {
            securityManager.setPin("2580")
            assertTrue(securityManager.hasPinSet.first())

            securityManager.removePin()

            val hasPinSet = securityManager.hasPinSet.first()
            assertFalse(hasPinSet)

            val isAppLockEnabled = securityManager.isAppLockEnabled.first()
            assertFalse(isAppLockEnabled)
        }

    private class FakeTimeProvider(private var currentMillis: Long) : TimeProvider {
        override fun nowMillis(): Long = currentMillis
    }
}
