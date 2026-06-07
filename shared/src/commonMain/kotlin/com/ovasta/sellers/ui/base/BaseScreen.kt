package com.ovasta.sellers.ui.base

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import com.ovasta.sellers.shared.resources.Res
import com.ovasta.sellers.shared.resources.an_error_occurred
import com.ovasta.sellers.shared.resources.dismiss
import com.ovasta.sellers.shared.resources.generic_unknown_error
import com.ovasta.sellers.shared.resources.ic_error
import com.ovasta.sellers.ui.components.BaseDialog
import com.ovasta.sellers.ui.theme.Primary
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun BaseScreen(
    viewModel: BaseViewModel,
    content: @Composable () -> Unit
) {
    // Handle navigation events
    ScreenDirectionEventHandler(viewModel)

    // Handle error dialog
    val exception by viewModel.appExceptionEvent.collectAsState()
    exception?.let { error ->
        BaseDialog(
            icon = painterResource(Res.drawable.ic_error),
            title = error.title ?: stringResource(Res.string.an_error_occurred),
            message = error.message ?: stringResource(Res.string.generic_unknown_error),
            primaryButtonText = stringResource(Res.string.dismiss),
            dismissOnClickOutside = true,
            onPrimaryClick = {
                error.actions.forEach { it.invoke() }
                viewModel.emitAppException(null)
            },
            onDismiss = { viewModel.emitAppException(null) }
        )
    }

    // Handle loading
    val isLoading by viewModel.loadingEvent.collectAsState()
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

@Composable
fun ScreenDirectionEventHandler(viewModel: BaseViewModel) {
    val navigator = LocalNavigator.current
    LaunchedEffect(Unit) {
        viewModel.screenDirectionEvent.collect { direction ->
            when (direction) {
                is ScreenDirection.Push -> navigator.push(direction.screen)
                is ScreenDirection.Pop -> navigator.pop()
                is ScreenDirection.Replace -> navigator.replace(direction.screen)
                is ScreenDirection.ReplaceAll -> navigator.replaceAll(direction.screen)
                null -> Unit
            }
        }
    }
}
