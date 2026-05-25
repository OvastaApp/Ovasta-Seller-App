package com.ovasta.sellers.presentation.home.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.ovasta.sellers.base.components.sharedComposable.BaseDialog

@Composable
fun LogoutDialog(
    isVisible: Boolean? = false,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (isVisible == true) {
        BaseDialog(
            title = "",
            message = "",
            primaryButtonText = "",
            onPrimaryClick = {
                onConfirm()
            },
            onDismiss = {
                onDismiss()
            }
        )
    }
}
@Preview
@Composable
fun PreviewLogoutDialog() {
    LogoutDialog(
        isVisible = true,
        onConfirm = {},
        onDismiss = {}
    )
}
