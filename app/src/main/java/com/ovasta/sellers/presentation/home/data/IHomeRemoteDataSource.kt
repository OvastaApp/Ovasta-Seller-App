package com.ovasta.sellers.presentation.home.data

import com.ovasta.sellers.presentation.home.data.model.DeliveryOrdersResponse
import com.ovasta.sellers.presentation.home.data.model.HomeInfo

interface IHomeRemoteDataSource {
    suspend fun getMyOrders(page: Int?): DeliveryOrdersResponse

    suspend fun getHomeInfo(): HomeInfo
}