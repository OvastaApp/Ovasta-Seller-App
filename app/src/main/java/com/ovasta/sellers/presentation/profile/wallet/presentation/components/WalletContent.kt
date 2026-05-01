package com.ovasta.sellers.presentation.profile.wallet.presentation.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ovasta.sellers.R
import com.ovasta.sellers.base.Amber
import com.ovasta.sellers.base.CenteredTextAppBar
import com.ovasta.sellers.base.Gray500
import com.ovasta.sellers.base.Green
import com.ovasta.sellers.base.Primary
import com.ovasta.sellers.base.mdSemiBold
import com.ovasta.sellers.base.smNormal
import com.ovasta.sellers.base.smSemiBold
import com.ovasta.sellers.base.xsMedium
import com.ovasta.sellers.presentation.home.data.model.OrderSteps
import com.ovasta.sellers.presentation.home.data.model.OrderSteps.Companion.fromStatusId
import com.ovasta.sellers.presentation.profile.wallet.data.TransactionsSteps
import com.ovasta.sellers.presentation.profile.wallet.data.WalletTransactions
import com.ovasta.sellers.presentation.profile.wallet.data.WithdrawRequests
import com.ovasta.sellers.presentation.profile.wallet.presentation.WalletAction
import com.ovasta.sellers.presentation.profile.wallet.presentation.WalletViewState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletContnet(
    viewState: WalletViewState,
    onAction: (WalletAction) -> Unit,
    onNavigateBack: () -> Unit = {}
) {

    LaunchedEffect(Unit) {
        onAction(WalletAction.LoadWalletTransactions)
    }

    val tabs = listOf(
        stringResource(R.string.wallet),
        stringResource(R.string.points)
    )

    Scaffold(
        modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            Surface(shadowElevation = 2.dp, color = Color.White) {
                CenteredTextAppBar(
                    stringResource(R.string.wallet),
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
            viewState.walletTransactions?.let { wallet ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Wallet Balance Card
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Primary)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.wallet_balance),
                                style = xsMedium,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${wallet.walletBalance ?: 0} EGP",
                                style = mdSemiBold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { onAction(WalletAction.RequestWithdraw) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = Primary
                                ),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.withdraw),
                                    style = xsMedium
                                )
                            }
                        }
                    }

                    // Points Card
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Amber.copy(alpha = 0.15f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.points),
                                style = xsMedium,
                                color = Amber
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${wallet.points ?: 0}",
                                style = mdSemiBold,
                                color = Amber
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { onAction(WalletAction.ConvertPoints) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Amber,
                                    contentColor = Color.White
                                ),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.convert_to_money),
                                    style = xsMedium
                                )
                            }
                        }
                    }
                }
            }

            // Tab Row
            TabRow(
                selectedTabIndex = viewState.selectedTab,
                containerColor = Color.White,
                contentColor = Primary,
                indicator = { tabPositions ->
                    Box(
                        Modifier
                            .tabIndicatorOffset(tabPositions[viewState.selectedTab])
                            .height(3.dp)
                            .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                            .background(Primary)
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
                                style = if (viewState.selectedTab == index) smSemiBold else smNormal,
                                color = if (viewState.selectedTab == index) Primary else Gray500
                            )
                        }
                    )
                }
            }

            // Tab Content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .animateContentSize()
            ) {
                when (viewState.selectedTab) {
                    0 -> WalletTransactionsList(
                        transactions = viewState.walletTransactions?.transactions ?: emptyList()
                    )

                    1 -> WithdrawRequestsList(
                        requests = viewState.withdrawRequests
                    )
                }
            }
        }
    }
}

@Composable
fun WalletTransactionsList(transactions: List<WalletTransactions>) {
    if (transactions.isEmpty()) {
        EmptyState(text = stringResource(R.string.empty_wallet_transactions))
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(transactions) { _, transaction ->
                TransactionItem(transaction)
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: WalletTransactions) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "#${transaction.id}",
                    style = smSemiBold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = transaction.createdAt ?: "",
                    style = xsMedium,
                    color = Gray500
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${transaction.amount ?: 0} EGP",
                    style = smSemiBold,
                    color = Primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                StatusChip(status = transaction.status)
            }
        }
    }
}

@Composable
fun WithdrawRequestsList(requests: List<WithdrawRequests>) {
    if (requests.isEmpty()) {
        EmptyState(text = stringResource(R.string.empty_withdraw_history))
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(requests) { _, request ->
                WithdrawItem(request)
            }
        }
    }
}

@Composable
fun WithdrawItem(request: WithdrawRequests) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "#${request.id}", style = smSemiBold, color = Color.Black)
                Text(text = "${request.amount ?: 0} EGP", style = smSemiBold, color = Primary)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = request.createdAt ?: "", style = xsMedium, color = Gray500)
                StatusChip(status = request.status)
            }
            if (!request.rejectionReason.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = request.rejectionReason ?: "",
                    style = xsMedium,
                    color = Color.Red.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun StatusChip(status: Int) {
    val stepId = TransactionsSteps.fromStatusId(status)
    val (text, color) = when (stepId) {
        TransactionsSteps.Pending -> Pair(stringResource(R.string.status_pending), Amber)
        TransactionsSteps.Approved -> Pair(stringResource(R.string.status_pending), Green)
        TransactionsSteps.Rejected -> Pair(stringResource(R.string.status_pending), Color.Red)
        else -> "Unknown" to Gray500
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.1f))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(text = text, style = xsMedium, color = color)
    }
}

@Composable
fun EmptyState(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = text, style = smNormal, color = Gray500, textAlign = TextAlign.Center)
    }
}

@Preview
@Composable
fun WalletContentPreview() {
    WalletContnet(
        viewState = WalletViewState(
            walletTransactions = com.ovasta.sellers.presentation.profile.wallet.data.WalletTransactionsResponse(
                walletBalance = 1500,
                transactions = listOf(
                    WalletTransactions(
                        id = 1,
                        amount = 500.0,
                        status = 1,
                        createdAt = "2024-06-01"
                    ),
                    WalletTransactions(
                        id = 2,
                        amount = 300.0,
                        status = 0,
                        createdAt = "2024-06-05"
                    ),
                    WalletTransactions(
                        id = 3,
                        amount = 200.0,
                        status = 2,
                        rejectionReason = "Insufficient documents",
                        createdAt = "2024-06-10"
                    )
                )
            ),
            withdrawRequests = listOf(
                WithdrawRequests(id = 1, amount = 400, status = 0, createdAt = "2024-06-02"),
                WithdrawRequests(id = 2, amount = 600, status = 1, createdAt = "2024-06-06"),
                WithdrawRequests(
                    id = 3,
                    amount = 300,
                    status = 2,
                    rejectionReason = "Account verification failed",
                    createdAt = "2024-06-11"
                )
            )
        ),
        onAction = {},
        onNavigateBack = {}
    )
}