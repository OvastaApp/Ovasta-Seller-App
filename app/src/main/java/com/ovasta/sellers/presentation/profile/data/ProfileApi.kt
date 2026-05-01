package com.ovasta.sellers.presentation.profile.data

import com.ovasta.sellers.data.ApiResponse
import com.ovasta.sellers.presentation.home.data.model.DeliveryOrdersResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ProfileApi {
    @GET("delivery-orders")
    suspend fun getLastOrders(
        @Query("Current_orders") isCurrentOrders: Boolean? = false,
        @Query("page") page: Int? = null
    ): ApiResponse<DeliveryOrdersResponse>
}