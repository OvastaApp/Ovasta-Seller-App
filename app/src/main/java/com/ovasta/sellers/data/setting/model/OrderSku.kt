package com.ovasta.sellers.data.setting.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class OrderSku(
    @SerializedName("unit") val unit: String,
    @SerializedName("name") val name: String,
    @SerializedName("price") val price: Double,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("image") val image: String
)
