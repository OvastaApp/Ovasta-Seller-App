package com.ovasta.sellers.presentation.home.data

import com.ovasta.sellers.data.ApiResponse
import com.ovasta.sellers.presentation.home.data.model.HomeInfo
import com.ovasta.sellers.presentation.home.data.model.OrderInfo
import retrofit2.http.GET
import retrofit2.http.POST

interface HomeApi {
    @POST("orders")
    suspend fun getMyOrders(): ApiResponse<List<OrderInfo>>

    @GET("home")
    suspend fun getHome(): ApiResponse<HomeInfo>
}