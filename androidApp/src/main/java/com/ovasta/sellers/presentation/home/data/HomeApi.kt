package com.ovasta.sellers.presentation.home.data

import com.ovasta.sellers.data.ApiResponse
import com.ovasta.sellers.presentation.home.data.model.DeliveryOrdersResponse
import com.ovasta.sellers.presentation.home.data.model.HomeInfo
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface HomeApi {
    @GET("delivery-orders")
    suspend fun getCurrentOrders(
        @Query("current_orders") isCurrentOrders: Boolean? = true,
        @Query("page") page: Int? = null
    ): ApiResponse<DeliveryOrdersResponse>

    @GET("home")
    suspend fun getHome(): ApiResponse<HomeInfo>


    @POST("delivery-orders/{order_id}/cancel")
    suspend fun cancelOrder(@Path("order_id") orderId: Int)
}