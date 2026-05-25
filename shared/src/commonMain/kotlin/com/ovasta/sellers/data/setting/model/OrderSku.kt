package com.ovasta.sellers.data.setting.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class OrderSku(
    @JsonNames("unit") val unit: String,
    @JsonNames("name") val name: String,
    @JsonNames("price") val price: Double,
    @JsonNames("quantity") val quantity: Int,
    @JsonNames("image") val image: String
)
