package com.ovasta.sellers.base.crashlyticsInfo

import com.ovasta.sellers.data.User

interface ICrashlyticsInfoRemoteDataSource {
   suspend fun setUserInfo(user: User?)
}