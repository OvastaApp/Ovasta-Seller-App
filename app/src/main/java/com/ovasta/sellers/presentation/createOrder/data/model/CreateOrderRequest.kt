package com.ovasta.sellers.presentation.createOrder.data.model

import com.google.gson.annotations.SerializedName

data class CreateOrderRequest(
    @SerializedName("to_address") val destination: String,
    @SerializedName("receiver_mobile") val clientPhone: String,
    @SerializedName("client_name") val clientName: String,
    @SerializedName("collection_amount") val collectionAmount: Double,
    @SerializedName("delivery_price") val deliveryFees: Double,
    @SerializedName("note") val note: String
)