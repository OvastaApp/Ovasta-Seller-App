package com.ovasta.sellers.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    @SerialName("status") val status: Int? = null,
    @SerialName("message") val message: String = "",
    @SerialName("data") val data: T? = null,
    @SerialName("token") val token: String = "",
)
