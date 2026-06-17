package com.ovasta.sellers.platform

import android.content.Context
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.koin.core.context.GlobalContext

/**
 * Emits Unit whenever the activity should recreate to apply a new locale.
 * MainActivity collects this flow and calls recreate().
 */
object LanguageChangeSignal {
    private val _recreateFlow = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val recreateFlow: SharedFlow<Unit> = _recreateFlow.asSharedFlow()

    fun requestRecreate() {
        _recreateFlow.tryEmit(Unit)
    }
}

/**
 * Saves the language to "app_prefs" SharedPreferences so that LocaleHelper
 * picks it up in attachBaseContext on the next Activity creation, then
 * signals the Activity to recreate itself.
 */
actual fun applyAppLanguage(languageCode: String) {
    val context = GlobalContext.get().get<Context>()
    // Write to app_prefs so LocaleHelper picks it up in attachBaseContext on recreate
    context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        .edit()
        .putString("language", languageCode)
        .apply()
    // Also write to SecureStorage so SettingsRepository.getLanguage() stays in sync
    val secureStorage = GlobalContext.get().get<com.ovasta.sellers.data.platform.SecureStorage>()
    secureStorage.putString("language", languageCode)

    LanguageChangeSignal.requestRecreate()
}
