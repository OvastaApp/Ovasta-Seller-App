package com.ovasta.sellers.presentation.createOrder.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.ovasta.sellers.base.components.sharedComposable.BaseScreen
import com.ovasta.sellers.presentation.createOrder.presentation.components.CreateOrderContent

@Composable
fun CreateOrderScreen(viewModel: CreateOrderViewModel) {
    val viewState by viewModel.viewState.collectAsState()

    BaseScreen(viewModel = viewModel) {
        CreateOrderContent(
            viewState = viewState,
            onAction = viewModel::onAction
        )
    }
}