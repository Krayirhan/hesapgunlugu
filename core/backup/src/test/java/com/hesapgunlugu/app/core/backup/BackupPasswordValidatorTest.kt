package com.hesapgunlugu.app.core.backup

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BackupPasswordValidatorTest {
    @Test
    fun validate_rejectsShortPassword() {
        val result = BackupPasswordValidator.validate("short")
        assertFalse(result.isValid)
    }

    @Test
    fun validate_acceptsStrongPassword() {
        val result = BackupPasswordValidator.validate("Str0ng!Passw0rd!")
        assertTrue(result.isValid)
    }
}
