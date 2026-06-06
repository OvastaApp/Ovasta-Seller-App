package com.ovasta.sellers.base.crashlyticsInfo

import com.ovasta.sellers.data.User

class CrashlyticsUserInfoUseCase(
    private val crashlyticsRemoteDataSource: ICrashlyticsInfoRemoteDataSource
) {
    suspend operator fun invoke(user: User?) {
        crashlyticsRemoteDataSource.setUserInfo(user = user)
    }
}