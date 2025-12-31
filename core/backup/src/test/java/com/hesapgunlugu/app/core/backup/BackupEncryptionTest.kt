package com.hesapgunlugu.app.core.backup

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class BackupEncryptionTest {
    @Test
    fun encryptDecrypt_roundTrip() {
        val password = "StrongPass!12345"
        val plainText = "{\"test\":true}"
        val encrypted = BackupEncryption.encrypt(plainText, password)
        assertNotNull(encrypted)

        val decrypted = BackupEncryption.decrypt(encrypted!!, password)
        assertEquals(plainText, decrypted)
    }
}
