package com.ovasta.sellers.presentation.createOrder.data

import com.ovasta.sellers.presentation.createOrder.data.model.CreateOrderRequest


class CreateOrderRemoteDataSource(
    private val createOrderApi: CreateOrderApi
) : ICreateOrderRemoteDataSource {

    override suspend fun createOrder(
        destination: String,
        clientPhone: String,
        clientName: String,
        collectionAmount: Double,
        note: String
    ) {
        val order = CreateOrderRequest(
            destination = destination,
            clientPhone = clientPhone,
            clientName = clientName,
            collectionAmount =collectionAmount,
            note = note
        )
        createOrderApi.createOrder(order)
    }
}