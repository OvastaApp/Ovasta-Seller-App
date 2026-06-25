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
            message = userFacingMessage(throwable.message),
            actions = listOf(retryAction)
        )
        _appExceptionEvent.value = exception
    }

    /**
     * Returns a safe message to show in the UI. Raw exception messages from the
     * network stack (timeouts, connection failures, etc.) often embed the request
     * URL/host, which must never be exposed to the user. Any message that looks
     * technical is replaced with a generic fallback.
     */
    private fun userFacingMessage(message: String?): String {
        val fallback = "An unknown error occurred"
        if (message.isNullOrBlank()) return fallback
        val looksTechnical = message.contains("http", ignoreCase = true) ||
            message.contains("://") ||
            URL_OR_IP_REGEX.containsMatchIn(message)
        return if (looksTechnical) fallback else message
    }

    private companion object {
        private val URL_OR_IP_REGEX = Regex("""\d{1,3}(\.\d{1,3}){3}|[\w-]+(\.[\w-]+)+(/|:\d)""")
    }
}
