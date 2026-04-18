package com.ovasta.sellers.data.setting.data

import com.ovasta.sellers.data.User

interface ISettingsLocalDataSource {
    suspend fun getUseData(): User?
    suspend fun clearUserData()
    suspend fun saveUserData(user: User)
}