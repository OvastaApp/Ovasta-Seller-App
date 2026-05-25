package com.ovasta.sellers.base.encryption

expect object Crypto {
    fun encrypt(bytes: ByteArray): ByteArray
    fun decrypt(bytes: ByteArray): ByteArray
}
