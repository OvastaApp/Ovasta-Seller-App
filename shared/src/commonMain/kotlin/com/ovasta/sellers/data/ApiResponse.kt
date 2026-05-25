package com.ovasta.sellers.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class ApiResponse<T>(
    @JsonNames("status")
    val status: Int,
    @JsonNames("message")
    val message: String,
    @JsonNames("data")
    val data: T,
    @JsonNames("token")
    val token: String,
) {
    override fun toString(): String {
        return "ApiResponse(success=$status, message=$message, data=$data)"
    }
}
