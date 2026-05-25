package com.ovasta.sellers.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class User(
    @JsonNames("id") val id: Int,
    @JsonNames("seller_id") val deliveryId: Int,
    @JsonNames("district_id") val districtId: Int,
    @JsonNames("name") val name: String? = null,
    @JsonNames("email") val email: String? = null,
    @JsonNames("mobile") val mobile: String? = null,
    @JsonNames("type_id") var userTypeId: Int? = null,
    var token: String? = null,
)
