package com.ovasta.sellers.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.ovasta.sellers.base.constants.LocalConstants.LANGUAGE_AR_ISO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Implementation of LanguagePreferences using SharedPreferences.
 * Uses "app_prefs" with key "language" to maintain compatibility with LocaleHelper.
 */
class LanguagePreferencesImpl(private val context: Context) : LanguagePreferences {
    
    companion object {
        private const val PREFS_NAME = "app_prefs"
        private const val KEY_LANGUAGE = "language"
    }

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    override suspend fun getLanguage(): String = withContext(Dispatchers.IO) {
        getLanguageSync()
    }

    override suspend fun setLanguage(languageCode: String) = withContext(Dispatchers.IO) {
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply()
    }

    override fun getLanguageSync(): String {
        return prefs.getString(KEY_LANGUAGE, LANGUAGE_AR_ISO) ?: LANGUAGE_AR_ISO
    }
}
