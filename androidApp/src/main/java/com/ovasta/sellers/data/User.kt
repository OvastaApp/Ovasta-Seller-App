package com.ovasta.sellers.data

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Keep
@Serializable
class User(
    @SerializedName("id") val id: Int,
    @SerializedName("seller_id") val deliveryId: Int,
    @SerializedName("district_id") val districtId: Int,
    @SerializedName("name") val name: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("mobile") val mobile: String?,
    @SerializedName("type_id") var userTypeId: Int?,
    var token: String?,
)

