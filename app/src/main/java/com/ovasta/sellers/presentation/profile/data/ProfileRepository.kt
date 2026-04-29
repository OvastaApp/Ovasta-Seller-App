package com.ovasta.sellers.presentation.profile.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProfileRepository(
    private val homeRemoteDataSource: IProfileRemoteDataSource,
) : IProfileRepository {

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