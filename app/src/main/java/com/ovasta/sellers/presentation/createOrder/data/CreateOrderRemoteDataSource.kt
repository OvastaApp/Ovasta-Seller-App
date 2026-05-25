package com.ovasta.sellers.presentation.createOrder.data

import com.google.gson.annotations.SerializedName
import com.ovasta.sellers.presentation.createOrder.data.model.CreateOrderRequest


class CreateOrderRemoteDataSource(
    private val createOrderApi: CreateOrderApi
) : ICreateOrderRemoteDataSource {

    override suspend fun createOrder(
        destination: String,
        clientPhone: String,
        collectionAmount: Double,
        deliveryFees: Double,
        note: String?
    ) {
        val order = CreateOrderRequest(
            destination = destination,
            clientPhone = clientPhone,
            collectionAmount = collectionAmount,
            deliveryFees = deliveryFees,
            note = note ?: ""
        )
        createOrderApi.createOrder(order)
    }
}