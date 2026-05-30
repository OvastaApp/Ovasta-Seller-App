package com.ovasta.sellers.ui.screens.orderhistory

import com.ovasta.sellers.domain.model.DeliveryOrdersResponse

data class OrderHistoryViewState(
    val deliveryOrdersResponse: DeliveryOrdersResponse? = null,
    val isRefreshing: Boolean = false,
)

sealed interface OrderHistoryAction {
    data object LoadOrderHistory : OrderHistoryAction
}
