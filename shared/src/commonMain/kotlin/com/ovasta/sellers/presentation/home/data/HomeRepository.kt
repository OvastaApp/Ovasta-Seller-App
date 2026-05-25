package com.ovasta.sellers.presentation.home.data

import com.ovasta.sellers.presentation.home.data.model.DeliveryOrdersResponse
import com.ovasta.sellers.presentation.home.data.model.HomeInfo

class HomeRepository(
    private val homeRemoteDataSource: IHomeRemoteDataSource
) : IHomeRepository {
    override suspend fun getCurrentOrders(page: Int?) = homeRemoteDataSource.getCurrentOrders(page)
    override suspend fun getHomeInfo() = homeRemoteDataSource.getHomeInfo()
    override suspend fun cancelOrder(orderId: Int) = homeRemoteDataSource.cancelOrder(orderId)
}