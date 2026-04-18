package com.ovasta.sellers.presentation.home.data.model

import com.google.gson.annotations.SerializedName

data class PartnerStatus(
    @SerializedName("status") val isOnline: Boolean,
)