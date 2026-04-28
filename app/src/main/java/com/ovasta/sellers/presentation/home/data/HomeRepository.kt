package com.ovasta.sellers.presentation.home.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HomeRepository(
    private val homeRemoteDataSource: IHomeRemoteDataSource,
) : IHomeRepository {

    override suspend fun getCurrentOrders(page: Int?) = withContext(Dispatchers.IO) {
        homeRemoteDataSource.getCurrentOrders(page)
    }

    override suspend fun getHomeInfo() = withContext(Dispatchers.IO) {
        homeRemoteDataSource.getHomeInfo()
    }

    override suspend fun cancelOrder(orderId: Int) = withContext(Dispatchers.IO) {
        homeRemoteDataSource.cancelOrder(orderId)
    }
}