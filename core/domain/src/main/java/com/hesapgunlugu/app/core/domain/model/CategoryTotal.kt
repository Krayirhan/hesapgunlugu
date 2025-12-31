package com.hesapgunlugu.app.core.domain.model

/**
 * Kategori bazlı toplam harcama modeli
 * Domain layer'da kullanılır, data layer'dan bağımsız
 * Immutable data class - thread-safe and predictable
 */
data class CategoryTotal(
    val category: String,
    val total: Double,
)
