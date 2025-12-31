package com.hesapgunlugu.app.core.data.remote

import com.hesapgunlugu.app.core.data.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BackendNetworkModule {
    @Provides
    @Singleton
    fun provideBackendConfig(): BackendConfig {
        return BackendConfig(
            baseUrl = BuildConfig.BACKEND_BASE_URL.trim(),
            apiKey = BuildConfig.BACKEND_API_KEY.trim(),
        )
    }

    @Provides
    @Singleton
    fun provideBackendRetrofit(config: BackendConfig): Retrofit {
        val baseUrl = normalizeBaseUrl(config.baseUrl)
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideBillingBackendApi(retrofit: Retrofit): BillingBackendApi {
        return retrofit.create(BillingBackendApi::class.java)
    }

    private fun normalizeBaseUrl(url: String): String {
        if (url.isBlank()) {
            return "https://example.invalid/"
        }
        return if (url.endsWith("/")) url else "$url/"
    }
}
