package com.ovasta.sellers.base.exception

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
class APIException(
    @JsonNames("msg", "message") val errorMessage: String,
    var code: Int? = null,
    @kotlinx.serialization.Transient
    val data: Any? = null
) : Exception(errorMessage)