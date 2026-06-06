package com.ovasta.sellers.data.remote

import com.ovasta.sellers.platform.httpLog
import com.ovasta.sellers.platform.isDebug
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Interface to provide auth headers dynamically.
 * Methods are NOT suspend because they are called from Ktor's defaultRequest block
 * which does not support coroutines. Implementations must use synchronous reads only.
 */
interface SessionHeaderProvider {
    fun getAccessToken(): String
    fun getDeviceId(): String
    fun getLanguage(): String
    fun getIdentifier(): String
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
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    return HttpClient(engine) {
        expectSuccess = true

        install(ContentNegotiation) {
            json(json)
        }

        HttpResponseValidator {
            handleResponseExceptionWithRequest { exception, _ ->
                if (exception !is ResponseException) return@handleResponseExceptionWithRequest
                val responseBody = exception.response.bodyAsText()
                val errorMessage = try {
                    val jsonObject = json.decodeFromString<JsonObject>(responseBody)
                    (jsonObject["message"] ?: jsonObject["msg"])?.jsonPrimitive?.content
                } catch (_: Exception) {
                    null
                }
                if (!errorMessage.isNullOrBlank()) {
                    throw Exception(errorMessage)
                }
            }
        }

        install(HttpTimeout) {
            connectTimeoutMillis = ApiConstants.CONNECT_TIMEOUT_MS
            requestTimeoutMillis = ApiConstants.READ_TIMEOUT_MS
            socketTimeoutMillis = ApiConstants.READ_TIMEOUT_MS
        }

        if (enableLogging) {
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        httpLog(message)
                    }
                }
                level = LogLevel.ALL
            }
        }

        defaultRequest {
            url(ApiConstants.BASE_URL)
            contentType(ContentType.Application.Json)
            header(ApiConstants.Headers.ACCEPT, "application/json")
            header(ApiConstants.Headers.IDENTIFIER, sessionHeaderProvider.getIdentifier())
            header(ApiConstants.Headers.LANG, sessionHeaderProvider.getLanguage())
            header(ApiConstants.Headers.DEVICE_ID, sessionHeaderProvider.getDeviceId())
            val token = sessionHeaderProvider.getAccessToken()
            if (token.isNotEmpty()) {
                header(ApiConstants.Headers.AUTHORIZATION, "Bearer $token")
            }
        }
    }
}
