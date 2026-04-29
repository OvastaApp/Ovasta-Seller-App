package com.ovasta.sellers.presentation.profile.data


class ProfileRemoteDataSource(
    private val homeApi: ProfileApi
) : IProfileRemoteDataSource {
    override suspend fun getCurrentOrders(page: Int?) =
        homeApi.getCurrentOrders(page = page).data

    override suspend fun getHomeInfo() = homeApi.getHome().data
    override suspend fun cancelOrder(orderId: Int) = homeApi.cancelOrder(orderId)
}