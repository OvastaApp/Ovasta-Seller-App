package com.ovasta.sellers.presentation.profile.orderhistory.data

import com.ovasta.sellers.data.ApiResponse
import com.ovasta.sellers.presentation.home.data.model.DeliveryOrdersResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OrderHistoryApi {
    @GET("delivery-orders")
    suspend fun getLastOrders(
        @Query("current_orders") isCurrentOrders: Boolean? = false,
        @Query("page") page: Int? = null
    ): ApiResponse<DeliveryOrdersResponse>
}