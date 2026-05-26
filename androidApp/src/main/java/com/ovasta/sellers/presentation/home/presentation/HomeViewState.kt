package com.ovasta.sellers.presentation.home.presentation

import com.ovasta.sellers.domain.model.HomeInfo
import com.ovasta.sellers.domain.model.DeliveryOrdersResponse

data class HomeViewState(
    val homeInfo: HomeInfo? = null,
    val deliveryOrdersResponse: DeliveryOrdersResponse? = null,
    val isLogoutDialogVisible: Boolean = false,
    val isRefreshing: Boolean? = false
)