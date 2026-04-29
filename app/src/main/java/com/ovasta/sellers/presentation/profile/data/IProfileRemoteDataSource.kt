package com.ovasta.sellers.presentation.profile.data

import com.ovasta.sellers.presentation.home.data.model.DeliveryOrdersResponse
import com.ovasta.sellers.presentation.home.data.model.HomeInfo

interface IProfileRemoteDataSource {
    suspend fun getCurrentOrders(page: Int?): DeliveryOrdersResponse

    suspend fun getHomeInfo(): HomeInfo

    suspend fun cancelOrder(orderId: Int)
}