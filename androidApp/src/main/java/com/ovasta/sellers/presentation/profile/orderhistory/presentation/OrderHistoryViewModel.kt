package com.ovasta.sellers.presentation.profile.orderhistory.presentation

import androidx.lifecycle.viewModelScope
import com.ovasta.sellers.base.BaseViewModel
import kotlinx.coroutines.launch
import com.ovasta.sellers.base.exception.toComposeUIException
import com.ovasta.sellers.data.setting.data.ISettingsRepository
import com.ovasta.sellers.domain.repository.IOrderHistoryRepository
import com.ovasta.sellers.presentation.profile.profile.data.IProfileRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class OrderHistoryViewModel(
    val profileRepository: IOrderHistoryRepository,
) : BaseViewModel() {

    private val _viewState = MutableStateFlow(OrderHistoryViewState())
    val viewState = _viewState.asStateFlow()

    fun onScreenAction(action: OrderHistoryAction) {
        when (action) {
            OrderHistoryAction.LoadOrderHistory -> {
                getLastOrders()
            }
        }
    }

    fun getLastOrders() {
        viewModelScope.launch {
            setComposeUILoading(true)
            runCatching {
                profileRepository.getLastOrders()
            }.onSuccess { response ->
                setComposeUILoading(false)
                updateViewState {
                    it.copy(
                        deliveryOrdersResponse = response
                    )
                }
            }.onFailure {
                updateViewStateWithFail(it)
            }
        }
    }

    fun updateViewState(update: (OrderHistoryViewState) -> OrderHistoryViewState) {
        _viewState.update(update)
    }

    fun updateViewStateWithFail(throwable: Throwable) {
        setComposeUILoading(false)
        emitComposeUIExceptionEvent(throwable.toComposeUIException())
    }
}