package com.ovasta.sellers.presentation.createOrder.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class CreateOrderRequest(
    @JsonNames("to_address") val destination: String,
    @JsonNames("receiver_mobile") val clientPhone: String,
    @JsonNames("collection_amount") val collectionAmount: Double,
    @JsonNames("delivery_price") val deliveryFees: Double,
    @JsonNames("note") val note: String
)