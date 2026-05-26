package com.ovasta.sellers.data.remote

import com.ovasta.sellers.domain.model.ApiResponse
import com.ovasta.sellers.domain.model.DeliveryOrdersResponse
import com.ovasta.sellers.domain.model.HomeInfo
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post

class HomeApiService(private val client: HttpClient) {
    suspend fun getHome(): ApiResponse<HomeInfo> {
        return client.get("home").body()
    }

    suspend fun getCurrentOrders(currentOrders: Boolean = true, page: Int? = null): ApiResponse<DeliveryOrdersResponse> {
        return client.get("delivery-orders") {
            parameter("current_orders", currentOrders)
            if (page != null) parameter("page", page)
        }.body()
    }

    suspend fun cancelOrder(orderId: Int) {
        client.post("delivery-orders/$orderId/cancel")
    }
}
