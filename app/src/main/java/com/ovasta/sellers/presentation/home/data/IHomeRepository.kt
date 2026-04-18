package com.ovasta.sellers.presentation.home.data

import com.ovasta.sellers.data.ApiResponse
import com.ovasta.sellers.presentation.home.data.model.OrderResponse
import com.ovasta.sellers.presentation.home.data.model.PointsInfo

interface IHomeRepository {

    suspend fun createOrder(
        destination: String, clientPhone: String, clientName: String, note: String
    )

    suspend fun getMyOrders(): ApiResponse<List<OrderResponse>>

    suspend fun getPointsInfo(): ApiResponse<PointsInfo>
}