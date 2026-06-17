package com.ovasta.sellers.platform

// iOS Crashlytics auto-captures all unhandled exceptions via the native SDK.
// Manual non-fatal logging from KMP shared code requires bridging the
// FirebaseCrashlytics CocoaPod — implement here when that bridge is added.
actual fun recordException(throwable: Throwable) = Unit
