package com.hesapgunlugu.app.core.export

import android.content.Context
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class CsvExportManagerTest {
    @Test
    fun deleteExportFile_removesExistingFile() =
        runBlocking {
            val tempFile = File.createTempFile("export", ".csv")
            assertTrue(tempFile.exists())

            val manager = CsvExportManager(mockk<Context>(relaxed = true))
            val result = manager.deleteExportFile(tempFile)

            assertTrue(result.isSuccess)
            assertFalse(tempFile.exists())
        }
}
