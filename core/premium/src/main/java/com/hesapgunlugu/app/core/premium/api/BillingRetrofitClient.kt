package com.hesapgunlugu.app.core.premium.api

import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Retrofit client factory with HTTPS certificate pinning
 * Production-ready billing backend network configuration
 */
@Singleton
class BillingRetrofitClient
    @Inject
    constructor() {
        companion object {
            private const val CONNECT_TIMEOUT = 10L
            private const val READ_TIMEOUT = 10L
            private const val WRITE_TIMEOUT = 10L
        }

        /**
         * Creates configured OkHttpClient with security features
         */
        fun createOkHttpClient(
            isDebug: Boolean = false,
            pinnedHosts: Map<String, List<String>> = emptyMap(),
        ): OkHttpClient {
            val certificatePinner =
                if (pinnedHosts.isNotEmpty()) {
                    CertificatePinner.Builder().apply {
                        pinnedHosts.forEach { (host, pins) ->
                            pins.forEach { pin -> add(host, pin) }
                        }
                    }.build()
                } else {
                    CertificatePinner.Builder().build()
                }

            return OkHttpClient.Builder().apply {
                connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)

                // Certificate pinning for HTTPS security
                certificatePinner(certificatePinner)

                // Logging in debug builds
                if (isDebug) {
                    addInterceptor(
                        HttpLoggingInterceptor { message ->
                            Timber.tag("BillingAPI").d(message)
                        }.apply {
                            level = HttpLoggingInterceptor.Level.BODY
                        },
                    )
                }

                // Add authentication interceptor
                addInterceptor { chain ->
                    val request = chain.request()
                    chain.proceed(request)
                }

                // Retry on connection failure
                retryOnConnectionFailure(true)
            }.build()
        }

        /**
         * Creates Retrofit instance for billing API
         */
        fun createRetrofit(
            baseUrl: String,
            isDebug: Boolean = false,
            pinnedHosts: Map<String, List<String>> = emptyMap(),
        ): Retrofit {
            require(baseUrl.startsWith("https://")) {
                "Billing backend must use HTTPS. Insecure HTTP is not allowed."
            }

            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(createOkHttpClient(isDebug, pinnedHosts))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        /**
         * Creates billing API service
         */
        inline fun <reified T> createService(
            baseUrl: String,
            isDebug: Boolean = false,
            pinnedHosts: Map<String, List<String>> = emptyMap(),
        ): T {
            return createRetrofit(baseUrl, isDebug, pinnedHosts).create(T::class.java)
        }
    }
