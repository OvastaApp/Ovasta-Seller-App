package com.ovasta.sellers.presentation.home.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class HomeInfo(
    @JsonNames("wallet_balance") var walletBalance: Double? = null,
    @JsonNames("points") var pointsCount: Double? = null,
    @JsonNames("points_per_pound") var pointsPerPound: Double? = null,
    @JsonNames("min_redeem_points") var minRedeemPoints: Double? = null,
    @JsonNames("min_order_delivery_price") var minOrderDeliveryPrice: Double? = null,
)
