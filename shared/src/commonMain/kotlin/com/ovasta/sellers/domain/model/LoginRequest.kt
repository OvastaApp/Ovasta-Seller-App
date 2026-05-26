package com.ovasta.sellers.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    @SerialName("mobile") val mobile: String,
    @SerialName("password") val password: String,
    @SerialName("fcm_token") val fcmToken: String? = null,
)
