package com.ovasta.sellers.presentation.profile.wallet.data

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class WithdrawRequests(
    @SerializedName("id") var id: Int,
    @SerializedName("amount") var amount: Double? = null,
    @SerializedName("status") var status: Int,
    @SerializedName("rejection_reason") var rejectionReason: String? = null,
    @SerializedName("created_at") var createdAt: String? = null,
)