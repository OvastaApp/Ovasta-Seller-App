package com.ovasta.sellers.data.preferences

/**
 * Interface for managing application language preferences.
 * Provides both synchronous and asynchronous methods for language access.
 */
interface LanguagePreferences {
    /**
     * Get the current language code asynchronously.
     * @return Language code (e.g., "ar", "en")
     */
    suspend fun getLanguage(): String

    /**
     * Set the language code asynchronously.
     * @param languageCode Language code to set (e.g., "ar", "en")
     */
    suspend fun setLanguage(languageCode: String)

    /**
     * Get the current language code synchronously.
     * Useful for Application.attachBaseContext() and similar synchronous contexts.
     * @return Language code (e.g., "ar", "en")
     */
    fun getLanguageSync(): String
}
