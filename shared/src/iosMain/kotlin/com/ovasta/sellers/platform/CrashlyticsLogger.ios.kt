package com.ovasta.sellers.platform

// iOS Crashlytics auto-captures all unhandled exceptions via the native SDK.
// Manual non-fatal logging from KMP shared code requires bridging the
// FirebaseCrashlytics CocoaPod — implement here when that bridge is added.
actual fun recordException(throwable: Throwable) = Unit

/**
 * Bridge the iOS app registers (in AppDelegate) to forward Crashlytics user
 * identity to the native FirebaseCrashlytics SDK, which the shared framework
 * cannot reference directly.
 *
 * Swift:
 *   CrashlyticsBridge.shared.userHandler = { id, name, phone in
 *       Crashlytics.crashlytics().setUserID(id)
 *       Crashlytics.crashlytics().setCustomValue(name, forKey: "user_name")
 *       Crashlytics.crashlytics().setCustomValue(phone, forKey: "user_phone")
 *   }
 *   CrashlyticsBridge.shared.clearHandler = {
 *       Crashlytics.crashlytics().setUserID("")
 *   }
 */
object CrashlyticsBridge {
    var userHandler: ((String, String, String) -> Unit)? = null
    var clearHandler: (() -> Unit)? = null
}

actual fun setCrashlyticsUser(id: String, name: String, phone: String) {
    CrashlyticsBridge.userHandler?.invoke(id, name, phone)
}

actual fun clearCrashlyticsUser() {
    CrashlyticsBridge.clearHandler?.invoke()
}
