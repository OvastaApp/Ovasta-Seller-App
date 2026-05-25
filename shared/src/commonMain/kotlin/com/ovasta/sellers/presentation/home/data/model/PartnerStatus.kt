package com.ovasta.sellers.presentation.home.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class PartnerStatus(
    @JsonNames("status") val isOnline: Boolean,
)
