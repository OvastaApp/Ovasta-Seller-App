package com.ovasta.sellers.presentation.home.data


import com.ovasta.sellers.presentation.home.data.model.CreateOrderRequest

class HomeRemoteDataSource(
    private val homeApi: HomeApi
) : IHomeRemoteDataSource {

    override suspend fun createOrder(
        destination: String, clientPhone: String, clientName: String, note: String
    ) {
        val order = CreateOrderRequest(
            destination = destination,
            clientPhone = clientPhone,
            clientName = clientName,
            note = note
        )
        homeApi.createOrder(order)
    }

    override suspend fun getMyOrders() = homeApi.getMyOrders()

    override suspend fun getPointsInfo() = homeApi.getPointsInfo()
}