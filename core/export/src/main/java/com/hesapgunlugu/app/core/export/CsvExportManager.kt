package com.hesapgunlugu.app.core.export

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.hesapgunlugu.app.core.domain.model.Transaction
import com.hesapgunlugu.app.core.util.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * CSV Export Manager
 * Tüm transaction verilerini CSV dosyasına export eder
 */
@Singleton
class CsvExportManager
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        /**
         * Transactions listesini CSV dosyasına export eder
         * @return Export edilen dosya yolu veya hata durumunda null
         */
        suspend fun exportTransactionsToCsv(transactions: List<Transaction>): Result<File> =
            withContext(Dispatchers.IO) {
                try {
                    Timber.d("CSV export başlatılıyor... ${transactions.size} işlem")

                    if (transactions.isEmpty()) {
                        return@withContext Result.failure(IllegalArgumentException("Export edilecek işlem bulunamadı"))
                    }

                    // Dosya adı oluştur
                    val timestamp =
                        SimpleDateFormat(
                            Constants.EXPORT_DATE_FORMAT,
                            Locale.getDefault(),
                        ).format(Date())
                    val fileName = "${Constants.EXPORT_FILENAME_PREFIX}_$timestamp.csv"

                    // CRITICAL: Android 11+ (API 30) requires MediaStore API (Scoped Storage)
                    val outputStream: OutputStream
                    val exportedFile: File?

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        // Android 10+ (API 29+): Use MediaStore API
                        val contentValues =
                            ContentValues().apply {
                                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                                put(MediaStore.MediaColumns.MIME_TYPE, "text/csv")
                                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                            }

                        val uri: Uri =
                            context.contentResolver.insert(
                                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                                contentValues,
                            ) ?: throw Exception("MediaStore URI oluşturulamadı")

                        outputStream = context.contentResolver.openOutputStream(uri)
                            ?: throw Exception("OutputStream açılamadı")

                        exportedFile = null // MediaStore doesn't provide File object
                        Timber.d("CSV export MediaStore URI: $uri")
                    } else {
                        // Android 9 and below: Legacy File API
                        @Suppress("DEPRECATION")
                        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        if (!downloadsDir.exists()) {
                            downloadsDir.mkdirs()
                        }
                        exportedFile = File(downloadsDir, fileName)
                        outputStream = exportedFile.outputStream()
                        Timber.d("CSV export legacy path: ${exportedFile.absolutePath}")
                    }

                    outputStream.bufferedWriter(Charsets.UTF_8).use { writer ->
                        // CSV Header
                        writer.append("ID,Başlık,Tutar,Tarih,Tür,Kategori,Emoji\n")

                        // CSV Data
                        val dateFormat = SimpleDateFormat(Constants.DATE_FORMAT_FULL, Locale("tr", "TR"))
                        transactions.forEach { transaction ->
                            writer.append("${transaction.id},")
                            writer.append("\"${escapeCsv(transaction.title)}\",")
                            writer.append("${transaction.amount},")
                            writer.append("\"${dateFormat.format(transaction.date)}\",")
                            writer.append("\"${transaction.type.name}\",")
                            writer.append("\"${escapeCsv(transaction.category)}\",")
                            writer.append("\"${transaction.emoji}\"\n")
                        }
                    }

                    Timber.d("CSV export başarılı: $fileName (MediaStore uyumlu)")
                    // Return dummy File for backward compatibility (actual file in MediaStore)
                    Result.success(exportedFile ?: File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName))
                } catch (e: Exception) {
                    Timber.e(e, "CSV export hatası")
                    Result.failure(e)
                }
            }

        /**
         * CSV string escape (virgül, tırnak işaretleri için)
         */
        private fun escapeCsv(text: String): String {
            return text.replace("\"", "\"\"")
        }

        /**
         * Export dosyasını siler
         */
        suspend fun deleteExportFile(file: File): Result<Unit> =
            withContext(Dispatchers.IO) {
                try {
                    if (file.exists()) {
                        file.delete()
                        Timber.d("Export dosyası silindi: ${file.name}")
                    }
                    Result.success(Unit)
                } catch (e: Exception) {
                    Timber.e(e, "Export dosyası silinemedi")
                    Result.failure(e)
                }
            }

        /**
         * Tüm export dosyalarını listeler
         * CRITICAL: Android 11+ (API 30) requires MediaStore queries
         */
        suspend fun listExportFiles(): Result<List<File>> =
            withContext(Dispatchers.IO) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        // Android 10+: Query MediaStore (cannot return File objects)
                        Timber.d("MediaStore kullanımında listExportFiles() desteklenmiyor (Scoped Storage)")
                        // Return empty list - UI should handle MediaStore URIs separately
                        Result.success(emptyList())
                    } else {
                        // Android 9 and below: Legacy File API
                        @Suppress("DEPRECATION")
                        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        val files =
                            downloadsDir.listFiles { file ->
                                file.name.startsWith(Constants.EXPORT_FILENAME_PREFIX) && file.name.endsWith(".csv")
                            }?.toList() ?: emptyList()

                        Result.success(files.sortedByDescending { it.lastModified() })
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Export dosyaları listelenemedi")
                    Result.failure(e)
                }
            }
    }
