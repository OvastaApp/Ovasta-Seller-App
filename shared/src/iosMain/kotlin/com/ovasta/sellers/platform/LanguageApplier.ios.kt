package com.ovasta.sellers.platform

import org.koin.mp.KoinPlatform
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSUserDefaults

const val LANGUAGE_CHANGED_NOTIFICATION = "com.ovasta.sellers.LanguageChanged"

actual fun applyAppLanguage(languageCode: String) {
    val userDefaults = NSUserDefaults.standardUserDefaults

    // "secure_language" — read by createComposeViewController() for LayoutDirection
    userDefaults.setObject(languageCode, forKey = "secure_language")

    // "AppleLanguages" — read by NSBundle / Compose Resources when resolving localised strings.
    // Must be an array; the system picks the first supported language from the list.
    userDefaults.setObject(listOf(languageCode), forKey = "AppleLanguages")

    userDefaults.synchronize()

    // Also write via SecureStorage so SettingsRepository.getLanguage() stays in sync
    val koin = KoinPlatform.getKoin()
    val secureStorage = koin.get<com.ovasta.sellers.data.platform.SecureStorage>()
    secureStorage.putString("language", languageCode)

    // Signal Swift to recreate the Compose view controller so strings + direction both update
    NSNotificationCenter.defaultCenter.postNotificationName(
        aName = LANGUAGE_CHANGED_NOTIFICATION,
        `object` = null
    )
}
