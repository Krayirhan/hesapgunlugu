package com.hesapgunlugu.app.core.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe Navigation Routes using Kotlin Serialization
 */
sealed interface Route {
    @Serializable
    data object Home : Route

    @Serializable
    data object History : Route

    @Serializable
    data object Statistics : Route

    @Serializable
    data object Settings : Route

    @Serializable
    data object Notifications : Route

    @Serializable
    data object Onboarding : Route

    @Serializable
    data object Privacy : Route

    @Serializable
    data object Scheduled : Route

    @Serializable
    data object Analysis : Route

    @Serializable
    data object CategoryManagement : Route

    @Serializable
    data object DataDeletion : Route

    @Serializable
    data class TransactionDetail(val transactionId: Long) : Route

    @Serializable
    data class CategoryDetail(val categoryName: String) : Route

    @Serializable
    data class EditTransaction(
        val transactionId: Long,
        val isEditing: Boolean = true,
    ) : Route
}
