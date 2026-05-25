package com.ovasta.sellers.base.ext

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.ovasta.sellers.base.BaseViewModel
import com.ovasta.sellers.platform.showPlatformToast

@Composable
fun ToastEventHandler(
    viewModel: BaseViewModel
) {
    LaunchedEffect(Unit) {
        viewModel.toastEvent.collect { event ->
            when (event) {
                is ToastEvent.ResourceToastEvent -> {
                    val displayText = event.stringId.key
                        .replace("_", " ")
                        .replaceFirstChar { it.uppercase() }
                    showPlatformToast(displayText)
                }
                is ToastEvent.StringToastEvent -> {
                    showPlatformToast(event.message)
                }
                else -> {}
            }
        }
    }
}
