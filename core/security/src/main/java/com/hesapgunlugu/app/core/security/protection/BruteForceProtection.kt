package com.hesapgunlugu.app.core.security.protection

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

private val Context.bruteForceDataStore by preferencesDataStore(name = "brute_force_prefs")

/**
 * Brute-force saldırı koruması.
 * Single Responsibility Principle uygulanarak SecurityManager'dan ayrıldı.
 *
 * ## Koruma Mekanizması
 * - Max 3 başarısız deneme → 30 saniye kilit
 * - 5+ başarısız deneme → 5 dakika kilit
 * - Kilit süresi yeniden başlatmalara dayanıklı
 */
@Singleton
class BruteForceProtection
    @Inject
    constructor(
        private val context: Context,
    ) {
        companion object {
            private val KEY_FAILED_ATTEMPTS = intPreferencesKey("failed_attempts")
            private val KEY_LOCKOUT_END_TIME = longPreferencesKey("lockout_end_time")

            const val MAX_FAILED_ATTEMPTS = 3
            const val LOCKOUT_DURATION_MS = 30_000L // 30 saniye
            const val EXTENDED_LOCKOUT_DURATION_MS = 300_000L // 5 dakika
        }

        /**
         * Kilitli mi kontrol et
         * @return Kalan kilit süresi (saniye), 0 ise kilitli değil
         */
        suspend fun getLockoutRemainingSeconds(): Int {
            val lockoutEndTime =
                context.bruteForceDataStore.data
                    .map { it[KEY_LOCKOUT_END_TIME] ?: 0L }
                    .first()

            val currentTime = System.currentTimeMillis()
            return if (lockoutEndTime > currentTime) {
                ((lockoutEndTime - currentTime) / 1000).toInt()
            } else {
                0
            }
        }

        /**
         * Kilitli mi kontrol et
         */
        suspend fun isLockedOut(): Boolean {
            return getLockoutRemainingSeconds() > 0
        }

        /**
         * Kalan deneme hakkı
         */
        suspend fun getRemainingAttempts(): Int {
            val failedAttempts =
                context.bruteForceDataStore.data
                    .map { it[KEY_FAILED_ATTEMPTS] ?: 0 }
                    .first()
            return (MAX_FAILED_ATTEMPTS - failedAttempts).coerceAtLeast(0)
        }

        /**
         * Başarısız deneme kaydet
         * @return LockoutResult - kilit durumu veya kalan deneme hakkı
         */
        suspend fun recordFailedAttempt(): LockoutResult {
            val currentTime = System.currentTimeMillis()

            val failedAttempts =
                context.bruteForceDataStore.data
                    .map { (it[KEY_FAILED_ATTEMPTS] ?: 0) + 1 }
                    .first()

            context.bruteForceDataStore.edit { preferences ->
                preferences[KEY_FAILED_ATTEMPTS] = failedAttempts

                // Kilit uygula
                if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
                    val lockoutDuration =
                        if (failedAttempts >= 5) {
                            EXTENDED_LOCKOUT_DURATION_MS
                        } else {
                            LOCKOUT_DURATION_MS
                        }
                    preferences[KEY_LOCKOUT_END_TIME] = currentTime + lockoutDuration
                    Timber.w("Çok fazla yanlış deneme: $failedAttempts, kilit süresi: ${lockoutDuration / 1000}s")
                }
            }

            val remainingAttempts = MAX_FAILED_ATTEMPTS - failedAttempts

            return if (remainingAttempts <= 0) {
                val lockoutSeconds = if (failedAttempts >= 5) 300 else 30
                LockoutResult.LockedOut(lockoutSeconds)
            } else {
                LockoutResult.AttemptsRemaining(remainingAttempts.coerceAtLeast(0))
            }
        }

        /**
         * Başarılı giriş - sayaçları sıfırla
         */
        suspend fun resetOnSuccess() {
            context.bruteForceDataStore.edit { preferences ->
                preferences[KEY_FAILED_ATTEMPTS] = 0
                preferences[KEY_LOCKOUT_END_TIME] = 0L
            }
            Timber.d("Brute-force sayaçları sıfırlandı")
        }
    }

/**
 * Kilit durumu sonucu
 */
sealed class LockoutResult {
    data class LockedOut(val remainingSeconds: Int) : LockoutResult()

    data class AttemptsRemaining(val attempts: Int) : LockoutResult()
}
