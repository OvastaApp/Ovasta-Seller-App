package com.ovasta.sellers.presentation.profile.wallet.data.model

import com.google.gson.annotations.SerializedName

data class RedeemPointsRequest(
    @SerializedName("points") val points: Int
)