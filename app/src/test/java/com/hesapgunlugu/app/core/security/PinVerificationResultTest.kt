package com.hesapgunlugu.app.core.security

import org.junit.Assert.*
import org.junit.Test

/**
 * PinVerificationResult için unit testler
 */
class PinVerificationResultTest {
    @Test
    fun `Success result is correct type`() {
        val result = PinVerificationResult.Success
        assertTrue("Should be Success type", result is PinVerificationResult.Success)
    }

    @Test
    fun `Failed result contains remaining attempts`() {
        val remainingAttempts = 2
        val result = PinVerificationResult.Failed(remainingAttempts)

        assertTrue("Should be Failed type", result is PinVerificationResult.Failed)
        assertEquals("Remaining attempts should match", remainingAttempts, result.remainingAttempts)
    }

    @Test
    fun `LockedOut result contains lockout seconds`() {
        val lockoutSeconds = 30
        val result = PinVerificationResult.LockedOut(lockoutSeconds)

        assertTrue("Should be LockedOut type", result is PinVerificationResult.LockedOut)
        assertEquals("Lockout seconds should match", lockoutSeconds, result.remainingSeconds)
    }

    @Test
    fun `when pattern matching works correctly`() {
        val successResult: PinVerificationResult = PinVerificationResult.Success
        val failedResult: PinVerificationResult = PinVerificationResult.Failed(1)
        val lockedOutResult: PinVerificationResult = PinVerificationResult.LockedOut(30)

        // Test when pattern matching
        val successMessage =
            when (successResult) {
                is PinVerificationResult.Success -> "success"
                is PinVerificationResult.Failed -> "failed"
                is PinVerificationResult.LockedOut -> "locked"
            }
        assertEquals("success", successMessage)

        val failedMessage =
            when (failedResult) {
                is PinVerificationResult.Success -> "success"
                is PinVerificationResult.Failed -> "failed"
                is PinVerificationResult.LockedOut -> "locked"
            }
        assertEquals("failed", failedMessage)

        val lockedMessage =
            when (lockedOutResult) {
                is PinVerificationResult.Success -> "success"
                is PinVerificationResult.Failed -> "failed"
                is PinVerificationResult.LockedOut -> "locked"
            }
        assertEquals("locked", lockedMessage)
    }

    @Test
    fun `Failed with zero remaining attempts is valid`() {
        val result = PinVerificationResult.Failed(0)
        assertEquals("Zero remaining attempts should be valid", 0, result.remainingAttempts)
    }

    @Test
    fun `LockedOut with different durations works`() {
        val shortLockout = PinVerificationResult.LockedOut(30)
        val longLockout = PinVerificationResult.LockedOut(300)

        assertEquals("Short lockout should be 30 seconds", 30, shortLockout.remainingSeconds)
        assertEquals("Long lockout should be 300 seconds", 300, longLockout.remainingSeconds)
    }
}
