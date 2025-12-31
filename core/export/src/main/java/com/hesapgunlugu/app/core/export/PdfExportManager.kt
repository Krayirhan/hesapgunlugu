package com.hesapgunlugu.app.core.export

import android.content.ContentValues
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.StringRes
import com.hesapgunlugu.app.core.domain.model.Transaction
import com.hesapgunlugu.app.core.domain.model.TransactionType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * PDF Export Manager
 * Exports transactions as PDF format.
 */
@Singleton
class PdfExportManager
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale("tr", "TR"))
        private val filenameDateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale("tr", "TR"))
        private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("tr", "TR"))

        sealed class PdfExportResult {
            data class Success(val filePath: String, val fileName: String) : PdfExportResult()

            data class Error(val message: String) : PdfExportResult()
        }

        /**
         * Export transactions as PDF.
         */
        suspend fun exportToPdf(
            transactions: List<Transaction>,
            title: String = "",
        ): PdfExportResult =
            withContext(Dispatchers.IO) {
                try {
                    val fileName = getString(R.string.pdf_filename, filenameDateFormat.format(Date()))
                    val reportTitle = title.ifBlank { getString(R.string.pdf_report_title_default) }

                    val document = PdfDocument()
                    var pageNumber = 1
                    var yPosition = 80f
                    val pageWidth = 595 // A4 width in points
                    val pageHeight = 842 // A4 height in points
                    val margin = 40f
                    val lineHeight = 25f

                    // Paints
                    val titlePaint =
                        Paint().apply {
                            color = Color.parseColor("#1E3A5F")
                            textSize = 24f
                            isFakeBoldText = true
                        }

                    val headerPaint =
                        Paint().apply {
                            color = Color.parseColor("#3B82F6")
                            textSize = 14f
                            isFakeBoldText = true
                        }

                    val textPaint =
                        Paint().apply {
                            color = Color.BLACK
                            textSize = 11f
                        }

                    val incomePaint =
                        Paint().apply {
                            color = Color.parseColor("#10B981")
                            textSize = 11f
                        }

                    val expensePaint =
                        Paint().apply {
                            color = Color.parseColor("#EF4444")
                            textSize = 11f
                        }

                    val linePaint =
                        Paint().apply {
                            color = Color.LTGRAY
                            strokeWidth = 1f
                        }

                    // First page
                    var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                    var page = document.startPage(pageInfo)
                    var canvas: Canvas = page.canvas

                    // Title
                    canvas.drawText(reportTitle, margin, yPosition, titlePaint)
                    yPosition += 30f

                    // Date
                    canvas.drawText(
                        getString(R.string.pdf_generated_on, dateFormat.format(Date())),
                        margin,
                        yPosition,
                        textPaint,
                    )
                    yPosition += 20f

                    // Summary
                    val totalIncome = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
                    val totalExpense = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
                    val balance = totalIncome - totalExpense

                    yPosition += 10f
                    canvas.drawLine(margin, yPosition, pageWidth - margin, yPosition, linePaint)
                    yPosition += 25f

                    canvas.drawText(getString(R.string.pdf_summary_title), margin, yPosition, headerPaint)
                    yPosition += lineHeight
                    canvas.drawText(
                        getString(R.string.pdf_total_income, currencyFormat.format(totalIncome)),
                        margin,
                        yPosition,
                        incomePaint,
                    )
                    yPosition += lineHeight
                    canvas.drawText(
                        getString(R.string.pdf_total_expense, currencyFormat.format(totalExpense)),
                        margin,
                        yPosition,
                        expensePaint,
                    )
                    yPosition += lineHeight
                    canvas.drawText(
                        getString(R.string.pdf_net_balance, currencyFormat.format(balance)),
                        margin,
                        yPosition,
                        if (balance >= 0) incomePaint else expensePaint,
                    )
                    yPosition += 30f

                    canvas.drawLine(margin, yPosition, pageWidth - margin, yPosition, linePaint)
                    yPosition += 25f

                    // Table headers
                    canvas.drawText(getString(R.string.pdf_transactions_title), margin, yPosition, headerPaint)
                    yPosition += lineHeight

                    val col1 = margin
                    val col2 = margin + 100
                    val col3 = margin + 250
                    val col4 = margin + 380

                    canvas.drawText(getString(R.string.pdf_column_date), col1, yPosition, headerPaint)
                    canvas.drawText(getString(R.string.pdf_column_title), col2, yPosition, headerPaint)
                    canvas.drawText(getString(R.string.pdf_column_category), col3, yPosition, headerPaint)
                    canvas.drawText(getString(R.string.pdf_column_amount), col4, yPosition, headerPaint)
                    yPosition += lineHeight

                    canvas.drawLine(margin, yPosition - 5, pageWidth - margin, yPosition - 5, linePaint)
                    yPosition += 5f

                    // Transactions
                    for (transaction in transactions.sortedByDescending { it.date }) {
                        if (yPosition > pageHeight - 60) {
                            document.finishPage(page)
                            pageNumber++
                            pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                            page = document.startPage(pageInfo)
                            canvas = page.canvas
                            yPosition = 60f

                            canvas.drawText(
                                getString(R.string.pdf_page_label, pageNumber),
                                pageWidth - 80f,
                                30f,
                                textPaint,
                            )
                        }

                        val amountPaint = if (transaction.type == TransactionType.INCOME) incomePaint else expensePaint
                        val amountPrefix = if (transaction.type == TransactionType.INCOME) "+" else "-"

                        canvas.drawText(dateFormat.format(transaction.date), col1, yPosition, textPaint)
                        canvas.drawText(transaction.title.take(25), col2, yPosition, textPaint)
                        canvas.drawText(transaction.category, col3, yPosition, textPaint)
                        canvas.drawText(
                            "$amountPrefix${currencyFormat.format(transaction.amount)}",
                            col4,
                            yPosition,
                            amountPaint,
                        )

                        yPosition += lineHeight
                    }

                    // Finish last page
                    document.finishPage(page)

                    // Save file
                    val outputStream: OutputStream
                    val filePath: String

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        val contentValues =
                            ContentValues().apply {
                                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                            }
                        val uri =
                            context.contentResolver.insert(
                                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                                contentValues,
                            ) ?: throw Exception(getString(R.string.pdf_error_uri_create))

                        outputStream = context.contentResolver.openOutputStream(uri)
                            ?: throw Exception(getString(R.string.pdf_error_output_stream))
                        filePath = "${Environment.DIRECTORY_DOWNLOADS}/$fileName"
                    } else {
                        @Suppress("DEPRECATION")
                        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        val file = File(downloadsDir, fileName)
                        outputStream = FileOutputStream(file)
                        filePath = file.absolutePath
                    }

                    document.writeTo(outputStream)
                    outputStream.close()
                    document.close()

                    Timber.d(getString(R.string.log_pdf_export_success, filePath))
                    PdfExportResult.Success(filePath, fileName)
                } catch (e: Exception) {
                    Timber.e(e, getString(R.string.log_pdf_export_failed))
                    PdfExportResult.Error(e.message ?: getString(R.string.pdf_error_create))
                }
            }

        private fun getString(
            @StringRes resId: Int,
            vararg args: Any,
        ): String {
            return if (args.isEmpty()) context.getString(resId) else context.getString(resId, *args)
        }
    }
