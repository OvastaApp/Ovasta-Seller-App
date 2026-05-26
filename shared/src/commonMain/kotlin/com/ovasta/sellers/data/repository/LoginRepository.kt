package com.ovasta.sellers.data.repository

import com.ovasta.sellers.data.remote.LoginApiService
import com.ovasta.sellers.domain.model.ApiResponse
import com.ovasta.sellers.domain.model.LoginRequest
import com.ovasta.sellers.domain.model.User
import com.ovasta.sellers.domain.repository.ILoginRepository

class LoginRepository(private val api: LoginApiService) : ILoginRepository {
    override suspend fun login(phone: String, password: String, userType: Int, fcmToken: String?): ApiResponse<User> {
        return api.login(LoginRequest(mobile = phone, password = password, fcmToken = fcmToken))
    }
}
