package com.ovasta.sellers

import androidx.compose.ui.window.ComposeUIViewController
import com.ovasta.sellers.di.initKoinIos
import com.ovasta.sellers.ui.App

fun createComposeViewController() = ComposeUIViewController {
    App()
}

fun initKoin() {
    initKoinIos()
}
