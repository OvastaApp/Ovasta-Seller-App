package com.ovasta.sellers.presentation.profile.wallet.data

import com.google.gson.annotations.SerializedName

data class WalletTransactionsResponse(
    @SerializedName("wallet_balance") var walletBalance: Int? = null,
    @SerializedName("points") var points: Int? = null,
    @SerializedName("transactions") var transactions: List<WalletTransactions> = arrayListOf()
)

data class WalletTransactions(
    @SerializedName("id") var id: Int,
    @SerializedName("amount") var amount: Double? = null,
    @SerializedName("status") var status: Int,
    @SerializedName("rejection_reason") var rejectionReason: String? = null,
    @SerializedName("created_at") var createdAt: String? = null,
)