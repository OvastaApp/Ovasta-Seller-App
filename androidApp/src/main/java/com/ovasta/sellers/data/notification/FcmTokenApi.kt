package com.ovasta.sellers.data.notification

import com.ovasta.sellers.domain.model.ApiResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface FcmTokenApi {
    @POST("fcm-token")
    suspend fun updateFcmToken(@Body request: FcmTokenRequest): ApiResponse<Unit>
}