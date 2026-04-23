package com.ovasta.sellers.presentation.createOrder.data

interface ICreateOrderRemoteDataSource {
    suspend fun createOrder(
        destination: String,
        clientPhone: String,
        clientName: String,
        collectionAmount: Double,
        note: String
    )
}