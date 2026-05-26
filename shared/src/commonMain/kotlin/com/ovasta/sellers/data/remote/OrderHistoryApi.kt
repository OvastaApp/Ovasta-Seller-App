package com.ovasta.sellers.data.remote

import com.ovasta.sellers.domain.model.ApiResponse
import com.ovasta.sellers.domain.model.DeliveryOrdersResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class OrderHistoryApiService(private val client: HttpClient) {
    suspend fun getLastOrders(page: Int? = null): ApiResponse<DeliveryOrdersResponse> {
        return client.get("delivery-orders") {
            parameter("current_orders", false)
            if (page != null) parameter("page", page)
        }.body()
    }
}
