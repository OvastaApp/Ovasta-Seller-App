package com.ovasta.sellers.presentation.auth.login.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class LoginRequest(
    @JsonNames("mobile") val mobile: String,
    @JsonNames("password") val password: String,
    @JsonNames("fcm_token") val fcmToken: String? = null,
)