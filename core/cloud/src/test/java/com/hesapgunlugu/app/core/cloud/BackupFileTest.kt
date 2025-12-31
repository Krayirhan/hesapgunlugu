package com.hesapgunlugu.app.core.cloud

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class BackupFileTest {
    @Test
    fun backupFile_preservesFields() {
        val file = BackupFile("id1", "backup.json", 100L, 10L)
        assertEquals("id1", file.id)
        assertEquals("backup.json", file.name)
        assertEquals(100L, file.size)
        assertEquals(10L, file.createdTime)
    }

    @Test
    fun backupFile_copyUpdatesName() {
        val file = BackupFile("id1", "old.json", 100L, 10L)
        val updated = file.copy(name = "new.json")
        assertEquals("new.json", updated.name)
        assertEquals("id1", updated.id)
    }

    @Test
    fun backupFile_equality() {
        val first = BackupFile("id1", "backup.json", 100L, 10L)
        val second = BackupFile("id1", "backup.json", 100L, 10L)
        val third = BackupFile("id2", "backup.json", 100L, 10L)
        assertEquals(first, second)
        assertNotEquals(first, third)
    }
}
