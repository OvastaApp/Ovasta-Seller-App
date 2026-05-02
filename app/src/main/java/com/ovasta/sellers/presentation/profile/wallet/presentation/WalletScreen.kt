package com.ovasta.sellers.presentation.profile.wallet.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.ovasta.sellers.base.components.sharedComposable.BaseScreen
import com.ovasta.sellers.presentation.profile.wallet.presentation.components.WalletContent

@Composable
fun WalletScreen(
    viewModel: WalletViewModel,
    onNavigateBack: () -> Unit
) {
    val viewState by viewModel.viewState.collectAsState()

    BaseScreen(viewModel = viewModel) {
        WalletContent(
            viewState = viewState,
            onAction = viewModel::onScreenAction,
            onNavigateBack = onNavigateBack

        )
    }
}