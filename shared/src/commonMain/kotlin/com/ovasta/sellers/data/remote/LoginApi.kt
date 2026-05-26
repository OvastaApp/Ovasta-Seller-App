package com.ovasta.sellers.data.remote

import com.ovasta.sellers.domain.model.ApiResponse
import com.ovasta.sellers.domain.model.LoginRequest
import com.ovasta.sellers.domain.model.User
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class LoginApiService(private val client: HttpClient) {
    suspend fun login(request: LoginRequest): ApiResponse<User> {
        return client.post("login") {
            setBody(request)
        }.body()
    }
}
