package com.ovasta.sellers.presentation.createOrder.data


interface ICreateOrderRepository {

    suspend fun createOrder(
        destination: String,
        clientPhone: String,
        clientName: String,
        collectionAmount: Double,
        note: String
    )
}