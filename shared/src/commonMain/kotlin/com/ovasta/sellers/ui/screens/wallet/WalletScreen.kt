package com.ovasta.sellers.ui.screens.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ovasta.sellers.domain.model.PointsHistory
import com.ovasta.sellers.domain.model.WithdrawRequests
import com.ovasta.sellers.shared.resources.Res
import com.ovasta.sellers.shared.resources.approved
import com.ovasta.sellers.shared.resources.confirm
import com.ovasta.sellers.shared.resources.back
import com.ovasta.sellers.shared.resources.convert_to_money
import com.ovasta.sellers.shared.resources.empty_wallet_transactions
import com.ovasta.sellers.shared.resources.empty_withdraw_history
import com.ovasta.sellers.shared.resources.ic_confirm
import com.ovasta.sellers.shared.resources.ok
import com.ovasta.sellers.shared.resources.pending_label
import com.ovasta.sellers.shared.resources.price_currency
import com.ovasta.sellers.shared.resources.rejected
import com.ovasta.sellers.shared.resources.success
import com.ovasta.sellers.shared.resources.the_points
import com.ovasta.sellers.shared.resources.wallet
import com.ovasta.sellers.shared.resources.wallet_balance
import com.ovasta.sellers.shared.resources.withdraw
import com.ovasta.sellers.shared.resources.withdraw_confirmation_message
import com.ovasta.sellers.shared.resources.withdraw_request_success
import com.ovasta.sellers.shared.resources.withdraw_requests
import com.ovasta.sellers.ui.base.BaseScreen
import com.ovasta.sellers.ui.base.LocalNavigator
import com.ovasta.sellers.ui.components.BaseDialog
import com.ovasta.sellers.ui.components.CenteredTextAppBar
import com.ovasta.sellers.ui.model.TransactionsSteps
import com.ovasta.sellers.ui.theme.Amber
import com.ovasta.sellers.ui.theme.Gray500
import com.ovasta.sellers.ui.theme.Green
import com.ovasta.sellers.ui.theme.Primary
import com.ovasta.sellers.ui.theme.mdSemiBold
import com.ovasta.sellers.ui.theme.smMedium
import com.ovasta.sellers.ui.theme.smNormal
import com.ovasta.sellers.ui.theme.smSemiBold
import com.ovasta.sellers.ui.theme.xsMedium
import com.ovasta.sellers.ui.theme.xsRegular
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun WalletScreen(viewModel: WalletViewModel) {
    val viewState by viewModel.viewState.collectAsState()
    val navigator = LocalNavigator.current

    LaunchedEffect(Unit) {
        viewModel.onScreenAction(WalletAction.LoadWalletTransactions)
    }

    BaseScreen(viewModel = viewModel) {
        WalletContent(
            viewState = viewState,
            onAction = viewModel::onScreenAction,
            onNavigateBack = { navigator.pop() }
        )
    }
}

@Composable
private fun WalletContent(
    viewState: WalletViewState,
    onAction: (WalletAction) -> Unit,
    onNavigateBack: () -> Unit = {}
) {
    // Withdraw Confirmation Dialog
    if (viewState.showWithdrawConfirmDialog) {
        BaseDialog(
            title = stringResource(Res.string.withdraw),
            message = stringResource(
                Res.string.withdraw_confirmation_message,
                "${viewState.wallet?.walletBalance ?: 0.0}"
            ),
            icon = painterResource(Res.drawable.ic_confirm),
            primaryButtonText = stringResource(Res.string.confirm),
            secondaryButtonText = stringResource(Res.string.back),
            onPrimaryClick = { onAction(WalletAction.ConfirmWithdraw) },
            onSecondaryClick = { onAction(WalletAction.DismissWithdrawDialog) },
            onDismiss = { onAction(WalletAction.DismissWithdrawDialog) }
        )
    }

    // Withdraw Success Dialog
    if (viewState.showWithdrawSuccessDialog) {
        BaseDialog(
            title = stringResource(Res.string.success),
            message = stringResource(Res.string.withdraw_request_success),
            primaryButtonText = stringResource(Res.string.ok),
            onPrimaryClick = { onAction(WalletAction.DismissSuccessDialog) },
            onDismiss = { onAction(WalletAction.DismissSuccessDialog) }
        )
    }

    val tabs = listOf(
        stringResource(Res.string.the_points),
        stringResource(Res.string.withdraw_requests)
    )

    Scaffold(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.statusBars)
            .background(Color.White),
        containerColor = Color.White,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            Surface(shadowElevation = 2.dp, color = Color.White) {
                CenteredTextAppBar(
                    stringResource(Res.string.wallet),
                    onBackButtonPressed = { onNavigateBack() }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            viewState.wallet?.let { wallet ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(IntrinsicSize.Min),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Points Card
                    Card(
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Amber)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = stringResource(Res.string.the_points),
                                style = xsMedium, color = Color.White.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "${wallet.points ?: 0}",
                                style = mdSemiBold, color = Color.White
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Button(
                                onClick = { onAction(WalletAction.ConvertPoints) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White, contentColor = Amber
                                ),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                Text(text = stringResource(Res.string.convert_to_money), style = xsMedium)
                            }
                        }
                    }
                    // Wallet Balance Card
                    Card(
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Primary)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = stringResource(Res.string.wallet_balance),
                                style = xsMedium, color = Color.White.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(Res.string.price_currency, "${wallet.walletBalance ?: 0.0}"),
                                style = mdSemiBold, color = Color.White
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Button(
                                onClick = { onAction(WalletAction.RequestWithdraw) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White, contentColor = Primary
                                ),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                Text(text = stringResource(Res.string.withdraw), style = xsMedium)
                            }
                        }
                    }
                }
            }

            // Tabs
            TabRow(
                selectedTabIndex = viewState.selectedTab,
                containerColor = Color.White,
                contentColor = Primary,
                indicator = { tabPositions ->
                    Box(
                        Modifier
                            .tabIndicatorOffset(tabPositions[viewState.selectedTab])
                            .height(3.dp)
                            .background(Primary, RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = viewState.selectedTab == index,
                        onClick = { onAction(WalletAction.SelectTab(index)) },
                        text = {
                            Text(
                                text = title,
                                style = smMedium,
                                color = if (viewState.selectedTab == index) Primary else Gray500
                            )
                        }
                    )
                }
            }

            // Tab Content
            when (viewState.selectedTab) {
                0 -> PointsHistoryTab(viewState.wallet?.pointsHistory.orEmpty())
                1 -> WithdrawRequestsTab(viewState.withdrawRequests)
            }
        }
    }
}

@Composable
private fun PointsHistoryTab(history: List<PointsHistory>) {
    if (history.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize().padding(top = 40.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Text(
                text = stringResource(Res.string.empty_wallet_transactions),
                style = smNormal, color = Gray500, textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(history) { _, item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = item.description ?: "", style = smMedium)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = item.createdAt ?: "", style = xsRegular, color = Gray500)
                        }
                        Text(
                            text = "${item.amount ?: 0}",
                            style = smSemiBold,
                            color = if ((item.amount ?: 0.0) > 0) Green else Color.Red
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WithdrawRequestsTab(requests: List<WithdrawRequests>) {
    if (requests.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize().padding(top = 40.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Text(
                text = stringResource(Res.string.empty_withdraw_history),
                style = smNormal, color = Gray500, textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(requests) { _, request ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = stringResource(Res.string.price_currency, "${request.amount ?: 0.0}"),
                                style = smSemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = request.createdAt ?: "", style = xsRegular, color = Gray500)
                        }
                        WithdrawStatusBadge(request.status)
                    }
                }
            }
        }
    }
}

@Composable
private fun WithdrawStatusBadge(statusId: Int) {
    val (text, color) = when (statusId) {
        TransactionsSteps.PENDING -> Pair(stringResource(Res.string.pending_label), Amber)
        TransactionsSteps.APPROVED -> Pair(stringResource(Res.string.approved), Green)
        TransactionsSteps.REJECTED -> Pair(stringResource(Res.string.rejected), Color.Red)
        else -> Pair("-", Color.Gray)
    }
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.15f),
        contentColor = color
    ) {
        Text(
            text = text, style = xsMedium, color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}
