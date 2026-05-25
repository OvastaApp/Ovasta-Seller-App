package com.ovasta.sellers.presentation.profile.orderhistory.data

import com.ovasta.sellers.presentation.home.data.model.DeliveryOrdersResponse

class OrderHistoryRepository(
    private val profileRemoteDataSource: IOrderHistoryRemoteDataSource
) : IOrderHistoryRepository {
    override suspend fun getLastOrders(page: Int?) = profileRemoteDataSource.getLastOrders(page = page)
}