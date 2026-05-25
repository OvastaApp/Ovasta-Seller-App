package com.ovasta.sellers.base.encryption

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.usePinned
import platform.CommonCrypto.CCCrypt
import platform.CommonCrypto.kCCAlgorithmAES128
import platform.CommonCrypto.kCCDecrypt
import platform.CommonCrypto.kCCEncrypt
import platform.CommonCrypto.kCCOptionPKCS7Padding
import platform.Foundation.NSData
import platform.Security.KSecAttrAccount
import platform.Security.KSecAttrService
import platform.Security.KSecAttrAccessible
import platform.Security.KSecAttrAccessibleAfterFirstUnlockThisDeviceOnly
import platform.Security.KSecClass
import platform.Security.KSecClassGenericPassword
import platform.Security.KSecReturnData
import platform.Security.KSecValueData
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemUpdate
import platform.Security.kCFBooleanTrue
import platform.posix.ULongVar
import platform.posix.memcpy
import kotlin.random.Random

@OptIn(ExperimentalForeignApi::class)
actual object Crypto {
    private const val KEY_ALIAS = "com.ovasta.sellers.crypto_key"
    private const val SERVICE_NAME = "com.ovasta.sellers"

    private fun getOrCreateKey(): ByteArray {
        val existing = getKeyFromKeychain()
        if (existing != null) return existing

        val newKey = Random.nextBytes(32)
        saveKeyToKeychain(newKey)
        return newKey
    }

    private fun getKeyFromKeychain(): ByteArray? {
        val query = mapOf<Any?, Any?>(
            KSecClass to KSecClassGenericPassword,
            KSecAttrService to SERVICE_NAME,
            KSecAttrAccount to KEY_ALIAS,
            KSecReturnData to kCFBooleanTrue
        )

        var result: AnyObject? = null
        val status = SecItemCopyMatching(query, result)

        if (status != 0L || result == null) return null

        val nsData = result as NSData
        return nsData.toByteArray()
    }

    private fun saveKeyToKeychain(key: ByteArray) {
        val nsData = key.toNSData()

        val existing = getKeyFromKeychain()
        if (existing != null) {
            val update = mapOf<Any?, Any?>(
                KSecValueData to nsData,
                KSecAttrAccessible to kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly
            )
            SecItemUpdate(
                mapOf(
                    KSecClass to KSecClassGenericPassword,
                    KSecAttrService to SERVICE_NAME,
                    KSecAttrAccount to KEY_ALIAS
                ),
                update
            )
        } else {
            val attributes = mapOf<Any?, Any?>(
                KSecClass to KSecClassGenericPassword,
                KSecAttrService to SERVICE_NAME,
                KSecAttrAccount to KEY_ALIAS,
                KSecValueData to nsData,
                KSecAttrAccessible to kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly
            )
            SecItemAdd(attributes, null)
        }
    }

    actual fun encrypt(bytes: ByteArray): ByteArray {
        val key = getOrCreateKey()
        val iv = Random.nextBytes(16)
        val encrypted = aesCbc(bytes, key, iv, kCCEncrypt)
        return iv + encrypted
    }

    actual fun decrypt(bytes: ByteArray): ByteArray {
        val key = getOrCreateKey()
        val iv = bytes.copyOfRange(0, 16)
        val data = bytes.copyOfRange(16, bytes.size)
        return aesCbc(data, key, iv, kCCDecrypt)
    }

    private fun aesCbc(input: ByteArray, key: ByteArray, iv: ByteArray, operation: ULong): ByteArray {
        val blockSize = 16
        val outputSize = input.size + blockSize

        return memScoped {
            val dataOut = ByteArray(outputSize)
            val dataOutMoved = alloc<ULongVar>()

            val status = CCCrypt(
                op = operation,
                alg = kCCAlgorithmAES128,
                options = kCCOptionPKCS7Padding,
                key = key.usePinned { pinned -> pinned.addressOf(0) },
                keyLength = key.size.toULong(),
                iv = iv.usePinned { pinned -> pinned.addressOf(0) },
                dataIn = input.usePinned { pinned -> pinned.addressOf(0) },
                dataInLength = input.size.toULong(),
                dataOut = dataOut.usePinned { pinned -> pinned.addressOf(0) },
                dataOutAvailable = dataOut.size.toULong(),
                dataOutMoved = dataOutMoved.ptr
            )

            if (status != 0) {
                throw SecurityException("CCCrypt failed with status: $status")
            }
            dataOut.copyOfRange(0, dataOutMoved.value.toInt())
        }
    }

    private fun ByteArray.toNSData(): NSData {
        return usePinned { pinned ->
            NSData(bytes = pinned.addressOf(0), length = this.size.toULong())
        }
    }

    private fun NSData.toByteArray(): ByteArray {
        val length = this.length.toInt()
        val bytes = ByteArray(length)
        if (length > 0) {
            memcpy(bytes.usePinned { it.addressOf(0) }, this.bytes, length.toULong())
        }
        return bytes
    }
}
