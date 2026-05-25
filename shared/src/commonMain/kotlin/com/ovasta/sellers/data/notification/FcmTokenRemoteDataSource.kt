package com.ovasta.sellers.data.notification

import com.ovasta.sellers.data.ApiResponse
import com.ovasta.sellers.data.network.SellerApiService

class FcmTokenRemoteDataSource(private val apiService: SellerApiService) : IFcmTokenRemoteDataSource {
    override suspend fun updateFcmToken(token: String): ApiResponse<Unit> {
        return apiService.updateFcmToken(FcmTokenRequest(token))
    }
}