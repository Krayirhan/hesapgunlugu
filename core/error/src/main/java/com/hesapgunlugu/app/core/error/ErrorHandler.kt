package com.hesapgunlugu.app.core.error

import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object ErrorHandler {
    fun getErrorMessage(throwable: Throwable): String {
        return when (throwable) {
            is UnknownHostException -> "İnternet bağlantınızı kontrol edin"
            is SocketTimeoutException -> "Bağlantı zaman aşımı. Lütfen tekrar deneyin"
            is IOException -> "Bağlantı hatası oluştu. Lütfen tekrar deneyin"
            is IllegalArgumentException -> throwable.message ?: "Geçersiz işlem"
            is NullPointerException -> "Beklenmeyen bir hata oluştu"
            else -> throwable.message?.takeIf { it.isNotBlank() } ?: "Beklenmeyen bir hata oluştu"
        }
    }

    fun handleDatabaseError(throwable: Throwable): String {
        val msg = throwable.message.orEmpty()
        return when {
            msg.contains("UNIQUE constraint", ignoreCase = true) -> "Bu kayıt zaten mevcut"
            msg.contains("NOT NULL constraint", ignoreCase = true) -> "Zorunlu alan boş bırakılamaz"
            else -> "Veritabanı hatası oluştu"
        }
    }
}
