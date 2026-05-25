package com.ovasta.sellers.presentation.home.presentation

import com.ovasta.sellers.data.User
import com.ovasta.sellers.presentation.home.data.model.DeliveryOrdersResponse
import com.ovasta.sellers.presentation.home.data.model.HomeInfo

data class HomeViewState(
    val homeInfo: HomeInfo? = null,
    val deliveryOrdersResponse: DeliveryOrdersResponse? = null,
    val isLogoutDialogVisible: Boolean = false,
    val isRefreshing: Boolean? = false
)
