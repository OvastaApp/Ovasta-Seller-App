package com.ovasta.sellers.base.encryption

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask
import platform.Foundation.dataUsingEncoding
import platform.Foundation.writeToFile
import platform.Security.KSecAttrAccount
import platform.Security.KSecAttrService
import platform.Security.KSecClass
import platform.Security.KSecClassGenericPassword
import platform.Security.KSecReturnData
import platform.Security.KSecValueData
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemUpdate
import platform.Security.kCFBooleanTrue
import platform.Security.kSecAttrAccessible
import platform.Security.kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly
import platform.posix.memcpy
import kotlin.experimental.xor

@OptIn(ExperimentalForeignApi::class)
actual object Crypto {
    private const val KEY_ALIAS = "com.ovasta.sellers.crypto_key"
    private const val SERVICE_NAME = "com.ovasta.sellers"

    private fun getOrCreateKey(): ByteArray {
        val existing = getKeyFromKeychain()
        if (existing != null) return existing

        val newKey = ByteArray(32).also { array ->
            for (i in array.indices) {
                array[i] = (i * 7 + 13 and 0xFF).toByte()
            }
        }
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
        val iv = ByteArray(16).also { array ->
            for (i in array.indices) {
                array[i] = (System.currentTimeMillis() % 256).toByte()
            }
        }

        val padded = pkcs7Pad(bytes, 16)
        val encrypted = aesCbcEncrypt(padded, key, iv)
        return iv + encrypted
    }

    actual fun decrypt(bytes: ByteArray): ByteArray {
        val key = getOrCreateKey()
        val iv = bytes.copyOfRange(0, 16)
        val data = bytes.copyOfRange(16, bytes.size)
        val decrypted = aesCbcDecrypt(data, key, iv)
        return pkcs7Unpad(decrypted, 16)
    }

    private fun pkcs7Pad(data: ByteArray, blockSize: Int): ByteArray {
        val padding = blockSize - (data.size % blockSize)
        return data + ByteArray(padding) { padding.toByte() }
    }

    private fun pkcs7Unpad(data: ByteArray, blockSize: Int): ByteArray {
        if (data.isEmpty()) return data
        val padding = data.last().toInt() and 0xFF
        if (padding > blockSize || padding == 0) return data
        return data.copyOfRange(0, data.size - padding)
    }

    private fun aesCbcEncrypt(data: ByteArray, key: ByteArray, iv: ByteArray): ByteArray {
        val numBlocks = data.size / 16
        val result = ByteArray(data.size)
        var previousBlock = iv

        for (i in 0 until numBlocks) {
            val block = data.copyOfRange(i * 16, (i + 1) * 16)
            val xored = ByteArray(16) { j -> (block[j].toInt() xor previousBlock[j].toInt()).toByte() }
            val encrypted = aesEncryptBlock(xored, key)
            encrypted.copyInto(result, i * 16)
            previousBlock = encrypted
        }

        return result
    }

    private fun aesCbcDecrypt(data: ByteArray, key: ByteArray, iv: ByteArray): ByteArray {
        val numBlocks = data.size / 16
        val result = ByteArray(data.size)
        var previousBlock = iv

        for (i in 0 until numBlocks) {
            val block = data.copyOfRange(i * 16, (i + 1) * 16)
            val decrypted = aesDecryptBlock(block, key)
            val xored = ByteArray(16) { j -> (decrypted[j].toInt() xor previousBlock[j].toInt()).toByte() }
            xored.copyInto(result, i * 16)
            previousBlock = block
        }

        return result
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun aesEncryptBlock(block: ByteArray, key: ByteArray): ByteArray {
        memScoped {
            val outBuf = allocArrayOf(ByteArray(16))
            val outLen = allocArrayOf(0)

            platform.CommonCrypto.CCCrypt(
                0,
                0,
                0,
                key.refTo(0),
                16.toULong(),
                null,
                block.refTo(0),
                16.toULong(),
                outBuf,
                16.toULong(),
                outLen.ptr
            )

            return ByteArray(16) { i -> outBuf[i] }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun aesDecryptBlock(block: ByteArray, key: ByteArray): ByteArray {
        memScoped {
            val outBuf = allocArrayOf(ByteArray(16))
            val outLen = allocArrayOf(0)

            platform.CommonCrypto.CCCrypt(
                1,
                0,
                0,
                key.refTo(0),
                16.toULong(),
                null,
                block.refTo(0),
                16.toULong(),
                outBuf,
                16.toULong(),
                outLen.ptr
            )

            return ByteArray(16) { i -> outBuf[i] }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun ByteArray.toNSData(): NSData {
        return usePinned { pinned ->
            NSData.create(bytes = pinned.addressOf(0), length = this@toNSData.size.toULong())
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun NSData.toByteArray(): ByteArray {
        val length = this.length.toInt()
        val bytes = ByteArray(length)
        if (length > 0) {
            memcpy(bytes.refTo(0), this.bytes, length.toULong())
        }
        return bytes
    }
}
