package com.ovasta.sellers.data.remote

object ApiConstants {
    const val BASE_URL = "http://167.172.209.252/api/seller-app/"

    /** Host root used to resolve relative image paths returned by the API. */
    const val IMAGE_BASE_URL = "http://167.172.209.252/"
    const val CONNECT_TIMEOUT_MS = 30_000L
    const val READ_TIMEOUT_MS = 30_000L

    object Headers {
        const val ACCEPT = "Accept"
        const val IDENTIFIER = "identifier"
        const val DEVICE_ID = "device_id"
        const val AUTHORIZATION = "Authorization"
        const val LANG = "lang"
    }
}
