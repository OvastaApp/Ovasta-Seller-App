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
            message = throwable.message ?: "An unknown error occurred",
            actions = listOf(retryAction)
        )
        _appExceptionEvent.value = exception
    }
}
