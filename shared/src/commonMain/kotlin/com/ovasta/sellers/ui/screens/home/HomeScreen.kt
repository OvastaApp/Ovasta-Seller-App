package com.ovasta.sellers.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ovasta.sellers.domain.model.CourierInfo
import com.ovasta.sellers.domain.model.DeliveryOrder
import com.ovasta.sellers.domain.model.HomeInfo
import com.ovasta.sellers.shared.resources.Res
import com.ovasta.sellers.shared.resources.are_you_sure_you_want_to_cancel_order
import com.ovasta.sellers.shared.resources.cancel_order
import com.ovasta.sellers.shared.resources.create_order
import com.ovasta.sellers.shared.resources.egp
import com.ovasta.sellers.shared.resources.hash_task
import com.ovasta.sellers.shared.resources.home
import com.ovasta.sellers.shared.resources.ic_add
import com.ovasta.sellers.shared.resources.ic_call
import com.ovasta.sellers.shared.resources.ic_delivery_agent
import com.ovasta.sellers.shared.resources.ic_delivery_fees
import com.ovasta.sellers.shared.resources.ic_location
import com.ovasta.sellers.shared.resources.ic_total_price
import com.ovasta.sellers.shared.resources.my_orders
import com.ovasta.sellers.shared.resources.no
import com.ovasta.sellers.shared.resources.no_orders_yet
import com.ovasta.sellers.shared.resources.points
import com.ovasta.sellers.shared.resources.points_rate_format
import com.ovasta.sellers.shared.resources.price_currency
import com.ovasta.sellers.shared.resources.status_assigned
import com.ovasta.sellers.shared.resources.status_cancelled
import com.ovasta.sellers.shared.resources.status_delivered
import com.ovasta.sellers.shared.resources.status_pending
import com.ovasta.sellers.shared.resources.status_picked
import com.ovasta.sellers.shared.resources.yes
import com.ovasta.sellers.ui.base.BaseScreen
import com.ovasta.sellers.ui.components.BaseDialog
import com.ovasta.sellers.ui.components.CenteredTextAppBar
import com.ovasta.sellers.ui.model.OrderSteps
import com.ovasta.sellers.ui.platform.openDialer
import com.ovasta.sellers.ui.theme.Amber
import com.ovasta.sellers.ui.theme.Gray100
import com.ovasta.sellers.ui.theme.Gray200
import com.ovasta.sellers.ui.theme.Gray500
import com.ovasta.sellers.ui.theme.Green
import com.ovasta.sellers.ui.theme.Primary
import com.ovasta.sellers.ui.theme.lgSemiBold
import com.ovasta.sellers.ui.theme.mdRegular
import com.ovasta.sellers.ui.theme.mdSemiBold
import com.ovasta.sellers.ui.theme.smNormal
import com.ovasta.sellers.ui.theme.smSemiBold
import com.ovasta.sellers.ui.theme.xsMedium
import com.ovasta.sellers.ui.theme.xsRegular
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val viewState by viewModel.viewState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onScreenAction(HomeScreenActions.RefreshHome)
    }

    BaseScreen(viewModel = viewModel) {
        SellerHomeContent(
            viewState = viewState,
            onAction = viewModel::onScreenAction
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SellerHomeContent(
    viewState: HomeViewState,
    onAction: (HomeScreenActions) -> Unit
) {
    var cancelOrderId by remember { mutableStateOf<Int?>(null) }
    var showCancelDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenteredTextAppBar(
                title = stringResource(Res.string.home),
                showBackButton = false
            )
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = viewState.isRefreshing == true,
            onRefresh = { onAction(HomeScreenActions.RefreshHome) },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .consumeWindowInsets(paddingValues)
                    .background(Gray100),
                contentPadding = PaddingValues(
                    start = 12.dp,
                    end = 12.dp,
                    top = 12.dp,
                    bottom = 12.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
            // Points card
            item(key = "points") {
                PointsCard(
                    homeInfo = viewState.homeInfo,
                    onClick = { onAction(HomeScreenActions.NavigateToWallet) }
                )
            }

            // Orders header + Create order button
            item(key = "orders_header") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(Res.string.my_orders),
                        style = lgSemiBold
                    )
                    Button(
                        onClick = { onAction(HomeScreenActions.CreateOrder) },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_add),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(Res.string.create_order),
                            style = xsMedium,
                            color = Color.White
                        )
                    }
                }
            }

            // Orders list
            val orders = viewState.deliveryOrdersResponse?.orders.orEmpty()
            if (orders.isEmpty()) {
                item(key = "empty") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(Res.string.no_orders_yet),
                            style = mdRegular,
                            color = Gray500
                        )
                    }
                }
            } else {
                itemsIndexed(
                    items = orders,
                    key = { index, order -> "${order.id}_$index" }
                ) { _, order ->
                    DeliveryOrderCard(
                        order = order,
                        onClick = { onAction(HomeScreenActions.OrderClicked(order.id)) },
                        onCallCourier = { phone -> openDialer(phone) },
                        onCancelOrder = {
                            cancelOrderId = order.id
                            showCancelDialog = true
                        }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }

    // Cancel confirmation dialog
    if (showCancelDialog && cancelOrderId != null) {
        BaseDialog(
            title = stringResource(Res.string.cancel_order),
            message = stringResource(Res.string.are_you_sure_you_want_to_cancel_order),
            primaryButtonText = stringResource(Res.string.yes),
            secondaryButtonText = stringResource(Res.string.no),
            onPrimaryClick = {
                onAction(HomeScreenActions.CancelOrder(cancelOrderId!!))
                showCancelDialog = false
                cancelOrderId = null
            },
            onSecondaryClick = {
                showCancelDialog = false
                cancelOrderId = null
            },
            onDismiss = {
                showCancelDialog = false
                cancelOrderId = null
            },
            dismissOnClickOutside = false
        )
    }
}

@Composable
private fun PointsCard(homeInfo: HomeInfo?, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val points = homeInfo?.pointsCount ?: 0.0
            val rate = homeInfo?.pointsPerPound ?: 0.0
            val moneyValue = if (rate > 0) points / rate else 0.0

            // Format without String.format (not available in KMP commonMain)
            val formattedPoints = points.toInt().toString()
            val money = ((moneyValue * 100).toInt() / 100.0).toString()
            val formattedRate = ((rate * 10).toInt() / 10.0).toString()

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "$formattedPoints ",
                    style = lgSemiBold,
                    color = Primary
                )
                Text(
                    text = "${stringResource(Res.string.points)} = ",
                    style = smNormal,
                    color = Gray500
                )
                Text(
                    text = "$money ${stringResource(Res.string.egp)}",
                    style = mdSemiBold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(Res.string.points_rate_format, formattedRate, stringResource(Res.string.points), stringResource(Res.string.egp)),
                style = xsRegular,
                color = Gray500
            )
        }
    }
}

@Composable
private fun DeliveryOrderCard(
    order: DeliveryOrder,
    onClick: () -> Unit,
    onCallCourier: (String) -> Unit = {},
    onCancelOrder: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header: Order ID + Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.hash_task, order.id.toString()),
                    style = mdSemiBold,
                    color = Primary
                )
                StatusBadge(order.statusId)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Address
            InfoRow(icon = Res.drawable.ic_location, value = order.toAddress)
            Spacer(modifier = Modifier.height(6.dp))

            // Receiver phone
            InfoRow(icon = Res.drawable.ic_call, value = order.receiverMobile)

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Gray200)
            Spacer(modifier = Modifier.height(10.dp))

            // Pricing section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_delivery_fees),
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Gray500
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(Res.string.price_currency, order.deliveryPrice.toString()),
                        style = smSemiBold,
                        maxLines = 1
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_total_price),
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Gray500
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(Res.string.price_currency, order.totalPrice.toString()),
                        style = smSemiBold,
                        maxLines = 1
                    )
                }
            }

            // Note
            if (!order.note.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = order.note ?: "",
                    style = xsMedium,
                    color = Gray500,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Courier info
            if (order.courier != null) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = Gray200)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_delivery_agent),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Primary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${order.courier?.firstName ?: ""} ${order.courier?.lastName ?: ""}",
                            style = smSemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (!order.courier?.mobile.isNullOrEmpty()) {
                        IconButton(
                            onClick = { onCallCourier(order.courier?.mobile ?: "") },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_call),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Primary
                            )
                        }
                    }
                }
            }

            // Cancel Button
            if (order.canCancelOrder == true && onCancelOrder != null) {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = { onCancelOrder() },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(Res.string.cancel_order),
                        style = xsMedium,
                        color = Color.Red,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(statusId: Int) {
    val step = OrderSteps.fromStatusId(statusId)
    val (text, color) = when (step) {
        OrderSteps.Pending -> Pair(stringResource(Res.string.status_pending), Color.Gray)
        OrderSteps.Assigned -> Pair(stringResource(Res.string.status_assigned), Primary)
        OrderSteps.Picked -> Pair(stringResource(Res.string.status_picked), Amber)
        OrderSteps.Delivered -> Pair(stringResource(Res.string.status_delivered), Green)
        OrderSteps.Canceled -> Pair(stringResource(Res.string.status_cancelled), Color.Red)
    }
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.15f),
        contentColor = color
    ) {
        Text(
            text = text,
            style = xsMedium,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun InfoRow(icon: org.jetbrains.compose.resources.DrawableResource, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = Gray500
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = value,
            style = smNormal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
