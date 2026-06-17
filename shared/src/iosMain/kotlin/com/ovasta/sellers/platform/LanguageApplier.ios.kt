package com.ovasta.sellers.platform

// On iOS, locale is applied via stored preference read at startup.
// The app must be restarted for the language change to take effect in the UI.
actual fun applyAppLanguage(languageCode: String) = Unit
