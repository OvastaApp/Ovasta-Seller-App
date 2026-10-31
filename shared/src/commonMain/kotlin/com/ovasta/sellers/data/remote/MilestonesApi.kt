package com.ovasta.sellers.data.remote

import com.ovasta.sellers.domain.model.ApiResponse
import com.ovasta.sellers.domain.model.MilestonesProgress
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class MilestonesApiService(private val client: HttpClient) {
    suspend fun getProgress(): ApiResponse<MilestonesProgress> {
        return client.get("milestones/progress").body()
    }
}
