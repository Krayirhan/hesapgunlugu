package com.hesapgunlugu.app.core.error

import org.acra.ReportField
import org.acra.data.CrashReportData
import timber.log.Timber

/**
 * ACRA Report Sanitizer - CRITICAL SECURITY FEATURE
 *
 * Removes Personally Identifiable Information (PII) and sensitive financial data
 * from crash reports before they are stored or sent.
 *
 * ## PII Categories Removed:
 * - Transaction amounts and balances
 * - User names and email addresses
 * - Credit card numbers (full or partial)
 * - Phone numbers
 * - Device IDs and IMEIs
 * - Scheduled payment details
 * - Category budget limits
 *
 * ## GDPR Compliance:
 * - Article 32: Security of processing (pseudonymization)
 * - Article 25: Data protection by design
 *
 * @see <a href="https://gdpr-info.eu/">GDPR Guidelines</a>
 */
object AcraSanitizer {
    // Regex patterns for PII detection
    private val AMOUNT_PATTERN = Regex("""amount[=:\s]+[\d.,]+""", RegexOption.IGNORE_CASE)
    private val BALANCE_PATTERN = Regex("""balance[=:\s]+[\d.,]+""", RegexOption.IGNORE_CASE)
    private val USERNAME_PATTERN = Regex("""userName[=:\s]+"[^"]+"""", RegexOption.IGNORE_CASE)
    private val EMAIL_PATTERN = Regex("""[\w._%+-]+@[\w.-]+\.[A-Za-z]{2,}""")
    private val CREDIT_CARD_PATTERN = Regex("""\b\d{4}[\s-]?\d{4}[\s-]?\d{4}[\s-]?\d{4}\b""")
    private val PHONE_PATTERN = Regex("""\b(\+\d{1,3}[-.\s]?)?\(?\d{3}\)?[-.\s]?\d{3}[-.\s]?\d{4}\b""")
    private val DEVICE_ID_PATTERN = Regex("""deviceId[=:\s]+"[^"]+"""", RegexOption.IGNORE_CASE)
    private val TITLE_PATTERN = Regex("""title[=:\s]+"[^"]+"""", RegexOption.IGNORE_CASE)
    private val CATEGORY_PATTERN = Regex("""category[=:\s]+"[^"]+"""", RegexOption.IGNORE_CASE)

    /**
     * Sanitizes a crash report by removing all PII
     * @param reportData The original crash report data
     * @return Sanitized report data safe for storage/transmission
     */
    fun sanitize(reportData: CrashReportData): CrashReportData {
        val sanitized = CrashReportData()

        // Iterate through all report fields
        for (field in ReportField.entries) {
            val value = reportData.getString(field) ?: continue

            when (field) {
                // REMOVE: Fields that may contain PII
                ReportField.USER_EMAIL,
                ReportField.USER_COMMENT,
                ReportField.DEVICE_ID,
                ReportField.LOGCAT, // May contain transaction details in logs
                ReportField.CUSTOM_DATA,
                -> {
                    // Skip these fields entirely
                    Timber.d("Removing PII field: $field")
                }

                // SANITIZE: Stack traces and thread dumps
                ReportField.STACK_TRACE,
                ReportField.THREAD_DETAILS,
                -> {
                    sanitized.put(field, sanitizeStackTrace(value))
                }

                // KEEP: Safe technical data
                else -> {
                    // Copy safe fields as-is
                    sanitized.put(field, value)
                }
            }
        }

        return sanitized
    }

    /**
     * Sanitizes stack trace by replacing sensitive values with placeholders
     */
    private fun sanitizeStackTrace(stackTrace: String): String {
        var sanitized = stackTrace

        // Replace transaction amounts
        sanitized = AMOUNT_PATTERN.replace(sanitized, "amount=***")
        sanitized = BALANCE_PATTERN.replace(sanitized, "balance=***")

        // Replace user identifiers
        sanitized = USERNAME_PATTERN.replace(sanitized, "userName=\"***\"")
        sanitized = EMAIL_PATTERN.replace(sanitized, "***@***.***")

        // Replace financial data
        sanitized = CREDIT_CARD_PATTERN.replace(sanitized, "****-****-****-****")
        sanitized = PHONE_PATTERN.replace(sanitized, "***-***-****")

        // Replace device identifiers
        sanitized = DEVICE_ID_PATTERN.replace(sanitized, "deviceId=\"***\"")

        // Replace transaction titles and categories (may be sensitive)
        sanitized = TITLE_PATTERN.replace(sanitized, "title=\"***\"")
        sanitized = CATEGORY_PATTERN.replace(sanitized, "category=\"***\"")

        return sanitized
    }

    /**
     * Validates that a sanitized report contains no PII
     * Used in testing to ensure sanitization is effective
     */
    fun validateSanitization(reportData: CrashReportData): Boolean {
        val reportJson = reportData.toJSON()

        return !EMAIL_PATTERN.containsMatchIn(reportJson) &&
            !CREDIT_CARD_PATTERN.containsMatchIn(reportJson) &&
            !reportJson.contains("userName", ignoreCase = true) &&
            !reportJson.contains("deviceId", ignoreCase = true)
    }
}
