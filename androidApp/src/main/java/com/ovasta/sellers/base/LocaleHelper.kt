package com.ovasta.sellers.base

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LocaleHelper {

    private val ARABIC = Locale("ar")

    fun wrapContext(context: Context): Context {
        Locale.setDefault(ARABIC)
        val config = Configuration(context.resources.configuration)
        config.setLocale(ARABIC)
        config.setLayoutDirection(ARABIC)
        return context.createConfigurationContext(config)
    }
}
