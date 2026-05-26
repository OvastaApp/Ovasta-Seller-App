package com.ovasta.sellers.data.repository

import com.ovasta.sellers.data.remote.OrderHistoryApiService
import com.ovasta.sellers.domain.model.DeliveryOrdersResponse
import com.ovasta.sellers.domain.repository.IOrderHistoryRepository

class OrderHistoryRepository(private val api: OrderHistoryApiService) : IOrderHistoryRepository {
    override suspend fun getLastOrders(page: Int?): DeliveryOrdersResponse =
        api.getLastOrders(page).data ?: DeliveryOrdersResponse()
}
