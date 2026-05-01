package com.ovasta.sellers.presentation.profile.wallet.data

import com.google.gson.annotations.SerializedName

data class WalletTransactionsResponse(
    @SerializedName("wallet_balance") var walletBalance: Int? = null,
    @SerializedName("points") var points: Int? = null,
    @SerializedName("transactions") var transactions: ArrayList<WalletTransactions> = arrayListOf()
)

data class WalletTransactions(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("seller_id") var sellerId: Int? = null,
    @SerializedName("amount") var amount: Int? = null,
    @SerializedName("status") var status: Int? = null,
    @SerializedName("processed_by") var processedBy: String? = null,
    @SerializedName("processed_at") var processedAt: String? = null,
    @SerializedName("rejection_reason") var rejectionReason: String? = null,
    @SerializedName("payment_transfer_proof") var paymentTransferProof: String? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null

)