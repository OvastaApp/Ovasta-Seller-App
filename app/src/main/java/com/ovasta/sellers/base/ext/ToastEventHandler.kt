package com.ovasta.sellers.base.ext

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.ovasta.sellers.base.BaseViewModel
import com.ovasta.sellers.base.StringResourceProvider
import com.ovasta.sellers.platform.showPlatformToast
import org.koin.compose.koinInject

@Composable
fun ToastEventHandler(
    viewModel: BaseViewModel
) {
    val stringProvider: StringResourceProvider = koinInject()
    LaunchedEffect(Unit) {
        viewModel.toastEvent.collect { event ->
            when (event) {
                is ToastEvent.ResourceToastEvent -> {
                    val message = stringProvider.getString(event.stringId, *event.args.toTypedArray())
                    showPlatformToast(message)
                }
                is ToastEvent.StringToastEvent -> {
                    showPlatformToast(event.message)
                }
                else -> {}
            }
        }
    }
}
