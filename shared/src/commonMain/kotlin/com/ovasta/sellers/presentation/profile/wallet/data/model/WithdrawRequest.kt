package com.ovasta.sellers.presentation.profile.wallet.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class WithdrawRequest(
    @JsonNames("amount") val amount: Double
)