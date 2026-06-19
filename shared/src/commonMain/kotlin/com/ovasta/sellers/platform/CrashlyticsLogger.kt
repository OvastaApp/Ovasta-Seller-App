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

/**
 * Attaches the signed-in seller's identity to Crashlytics so it shows on every
 * crash and non-fatal report: the user id becomes the Crashlytics user identifier,
 * and name/phone are attached as custom keys ("user_name" / "user_phone").
 *
 * Call on login and on app relaunch once the stored user is restored.
 *
 * Android: uses Firebase Crashlytics directly.
 * iOS: forwards to a handler the iOS app registers (see crashlyticsUserHandler);
 *      the shared framework cannot call the Firebase pod directly.
 */
expect fun setCrashlyticsUser(id: String, name: String, phone: String)

/** Clears the Crashlytics user identity (call on logout). */
expect fun clearCrashlyticsUser()
