package com.ovasta.sellers.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateOrderRequest(
    @SerialName("to_address") val destination: String,
    @SerialName("receiver_mobile") val clientPhone: String,
    @SerialName("collection_amount") val collectionAmount: Double,
    @SerialName("delivery_price") val deliveryFees: Double,
    @SerialName("note") val note: String = "",
)
