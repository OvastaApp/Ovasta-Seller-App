package com.ovasta.sellers.presentation.profile.wallet.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class WithdrawRequests(
    @JsonNames("id") var id: Int,
    @JsonNames("amount") var amount: Double? = null,
    @JsonNames("status") var status: Int,
    @JsonNames("rejection_reason") var rejectionReason: String? = null,
    @JsonNames("created_at") var createdAt: String? = null,
)