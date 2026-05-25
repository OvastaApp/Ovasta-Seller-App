package com.ovasta.sellers.data.setting.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class Orders(
    @JsonNames("id") val id: Int,
    @JsonNames("date") val date: String,
    @JsonNames("total_price") val totalPrice: Double,
    @JsonNames("discount") val discount: Double,
    @JsonNames("coupons") val coupons: Double,
    @JsonNames("weight") val weight: Float,
    @JsonNames("partner_name") val partnerName: String,
    @JsonNames("status_id") val statusId: Int,
    @JsonNames("status_name") val statusName: String,
    @JsonNames("price") val oldPrice: Double = 0.0,
)

@Serializable
data class OrderStatus(
    @JsonNames("status") val status: String,
    @JsonNames("id") val id: Int
)
