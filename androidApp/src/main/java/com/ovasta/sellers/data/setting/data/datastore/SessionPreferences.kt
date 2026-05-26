package com.ovasta.sellers.data.setting.data.datastore

import androidx.datastore.core.Serializer
import com.ovasta.sellers.data.setting.model.RemoteConfigModel
import com.ovasta.sellers.base.constants.LocalConstants.LANGUAGE_AR_ISO
import com.ovasta.sellers.base.encryption.Crypto
import com.ovasta.sellers.domain.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.annotation.Keep
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import android.util.Base64
import com.ovasta.sellers.domain.model.HomeInfo

@Keep
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

object SessionPreferencesSerializer : Serializer<SessionPreferences> {
    override val defaultValue: SessionPreferences
        get() = SessionPreferences()

    private val json = Json {
        ignoreUnknownKeys = true
    }

    override suspend fun readFrom(input: InputStream): SessionPreferences {
        return try {
            val encryptedBytes = withContext(Dispatchers.IO) {
                input.use { it.readBytes() }
            }
            if (encryptedBytes.isEmpty()) return defaultValue

            val jsonString = try {
                val decoded = Base64.decode(encryptedBytes, Base64.NO_WRAP)
                val decrypted = Crypto.decrypt(decoded)
                decrypted.decodeToString()
            } catch (e: Exception) {
                encryptedBytes.decodeToString()
            }

            json.decodeFromString(jsonString)

        } catch (e: Exception) {
            e.printStackTrace()

            defaultValue
        }
    }

    override suspend fun writeTo(
        t: SessionPreferences,
        output: OutputStream
    ) {
        val json = Json.encodeToString(t)
        val bytes = json.toByteArray()

        val encryptedBytes = Crypto.encrypt(bytes)

        val encryptedBytesBase64 =
            Base64.encode(encryptedBytes, Base64.DEFAULT)

        withContext(Dispatchers.IO) {
            output.use {
                it.write(encryptedBytesBase64)
            }
        }
    }
}