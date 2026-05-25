package com.ovasta.sellers.presentation.auth.login.data

import com.ovasta.sellers.data.ApiResponse
import com.ovasta.sellers.data.User
import com.ovasta.sellers.data.network.SellerApiService
import com.ovasta.sellers.presentation.auth.login.data.model.LoginRequest

class LoginRemoteDataSource(private val apiService: SellerApiService) : ILoginRemoteDataSource {
    override suspend fun login(phone: String, password: String, userType: Int, fcmToken: String?): ApiResponse<User> {
        return apiService.login(LoginRequest(mobile = phone, password = password, fcmToken = fcmToken))
    }
}