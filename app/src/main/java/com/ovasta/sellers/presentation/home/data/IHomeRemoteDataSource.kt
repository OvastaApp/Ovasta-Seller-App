package com.ovasta.sellers.presentation.home.data

import com.ovasta.sellers.presentation.home.data.model.HomeInfo
import com.ovasta.sellers.presentation.home.data.model.OrderInfo

interface IHomeRemoteDataSource {
    suspend fun getMyOrders(): List<OrderInfo>

    suspend fun getHomeInfo(): HomeInfo
}