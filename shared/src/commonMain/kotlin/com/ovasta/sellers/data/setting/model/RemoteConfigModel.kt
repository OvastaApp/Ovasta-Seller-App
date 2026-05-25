package com.ovasta.sellers.data.setting.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class RemoteConfigModel(
    @JsonNames("force_update_message")
    val forceUpdateMessage: String = "",
    @JsonNames("force_update_title")
    val forceUpdateTitle: String = "",
    @JsonNames("force_version")
    val forceVersion: String = "",
    @JsonNames("force_version_sandbox")
    val forceVersionSandbox: String = "",
    @JsonNames("update")
    val update: String = "",
    @JsonNames("update_sandBox")
    val updateSandBox: String = "",
    @JsonNames("COLLECTION_AMOUNT_UP_TO")
    val collectionAmountUpTo: String = "1"
)
