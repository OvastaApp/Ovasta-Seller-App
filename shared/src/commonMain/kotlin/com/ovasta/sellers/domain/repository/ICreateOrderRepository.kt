package com.ovasta.sellers.domain.repository

interface ICreateOrderRepository {
    suspend fun createOrder(
        destination: String,
        clientPhone: String,
        collectionAmount: Double,
        deliveryFees: Double,
        note: String? = null,
    )
}
