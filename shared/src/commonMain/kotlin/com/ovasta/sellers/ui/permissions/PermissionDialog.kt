package com.ovasta.sellers.ui.permissions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.ovasta.sellers.shared.resources.Res
import com.ovasta.sellers.shared.resources.dismiss
import com.ovasta.sellers.shared.resources.enable_notifications
import com.ovasta.sellers.shared.resources.not_now
import com.ovasta.sellers.shared.resources.notification_permission_denied
import com.ovasta.sellers.shared.resources.notification_permission_denied_message
import com.ovasta.sellers.shared.resources.notification_permission_message
import com.ovasta.sellers.shared.resources.notification_permission_title
import com.ovasta.sellers.shared.resources.open_settings
import com.ovasta.sellers.ui.components.BaseDialog
import org.jetbrains.compose.resources.stringResource

@Composable
fun PermissionDialogHandler(
    viewModel: PermissionViewModel
) {
    val state by viewModel.permissionState.collectAsState()

    if (state.shouldShowDialog && !state.isGranted) {
        BaseDialog(
            title = stringResource(Res.string.notification_permission_title),
            message = stringResource(Res.string.notification_permission_message),
            primaryButtonText = stringResource(Res.string.enable_notifications),
            secondaryButtonText = stringResource(Res.string.not_now),
            onPrimaryClick = { viewModel.requestNotificationPermission() },
            onSecondaryClick = { viewModel.dismissPermissionDialog() },
            onDismiss = { viewModel.dismissPermissionDialog() }
        )
    }

    if (state.shouldShowDeniedDialog && state.hasRequested && !state.isGranted) {
        BaseDialog(
            title = stringResource(Res.string.notification_permission_denied),
            message = stringResource(Res.string.notification_permission_denied_message),
            primaryButtonText = stringResource(Res.string.open_settings),
            secondaryButtonText = stringResource(Res.string.dismiss),
            onPrimaryClick = { viewModel.openSettings() },
            onSecondaryClick = { viewModel.dismissDeniedDialog() },
            onDismiss = { viewModel.dismissDeniedDialog() }
        )
    }
}
