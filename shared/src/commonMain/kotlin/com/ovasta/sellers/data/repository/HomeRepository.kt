package com.ovasta.sellers.data.repository

import com.ovasta.sellers.data.remote.HomeApiService
import com.ovasta.sellers.domain.model.DeliveryOrdersResponse
import com.ovasta.sellers.domain.model.HomeInfo
import com.ovasta.sellers.domain.repository.IHomeRepository

class HomeRepository(private val api: HomeApiService) : IHomeRepository {
    override suspend fun getHomeInfo(): HomeInfo = api.getHome().data ?: HomeInfo()
    override suspend fun getCurrentOrders(page: Int?): DeliveryOrdersResponse =
        api.getCurrentOrders(page = page).data ?: DeliveryOrdersResponse()
    override suspend fun cancelOrder(orderId: Int) = api.cancelOrder(orderId)
}
