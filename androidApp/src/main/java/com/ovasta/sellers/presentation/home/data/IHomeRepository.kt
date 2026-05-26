package com.ovasta.sellers.presentation.home.data

import com.ovasta.sellers.presentation.home.data.model.HomeInfo
import com.ovasta.sellers.presentation.home.data.model.DeliveryOrdersResponse
import retrofit2.http.Path

interface IHomeRepository {
    suspend fun getCurrentOrders(page: Int? = null): DeliveryOrdersResponse?

    suspend fun getHomeInfo(): HomeInfo

    suspend fun cancelOrder(orderId: Int)

}