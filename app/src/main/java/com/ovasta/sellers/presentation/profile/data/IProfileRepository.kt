package com.ovasta.sellers.presentation.profile.data

import com.ovasta.sellers.presentation.home.data.model.HomeInfo
import com.ovasta.sellers.presentation.home.data.model.DeliveryOrdersResponse

interface IProfileRepository {
    suspend fun getCurrentOrders(page: Int? = null): DeliveryOrdersResponse?

    suspend fun getHomeInfo(): HomeInfo

    suspend fun cancelOrder(orderId: Int)

}