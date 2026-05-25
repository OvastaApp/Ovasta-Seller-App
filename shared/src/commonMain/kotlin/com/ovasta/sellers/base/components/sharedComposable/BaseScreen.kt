package com.ovasta.sellers.base.components.sharedComposable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.window.Dialog
import com.ovasta.sellers.base.BaseViewModel
import com.ovasta.sellers.base.Primary
import com.ovasta.sellers.base.ScreenDirectionEventHandler
import com.ovasta.sellers.base.ext.ToastEventHandler

@Composable
fun BaseScreen(
    viewModel: BaseViewModel,
    content: @Composable () -> Unit
) {
    ScreenDirectionEventHandler(viewModel = viewModel)
    ToastEventHandler(viewModel)

    val exceptionState by viewModel.composeUIExceptionEvent.collectAsState(null)
    exceptionState?.let {
        BaseDialog(
            icon = null,
            title = it.exceptionTitle ?: "Error",
            message = it.errorMessage ?: "An unexpected error occurred",
            dismissOnClickOutside = true,
            primaryButtonText = "Dismiss",
            onPrimaryClick = { viewModel.emitComposeUIExceptionEvent(null) },
            onDismiss = { viewModel.emitComposeUIExceptionEvent(null) }
        )
    }

    val isLoading by viewModel.composeUILoadingEvent.collectAsState(false)
    if (isLoading) {
        Dialog(onDismissRequest = {}) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Primary)
            }
        }
    }

    content()
}
