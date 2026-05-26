package com.ovasta.sellers.data.notification

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class FcmTokenRequest(
    @SerializedName("fcm_token") val fcmToken: String
)