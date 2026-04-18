package com.ovasta.sellers.presentation.auth.login.data

import com.ovasta.sellers.data.ApiResponse
import com.ovasta.sellers.data.User
import com.ovasta.sellers.presentation.auth.login.data.model.LoginRequest
import retrofit2.http.*

interface LoginApi {
    @POST("login")
    suspend fun login(@Body login: LoginRequest): ApiResponse<User>
}