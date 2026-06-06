package com.ovasta.sellers.base.crashlyticsInfo

import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.google.firebase.crashlytics.setCustomKeys
import com.ovasta.sellers.data.FirebaseConstants.USER_ID_KEY
import com.ovasta.sellers.data.FirebaseConstants.USER_NAME_KEY
import com.ovasta.sellers.data.FirebaseConstants.USER_PHONE_KEY
import com.ovasta.sellers.data.User

class CrashlyticsInfoRemoteDataSource : ICrashlyticsInfoRemoteDataSource {
    override suspend fun setUserInfo(user: User?) {
        user?.let {
            Firebase.crashlytics.setUserId(it.id.toString())
            Firebase.crashlytics.setCustomKeys {
                key(USER_ID_KEY, it.id.toString())
                key(USER_NAME_KEY, it.name ?: "")
                key(USER_PHONE_KEY, it.mobile ?: "")
            }
        }
    }
}