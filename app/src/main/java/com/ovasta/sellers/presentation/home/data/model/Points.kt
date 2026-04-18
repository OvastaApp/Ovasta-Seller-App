package com.ovasta.sellers.presentation.home.data.model

import com.google.gson.annotations.SerializedName

data class PointsInfo(
    @SerializedName("points") var points: Long? = null,
    @SerializedName("money") var deliveryProfitSum: Double? = null,
)