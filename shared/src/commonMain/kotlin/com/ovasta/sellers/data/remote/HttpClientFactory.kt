package com.ovasta.sellers.data.remote

import com.ovasta.sellers.platform.isDebug
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
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

/**
 * Interface to provide auth headers dynamically.
 * Implemented per-platform or via DI to read from session storage.
 */
interface SessionHeaderProvider {
    suspend fun getAccessToken(): String
    suspend fun getDeviceId(): String
    suspend fun getLanguage(): String
    suspend fun getIdentifier(): String
}

object HttpClientFactory {
    fun create(sessionHeaderProvider: SessionHeaderProvider): HttpClient {
        return createHttpClient(getHttpClientEngine(), sessionHeaderProvider, enableLogging = isDebug)
    }
}

/**
 * Platform-specific HTTP client engine.
 * Android: OkHttp
 * iOS: Darwin
 */
expect fun getHttpClientEngine(): io.ktor.client.engine.HttpClientEngine

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
            // Note: Using runBlocking here is not ideal but Ktor's defaultRequest doesn't support suspend
            // In production, consider refactoring to use an interceptor instead
            val identifier = runBlocking { sessionHeaderProvider.getIdentifier() }
            val lang = runBlocking { sessionHeaderProvider.getLanguage() }
            val deviceId = runBlocking { sessionHeaderProvider.getDeviceId() }
            val token = runBlocking { sessionHeaderProvider.getAccessToken() }
            
            header(ApiConstants.Headers.IDENTIFIER, identifier)
            header(ApiConstants.Headers.LANG, lang)
            header(ApiConstants.Headers.DEVICE_ID, deviceId)
            if (token.isNotEmpty()) {
                header(ApiConstants.Headers.AUTHORIZATION, "Bearer $token")
            }
        }
    }
}
