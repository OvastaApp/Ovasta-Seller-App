package com.ovasta.sellers.presentation.home.data

import com.ovasta.sellers.data.ApiResponse
import com.ovasta.sellers.presentation.home.data.model.ChangeStatusRequest
import com.ovasta.sellers.presentation.home.data.model.PartnerStatistics
import com.ovasta.sellers.presentation.home.data.model.PartnerStatus
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface HomeApi {
    @POST("change-partner-status")
    suspend fun changePartnerStatus(@Body changeStatusRequest: ChangeStatusRequest)

    @GET("get-partner-status")
    suspend fun getPartnerStatus(): ApiResponse<PartnerStatus>

    @GET("partner-statistics")
    suspend fun getPartnerStatistics(): ApiResponse<PartnerStatistics>
}