package com.hesapgunlugu.app.core.premium

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface BillingModule {
    @Binds
    @Singleton
    fun bindBillingBackendVerifier(impl: BillingBackendVerifierImpl): BillingBackendVerifier
}
