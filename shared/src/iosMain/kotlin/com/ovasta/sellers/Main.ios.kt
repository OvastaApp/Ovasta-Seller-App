package com.ovasta.sellers

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.ComposeUIViewController

fun createComposeViewController() = ComposeUIViewController {
    App()
}

@Composable
fun App() {
    MaterialTheme {
        Text("Ovasta Sellers - iOS")
    }
}
