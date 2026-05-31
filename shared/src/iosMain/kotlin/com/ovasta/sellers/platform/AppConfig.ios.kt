package com.ovasta.sellers.platform

actual val isDebug: Boolean = true

actual fun httpLog(message: String) {
    println("OvastaHttp: $message")
}
