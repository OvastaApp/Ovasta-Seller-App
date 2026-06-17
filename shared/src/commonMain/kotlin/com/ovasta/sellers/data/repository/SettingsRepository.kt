package com.ovasta.sellers.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ovasta.sellers.data.platform.DataStoreProvider
import com.ovasta.sellers.data.platform.DeviceInfoProvider
import com.ovasta.sellers.data.platform.FirebaseMessagingProvider
import com.ovasta.sellers.data.platform.SecureStorage
import com.ovasta.sellers.domain.model.HomeInfo
import com.ovasta.sellers.domain.model.User
import com.ovasta.sellers.domain.repository.ISettingsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SettingsRepository(
    private val secureStorage: SecureStorage,
    dataStoreProvider: DataStoreProvider,
    private val deviceInfoProvider: DeviceInfoProvider,
    private val firebaseMessaging: FirebaseMessagingProvider,
) : ISettingsRepository {

    private val dataStore: DataStore<Preferences> = dataStoreProvider.getUserDataStore() as DataStore<Preferences>
    private val json = Json { ignoreUnknownKeys = true }

    companion object {
        private val USER_DATA_KEY = stringPreferencesKey("user_data")
        private val HOME_INFO_KEY = stringPreferencesKey("home_info")
        private const val SECURE_TOKEN_KEY = "access_token"
        private const val SECURE_FCM_TOKEN_KEY = "fcm_token"
        private const val SECURE_DEVICE_ID_KEY = "device_id"
        private const val SECURE_LANGUAGE_KEY = "language"
        const val DEFAULT_LANGUAGE = "ar"
    }

    override suspend fun saveUserData(user: User) {
        dataStore.edit { prefs ->
            prefs[USER_DATA_KEY] = json.encodeToString(user)
        }
        // Save access token to secure storage
        user.token?.let { secureStorage.putString(SECURE_TOKEN_KEY, it) }
    }

    override suspend fun getUserData(): User? {
        return dataStore.data.map { prefs ->
            prefs[USER_DATA_KEY]?.let { json.decodeFromString<User>(it) }
        }.first()
    }

    override suspend fun saveHomeData(homeInfo: HomeInfo) {
        dataStore.edit { prefs ->
            prefs[HOME_INFO_KEY] = json.encodeToString(homeInfo)
        }
    }

    override suspend fun getHomeInfo(): HomeInfo? {
        return dataStore.data.map { prefs ->
            prefs[HOME_INFO_KEY]?.let { json.decodeFromString<HomeInfo>(it) }
        }.first()
    }

    override suspend fun logout() {
        clearUserData()
    }

    override suspend fun clearUserData() {
        dataStore.edit { it.clear() }
        secureStorage.clear()
    }

    override suspend fun getDeviceId(): String {
        var deviceId = secureStorage.getString(SECURE_DEVICE_ID_KEY)
        if (deviceId == null) {
            deviceId = deviceInfoProvider.getDeviceId()
            secureStorage.putString(SECURE_DEVICE_ID_KEY, deviceId)
        }
        return deviceId
    }

    override suspend fun saveDeviceId(deviceId: String) {
        secureStorage.putString(SECURE_DEVICE_ID_KEY, deviceId)
    }

    override suspend fun getAccessToken(): String {
        return secureStorage.getString(SECURE_TOKEN_KEY) ?: ""
    }

    override suspend fun getFcmToken(): String {
        var token = secureStorage.getString(SECURE_FCM_TOKEN_KEY)
        if (token.isNullOrEmpty()) {
            token = firebaseMessaging.getToken()
            if (token.isNotEmpty()) {
                secureStorage.putString(SECURE_FCM_TOKEN_KEY, token)
            }
        }
        return token
    }

    override suspend fun saveFcmToken(token: String) {
        secureStorage.putString(SECURE_FCM_TOKEN_KEY, token)
    }

    override suspend fun saveLanguage(languageCode: String) {
        secureStorage.putString(SECURE_LANGUAGE_KEY, languageCode)
    }

    override fun getLanguage(): String {
        return secureStorage.getString(SECURE_LANGUAGE_KEY) ?: DEFAULT_LANGUAGE
    }
}
