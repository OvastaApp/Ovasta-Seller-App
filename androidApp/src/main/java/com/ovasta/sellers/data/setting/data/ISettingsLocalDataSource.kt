package com.ovasta.sellers.data.setting.data

import com.ovasta.sellers.domain.model.User
import com.ovasta.sellers.domain.model.HomeInfo

interface ISettingsLocalDataSource {
    suspend fun getUseData(): User?
    suspend fun clearUserData()
    suspend fun saveUserData(user: User)
    suspend fun saveHomeData(homeResponse: HomeInfo)
    suspend fun getHomeData(): HomeInfo?
    suspend fun getDeviceId(): String
    suspend fun saveDeviceId(deviceId: String)
}