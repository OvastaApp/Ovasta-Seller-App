package com.ovasta.sellers.data.notification

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class FcmTokenRequest(
    @JsonNames("fcm_token") val fcmToken: String
)