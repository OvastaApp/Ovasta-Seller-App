package com.ovasta.sellers.presentation.profile.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.ovasta.sellers.base.components.sharedComposable.BaseScreen
import com.ovasta.sellers.presentation.profile.presentation.components.OrderHistoryContent

@Composable
fun OrdersScreen(viewModel: ProfileViewModel) {
    val viewState by viewModel.viewState.collectAsState()

    BaseScreen(viewModel = viewModel) {
        OrderHistoryContent(
            viewState = viewState,
            onAction = viewModel::onScreenAction
        )
    }
}