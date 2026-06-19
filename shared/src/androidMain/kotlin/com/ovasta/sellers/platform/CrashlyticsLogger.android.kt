package com.ovasta.sellers.platform

import com.google.firebase.crashlytics.FirebaseCrashlytics

actual fun recordException(throwable: Throwable) {
    FirebaseCrashlytics.getInstance().recordException(throwable)
}

actual fun setCrashlyticsUser(id: String, name: String, phone: String) {
    FirebaseCrashlytics.getInstance().apply {
        setUserId(id)
        setCustomKey("user_name", name)
        setCustomKey("user_phone", phone)
    }
}

actual fun clearCrashlyticsUser() {
    FirebaseCrashlytics.getInstance().apply {
        setUserId("")
        setCustomKey("user_name", "")
        setCustomKey("user_phone", "")
    }
}
