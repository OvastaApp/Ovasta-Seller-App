package com.ovasta.sellers.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Interface to provide auth headers dynamically.
 * Implemented per-platform or via DI to read from session storage.
 */
interface SessionHeaderProvider {
    fun getAccessToken(): String
    fun getDeviceId(): String
    fun getLanguage(): String
}

fun createHttpClient(
    engine: io.ktor.client.engine.HttpClientEngine,
    sessionHeaderProvider: SessionHeaderProvider,
    enableLogging: Boolean = false,
): HttpClient {
    return HttpClient(engine) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = true
            })
        }

        install(HttpTimeout) {
            connectTimeoutMillis = ApiConstants.CONNECT_TIMEOUT_MS
            requestTimeoutMillis = ApiConstants.READ_TIMEOUT_MS
            socketTimeoutMillis = ApiConstants.READ_TIMEOUT_MS
        }

        if (enableLogging) {
            install(Logging) {
                level = LogLevel.BODY
            }
        }

        defaultRequest {
            url(ApiConstants.BASE_URL)
            contentType(ContentType.Application.Json)
            header(ApiConstants.Headers.ACCEPT, "application/json")
            header(ApiConstants.Headers.IDENTIFIER, "\$2a\$12\$BeuZVyrk1vlnlws5ljkRnuHA5UypUwVW3gyGoFaGvpdF5sgeSzXr2")
            header(ApiConstants.Headers.LANG, sessionHeaderProvider.getLanguage())
            header(ApiConstants.Headers.DEVICE_ID, sessionHeaderProvider.getDeviceId())
            val token = sessionHeaderProvider.getAccessToken()
            if (token.isNotEmpty()) {
                header(ApiConstants.Headers.AUTHORIZATION, "Bearer $token")
            }
        }
    }
}
