package com.ovasta.sellers.data.setting.data

import com.ovasta.sellers.data.User

interface ISettingsRepository {

    suspend fun saveUserData(user: User)
    suspend fun getUseData(): User?
    suspend fun logout()
    suspend fun clearUserData()

}