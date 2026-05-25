package com.ovasta.sellers.presentation.profile.orderhistory.presentation

import androidx.lifecycle.viewModelScope
import com.ovasta.sellers.base.BaseViewModel
import com.ovasta.sellers.base.exception.toComposeUIException
import com.ovasta.sellers.presentation.profile.orderhistory.data.IOrderHistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OrderHistoryViewModel(
    val orderHistoryRepository: IOrderHistoryRepository,
) : BaseViewModel() {

    private val _viewState = MutableStateFlow(OrderHistoryViewState())
    val viewState = _viewState.asStateFlow()

    fun onScreenAction(action: OrderHistoryAction) {
        when (action) {
            OrderHistoryAction.LoadOrderHistory -> {
                getLastOrders()
            }
            OrderHistoryAction.RefreshHistory -> {
                getLastOrders()
            }
        }
    }

    fun getLastOrders() {
        viewModelScope.launch {
            setComposeUILoading(true)
            runCatching {
                orderHistoryRepository.getLastOrders()
            }.onSuccess { response ->
                setComposeUILoading(false)
                updateViewState {
                    it.copy(deliveryOrdersResponse = response)
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
