package com.hesapgunlugu.app.di

import com.hesapgunlugu.app.core.domain.repository.RecurringRuleRepository
import com.hesapgunlugu.app.core.domain.repository.ScheduledPaymentRepository
import com.hesapgunlugu.app.core.domain.repository.SettingsRepository
import com.hesapgunlugu.app.core.domain.repository.TransactionRepository
import com.hesapgunlugu.app.core.domain.usecase.*
import com.hesapgunlugu.app.core.domain.usecase.recurring.AddRecurringRuleUseCase
import com.hesapgunlugu.app.core.domain.usecase.scheduled.*
import com.hesapgunlugu.app.core.domain.usecase.transaction.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

/**
 * Use Case Module
 * Provides all domain use cases with ViewModel scope
 * Clean Architecture: Use cases encapsulate business logic
 */
@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {
    // ========== Settings Use Cases ==========

    @Provides
    @ViewModelScoped
    fun provideGetUserSettingsUseCase(repository: SettingsRepository): GetUserSettingsUseCase = GetUserSettingsUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideUpdateCategoryBudgetUseCase(repository: SettingsRepository): UpdateCategoryBudgetUseCase = UpdateCategoryBudgetUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideUpdateThemeUseCase(repository: SettingsRepository): UpdateThemeUseCase = UpdateThemeUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideUpdateMonthlyLimitUseCase(repository: SettingsRepository): UpdateMonthlyLimitUseCase = UpdateMonthlyLimitUseCase(repository)

    // ========== Transaction Use Cases ==========

    @Provides
    @ViewModelScoped
    fun provideGetTransactionsUseCase(repository: TransactionRepository): GetTransactionsUseCase = GetTransactionsUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideGetTransactionsByDateRangeUseCase(repository: TransactionRepository): GetTransactionsByDateRangeUseCase = GetTransactionsByDateRangeUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideAddTransactionUseCase(repository: TransactionRepository): AddTransactionUseCase = AddTransactionUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideUpdateTransactionUseCase(repository: TransactionRepository): UpdateTransactionUseCase = UpdateTransactionUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideDeleteTransactionUseCase(repository: TransactionRepository): DeleteTransactionUseCase = DeleteTransactionUseCase(repository)

    // ========== Scheduled Payment Use Cases ==========

    @Provides
    @ViewModelScoped
    fun provideGetScheduledPaymentsUseCase(repository: ScheduledPaymentRepository): GetScheduledPaymentsUseCase = GetScheduledPaymentsUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideAddScheduledPaymentUseCase(repository: ScheduledPaymentRepository): AddScheduledPaymentUseCase = AddScheduledPaymentUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideDeleteScheduledPaymentUseCase(repository: ScheduledPaymentRepository): DeleteScheduledPaymentUseCase = DeleteScheduledPaymentUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideMarkPaymentAsPaidUseCase(
        scheduledPaymentRepository: ScheduledPaymentRepository,
        transactionRepository: TransactionRepository,
        recurringRuleRepository: RecurringRuleRepository,
    ): MarkPaymentAsPaidUseCase =
        MarkPaymentAsPaidUseCase(
            scheduledPaymentRepository,
            transactionRepository,
            recurringRuleRepository,
        )

    @Provides
    @ViewModelScoped
    fun provideGetPlannedOccurrencesUseCase(
        scheduledPaymentRepository: ScheduledPaymentRepository,
        recurringRuleRepository: RecurringRuleRepository,
    ): GetPlannedOccurrencesUseCase =
        GetPlannedOccurrencesUseCase(
            scheduledPaymentRepository,
            recurringRuleRepository,
        )

    // ========== Recurring Use Cases ==========

    @Provides
    @ViewModelScoped
    fun provideAddRecurringRuleUseCase(repository: RecurringRuleRepository): AddRecurringRuleUseCase = AddRecurringRuleUseCase(repository)
}
