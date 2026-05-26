package com.ovasta.sellers.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HomeInfo(
    @SerialName("wallet_balance") val walletBalance: Double? = null,
    @SerialName("points") val pointsCount: Double? = null,
    @SerialName("points_per_pound") val pointsPerPound: Double? = null,
    @SerialName("min_redeem_points") val minRedeemPoints: Double? = null,
    @SerialName("min_order_delivery_price") val minOrderDeliveryPrice: Double? = null,
)
