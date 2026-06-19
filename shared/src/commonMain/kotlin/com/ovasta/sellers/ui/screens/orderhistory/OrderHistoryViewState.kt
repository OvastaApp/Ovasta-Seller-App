package com.ovasta.sellers.ui.screens.orderhistory

import com.ovasta.sellers.domain.model.DeliveryOrder

data class OrderHistoryViewState(
    val orders: List<DeliveryOrder> = emptyList(),
    val currentPage: Int = 0,
    val lastPage: Int = 0,
    val isLoadingMore: Boolean = false,
) {
    /** True when there are more pages to load from the server. */
    val canLoadMore: Boolean get() = currentPage in 1 until lastPage
}

sealed interface OrderHistoryAction {
    data object LoadOrderHistory : OrderHistoryAction
    data object LoadMore : OrderHistoryAction
}
