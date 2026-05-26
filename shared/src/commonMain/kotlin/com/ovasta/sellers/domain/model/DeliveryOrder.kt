package com.ovasta.sellers.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeliveryOrdersResponse(
    @SerialName("current_page") val currentPage: Int = 0,
    @SerialName("data") val orders: List<DeliveryOrder> = emptyList(),
    @SerialName("per_page") val perPage: Int = 0,
    @SerialName("total") val total: Int = 0,
    @SerialName("last_page") val lastPage: Int = 0,
)

@Serializable
data class DeliveryOrder(
    val id: Int,
    @SerialName("to_address") val toAddress: String = "",
    @SerialName("receiver_mobile") val receiverMobile: String = "",
    @SerialName("delivery_price") val deliveryPrice: Double = 0.0,
    @SerialName("collection_amount") val collectionAmount: Double = 0.0,
    @SerialName("status_id") val statusId: Int = 0,
    @SerialName("delivered_at") val deliveredAt: String? = null,
    @SerialName("cashback_awarded") val cashbackAwarded: Boolean = false,
    @SerialName("note") val note: String? = null,
    @SerialName("total_price") val totalPrice: Double = 0.0,
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("courier") val courier: CourierInfo? = null,
    @SerialName("can_cancel") val canCancelOrder: Boolean? = true,
)

@Serializable
data class CourierInfo(
    @SerialName("first_name") val firstName: String = "",
    @SerialName("last_name") val lastName: String = "",
    @SerialName("mobile") val mobile: String = "",
)
