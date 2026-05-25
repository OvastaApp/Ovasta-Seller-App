package com.ovasta.sellers.presentation.home.data

import com.ovasta.sellers.data.network.SellerApiService
import com.ovasta.sellers.presentation.home.data.model.DeliveryOrdersResponse
import com.ovasta.sellers.presentation.home.data.model.HomeInfo

class HomeRemoteDataSource(private val apiService: SellerApiService) : IHomeRemoteDataSource {
    override suspend fun getCurrentOrders(page: Int?): DeliveryOrdersResponse {
        return apiService.getCurrentOrders(page = page).data
    }

    override suspend fun getHomeInfo(): HomeInfo {
        return apiService.getHomeInfo().data
    }

    override suspend fun cancelOrder(orderId: Int) {
        apiService.cancelOrder(orderId)
    }
}