package com.ovasta.sellers.presentation.profile.orderhistory.data

import com.ovasta.sellers.data.network.SellerApiService
import com.ovasta.sellers.presentation.home.data.model.DeliveryOrdersResponse

class OrderHistoryRemoteDataSource(private val apiService: SellerApiService) : IOrderHistoryRemoteDataSource {
    override suspend fun getLastOrders(page: Int?): DeliveryOrdersResponse {
        return apiService.getPastOrders(page = page).data
    }
}