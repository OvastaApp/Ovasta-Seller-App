package com.ovasta.sellers.data.notification

import com.ovasta.sellers.data.ApiResponse

interface IFcmTokenRemoteDataSource {
    suspend fun updateFcmToken(token: String): ApiResponse<Unit>
}