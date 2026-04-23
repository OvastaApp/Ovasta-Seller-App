package com.ovasta.sellers.presentation.createOrder.data.model

import com.google.gson.annotations.SerializedName

data class CreateOrderRequest(
    @SerializedName("destination") val destination: String,
    @SerializedName("client_phone") val clientPhone: String,
    @SerializedName("client_name") val clientName: String,
    @SerializedName("collection_amount") val collectionAmount: Double,
    @SerializedName("note") val note: String
)