package com.hesapgunlugu.app.core.common

/**
 * UI State wrapper - Loading, Success, Error durumlarını yönetir
 */
sealed class UiState<out T> {
    /**
     * Veri yükleniyor
     */
    object Loading : UiState<Nothing>()

    /**
     * Veri başarıyla yüklendi
     */
    data class Success<T>(val data: T) : UiState<T>()

    /**
     * Hata oluştu
     */
    data class Error(
        val message: String,
        val throwable: Throwable? = null,
    ) : UiState<Nothing>()

    /**
     * Boş veri durumu
     */
    object Empty : UiState<Nothing>()

    // Yardımcı fonksiyonlar
    val isLoading: Boolean get() = this is Loading
    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
    val isEmpty: Boolean get() = this is Empty

    fun getOrNull(): T? = (this as? Success)?.data

    fun errorMessage(): String? = (this as? Error)?.message
}

/**
 * Result extension - Result'ı UiState'e çevirir
 */
fun <T> Result<T>.toUiState(): UiState<T> {
    return fold(
        onSuccess = { UiState.Success(it) },
        onFailure = { UiState.Error(it.message ?: "Bilinmeyen hata", it) },
    )
}

/**
 * Standart hata mesajları
 */
object ErrorMessages {
    const val NETWORK_ERROR = "İnternet bağlantınızı kontrol edin"
    const val DATABASE_ERROR = "Veri kaydedilirken hata oluştu"
    const val UNKNOWN_ERROR = "Beklenmeyen bir hata oluştu"
    const val LOAD_ERROR = "Veriler yüklenirken hata oluştu"
    const val SAVE_ERROR = "Kaydetme işlemi başarısız"
    const val DELETE_ERROR = "Silme işlemi başarısız"
}
