package com.ovasta.sellers.presentation.home.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HomeRepository(
    private val homeRemoteDataSource: IHomeRemoteDataSource,
) : IHomeRepository {

    override suspend fun getMyOrders(page: Int?) = withContext(Dispatchers.IO) {
        homeRemoteDataSource.getMyOrders(page)
    }

    override suspend fun getHomeInfo() = withContext(Dispatchers.IO) {
        homeRemoteDataSource.getHomeInfo()
    }
}