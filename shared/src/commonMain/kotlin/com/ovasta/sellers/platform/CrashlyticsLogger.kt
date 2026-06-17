package com.ovasta.sellers.platform

/**
 * Records a non-fatal exception to Crashlytics so it appears in the Firebase Console
 * without crashing the app. Called from BaseViewModel.handleError for every caught error.
 *
 * Android: uses Firebase Crashlytics KTX.
 * iOS: no-op — Crashlytics auto-captures unhandled crashes; non-fatal logging
 *       from KMP requires the native SDK and is not yet bridged.
 */
expect fun recordException(throwable: Throwable)
