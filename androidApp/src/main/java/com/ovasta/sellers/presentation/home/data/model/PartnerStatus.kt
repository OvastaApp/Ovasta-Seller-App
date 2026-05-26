package com.ovasta.sellers.presentation.home.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class PartnerStatus(
    @SerializedName("status") val isOnline: Boolean,
)