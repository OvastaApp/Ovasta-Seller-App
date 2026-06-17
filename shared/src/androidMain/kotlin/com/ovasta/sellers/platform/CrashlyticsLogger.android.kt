package com.ovasta.sellers.platform

import com.google.firebase.crashlytics.FirebaseCrashlytics

actual fun recordException(throwable: Throwable) {
    FirebaseCrashlytics.getInstance().recordException(throwable)
}
