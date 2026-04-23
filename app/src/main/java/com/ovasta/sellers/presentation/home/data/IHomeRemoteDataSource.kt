package com.ovasta.sellers.presentation.home.data

import com.ovasta.sellers.data.ApiResponse
import com.ovasta.sellers.presentation.home.data.model.OrderInfo
import com.ovasta.sellers.presentation.home.data.model.PointsInfo

interface IHomeRemoteDataSource {
    suspend fun getMyOrders(): List<OrderInfo>

    suspend fun getHomeInfo(): PointsInfo
}