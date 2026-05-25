package com.ovasta.sellers.presentation.profile.orderhistory.data

import com.ovasta.sellers.presentation.home.data.model.DeliveryOrdersResponse

class OrderHistoryRemoteDataSource(
    private val profileApi: OrderHistoryApi
) : IOrderHistoryRemoteDataSource {
    override suspend fun getLastOrders(page: Int?): DeliveryOrdersResponse =
        profileApi.getLastOrders(page = page).data
}