package com.hesapgunlugu.app.di

import com.hesapgunlugu.app.core.common.NotificationHelper
import com.hesapgunlugu.app.core.common.NotificationHelperImpl
import com.hesapgunlugu.app.core.common.StringProvider
import com.hesapgunlugu.app.core.common.StringProviderImpl
import com.hesapgunlugu.app.core.data.repository.RecurringRuleRepositoryImpl
import com.hesapgunlugu.app.core.data.repository.SettingsRepositoryImpl
import com.hesapgunlugu.app.core.domain.repository.RecurringRuleRepository
import com.hesapgunlugu.app.core.domain.repository.SettingsRepository
import com.hesapgunlugu.app.core.feedback.AppInfoProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CommonModule {
    @Binds
    @Singleton
    abstract fun bindNotificationHelper(impl: NotificationHelperImpl): NotificationHelper

    @Binds
    @Singleton
    abstract fun bindStringProvider(impl: StringProviderImpl): StringProvider

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindAppInfoProvider(impl: AppInfoProviderImpl): AppInfoProvider

    @Binds
    @Singleton
    abstract fun bindRecurringRuleRepository(impl: RecurringRuleRepositoryImpl): RecurringRuleRepository
}
