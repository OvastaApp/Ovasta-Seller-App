package com.ovasta.sellers.ui.screens.orderhistory

import androidx.lifecycle.viewModelScope
import com.ovasta.sellers.domain.repository.IOrderHistoryRepository
import com.ovasta.sellers.ui.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OrderHistoryViewModel(
    private val orderHistoryRepository: IOrderHistoryRepository,
) : BaseViewModel() {

    private val _viewState = MutableStateFlow(OrderHistoryViewState())
    val viewState = _viewState.asStateFlow()

    fun onScreenAction(action: OrderHistoryAction) {
        when (action) {
            OrderHistoryAction.LoadOrderHistory -> getLastOrders()
        }
    }

    fun getLastOrders() {
        viewModelScope.launch {
            setLoading(true)
            runCatching { orderHistoryRepository.getLastOrders() }
                .onSuccess { response ->
                    setLoading(false)
                    _viewState.update { it.copy(deliveryOrdersResponse = response) }
                }
                .onFailure {
                    setLoading(false)
                    handleError(it)
                }
        }
    }
}
