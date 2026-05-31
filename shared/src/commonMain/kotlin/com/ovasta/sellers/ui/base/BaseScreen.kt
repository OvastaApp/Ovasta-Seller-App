package com.ovasta.sellers.ui.base

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import com.ovasta.sellers.ui.theme.Primary

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
        AlertDialog(
            onDismissRequest = { viewModel.emitAppException(null) },
            title = { Text(error.title ?: "Error") },
            text = { Text(error.message ?: "An unknown error occurred") },
            confirmButton = {
                TextButton(onClick = {
                    error.actions.forEach { it.invoke() }
                    viewModel.emitAppException(null)
                }) {
                    Text("OK")
                }
            }
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
