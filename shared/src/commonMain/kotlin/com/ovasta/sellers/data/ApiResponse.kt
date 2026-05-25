package com.ovasta.sellers.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class ApiResponse<T>(
    @JsonNames("status")
    val status: Int? = null,
    @JsonNames("message")
    val message: String = "",
    @JsonNames("data")
    val data: T? = null,
    @JsonNames("token")
    val token: String? = null,
) {
    override fun toString(): String {
        return "ApiResponse(success=$status, message=$message, data=$data)"
    }
}
