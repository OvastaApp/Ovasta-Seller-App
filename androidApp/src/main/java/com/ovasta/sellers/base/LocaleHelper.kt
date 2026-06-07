package com.ovasta.sellers.base

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LocaleHelper {

    /**
     * Wrap context with the specified language locale
     * @param context The base context
     * @param languageCode Language code ("ar" for Arabic, "en" for English)
     * @return Context configured with the specified locale
     */
    fun wrapContext(context: Context, languageCode: String = "ar"): Context {
        val locale = getLocale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        return context.createConfigurationContext(config)
    }

    /**
     * Get Locale object from language code
     * @param languageCode "ar" or "en"
     * @return Locale object
     */
    fun getLocale(languageCode: String): Locale {
        return Locale(languageCode)
    }

    /**
     * Get language code from SharedPreferences
     * @param context Context to access SharedPreferences
     * @return Language code ("ar" or "en")
     */
    fun getLanguageFromPrefs(context: Context): String {
        return context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            .getString("language", "ar") ?: "ar"
    }
}
