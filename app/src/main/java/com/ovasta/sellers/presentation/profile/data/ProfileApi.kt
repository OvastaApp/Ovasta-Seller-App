package com.ovasta.sellers.presentation.profile.data

import com.ovasta.sellers.data.ApiResponse
import com.ovasta.sellers.presentation.home.data.model.DeliveryOrdersResponse
import com.ovasta.sellers.presentation.home.data.model.HomeInfo
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProfileApi {
    @GET("delivery-orders")
    suspend fun getCurrentOrders(
        @Query("Current_orders") isCurrentOrders: Boolean? = true,
        @Query("page") page: Int? = null
    ): ApiResponse<DeliveryOrdersResponse>

    @GET("home")
    suspend fun getHome(): ApiResponse<HomeInfo>


    @GET("cancel-order/{order_id}")
    suspend fun cancelOrder(@Path("order_id") orderId: Int)
}