package com.ovasta.sellers.domain.repository

import com.ovasta.sellers.domain.model.HomeInfo
import com.ovasta.sellers.domain.model.User

interface ISettingsRepository {
    suspend fun saveUserData(user: User)
    suspend fun getUserData(): User?
    suspend fun saveHomeData(homeInfo: HomeInfo)
    suspend fun getHomeInfo(): HomeInfo?
    suspend fun logout()
    suspend fun clearUserData()
    suspend fun getDeviceId(): String
    suspend fun saveDeviceId(deviceId: String)
    suspend fun getAccessToken(): String
    suspend fun getFcmToken(): String
    suspend fun saveFcmToken(token: String)
    suspend fun saveLanguage(languageCode: String)
    fun getLanguage(): String
}
