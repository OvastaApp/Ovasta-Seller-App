package com.ovasta.sellers.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WalletResponse(
    @SerialName("wallet_balance") val walletBalance: Double? = null,
    @SerialName("points") val points: Double? = null,
    @SerialName("transactions") val pointsHistory: List<PointsHistory> = emptyList(),
)

@Serializable
data class PointsHistory(
    @SerialName("id") val id: Int,
    @SerialName("amount") val amount: Double? = null,
    @SerialName("rejection_reason") val rejectionReason: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
)

@Serializable
data class WithdrawRequests(
    @SerialName("id") val id: Int,
    @SerialName("amount") val amount: Double? = null,
    @SerialName("status") val status: Int = 0,
    @SerialName("rejection_reason") val rejectionReason: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
)

@Serializable
data class RedeemPointsRequest(
    @SerialName("points") val points: Int,
)

@Serializable
data class WithdrawRequest(
    @SerialName("amount") val amount: Double,
)
