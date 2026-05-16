package com.ovasta.sellers.data

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class ApiResponse<T>(
    @SerializedName("status")
    @Expose
    var status: Int,
    @SerializedName("message")
    @Expose
    var message: String,
    @SerializedName("data")
    @Expose
    var data: T,
    @SerializedName("token")
    val token: String,
) {
    override fun toString(): String {
        return "ApiResponse(success=$status, message=$message, data=$data)"
    }
}
