package com.ovasta.sellers.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.ovasta.sellers.platform.recordException
import org.koin.core.component.KoinComponent

open class BaseViewModel : ViewModel(), KoinComponent {
    val dispatcher: CoroutineDispatcher = Dispatchers.Main

    private val _screenDirectionEvent = MutableSharedFlow<ScreenDirection?>()
    val screenDirectionEvent = _screenDirectionEvent.asSharedFlow()

    private val _appExceptionEvent = MutableStateFlow<AppException?>(null)
    val appExceptionEvent = _appExceptionEvent.asStateFlow()

    private val _loadingEvent = MutableStateFlow(false)
    val loadingEvent = _loadingEvent.asStateFlow()

    private val _messageEvent = MutableSharedFlow<String?>()
    val messageEvent = _messageEvent.asSharedFlow()

    fun emitScreenDirection(direction: ScreenDirection) {
        viewModelScope.launch { _screenDirectionEvent.emit(direction) }
    }

    fun emitAppException(exception: AppException?) {
        _appExceptionEvent.value = exception
    }

    fun setLoading(isLoading: Boolean) {
        _loadingEvent.value = isLoading
    }

    fun emitMessage(message: String) {
        viewModelScope.launch { _messageEvent.emit(message) }
    }

    protected fun handleError(throwable: Throwable, retryAction: () -> Unit = {}) {
        recordException(throwable)
        val exception = AppException(
            message = userFacingMessage(throwable),
            actions = listOf(retryAction)
        )
        _appExceptionEvent.value = exception
    }

    /**
     * Returns a safe message to show in the UI. Only messages we know originated
     * from our own server-message extraction (short, plain text) are shown. Raw
     * exception messages from the network/serialization stack — timeouts,
     * connection failures, JSON parse errors — embed URLs, hosts, stack details,
     * or fragments of the response body and must never surface to the user.
     */
    private fun userFacingMessage(throwable: Throwable): String {
        val fallback = "An unknown error occurred"
        val name = throwable::class.simpleName.orEmpty()
        if (name in UNSAFE_EXCEPTION_NAMES) return fallback
        val message = throwable.message
        if (message.isNullOrBlank()) return fallback
        if (message.length > MAX_SAFE_MESSAGE_LENGTH) return fallback
        val looksTechnical = message.contains("http", ignoreCase = true) ||
            message.contains("://") ||
            URL_OR_IP_REGEX.containsMatchIn(message) ||
            RAW_PAYLOAD_REGEX.containsMatchIn(message)
        return if (looksTechnical) fallback else message
    }

    private companion object {
        private const val MAX_SAFE_MESSAGE_LENGTH = 200
        private val URL_OR_IP_REGEX = Regex("""\d{1,3}(\.\d{1,3}){3}|[\w-]+(\.[\w-]+)+(/|:\d)""")
        // Structural markers that indicate the message is carrying JSON, an
        // escape sequence, a stack path, or other raw payload fragments.
        private val RAW_PAYLOAD_REGEX = Regex("""[{}\[\]]|\\u[0-9a-fA-F]{4}|\bat path\b|JSON|serial|deserializ""",
            RegexOption.IGNORE_CASE)
        private val UNSAFE_EXCEPTION_NAMES = setOf(
            "SerializationException",
            "JsonConvertException",
            "JsonDecodingException",
            "JsonEncodingException",
            "MissingFieldException",
            "IOException",
            "SocketTimeoutException",
            "ConnectTimeoutException",
            "HttpRequestTimeoutException",
            "SocketException",
            "UnknownHostException",
            "SSLException",
            "SSLHandshakeException",
            "ResponseException",
            "ClientRequestException",
            "ServerResponseException",
            "RedirectResponseException",
            "NoTransformationFoundException",
        )
    }
}
