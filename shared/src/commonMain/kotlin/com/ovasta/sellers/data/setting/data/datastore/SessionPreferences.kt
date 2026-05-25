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
