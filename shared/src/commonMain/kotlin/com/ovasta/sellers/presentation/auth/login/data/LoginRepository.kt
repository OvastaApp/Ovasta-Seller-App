package com.ovasta.sellers.presentation.auth.login.data

import com.ovasta.sellers.data.ApiResponse
import com.ovasta.sellers.data.User

class LoginRepository(
    private val loginRemoteDataSource: ILoginRemoteDataSource
) : ILoginRepository {
    override suspend fun login(phone: String, password: String, usertype: Int, fcmToken: String?): ApiResponse<User> {
        return loginRemoteDataSource.login(phone, password, usertype, fcmToken)
    }
}