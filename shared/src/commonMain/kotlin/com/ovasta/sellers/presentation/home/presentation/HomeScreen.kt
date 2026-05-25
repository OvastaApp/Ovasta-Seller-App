package com.ovasta.sellers.presentation.home.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.ovasta.sellers.base.components.sharedComposable.BaseScreen
import com.ovasta.sellers.presentation.home.presentation.components.LogoutDialog
import com.ovasta.sellers.presentation.home.presentation.components.SellerHomeContent

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val viewState by viewModel.viewState.collectAsState()

    BaseScreen(viewModel = viewModel) {
        SellerHomeContent(
            viewState = viewState,
            onAction = viewModel::onScreenAction
        )
    }
}
