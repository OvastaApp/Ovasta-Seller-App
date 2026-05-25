package com.ovasta.sellers.presentation.profile.wallet.data

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class WalletResponse(
    @SerializedName("wallet_balance") var walletBalance: Double? = null,
    @SerializedName("points") var points: Double? = null,
    @SerializedName("transactions") var pointsHistory: List<PointsHistory> = arrayListOf()
)

@Keep
data class PointsHistory(
    @SerializedName("id") var id: Int,
    @SerializedName("amount") var amount: Double? = null,
    @SerializedName("rejection_reason") var rejectionReason: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("created_at") var createdAt: String? = null,
)