package com.ovasta.sellers

import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.ComposeUIViewController
import com.ovasta.sellers.di.initKoinIos
import com.ovasta.sellers.ui.App
import platform.Foundation.NSUserDefaults

fun createComposeViewController() = ComposeUIViewController {
    // "secure_language" is the key used by iOS SecureStorage (prefix "secure_" + "language")
    val language = NSUserDefaults.standardUserDefaults.stringForKey("secure_language") ?: "ar"
    val layoutDirection = if (language == "ar") LayoutDirection.Rtl else LayoutDirection.Ltr
    App(layoutDirection = layoutDirection)
}

fun doInitKoin() {
    initKoinIos()
}
