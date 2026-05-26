package com.ovasta.sellers.domain.repository

import com.ovasta.sellers.domain.model.DeliveryOrdersResponse

interface IOrderHistoryRepository {
    suspend fun getLastOrders(page: Int? = null): DeliveryOrdersResponse
}
