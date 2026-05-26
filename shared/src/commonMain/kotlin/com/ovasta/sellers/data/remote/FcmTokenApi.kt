package com.ovasta.sellers.data.remote

import com.ovasta.sellers.domain.model.FcmTokenRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class FcmTokenApiService(private val client: HttpClient) {
    suspend fun updateFcmToken(request: FcmTokenRequest) {
        client.post("fcm-token") {
            setBody(request)
        }
    }
}
