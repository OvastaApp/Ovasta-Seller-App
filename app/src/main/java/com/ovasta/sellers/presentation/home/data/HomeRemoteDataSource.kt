package com.ovasta.sellers.presentation.home.data


class HomeRemoteDataSource(
    private val homeApi: HomeApi
) : IHomeRemoteDataSource {
    override suspend fun getMyOrders(page: Int?) =
        homeApi.getMyOrders(page).data

    override suspend fun getHomeInfo() = homeApi.getHome().data
}