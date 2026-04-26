package com.ovasta.sellers.presentation.home.presentation

import com.ovasta.sellers.presentation.home.data.model.HomeInfo
import com.ovasta.sellers.presentation.home.data.model.OrderInfo

data class HomeViewState(
    val homeInfo: HomeInfo? = null,
    val myOrders: List<OrderInfo>? = null,
    val isLogoutDialogVisible: Boolean = false
)