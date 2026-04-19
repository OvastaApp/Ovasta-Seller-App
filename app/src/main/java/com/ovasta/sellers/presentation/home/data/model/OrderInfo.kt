package com.ovasta.sellers.presentation.home.data.model

import com.google.gson.annotations.SerializedName

data class OrderInfo(
    @SerializedName("id") var id: Long = 0,
    @SerializedName("client_name") var clientName: String? = null,
    @SerializedName("client_address") var clientAddress: String,
    @SerializedName("client_phone") var clientPhone: String,
    @SerializedName("courier") var courier: Courier

)

data class Courier(
    @SerializedName("id") var id: Long = 0,
    @SerializedName("name") var name: String? = null,
    @SerializedName("phone") var phone: String? = null
)