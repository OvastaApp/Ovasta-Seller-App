package com.ovasta.sellers.presentation.home.data

import com.ovasta.sellers.data.ApiResponse
import com.ovasta.sellers.presentation.home.data.model.CreateOrderRequest
import com.ovasta.sellers.presentation.home.data.model.OrderResponse
import com.ovasta.sellers.presentation.home.data.model.PointsInfo
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface HomeApi {
    @POST("create-order")
    suspend fun createOrder(@Body createOrderRequest: CreateOrderRequest)

    @POST("orders")
    suspend fun getMyOrders(): ApiResponse<List<OrderResponse>>

    @GET("points")
    suspend fun getPointsInfo(): ApiResponse<PointsInfo>
}