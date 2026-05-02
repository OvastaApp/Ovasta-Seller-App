package com.ovasta.sellers.presentation.profile.wallet.data.model

import com.google.gson.annotations.SerializedName

data class WithdrawRequest(
    @SerializedName("amount") val amount: Double
)