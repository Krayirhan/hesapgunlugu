package com.hesapgunlugu.app.core.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GetTokenResult
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class BackendTokenProvider
    @Inject
    constructor(
        private val tokenStore: BackendTokenStore,
        private val config: BackendConfig,
        private val firebaseAuth: FirebaseAuth,
    ) {
        companion object {
            private const val API_KEY_TTL_MS = 24 * 60 * 60 * 1000L
        }

        suspend fun getToken(): Result<String> {
            val cached = tokenStore.getValidToken()
            if (!cached.isNullOrBlank()) {
                return Result.success(cached)
            }

            val firebaseToken = fetchFirebaseToken()
            if (!firebaseToken?.token.isNullOrBlank()) {
                val token = firebaseToken?.token.orEmpty()
                tokenStore.saveToken(token, firebaseToken?.expirationTimestamp ?: System.currentTimeMillis())
                return Result.success(token)
            }

            if (config.apiKey.isBlank()) {
                return Result.failure(IllegalStateException("Backend API key not configured"))
            }

            val now = System.currentTimeMillis()
            tokenStore.saveToken(config.apiKey, now + API_KEY_TTL_MS)
            return Result.success(config.apiKey)
        }

        fun updateToken(
            token: String,
            expiresInSeconds: Long,
        ) {
            val expiresAt = System.currentTimeMillis() + (expiresInSeconds * 1000L)
            tokenStore.saveToken(token, expiresAt)
        }

        fun clear() {
            tokenStore.clear()
        }

        private suspend fun fetchFirebaseToken(): GetTokenResult? =
            suspendCancellableCoroutine { cont ->
                val user = firebaseAuth.currentUser
                if (user == null) {
                    cont.resume(null)
                    return@suspendCancellableCoroutine
                }

                user.getIdToken(false)
                    .addOnSuccessListener { result -> cont.resume(result) }
                    .addOnFailureListener { cont.resume(null) }
            }
    }
