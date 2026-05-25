package com.ovasta.sellers.presentation.home.data

import com.ovasta.sellers.data.network.SellerApiService
import com.ovasta.sellers.presentation.home.data.model.DeliveryOrdersResponse
import com.ovasta.sellers.presentation.home.data.model.HomeInfo

class HomeRemoteDataSource(private val apiService: SellerApiService) : IHomeRemoteDataSource {
    override suspend fun getCurrentOrders(page: Int?): DeliveryOrdersResponse {
        val response = apiService.getCurrentOrders(page = page)
        return response.data ?: throw IllegalStateException("Failed to fetch orders: ${response.message}")
    }

    override suspend fun getHomeInfo(): HomeInfo {
        val response = apiService.getHomeInfo()
        return response.data ?: throw IllegalStateException("Failed to fetch home info: ${response.message}")
    }

    override suspend fun cancelOrder(orderId: Int) {
        apiService.cancelOrder(orderId)
    }
}