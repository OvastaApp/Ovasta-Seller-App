package com.ovasta.sellers.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.request.get

class SettingsApiService(private val client: HttpClient) {
    suspend fun logout() {
        client.get("logout")
    }
}
