package com.hesapgunlugu.app.core.export

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Email sharing manager
 * Shares exported files via email
 */
@Singleton
class EmailShareManager
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        /**
         * Share file via email
         */
        fun shareViaEmail(
            file: File,
            subject: String = "İşlem Raporu",
            body: String = "İşlem raporunuz ekte bulunmaktadır.",
        ): Intent {
            val uri =
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file,
                )

            return Intent(Intent.ACTION_SEND).apply {
                type =
                    when {
                        file.extension == "pdf" -> "application/pdf"
                        file.extension == "csv" -> "text/csv"
                        else -> "application/octet-stream"
                    }
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        }

        /**
         * Create email chooser intent
         */
        fun createEmailChooser(file: File): Intent {
            val shareIntent = shareViaEmail(file)
            return Intent.createChooser(shareIntent, "Raporu Paylaş")
        }
    }
