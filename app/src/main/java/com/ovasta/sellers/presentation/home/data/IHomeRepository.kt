package com.ovasta.sellers.presentation.home.data

import com.ovasta.sellers.presentation.home.data.model.HomeInfo
import com.ovasta.sellers.presentation.home.data.model.DeliveryOrdersResponse

interface IHomeRepository {
    suspend fun getMyOrders( page: Int? = null): DeliveryOrdersResponse?

    suspend fun getHomeInfo(): HomeInfo
}