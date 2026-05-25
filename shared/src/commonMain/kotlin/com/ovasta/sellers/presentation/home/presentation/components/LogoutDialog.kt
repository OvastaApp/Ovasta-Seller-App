package com.ovasta.sellers.presentation.home.presentation.components

import androidx.compose.runtime.Composable
import com.ovasta.sellers.base.components.sharedComposable.BaseDialog
import com.ovasta.sellers.resources.Res
import com.ovasta.sellers.resources.logout
import com.ovasta.sellers.resources.logout_message
import com.ovasta.sellers.resources.ok
import org.jetbrains.compose.resources.stringResource

@Composable
fun LogoutDialog(
    isVisible: Boolean? = false,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (isVisible == true) {
        BaseDialog(
            title = stringResource(Res.string.logout),
            message = stringResource(Res.string.logout_message),
            primaryButtonText = stringResource(Res.string.ok),
            onPrimaryClick = {
                onConfirm()
            },
            onDismiss = {
                onDismiss()
            }
        )
    }
}
