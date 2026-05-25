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
                    val key = event.stringId.key
                    showPlatformToast(key)
                }
                is ToastEvent.StringToastEvent -> {
                    showPlatformToast(event.message)
                }
                else -> {}
            }
        }
    }
}
