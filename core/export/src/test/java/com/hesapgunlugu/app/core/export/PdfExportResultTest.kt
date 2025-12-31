package com.hesapgunlugu.app.core.export

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PdfExportResultTest {
    @Test
    fun success_holdsFileInfo() {
        val result =
            PdfExportManager.PdfExportResult.Success(
                filePath = "/tmp/report.pdf",
                fileName = "report.pdf",
            )
        assertEquals("/tmp/report.pdf", result.filePath)
        assertEquals("report.pdf", result.fileName)
    }

    @Test
    fun error_holdsMessage() {
        val result = PdfExportManager.PdfExportResult.Error("failed")
        assertEquals("failed", result.message)
    }

    @Test
    fun success_isPdfExportResult() {
        val result: PdfExportManager.PdfExportResult =
            PdfExportManager.PdfExportResult.Success("/tmp/a.pdf", "a.pdf")
        assertTrue(result is PdfExportManager.PdfExportResult.Success)
    }
}
