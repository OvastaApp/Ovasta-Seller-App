package com.ovasta.sellers.presentation.home.data.model

import com.google.gson.annotations.SerializedName

data class HomeInfo(
    @SerializedName("wallet_balance") var walletBalance: Double? = null,
    @SerializedName("points") var pointsCount: Double? = null,
    @SerializedName("points_per_pound") var pointsPerPound: Double? = null,
    @SerializedName("min_redeem_points") var minRedeemPoints: Double? = null,
)