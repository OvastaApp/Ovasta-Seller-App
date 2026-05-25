package com.ovasta.sellers.base.components.sharedComposable

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import com.ovasta.sellers.listener.LogoutListener
import com.ovasta.sellers.R
import com.ovasta.sellers.base.BaseViewModel
import com.ovasta.sellers.base.Primary
import com.ovasta.sellers.base.ScreenDirectionEventHandler
import com.ovasta.sellers.base.ext.ToastEventHandler
import com.ovasta.sellers.data.RemoteConstants

@Composable
fun BaseScreen(
    viewModel: BaseViewModel,
    content: @Composable () -> Unit
) {
    ScreenDirectionEventHandler(
        viewModel = viewModel
    )
    ToastEventHandler(viewModel)

    val logoutListener = LocalActivity.current as? LogoutListener

    /** Handling Base Exception **/
    val exceptionState by viewModel.composeUIExceptionEvent.collectAsState(null)
    exceptionState?.let {
        if (exceptionState?.code == RemoteConstants.UNAUTHORIZED_CODE) {
            logoutListener?.onUnauthorized()
        } else {

            BaseDialog(
                icon = painterResource(R.drawable.ic_error),
                title = exceptionState?.exceptionTitle
                        ?: stringResource(R.string.an_error_occurred),
                message = exceptionState?.errorMessage
                        ?: stringResource(R.string.generic_unknown_error),
                dismissOnClickOutside = true,
                primaryButtonText = stringResource(R.string.dismiss),
                onPrimaryClick = {
                    exceptionState?.actions?.forEach { it.invoke() }
                    viewModel.emitComposeUIExceptionEvent(null)
                },
                onDismiss = { viewModel.emitComposeUIExceptionEvent(null) }
            )

        }
    }

    /** Handling Base Loading **/
    val isLoading by viewModel.composeUILoadingEvent.collectAsState(false)
    if (isLoading) {
        Dialog(
            onDismissRequest = {}
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                CircularProgressIndicator(
                    color = Primary
                )

            }
        }
    }

    content()
}