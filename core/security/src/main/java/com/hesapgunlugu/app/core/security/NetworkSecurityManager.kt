package com.hesapgunlugu.app.core.security

import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkSecurityManager
    @Inject
    constructor() {
        fun createSecureHttpClient(
            enableLogging: Boolean = false,
            clientVersion: String = "unknown",
            pinnedHosts: Map<String, List<String>> = emptyMap(),
            requirePinsInRelease: Boolean = true,
        ): OkHttpClient {
            if (requirePinsInRelease && !BuildConfig.DEBUG && pinnedHosts.isEmpty()) {
                throw IllegalStateException("Certificate pinning is required for release builds.")
            }

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

            val builder =
                OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .certificatePinner(certificatePinner)

            if (enableLogging && BuildConfig.DEBUG) {
                val logging =
                    HttpLoggingInterceptor { message ->
                        Timber.tag("OkHttp").d(message)
                    }.apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    }
                builder.addInterceptor(logging)
            }

            builder.addInterceptor { chain ->
                val originalRequest = chain.request()
                val requestBuilder =
                    originalRequest.newBuilder()
                        .header("X-Requested-With", "XMLHttpRequest")
                        .header("X-Client-Version", clientVersion)
                        .method(originalRequest.method, originalRequest.body)

                chain.proceed(requestBuilder.build())
            }

            return builder.build()
        }
    }
