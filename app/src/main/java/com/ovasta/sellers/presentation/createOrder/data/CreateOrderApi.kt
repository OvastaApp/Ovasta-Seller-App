package com.ovasta.sellers.presentation.createOrder.data

import com.ovasta.sellers.presentation.createOrder.data.model.CreateOrderRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface CreateOrderApi {
    @POST("create-order")
    suspend fun createOrder(@Body createOrderRequest: CreateOrderRequest)
}