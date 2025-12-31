package com.hesapgunlugu.app.core.security

import android.content.Context
import com.google.android.play.core.integrity.IntegrityManagerFactory
import com.google.android.play.core.integrity.IntegrityTokenRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import java.security.SecureRandom
import java.util.Base64
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

sealed class IntegrityCheckResult {
    data class Success(val token: String) : IntegrityCheckResult()

    data class Failed(val reason: String) : IntegrityCheckResult()
}

@Singleton
class PlayIntegrityManager
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        private val integrityManager = IntegrityManagerFactory.create(context)

        suspend fun checkIntegrity(): IntegrityCheckResult {
            val nonce = createNonce()
            return requestIntegrityToken(nonce)
        }

        private suspend fun requestIntegrityToken(nonce: String): IntegrityCheckResult {
            return suspendCancellableCoroutine { cont ->
                val request =
                    IntegrityTokenRequest.builder()
                        .setNonce(nonce)
                        .build()

                integrityManager.requestIntegrityToken(request)
                    .addOnSuccessListener { response ->
                        if (cont.isActive) {
                            cont.resume(IntegrityCheckResult.Success(response.token()))
                        }
                    }
                    .addOnFailureListener { error ->
                        Timber.e(error, "Play Integrity token request failed")
                        if (cont.isActive) {
                            cont.resume(IntegrityCheckResult.Failed("Play Integrity verification failed."))
                        }
                    }
            }
        }

        private fun createNonce(): String {
            val bytes = ByteArray(NONCE_SIZE_BYTES)
            SecureRandom().nextBytes(bytes)
            return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
        }

        companion object {
            private const val NONCE_SIZE_BYTES = 32
        }
    }
