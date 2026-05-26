package com.ovasta.sellers.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteConfigModel(
    @SerialName("force_update_message") val forceUpdateMessage: String = "",
    @SerialName("force_update_title") val forceUpdateTitle: String = "",
    @SerialName("force_version") val forceVersion: String = "",
    @SerialName("force_version_sandbox") val forceVersionSandbox: String = "",
    @SerialName("update") val update: String = "",
    @SerialName("update_sandBox") val updateSandBox: String = "",
    @SerialName("COLLECTION_AMOUNT_UP_TO") val collectionAmountUpTo: String = "1",
)
