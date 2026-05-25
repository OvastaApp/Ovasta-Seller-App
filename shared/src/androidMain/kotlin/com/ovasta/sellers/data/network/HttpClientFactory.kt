package com.ovasta.sellers.data.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.headers
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import com.ovasta.sellers.data.RemoteConstants
import okhttp3.Interceptor
import okhttp3.Response

actual fun createHttpClient(tokenProvider: AuthTokenProvider?): HttpClient = HttpClient(OkHttp) {
    install(ContentNegotiation) {
        json(json = Json {
            ignoreUnknownKeys = true
            isLenient = true
        })
    }

    install(HttpTimeout) {
        connectTimeoutMillis = RemoteConstants.CONNECT_TIMEOUT * 1000
        requestTimeoutMillis = RemoteConstants.READ_TIMEOUT * 1000
        socketTimeoutMillis = RemoteConstants.WRITE_TIMEOUT * 1000
    }

    install(DefaultRequest) {
        url(RemoteConstants.BASE_URL)
        contentType(ContentType.Application.Json)
        headers {
            append(RemoteConstants.HeadersConst.ACCEPT, "application/json")
            append(RemoteConstants.HeadersConst.IDENTIFIER, RemoteConstants.IDENTIFIER_HASH)
            append(RemoteConstants.HeadersConst.LANG, "ar")
        }
    }

    install(Logging) {
        level = LogLevel.ALL
        logger = object : io.ktor.client.plugins.logging.Logger {
            override fun log(message: String) {
                android.util.Log.d("HttpLogger", message)
            }
        }
    }

    engine {
        config {
            followRedirects(true)
            addInterceptor(Interceptor { chain ->
                tokenProvider?.let { provider ->
                    val token = provider.token
                    if (token.isNotEmpty()) {
                        val newRequest = chain.request().newBuilder()
                            .addHeader(RemoteConstants.HeadersConst.AUTHORIZATION, "Bearer $token")
                            .build()
                        return@Interceptor chain.proceed(newRequest)
                    }
                }
                chain.proceed(chain.request())
            })
        }
    }
}