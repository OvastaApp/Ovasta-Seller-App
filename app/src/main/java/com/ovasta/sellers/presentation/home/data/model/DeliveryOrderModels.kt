package com.ovasta.sellers.presentation.home.data.model

import com.google.gson.annotations.SerializedName

data class DeliveryOrdersResponse(
    @SerializedName("current_page") val currentPage: Int,
    @SerializedName("data") val orders: List<DeliveryOrder>,
    @SerializedName("per_page") val perPage: Int,
    @SerializedName("total") val total: Int,
    @SerializedName("last_page") val lastPage: Int
)

data class DeliveryOrder(
    val id: Int,
    @SerializedName("to_address") val toAddress: String,
    @SerializedName("receiver_mobile") val receiverMobile: String,
    @SerializedName("delivery_price") val deliveryPrice: Double,
    @SerializedName("collection_amount") val collectionAmount: Double,
    @SerializedName("status_id") val statusId: Int,
    @SerializedName("delivered_at") val deliveredAt: String?,
    @SerializedName("cashback_awarded") val cashbackAwarded: Boolean,
    @SerializedName("note") val note: String?,
    @SerializedName("total_price") val totalPrice: Double,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("courier") val courier: CourierInfo?,
    @SerializedName("can_cancel") val canCancelOrder: Boolean?=true
)

data class CourierInfo(
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("mobile") val mobile: String
)

