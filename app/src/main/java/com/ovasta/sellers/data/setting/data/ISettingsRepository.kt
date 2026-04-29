package com.ovasta.sellers.data.setting.data

import com.ovasta.sellers.data.User
import com.ovasta.sellers.presentation.home.data.model.HomeInfo

interface ISettingsRepository {

    suspend fun saveUserData(user: User)
    suspend fun getUseData(): User?
    suspend fun saveHomeData(homeResponse: HomeInfo)
    suspend fun getHomeInfo(): HomeInfo?
    suspend fun logout()
    suspend fun clearUserData()

}