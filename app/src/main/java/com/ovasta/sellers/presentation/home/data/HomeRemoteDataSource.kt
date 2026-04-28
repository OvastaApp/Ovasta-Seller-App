package com.ovasta.sellers.presentation.home.data


class HomeRemoteDataSource(
    private val homeApi: HomeApi
) : IHomeRemoteDataSource {
    override suspend fun getCurrentOrders(page: Int?) =
        homeApi.getCurrentOrders(page = page).data

    override suspend fun getHomeInfo() = homeApi.getHome().data
    override suspend fun cancelOrder(orderId: Int) = homeApi.cancelOrder(orderId)
}