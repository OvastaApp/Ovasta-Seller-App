package com.ovasta.sellers.domain.repository

import com.ovasta.sellers.domain.model.ApiResponse
import com.ovasta.sellers.domain.model.User

interface ILoginRepository {
    suspend fun login(phone: String, password: String, userType: Int, fcmToken: String?): ApiResponse<User>
}
