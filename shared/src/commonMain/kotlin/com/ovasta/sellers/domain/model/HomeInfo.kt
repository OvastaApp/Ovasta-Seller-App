package com.ovasta.sellers.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HomeInfo(
    @SerialName("wallet_balance") @Serializable(with = FlexibleDoubleNullableSerializer::class) val walletBalance: Double? = null,
    @SerialName("min_order_delivery_price") @Serializable(with = FlexibleDoubleNullableSerializer::class) val minOrderDeliveryPrice: Double? = null,
    @SerialName("can_update_products") val canUpdateProducts: Boolean? = false,
)
