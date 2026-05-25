package com.ovasta.sellers.presentation.auth.login.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class LoginRequest(
    @SerializedName("mobile") val mobile: String,
    @SerializedName("password") val password: String,
    @SerializedName("fcm_token") val fcmToken: String? = null,
)