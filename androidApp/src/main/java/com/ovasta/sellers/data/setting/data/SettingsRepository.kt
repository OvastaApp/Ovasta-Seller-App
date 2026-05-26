package com.ovasta.sellers.data.setting.data

import com.ovasta.sellers.domain.model.User
import com.ovasta.sellers.presentation.home.data.model.HomeInfo

class SettingsRepository(
    private val settingsRemoteDataSource: ISettingsRemoteDataSource,
    private val settingsLocalDataSource: ISettingsLocalDataSource
) : ISettingsRepository {
    override suspend fun saveUserData(user: User) {
        settingsLocalDataSource.saveUserData(user)
    }

    override suspend fun getUseData(): User? {
        return settingsLocalDataSource.getUseData()
    }

    override suspend fun saveHomeData(homeResponse: HomeInfo) =
        settingsLocalDataSource.saveHomeData(homeResponse)

    override suspend fun getHomeInfo(): HomeInfo? =
        settingsLocalDataSource.getHomeData()

    override suspend fun logout() = settingsRemoteDataSource.logout()

    override suspend fun clearUserData() {
        settingsLocalDataSource.clearUserData()
    }

    override suspend fun getDeviceId(): String = settingsLocalDataSource.getDeviceId()

    override suspend fun saveDeviceId(deviceId: String) {
        settingsLocalDataSource.saveDeviceId(deviceId)
    }
}