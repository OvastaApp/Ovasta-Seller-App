package com.ovasta.sellers.platform

/**
 * Applies the given language code as the app locale at runtime.
 * Android: uses AppCompatDelegate.setApplicationLocales (no restart needed on API 33+, restarts activity on older).
 * iOS: no-op — locale change takes effect on next app launch via stored preference.
 */
expect fun applyAppLanguage(languageCode: String)
