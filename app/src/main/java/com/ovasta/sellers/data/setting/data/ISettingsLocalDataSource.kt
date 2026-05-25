package com.ovasta.sellers.data.setting.data

import com.ovasta.sellers.data.User
import com.ovasta.sellers.presentation.home.data.model.HomeInfo

interface ISettingsLocalDataSource {
    suspend fun getUseData(): User?
    suspend fun clearUserData()
    suspend fun saveUserData(user: User)
    suspend fun saveHomeData(homeResponse: HomeInfo)
    suspend fun getHomeData(): HomeInfo?
}