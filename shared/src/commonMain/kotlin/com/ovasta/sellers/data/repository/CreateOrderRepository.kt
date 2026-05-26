package com.ovasta.sellers.data.repository

import com.ovasta.sellers.data.remote.CreateOrderApiService
import com.ovasta.sellers.domain.model.CreateOrderRequest
import com.ovasta.sellers.domain.repository.ICreateOrderRepository

class CreateOrderRepository(private val api: CreateOrderApiService) : ICreateOrderRepository {
    override suspend fun createOrder(
        destination: String,
        clientPhone: String,
        collectionAmount: Double,
        deliveryFees: Double,
        note: String?,
    ) {
        api.createOrder(
            CreateOrderRequest(
                destination = destination,
                clientPhone = clientPhone,
                collectionAmount = collectionAmount,
                deliveryFees = deliveryFees,
                note = note ?: "",
            )
        )
    }
}
