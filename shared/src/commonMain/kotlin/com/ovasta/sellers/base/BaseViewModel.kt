package com.ovasta.sellers.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ovasta.sellers.base.exception.ComposeUIException
import com.ovasta.sellers.base.exception.toComposeUIException
import com.ovasta.sellers.base.ext.ToastEvent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

open class BaseViewModel : ViewModel(), KoinComponent {
    val dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate

    private val _error = MutableSharedFlow<Throwable>(extraBufferCapacity = 1)
    val error = _error.asSharedFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _screenDirectionEvent = MutableSharedFlow<ScreenDirection?>(extraBufferCapacity = 1)
    val screenDirectionEvent = _screenDirectionEvent.asSharedFlow()

    private val _composeUIExceptionEvent = MutableStateFlow<ComposeUIException?>(null)
    val composeUIExceptionEvent = _composeUIExceptionEvent.asStateFlow()

    private val _composeUILoadingEvent = MutableStateFlow(false)
    val composeUILoadingEvent = _composeUILoadingEvent.asStateFlow()

    private val _toastEvent = MutableSharedFlow<ToastEvent?>(extraBufferCapacity = 1)
    val toastEvent = _toastEvent.asSharedFlow()

    fun emitScreenDirectionEvent(direction: ScreenDirection) {
        viewModelScope.launch { _screenDirectionEvent.emit(direction) }
    }

    fun emitComposeUIExceptionEvent(exception: ComposeUIException?) {
        viewModelScope.launch { _composeUIExceptionEvent.emit(exception) }
    }

    fun setComposeUILoading(isLoading: Boolean) {
        viewModelScope.launch { _composeUILoadingEvent.emit(isLoading) }
    }

    protected fun emitComposeUIExceptionEvent(newAction: () -> Unit, throwable: Throwable) {
        val current = _composeUIExceptionEvent.value
        val newEvent = throwable.toComposeUIException(newAction)

        if (current == null) {
            _composeUIExceptionEvent.value = newEvent
        } else {
            _composeUIExceptionEvent.value = current.copy(
                actions = current.actions.toMutableList().apply {
                    addAll(newEvent.actions)
                }
            )
        }
    }

    fun emitToastEvent(toastEvent: ToastEvent) {
        viewModelScope.launch { _toastEvent.emit(toastEvent) }
    }

    protected fun emitError(throwable: Throwable) {
        viewModelScope.launch { _error.emit(throwable) }
    }

    protected fun setLoading(isLoading: Boolean) {
        viewModelScope.launch { _loading.emit(isLoading) }
    }
}
