package com.ovasta.sellers.platform

import org.koin.core.context.GlobalContext
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSUserDefaults

const val LANGUAGE_CHANGED_NOTIFICATION = "com.ovasta.sellers.LanguageChanged"

actual fun applyAppLanguage(languageCode: String) {
    // Save to NSUserDefaults so createComposeViewController() picks it up on next render
    NSUserDefaults.standardUserDefaults.setObject(languageCode, forKey = "secure_language")
    NSUserDefaults.standardUserDefaults.synchronize()

    // Also write via SecureStorage so SettingsRepository.getLanguage() stays in sync
    val secureStorage = GlobalContext.get().get<com.ovasta.sellers.data.platform.SecureStorage>()
    secureStorage.putString("language", languageCode)

    // Signal Swift to recreate the Compose view controller so the new locale takes effect
    NSNotificationCenter.defaultCenter.postNotificationName(
        name = LANGUAGE_CHANGED_NOTIFICATION,
        `object` = null
    )
}
