package com.ovasta.sellers.base.components.sharedComposable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import com.ovasta.sellers.base.BaseViewModel
import com.ovasta.sellers.base.Primary
import com.ovasta.sellers.base.ScreenDirectionEventHandler
import com.ovasta.sellers.base.ext.ToastEventHandler
import com.ovasta.sellers.resources.Res
import com.ovasta.sellers.resources.an_error_occurred
import com.ovasta.sellers.resources.dismiss
import org.jetbrains.compose.resources.stringResource

@Composable
fun BaseScreen(
    viewModel: BaseViewModel,
    content: @Composable () -> Unit
) {
    ScreenDirectionEventHandler(viewModel = viewModel)
    ToastEventHandler(viewModel)

    val exceptionState by viewModel.composeUIExceptionEvent.collectAsState(null)
    val isLoading by viewModel.composeUILoadingEvent.collectAsState(false)

    if (exceptionState != null) {
        BaseDialog(
            icon = Icons.Default.Warning,
            title = exceptionState?.exceptionTitle ?: stringResource(Res.string.an_error_occurred),
            message = exceptionState?.errorMessage ?: stringResource(Res.string.an_error_occurred),
            dismissOnClickOutside = true,
            primaryButtonText = stringResource(Res.string.dismiss),
            onPrimaryClick = { viewModel.emitComposeUIExceptionEvent(null) },
            onDismiss = { viewModel.emitComposeUIExceptionEvent(null) }
        )
    } else if (isLoading) {
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
