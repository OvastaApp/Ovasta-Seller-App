package com.ovasta.sellers.presentation.home.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class HomeAssignedTask(
    @JsonNames("id")
    val id: Int = 1,
)
