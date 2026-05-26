package com.ovasta.sellers.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("id") val id: Int,
    @SerialName("seller_id") val deliveryId: Int = 0,
    @SerialName("district_id") val districtId: Int = 0,
    @SerialName("name") val name: String? = null,
    @SerialName("email") val email: String? = null,
    @SerialName("mobile") val mobile: String? = null,
    @SerialName("type_id") val userTypeId: Int? = null,
    var token: String? = null,
)
