package com.ovasta.sellers.presentation.profile.orderhistory.data

import com.ovasta.sellers.presentation.home.data.model.DeliveryOrdersResponse

interface IOrderHistoryRemoteDataSource {
    suspend fun getLastOrders(page: Int? = null): DeliveryOrdersResponse
}