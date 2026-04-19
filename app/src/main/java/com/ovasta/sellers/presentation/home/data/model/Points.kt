package com.ovasta.sellers.presentation.home.data.model

import com.google.gson.annotations.SerializedName

data class PointsInfo(
    @SerializedName("points") var points: Long? = null,
    @SerializedName("money") var deliveryProfitSum: Double? = null,
    @SerializedName("points_rate") var pointsRate: Double? = null,
)