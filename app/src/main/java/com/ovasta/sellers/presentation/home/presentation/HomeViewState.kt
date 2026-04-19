package com.ovasta.sellers.presentation.home.presentation

import com.ovasta.sellers.presentation.home.data.model.OrderInfo
import com.ovasta.sellers.presentation.home.data.model.PointsInfo

data class HomeViewState(
    val pointsInfo: PointsInfo? = null,
    val myOrders: List<OrderInfo>? = null,
    val isLogoutDialogVisible: Boolean = false
)