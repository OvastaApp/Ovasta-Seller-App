package com.ovasta.sellers.presentation.createOrder.data

interface ICreateOrderRepository {
    suspend fun createOrder(
        destination: String,
        clientPhone: String,
        collectionAmount: Double,
        deliveryFees: Double,
        note: String? = null
    )
}