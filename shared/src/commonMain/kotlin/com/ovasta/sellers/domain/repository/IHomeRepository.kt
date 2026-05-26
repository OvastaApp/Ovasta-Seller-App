package com.ovasta.sellers.domain.repository

import com.ovasta.sellers.domain.model.DeliveryOrdersResponse
import com.ovasta.sellers.domain.model.HomeInfo

interface IHomeRepository {
    suspend fun getHomeInfo(): HomeInfo
    suspend fun getCurrentOrders(page: Int? = null): DeliveryOrdersResponse
    suspend fun cancelOrder(orderId: Int)
}
