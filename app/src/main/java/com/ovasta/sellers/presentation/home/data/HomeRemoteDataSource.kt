package com.ovasta.sellers.presentation.home.data


class HomeRemoteDataSource(
    private val homeApi: HomeApi
) : IHomeRemoteDataSource {
    override suspend fun getMyOrders() = homeApi.getMyOrders().data

    override suspend fun getHomeInfo() = homeApi.getHome().data
}