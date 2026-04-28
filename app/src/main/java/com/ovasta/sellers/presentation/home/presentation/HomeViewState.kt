package com.ovasta.sellers.presentation.home.presentation

import com.ovasta.sellers.presentation.home.data.model.HomeInfo
import com.ovasta.sellers.presentation.home.data.model.DeliveryOrdersResponse

data class HomeViewState(
    val homeInfo: HomeInfo? = null,
    val deliveryOrdersResponse: DeliveryOrdersResponse? = null,
    val isLogoutDialogVisible: Boolean = false,
    val isRefreshing: Boolean? = false
)