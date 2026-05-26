package com.ovasta.sellers.presentation.profile.orderhistory.presentation

import com.ovasta.sellers.domain.model.DeliveryOrdersResponse

data class OrderHistoryViewState(
    val deliveryOrdersResponse: DeliveryOrdersResponse? = null,
    val isRefreshing: Boolean = false,
)