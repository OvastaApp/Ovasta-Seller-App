package com.ovasta.sellers

import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.ComposeUIViewController
import com.ovasta.sellers.di.initKoinIos
import com.ovasta.sellers.ui.App
import platform.Foundation.NSUserDefaults

fun createComposeViewController() = ComposeUIViewController {
    val userDefaults = NSUserDefaults.standardUserDefaults
    val language = userDefaults.stringForKey("secure_language") ?: "ar"

    // Set AppleLanguages so Compose Multiplatform resources resolve to the correct language.
    // This must happen before any stringResource() call inside App().
    userDefaults.setObject(listOf(language), forKey = "AppleLanguages")
    userDefaults.synchronize()

    val layoutDirection = if (language == "ar") LayoutDirection.Rtl else LayoutDirection.Ltr
    App(layoutDirection = layoutDirection)
}

fun doInitKoin() {
    initKoinIos()
}
