package com.ovasta.sellers.data.remote

import com.ovasta.sellers.domain.model.CreateOrderRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class CreateOrderApiService(private val client: HttpClient) {
    suspend fun createOrder(request: CreateOrderRequest) {
        client.post("delivery-orders") {
            setBody(request)
        }
    }
}
