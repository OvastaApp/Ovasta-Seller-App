package com.ovasta.sellers.presentation.home.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class DeliveryOrdersResponse(
    @JsonNames("current_page") val currentPage: Int,
    @JsonNames("data") val orders: List<DeliveryOrder>,
    @JsonNames("per_page") val perPage: Int,
    @JsonNames("total") val total: Int,
    @JsonNames("last_page") val lastPage: Int
)

@Serializable
data class DeliveryOrder(
    @JsonNames("id") val id: Int,
    @JsonNames("to_address") val toAddress: String,
    @JsonNames("receiver_mobile") val receiverMobile: String,
    @JsonNames("delivery_price") val deliveryPrice: Double,
    @JsonNames("collection_amount") val collectionAmount: Double,
    @JsonNames("status_id") val statusId: Int,
    @JsonNames("delivered_at") val deliveredAt: String? = null,
    @JsonNames("cashback_awarded") val cashbackAwarded: Boolean,
    @JsonNames("note") val note: String? = null,
    @JsonNames("total_price") val totalPrice: Double,
    @JsonNames("created_at") val createdAt: String,
    @JsonNames("courier") val courier: CourierInfo? = null,
    @JsonNames("can_cancel") val canCancelOrder: Boolean = true
)

@Serializable
data class CourierInfo(
    @JsonNames("first_name") val firstName: String,
    @JsonNames("last_name") val lastName: String,
    @JsonNames("mobile") val mobile: String
)
