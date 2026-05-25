package com.ovasta.sellers.data

object RemoteConstants {
    const val BASE_URL = "http://167.172.209.252/api/seller-app/"
    const val CONNECT_TIMEOUT: Long = 30
    const val READ_TIMEOUT: Long = 30
    const val WRITE_TIMEOUT: Long = 30
    const val TIME_OUT_STATUS_CODE = -1
    const val LOST_CONNECTION_STATUS_CODE = -2
    const val SERVER_ERROR = 500
    const val UNAUTHORIZED_CODE = 401
    const val CACHE_NAME = "maxman_http_cache"
    const val CACHE_SIZE = 50 * 1024 * 1024L
    const val IDENTIFIER_HASH = "\$2a\$12\$BeuZVyrk1vlnlws5ljkRnuHA5UypUwVW3gyGoFaGvpdF5sgeSzXr2"

    object HeadersConst {
        const val VERSION = "Version"
        const val DEVICE_MODEL = "device_model"
        const val ANDROID_VERSION = "android_version"
        const val ACCEPT = "Accept"
        const val IDENTIFIER = "identifier"
        const val DEVICE_ID = "device-id"
        const val AUTHORIZATION = "Authorization"
        const val LANG = "lang"
    }
}
