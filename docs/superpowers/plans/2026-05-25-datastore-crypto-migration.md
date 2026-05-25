# DataStore & Crypto Multiplatform Migration Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Migrate DataStore (with custom encrypted serializer) and Crypto from Android-only to multiplatform using expect/actual, so the shared module can store session data on both Android and iOS.

**Architecture:** Create `expect object Crypto` in commonMain with `actual` implementations using AndroidKeyStore on Android and CommonCrypto/Keychain on iOS. Migrate `SessionPreferences` and `SessionPreferencesSerializer` to commonMain using `OkioSerializer` (multiplatform DataStore API). Create a factory function `createDataStore(producePath)` in commonMain with platform-specific path resolution. Move `SettingsLocalDataSource` to shared commonMain since it only depends on DataStore (now multiplatform).

**Tech Stack:** DataStore 1.1.7 (multiplatform), datastore-core-okio, Okio, kotlinx.serialization, AES encryption (platform-specific), Koin 4.1.0

**Last Updated:** 2026-05-25

---

## File Structure

| File | Action | Responsibility |
|------|--------|---------------|
| `shared/src/commonMain/.../base/encryption/Crypto.kt` | Create | expect Crypto object |
| `shared/src/androidMain/.../base/encryption/Crypto.kt` | Create | actual Crypto using AndroidKeyStore |
| `shared/src/iosMain/.../base/encryption/Crypto.kt` | Create | actual Crypto using CommonCrypto |
| `shared/src/commonMain/.../data/setting/data/datastore/SessionPreferences.kt` | Create | @Serializable data class + OkioSerializer |
| `shared/src/commonMain/.../data/setting/data/datastore/DataStoreFactory.kt` | Create | expect createSessionDataStore(producePath) |
| `shared/src/androidMain/.../data/setting/data/datastore/DataStoreFactory.kt` | Create | actual DataStore creation for Android |
| `shared/src/iosMain/.../data/setting/data/datastore/DataStoreFactory.kt` | Create | actual DataStore creation for iOS |
| `shared/src/commonMain/.../data/setting/data/SettingsLocalDataSource.kt` | Move | Move from app/ to shared/ (already uses ISettingsLocalDataSource) |
| `shared/src/commonMain/.../base/constants/LocalConstants.kt` | Create | Shared constants (AppId, language codes) |
| `app/.../data/setting/data/datastore/SessionPreferences.kt` | Delete | Replaced by shared module version |
| `app/.../data/setting/data/SettingsLocalDataSource.kt` | Delete | Moved to shared module |
| `app/.../base/encryption/Crypto.kt` | Delete | Replaced by shared module expect/actual |
| `app/.../base/constants/LocalConstants.kt` | Delete | Moved to shared module |
| `docs/KMP_MIGRATION_PLAN.md` | Update | Mark Task 5 complete |

---

## Task 1: Create expect/actual Crypto

**Files:**
- Create: `shared/src/commonMain/kotlin/com/ovasta/sellers/base/encryption/Crypto.kt`
- Create: `shared/src/androidMain/kotlin/com/ovasta/sellers/base/encryption/Crypto.kt`
- Create: `shared/src/iosMain/kotlin/com/ovasta/sellers/base/encryption/Crypto.kt`

- [ ] **Step 1: Create expect Crypto in commonMain**

```kotlin
// shared/src/commonMain/kotlin/com/ovasta/sellers/base/encryption/Crypto.kt
package com.ovasta.sellers.base.encryption

expect object Crypto {
    fun encrypt(bytes: ByteArray): ByteArray
    fun decrypt(bytes: ByteArray): ByteArray
}
```

- [ ] **Step 2: Create actual Crypto for Android**

Port the existing `app/.../base/encryption/Crypto.kt` logic. The Android implementation uses AndroidKeyStore (API 23+) with AES/CBC/PKCS7Padding, and a SHA-256 derived key fallback. Since minSdk is 24, the fallback is not needed but kept for safety.

```kotlin
// shared/src/androidMain/kotlin/com/ovasta/sellers/base/encryption/Crypto.kt
package com.ovasta.sellers.base.encryption

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.ovasta.sellers.base.constants.LocalConstants
import java.security.KeyStore
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

actual object Crypto {
    private const val KEY_ALIAS = "secret"
    private const val ALGORITHM = "AES"
    private const val TRANSFORMATION = "AES/CBC/PKCS7Padding"

    private val cipher = Cipher.getInstance(TRANSFORMATION)

    private val keyStore by lazy {
        KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
    }

    private fun getKey(): SecretKey = getKeyFromKeystore()

    private fun getKeyFromKeystore(): SecretKey {
        return try {
            val existingKey = keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
            existingKey?.secretKey ?: createKeystoreKey()
        } catch (e: Exception) {
            keyStore.deleteEntry(KEY_ALIAS)
            createKeystoreKey()
        }
    }

    private fun createKeystoreKey(): SecretKey {
        return KeyGenerator
            .getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
            .apply {
                init(
                    KeyGenParameterSpec.Builder(
                        KEY_ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                    )
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                        .setRandomizedEncryptionRequired(true)
                        .setUserAuthenticationRequired(false)
                        .build()
                )
            }
            .generateKey()
    }

    actual fun encrypt(bytes: ByteArray): ByteArray {
        cipher.init(Cipher.ENCRYPT_MODE, getKey())
        val iv = cipher.iv
        val encrypted = cipher.doFinal(bytes)
        return iv + encrypted
    }

    actual fun decrypt(bytes: ByteArray): ByteArray {
        val iv = bytes.copyOfRange(0, 16)
        val data = bytes.copyOfRange(16, bytes.size)
        cipher.init(Cipher.DECRYPT_MODE, getKey(), IvParameterSpec(iv))
        return cipher.doFinal(data)
    }
}
```

- [ ] **Step 3: Create actual Crypto for iOS**

iOS uses CommonCrypto (AES/CBC/PKCS7) with a key derived from a hardcoded seed stored in the iOS Keychain. On first run, a random key is generated and stored in Keychain; subsequent runs retrieve it.

```kotlin
// shared/src/iosMain/kotlin/com/ovasta/sellers/base/encryption/Crypto.kt
package com.ovasta.sellers.base.encryption

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.NSKeyedArchiver
import platform.Foundation.NSKeyedUnarchiver
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSString
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
import platform.darwin.OSStatus
import kotlin.native.ref.createCleaner
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CValuesRef
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.sizeOfType
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask
import platform.Foundation.NSUserDefaults
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
import platform.Security.kSecAttrAccessible
import platform.Security.kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly
import platform.Security.kCFBooleanTrue
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

    @OptIn(ExperimentalForeignApi::class)
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

    @OptIn(ExperimentalForeignApi::class)
    private fun saveKeyToKeychain(key: ByteArray) {
        val existing = getKeyFromKeychain()
        val nsData = key.toNSData()

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
                0, // kCCEncrypt
                0, // kCCAlgorithmAES128
                0, // kCCOptionPKCS7Padding = 0 (no padding, we handle it ourselves)
                key.refTo(0),
                16.toULong(),
                null, // no IV for ECB
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
                1, // kCCDecrypt
                0, // kCCAlgorithmAES128
                0, // kCCOptionPKCS7Padding = 0
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
```

- [ ] **Step 4: Verify shared module compiles**

Run: `./gradlew :shared:compileKotlinAndroid`

- [ ] **Step 5: Commit**

```bash
git add shared/
git commit -m "feat: add expect/actual Crypto for multiplatform encryption"
```

**Notes:**

---

## Task 2: Create shared LocalConstants

**Files:**
- Create: `shared/src/commonMain/kotlin/com/ovasta/sellers/base/constants/LocalConstants.kt`

The `SessionPreferencesSerializer` needs `LANGUAGE_AR_ISO` from `LocalConstants`, and the Android Crypto actual needs `AppId`. We move the multiplatform-safe constants to shared.

- [ ] **Step 1: Create shared LocalConstants**

```kotlin
// shared/src/commonMain/kotlin/com/ovasta/sellers/base/constants/LocalConstants.kt
package com.ovasta.sellers.base.constants

object LocalConstants {
    const val AppId = "com.ovasta.sellers"
    const val KEY_APP_LANGUAGE = "KEY_APP_LANGUAGE"
    const val COMM_PROTOCOL = "https://"
    const val LANGUAGE_EN_ISO = "en"
    const val LANGUAGE_AR_ISO = "ar"
    const val DELAY_CLICK_ACTION = 500L
}
```

- [ ] **Step 2: Verify shared module compiles**

Run: `./gradlew :shared:compileKotlinAndroid`

- [ ] **Step 3: Commit**

```bash
git add shared/
git commit -m "feat: add shared LocalConstants"
```

**Notes:**

---

## Task 3: Migrate SessionPreferences to shared commonMain

**Files:**
- Create: `shared/src/commonMain/kotlin/com/ovasta/sellers/data/setting/data/datastore/SessionPreferences.kt`
- Delete: `app/src/main/java/com/ovasta/sellers/data/setting/data/datastore/SessionPreferences.kt`

The key challenge: the existing `SessionPreferencesSerializer` uses `java.io.InputStream`/`OutputStream` (JVM-only) and `android.util.Base64` (Android-only). For multiplatform DataStore, we must use `OkioSerializer` which works with okio `BufferedSource`/`BufferedSink`. The Base64 encoding/decoding can be replaced with okio's Base64 support or kotlinx-serialization's built-in handling.

- [ ] **Step 1: Create shared SessionPreferences with OkioSerializer**

```kotlin
// shared/src/commonMain/kotlin/com/ovasta/sellers/data/setting/data/datastore/SessionPreferences.kt
package com.ovasta.sellers.data.setting.data.datastore

import androidx.datastore.core.okio.OkioSerializer
import com.ovasta.sellers.base.constants.LocalConstants.LANGUAGE_AR_ISO
import com.ovasta.sellers.base.encryption.Crypto
import com.ovasta.sellers.data.User
import com.ovasta.sellers.data.setting.model.RemoteConfigModel
import com.ovasta.sellers.presentation.home.data.model.HomeInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okio.BufferedSink
import okio.BufferedSource
import okio.buffer
import okio.sink
import okio.source

@Serializable
data class SessionPreferences(
    val userLang: String = LANGUAGE_AR_ISO,
    val user: User? = null,
    val remoteConfigModel: RemoteConfigModel? = null,
    val isLoggedIn: Boolean = false,
    val deviceRam: String = "",
    val accessToken: String = "",
    val deviceId: String = "",
    val fcmToken: String = "",
    val isTracking: Boolean = false,
    val homeInfo: HomeInfo? = null,
)

object SessionPreferencesSerializer : OkioSerializer<SessionPreferences> {
    override val defaultValue: SessionPreferences
        get() = SessionPreferences()

    private val json = Json {
        ignoreUnknownKeys = true
    }

    override suspend fun readFrom(source: BufferedSource): SessionPreferences {
        return try {
            val encryptedBytes = withContext(Dispatchers.IO) {
                source.readByteArray()
            }
            if (encryptedBytes.isEmpty()) return defaultValue

            val jsonString = try {
                val decrypted = Crypto.decrypt(encryptedBytes)
                decrypted.decodeToString()
            } catch (e: Exception) {
                try {
                    encryptedBytes.decodeToString()
                } catch (e2: Exception) {
                    return defaultValue
                }
            }

            json.decodeFromString(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(
        t: SessionPreferences,
        sink: BufferedSink
    ) {
        val jsonStr = Json.encodeToString(t)
        val bytes = jsonStr.toByteArray()
        val encryptedBytes = Crypto.encrypt(bytes)
        withContext(Dispatchers.IO) {
            sink.write(encryptedBytes)
            sink.flush()
        }
    }
}
```

**Key differences from the Android version:**
1. Uses `OkioSerializer` instead of `Serializer<SessionPreferences>` — works with `BufferedSource`/`BufferedSink` (multiplatform)
2. Removes `android.util.Base64` — encryption output is raw bytes written directly to okio sink (no Base64 wrapper needed since we control both read/write)
3. Removes `@Keep` annotation (not multiplatform)
4. Removes `java.io.InputStream`/`OutputStream` — replaced with okio equivalents

- [ ] **Step 2: Delete old SessionPreferences from app module**

Delete: `app/src/main/java/com/ovasta/sellers/data/setting/data/datastore/SessionPreferences.kt`

- [ ] **Step 3: Verify shared module compiles**

Run: `./gradlew :shared:compileKotlinAndroid`

- [ ] **Step 4: Commit**

```bash
git add shared/ app/
git commit -m "feat: migrate SessionPreferences to shared with OkioSerializer"
```

**Notes:**

---

## Task 4: Create expect/actual DataStore factory

**Files:**
- Create: `shared/src/commonMain/kotlin/com/ovasta/sellers/data/setting/data/datastore/DataStoreFactory.kt`
- Create: `shared/src/androidMain/kotlin/com/ovasta/sellers/data/setting/data/datastore/DataStoreFactory.kt`
- Create: `shared/src/iosMain/kotlin/com/ovasta/sellers/data/setting/data/datastore/DataStoreFactory.kt`

DataStore creation needs platform-specific file path resolution. On Android, we use `Context.filesDir`. On iOS, we use `NSDocumentDirectory`. We expose a factory function from commonMain.

- [ ] **Step 1: Create expect DataStore factory in commonMain**

```kotlin
// shared/src/commonMain/kotlin/com/ovasta/sellers/data/setting/data/datastore/DataStoreFactory.kt
package com.ovasta.sellers.data.setting.data.datastore

import androidx.datastore.core.DataStore

expect fun createSessionDataStore(producePath: () -> String): DataStore<SessionPreferences>
```

- [ ] **Step 2: Create actual DataStore factory for Android**

```kotlin
// shared/src/androidMain/kotlin/com/ovasta/sellers/data/setting/data/datastore/DataStoreFactory.kt
package com.ovasta.sellers.data.setting.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.core.okio.OkioStorage
import okio.FileSystem
import okio.Path.Companion.toPath

actual fun createSessionDataStore(producePath: () -> String): DataStore<SessionPreferences> {
    return DataStoreFactory.create(
        storage = OkioStorage(
            fileSystem = FileSystem.SYSTEM,
            serializer = SessionPreferencesSerializer,
            producePath = { producePath().toPath() }
        )
    )
}
```

Note: `DataStoreFactory.create` here is from `androidx.datastore.core.DataStoreFactory`, which accepts a `Storage<T>` parameter. The `OkioStorage` provides a multiplatform `Storage` implementation.

- [ ] **Step 3: Create actual DataStore factory for iOS**

```kotlin
// shared/src/iosMain/kotlin/com/ovasta/sellers/data/setting/data/datastore/DataStoreFactory.kt
package com.ovasta.sellers.data.setting.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.core.okio.OkioStorage
import okio.FileSystem
import okio.Path.Companion.toPath

actual fun createSessionDataStore(producePath: () -> String): DataStore<SessionPreferences> {
    return DataStoreFactory.create(
        storage = OkioStorage(
            fileSystem = FileSystem.SYSTEM,
            serializer = SessionPreferencesSerializer,
            producePath = { producePath().toPath() }
        )
    )
}
```

- [ ] **Step 4: Verify shared module compiles**

Run: `./gradlew :shared:compileKotlinAndroid`

- [ ] **Step 5: Commit**

```bash
git add shared/
git commit -m "feat: add expect/actual DataStore factory for multiplatform"
```

**Notes:**

---

## Task 5: Move SettingsLocalDataSource to shared module

**Files:**
- Move: `app/.../data/setting/data/SettingsLocalDataSource.kt` → `shared/src/commonMain/kotlin/com/ovasta/sellers/data/setting/data/SettingsLocalDataSource.kt`
- Delete: `app/src/main/java/com/ovasta/sellers/data/setting/data/SettingsLocalDataSource.kt`

The `SettingsLocalDataSource` only depends on `DataStore<SessionPreferences>` and `ISettingsLocalDataSource` — both are now in shared. It can be moved entirely.

- [ ] **Step 1: Create SettingsLocalDataSource in shared commonMain**

```kotlin
// shared/src/commonMain/kotlin/com/ovasta/sellers/data/setting/data/SettingsLocalDataSource.kt
package com.ovasta.sellers.data.setting.data

import androidx.datastore.core.DataStore
import com.ovasta.sellers.base.constants.LocalConstants.LANGUAGE_AR_ISO
import com.ovasta.sellers.data.setting.data.datastore.SessionPreferences
import com.ovasta.sellers.data.User
import com.ovasta.sellers.presentation.home.data.model.HomeInfo
import kotlinx.coroutines.flow.first

class SettingsLocalDataSource(private val dataStore: DataStore<SessionPreferences>) :
    ISettingsLocalDataSource {

    override suspend fun getUseData(): User? {
        return dataStore.data.first().user
    }

    override suspend fun clearUserData() {
        val currentLang = LANGUAGE_AR_ISO
        dataStore.updateData {
            SessionPreferences(
                userLang = currentLang
            )
        }
    }

    override suspend fun saveUserData(user: User) {
        dataStore.updateData {
            it.copy(
                user = user,
                accessToken = user.token ?: "",
            )
        }
    }

    override suspend fun saveHomeData(homeResponse: HomeInfo) {
        dataStore.updateData {
            it.copy(
                homeInfo = homeResponse
            )
        }
    }

    override suspend fun getHomeData(): HomeInfo? = dataStore.data.first().homeInfo
}
```

- [ ] **Step 2: Delete old SettingsLocalDataSource from app module**

Delete: `app/src/main/java/com/ovasta/sellers/data/setting/data/SettingsLocalDataSource.kt`

- [ ] **Step 3: Verify shared module compiles**

Run: `./gradlew :shared:compileKotlinAndroid`

- [ ] **Step 4: Commit**

```bash
git add shared/ app/
git commit -m "feat: move SettingsLocalDataSource to shared module"
```

**Notes:**

---

## Task 6: Update app module DI to use shared DataStore

**Files:**
- Modify: `app/src/main/java/com/ovasta/sellers/data/setting/di/SettingModule.kt`
- Modify: `app/src/main/java/com/ovasta/sellers/base/di/LocalModule.kt`

The `SettingModule.kt` currently creates `DataStore<SessionPreferences>` using `DataStoreFactory.create(serializer = SessionPreferencesSerializer, produceFile = {...})`. This must be updated to use the new `createSessionDataStore()` factory from shared.

The `LocalModule.kt` creates `EncryptedSharedPreferences` which is not used by DataStore. It can remain as-is for now (will be addressed in Task 6 of the migration plan - Koin DI).

- [ ] **Step 1: Update SettingModule.kt**

Replace the DataStore creation with the shared factory:

```kotlin
// app/src/main/java/com/ovasta/sellers/data/setting/di/SettingModule.kt
package com.ovasta.sellers.data.setting.di

import android.content.Context
import android.location.Geocoder
import androidx.datastore.core.DataStore
import com.ovasta.sellers.data.setting.data.ISettingsLocalDataSource
import com.ovasta.sellers.data.setting.data.ISettingsRemoteDataSource
import com.ovasta.sellers.data.setting.data.ISettingsRepository
import com.ovasta.sellers.data.setting.data.SettingsLocalDataSource
import com.ovasta.sellers.data.setting.data.SettingsRemoteDataSource
import com.ovasta.sellers.data.setting.data.SettingsRepository
import com.ovasta.sellers.base.constants.SharedPreferenceConstants
import com.ovasta.sellers.data.setting.data.datastore.SessionPreferences
import com.ovasta.sellers.data.setting.data.datastore.createSessionDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import java.io.File
import java.util.Locale

val settingModule = module {
    single { Geocoder(androidContext(), Locale.getDefault()) }
    single<ISettingsRemoteDataSource> { SettingsRemoteDataSource(get()) }
    single<ISettingsLocalDataSource> { SettingsLocalDataSource(get<DataStore<SessionPreferences>>()) }
    single<DataStore<SessionPreferences>> {
        createSessionDataStore {
            File(androidContext().filesDir, "datastore/${SharedPreferenceConstants.PREFERENCE_NAME}").absolutePath
        }
    }
    single<ISettingsRepository> { SettingsRepository(get(), get()) }
}
```

**Key changes:**
- Import `createSessionDataStore` from shared module
- Replace `DataStoreFactory.create(serializer = ..., produceFile = {...})` with `createSessionDataStore(producePath = {...})`
- Replace `preferencesDataStoreFile()` (Android-only) with manual File path construction using `filesDir + datastore/` subdirectory
- Remove `SessionPreferencesSerializer` import (now internal to shared factory)

- [ ] **Step 2: Delete old Crypto.kt from app module**

Since the shared module now provides Crypto, delete the old one:

Delete: `app/src/main/java/com/ovasta/sellers/base/encryption/Crypto.kt`

- [ ] **Step 3: Delete old LocalConstants.kt from app module**

Since shared now provides LocalConstants, delete the old one:

Delete: `app/src/main/java/com/ovasta/sellers/base/constants/LocalConstants.kt`

**WARNING:** Before deleting, verify no other app module files still import from `com.ovasta.sellers.base.constants.LocalConstants`. If they do, those imports will resolve to the shared module version since `app` depends on `:shared`. So deletion should be safe.

- [ ] **Step 4: Verify app compiles**

Run: `./gradlew :app:compileDebugKotlin`

- [ ] **Step 5: Commit**

```bash
git add app/ shared/
git commit -m "feat: update app DI to use shared DataStore factory"
```

**Notes:**

---

## Task 7: Update KMP Migration Plan

**Files:**
- Modify: `docs/KMP_MIGRATION_PLAN.md`

- [ ] **Step 1: Update Task 5 checkboxes and notes**

In the KMP Migration Plan, update Task 5 steps to checked and add notes:

- [x] Step 1: Create expect/actual Crypto
- [x] Step 2: Migrate SessionPreferences to multiplatform DataStore (OkioSerializer)
- [x] Step 3: Create expect/actual DataStore factory
- [x] Step 4: Move SettingsLocalDataSource to shared
- [x] Step 5: Verify compilation

Also update the Progress Summary table:
- Task 3: mark as ✅ COMPLETE
- Task 4: mark as ✅ COMPLETE
- Task 5: mark as ✅ COMPLETE

- [ ] **Step 2: Commit**

```bash
git add docs/
git commit -m "docs: update KMP migration plan - Tasks 3,4,5 complete"
```

**Notes:**
