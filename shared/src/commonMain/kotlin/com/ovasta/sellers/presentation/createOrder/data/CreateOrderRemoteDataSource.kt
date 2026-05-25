package com.ovasta.sellers.presentation.createOrder.data

import com.ovasta.sellers.data.network.SellerApiService
import com.ovasta.sellers.presentation.createOrder.data.model.CreateOrderRequest

class CreateOrderRemoteDataSource(private val apiService: SellerApiService) : ICreateOrderRemoteDataSource {
    override suspend fun createOrder(destination: String, clientPhone: String, collectionAmount: Double, deliveryFees: Double, note: String?) {
        val request = CreateOrderRequest(
            destination = destination,
            clientPhone = clientPhone,
            collectionAmount = collectionAmount,
            deliveryFees = deliveryFees,
            note = note ?: ""
        )
        apiService.createOrder(request)
    }
}