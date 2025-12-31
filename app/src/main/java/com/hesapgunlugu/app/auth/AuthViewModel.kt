package com.hesapgunlugu.app.auth

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.hesapgunlugu.app.core.data.repository.SyncRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

/** UI state for authentication banner/button. */
data class AuthUiState(
    val isSignedIn: Boolean = false,
    val displayName: String? = null,
    val email: String? = null,
    val photoUrl: String? = null,
    val isLoading: Boolean = false,
    val isSyncing: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class AuthViewModel
    @Inject
    constructor(
        private val firebaseAuth: FirebaseAuth,
        private val googleSignInClient: GoogleSignInClient,
        private val syncRepository: SyncRepository,
    ) : ViewModel() {
        private val _state = MutableStateFlow(AuthUiState())
        val state: StateFlow<AuthUiState> = _state.asStateFlow()

        private val authStateListener =
            FirebaseAuth.AuthStateListener { auth ->
                val user = auth.currentUser
                if (user != null) {
                    // User is signed in, update state
                    _state.value =
                        _state.value.copy(
                            isSignedIn = true,
                            displayName = user.displayName,
                            email = user.email,
                            photoUrl = user.photoUrl?.toString(),
                        )
                } else {
                    // User is signed out (could be from another device or session)
                    if (_state.value.isSignedIn) {
                        _state.value = AuthUiState()
                        Timber.i("User signed out externally (token expired or revoked)")
                    }
                }
            }

        init {
            // Add auth state listener for real-time updates
            firebaseAuth.addAuthStateListener(authStateListener)

            // Initialize state from current user
            firebaseAuth.currentUser?.let { user ->
                _state.value =
                    _state.value.copy(
                        isSignedIn = true,
                        displayName = user.displayName,
                        email = user.email,
                        photoUrl = user.photoUrl?.toString(),
                    )
            }
        }

        override fun onCleared() {
            super.onCleared()
            // Remove listener to prevent memory leak
            firebaseAuth.removeAuthStateListener(authStateListener)
        }

        fun getSignInIntent(): Intent = googleSignInClient.signInIntent

        fun onGoogleSignInResult(data: Intent?) {
            viewModelScope.launch {
                try {
                    // Early check: user cancelled sign-in
                    if (data == null) {
                        Timber.d("User cancelled Google sign-in")
                        _state.value =
                            _state.value.copy(
                                isLoading = false,
                                error = "Sign-in cancelled.",
                            )
                        return@launch
                    }

                    _state.value = _state.value.copy(isLoading = true, error = null)
                    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                    val account = task.getResult(ApiException::class.java)

                    // Security: Validate account and token
                    if (account.idToken.isNullOrBlank()) {
                        Timber.e("Invalid sign-in: missing ID token")
                        _state.value =
                            _state.value.copy(
                                isLoading = false,
                                error = "Sign-in failed. Please try again.",
                            )
                        return@launch
                    }

                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    // Use coroutine-friendly await() instead of callback
                    val result = firebaseAuth.signInWithCredential(credential).await()

                    val user = result.user
                    if (user != null && user.email != null) {
                        _state.value =
                            _state.value.copy(
                                isSignedIn = true,
                                displayName = user.displayName,
                                email = user.email,
                                photoUrl = user.photoUrl?.toString(),
                                isLoading = false,
                                isSyncing = true,
                                error = null,
                            )
                        Timber.i("User signed in: ${user.uid}")

                        // Firestore'dan kullanıcı verilerini senkronize et
                        syncFromCloud()
                    } else {
                        Timber.e("Sign-in succeeded but user is null or email missing")
                        _state.value =
                            _state.value.copy(
                                isLoading = false,
                                error = "Account could not be verified. Please try again.",
                            )
                    }
                } catch (e: ApiException) {
                    // Security: Don't expose internal error codes to UI
                    Timber.e(e, "Google sign-in API error: ${e.statusCode}")
                    _state.value =
                        _state.value.copy(
                            isLoading = false,
                            error =
                                when (e.statusCode) {
                                    12501 -> "Sign-in cancelled."
                                    12502 -> "A temporary error occurred. Please try again."
                                    else -> "Sign-in failed. Please try again."
                                },
                        )
                } catch (e: Exception) {
                    Timber.e(e, "Unexpected sign-in error")
                    _state.value =
                        _state.value.copy(
                            isLoading = false,
                            error = "An unexpected error occurred. Please try again.",
                        )
                }
            }
        }

        fun signOut() {
            viewModelScope.launch {
                try {
                    _state.value = _state.value.copy(isLoading = true)
                    val userId = firebaseAuth.currentUser?.uid

                    // Yerel verileri temizle (kullanıcı çıkışı)
                    syncRepository.clearLocalData()

                    firebaseAuth.signOut()
                    // Use coroutine-friendly await() instead of callback
                    googleSignInClient.signOut().await()
                    _state.value = AuthUiState()
                    Timber.i("User signed out: $userId")
                } catch (e: Exception) {
                    Timber.e(e, "Sign-out failed")
                    _state.value =
                        _state.value.copy(
                            isLoading = false,
                            error = "Sign-out failed. Please try again.",
                        )
                }
            }
        }

        /**
         * Firestore'dan verileri çeker ve yerel veritabanına yazar
         */
        private fun syncFromCloud() {
            viewModelScope.launch {
                try {
                    val result = syncRepository.syncFromFirestore(clearLocalFirst = true)
                    if (result.isSuccess) {
                        Timber.i("Firestore senkronizasyonu başarılı: ${result.getOrNull()} transaction")
                    } else {
                        Timber.e(result.exceptionOrNull(), "Firestore senkronizasyonu başarısız")
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Senkronizasyon hatası")
                } finally {
                    _state.value = _state.value.copy(isSyncing = false)
                }
            }
        }

        /**
         * Yerel verileri Firestore'a yükler (manuel senkronizasyon)
         */
        fun syncToCloud() {
            viewModelScope.launch {
                try {
                    _state.value = _state.value.copy(isSyncing = true)
                    val result = syncRepository.syncToFirestore()
                    if (result.isSuccess) {
                        Timber.i("Firestore'a yükleme başarılı: ${result.getOrNull()} transaction")
                    } else {
                        Timber.e(result.exceptionOrNull(), "Firestore'a yükleme başarısız")
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Yükleme hatası")
                } finally {
                    _state.value = _state.value.copy(isSyncing = false)
                }
            }
        }

        fun clearError() {
            _state.value = _state.value.copy(error = null)
        }
    }
