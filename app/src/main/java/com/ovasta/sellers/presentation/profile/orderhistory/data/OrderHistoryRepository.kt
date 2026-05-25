package com.ovasta.sellers.presentation.profile.orderhistory.data

class OrderHistoryRepository(
    private val profileRemoteDataSource: IOrderHistoryRemoteDataSource,
) : IOrderHistoryRepository {
    override suspend fun getLastOrders(page: Int?) =
        profileRemoteDataSource.getLastOrders(page = page)
}