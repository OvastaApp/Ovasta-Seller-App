package com.ovasta.sellers.presentation.profile.orderhistory.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.ovasta.sellers.base.components.sharedComposable.BaseScreen
import com.ovasta.sellers.presentation.profile.orderhistory.presentation.components.OrderHistoryContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    viewModel: OrderHistoryViewModel,
    onNavigateBack: () -> Unit
) {
    val viewState by viewModel.viewState.collectAsState()
    val pullState = rememberPullToRefreshState()

    BaseScreen(viewModel = viewModel) {
        PullToRefreshBox(
            isRefreshing = viewState.isRefreshing,
            onRefresh = { viewModel.onScreenAction(OrderHistoryAction.RefreshHistory) },
            state = pullState,
            modifier = Modifier.fillMaxSize()
        ) {
            OrderHistoryContent(
                viewState = viewState,
                onAction = viewModel::onScreenAction,
                onNavigateBack = onNavigateBack
            )
        }
    }
}
