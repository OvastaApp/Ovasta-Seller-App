package com.ovasta.sellers.base.exception

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.IOException

@Keep
class APIException(
    @Expose var code: Int?,
    @SerializedName("msg", alternate = ["message"]) val errorMessage: String,
    @SerializedName("data") val data: Any? = null
) : IOException()
