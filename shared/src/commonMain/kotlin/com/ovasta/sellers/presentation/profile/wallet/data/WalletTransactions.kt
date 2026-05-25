package com.ovasta.sellers.presentation.profile.wallet.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class WalletResponse(
    @JsonNames("wallet_balance") var walletBalance: Double? = null,
    @JsonNames("points") var points: Double? = null,
    @JsonNames("transactions") var pointsHistory: List<PointsHistory> = emptyList()
)

@Serializable
data class PointsHistory(
    @JsonNames("id") var id: Int,
    @JsonNames("amount") var amount: Double? = null,
    @JsonNames("rejection_reason") var rejectionReason: String? = null,
    @JsonNames("description") var description: String? = null,
    @JsonNames("created_at") var createdAt: String? = null,
)