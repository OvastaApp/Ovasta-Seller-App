package com.ovasta.sellers.presentation.profile.wallet.presentation.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ovasta.sellers.base.*
import com.ovasta.sellers.base.components.sharedComposable.BaseDialog
import com.ovasta.sellers.platform.showPlatformToast
import com.ovasta.sellers.presentation.profile.wallet.data.PointsHistory
import com.ovasta.sellers.presentation.profile.wallet.data.TransactionsSteps
import com.ovasta.sellers.presentation.profile.wallet.data.WithdrawRequests
import com.ovasta.sellers.presentation.profile.wallet.presentation.WalletAction
import com.ovasta.sellers.presentation.profile.wallet.presentation.WalletViewState
import com.ovasta.sellers.resources.Res
import com.ovasta.sellers.resources.approved
import com.ovasta.sellers.resources.back
import com.ovasta.sellers.resources.confirm
import com.ovasta.sellers.resources.convert_to_money
import com.ovasta.sellers.resources.empty_wallet_transactions
import com.ovasta.sellers.resources.empty_withdraw_history
import com.ovasta.sellers.resources.ok
import com.ovasta.sellers.resources.point
import com.ovasta.sellers.resources.price_currency
import com.ovasta.sellers.resources.rejected
import com.ovasta.sellers.resources.status_pending
import com.ovasta.sellers.resources.success
import com.ovasta.sellers.resources.the_points
import com.ovasta.sellers.resources.wallet
import com.ovasta.sellers.resources.wallet_balance
import com.ovasta.sellers.resources.withdraw
import com.ovasta.sellers.resources.withdraw_confirmation_message
import com.ovasta.sellers.resources.withdraw_request_success
import com.ovasta.sellers.resources.withdraw_requests
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletContent(
    viewState: WalletViewState,
    onAction: (WalletAction) -> Unit,
    onNavigateBack: () -> Unit = {}
) {
    LaunchedEffect(Unit) {
        onAction(WalletAction.LoadWalletTransactions)
    }

    LaunchedEffect(viewState.toastMessage) {
        viewState.toastMessage?.let {
            showPlatformToast(it)
            onAction(WalletAction.DismissToast)
        }
    }

    if (viewState.showRedeemBottomSheet) {
        RedeemPointsBottomSheet(
            viewState = viewState,
            onAction = onAction
        )
    }

    if (viewState.showWithdrawConfirmDialog) {
        BaseDialog(
            title = stringResource(Res.string.withdraw),
            message = stringResource(Res.string.withdraw_confirmation_message, (viewState.wallet?.walletBalance ?: 0.0).toString()),
            primaryButtonText = stringResource(Res.string.confirm),
            secondaryButtonText = stringResource(Res.string.back),
            onPrimaryClick = { onAction(WalletAction.ConfirmWithdraw) },
            onSecondaryClick = { onAction(WalletAction.DismissWithdrawDialog) }
        )
    }

    if (viewState.showWithdrawSuccessDialog) {
        BaseDialog(
            title = stringResource(Res.string.success),
            message = stringResource(Res.string.withdraw_request_success),
            primaryButtonText = stringResource(Res.string.ok),
            onPrimaryClick = { onAction(WalletAction.DismissSuccessDialog) }
        )
    }

    val tabs = listOf(
        stringResource(Res.string.the_points),
        stringResource(Res.string.withdraw_requests)
    )

    Scaffold(
        modifier = Modifier
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
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Amber)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                        Text(
                            text = stringResource(Res.string.the_points),
                            style = xsMedium,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "${wallet.points ?: 0}",
                                style = mdSemiBold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Button(
                                onClick = { onAction(WalletAction.ConvertPoints) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = Amber
                                ),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                Text(
                                    text = stringResource(Res.string.convert_to_money),
                                    style = xsMedium
                                )
                            }
                        }
                    }
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Primary)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = stringResource(Res.string.wallet_balance),
                                style = xsMedium,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(Res.string.price_currency, (wallet.walletBalance ?: 0).toString()),
                                style = mdSemiBold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.weight(1f))
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
                                    text = stringResource(Res.string.withdraw),
                                    style = xsMedium
                                )
                            }
                        }
                    }
                }
            }

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

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .animateContentSize()
            ) {
                when (viewState.selectedTab) {
                    0 -> PointsHistoryList(
                        requests = viewState.wallet?.pointsHistory ?: emptyList()
                    )

                    1 -> WalletWithdrawList(
                        transactions = viewState.withdrawRequests
                    )
                }
            }
        }
    }
}

@Composable
fun WalletWithdrawList(transactions: List<WithdrawRequests>) {
    if (transactions.isEmpty()) {
        EmptyState(text = stringResource(Res.string.empty_wallet_transactions))
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(transactions) { _, transaction ->
                WithdrawItem(transaction)
            }
        }
    }
}

@Composable
fun PointsItem(transaction: PointsHistory) {
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
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "#${transaction.id}",
                    style = smSemiBold,
                    color = Color.Black
                )
                if (!transaction.description.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = transaction.description ?: "",
                        style = xsRegular,
                        color = Color.Black.copy(alpha = 0.7f)
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${transaction.amount ?: 0} ${stringResource(Res.string.point)}",
                    style = smSemiBold,
                    color = Primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatDate(transaction.createdAt),
                    style = xsRegular,
                    color = Gray500
                )
            }
        }
    }
}

@Composable
fun PointsHistoryList(requests: List<PointsHistory>) {
    if (requests.isEmpty()) {
        EmptyState(text = stringResource(Res.string.empty_withdraw_history))
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(requests) { _, request ->
                PointsItem(request)
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
                Text(text = stringResource(Res.string.price_currency, (request.amount ?: 0).toString()), style = smSemiBold, color = Primary)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = formatDate(request.createdAt), style = smMedium, color = Gray500)
                StatusChip(status = request.status)
            }
            if (!request.rejectionReason.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = request.rejectionReason ?: "",
                    style = smMedium,
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
        TransactionsSteps.Pending -> Pair(stringResource(Res.string.status_pending), Amber)
        TransactionsSteps.Approved -> Pair(stringResource(Res.string.approved), Green)
        TransactionsSteps.Rejected -> Pair(stringResource(Res.string.rejected), Color.Red)
        else -> stringResource(Res.string.rejected) to Gray500
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

fun formatDate(isoDate: String?): String {
    if (isoDate.isNullOrEmpty()) return ""

    return try {
        val cleaned = isoDate.substringBefore(".").removeSuffix("Z")
        val parts = cleaned.split("T")
        if (parts.size != 2) return isoDate

        val dateParts = parts[0].split("-")
        val timeParts = parts[1].split(":")

        if (dateParts.size < 3) return isoDate

        val month = dateParts[1]
        val day = dateParts[0]
        val hour = timeParts.getOrElse(0) { "00" }.toInt()
        val minute = timeParts.getOrElse(1) { "00" }

        val monthNames = listOf(
            "يناير", "فبراير", "مارس", "أبريل", "مايو", "يونيو",
            "يوليو", "أغسطس", "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر"
        )
        val monthIndex = month.toIntOrNull()?.minus(1)?.coerceIn(0, 11) ?: return isoDate
        val monthName = monthNames[monthIndex]

        val amPm = if (hour < 12) "ص" else "م"
        val hour12 = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
        val timeStr = "$hour12:$minute $amPm"

        "$day-$monthName\n$timeStr"

    } catch (e: Exception) {
        isoDate
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
fun WalletContentPreview() {
    WalletContent(
        viewState = WalletViewState(
            wallet = com.ovasta.sellers.presentation.profile.wallet.data.WalletResponse(
                walletBalance = 1500.0,
                pointsHistory = listOf(
                    PointsHistory(
                        id = 1,
                        amount = 500.0,
                        createdAt = "2024-06-01"
                    ),
                    PointsHistory(
                        id = 2,
                        amount = 300.0,
                        createdAt = "2024-06-05"
                    ),
                    PointsHistory(
                        id = 3,
                        amount = 200.0,
                        rejectionReason = "Insufficient documents",
                        createdAt = "2024-06-10"
                    )
                )
            ),
            withdrawRequests = listOf(
                WithdrawRequests(id = 1, amount = 400.0, status = 0, createdAt = "2024-06-02"),
                WithdrawRequests(id = 2, amount = 600.0, status = 1, createdAt = "2024-06-06"),
                WithdrawRequests(
                    id = 3,
                    amount = 300.0,
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
