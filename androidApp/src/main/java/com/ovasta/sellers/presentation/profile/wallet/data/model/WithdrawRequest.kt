package com.ovasta.sellers.presentation.profile.wallet.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class WithdrawRequest(
    @SerializedName("amount") val amount: Double
)