package com.ovasta.sellers.ui.screens.orderhistory

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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ovasta.sellers.domain.model.DeliveryOrder
import com.ovasta.sellers.shared.resources.Res
import com.ovasta.sellers.shared.resources.hash_task
import com.ovasta.sellers.shared.resources.ic_call
import com.ovasta.sellers.shared.resources.ic_delivery_agent
import com.ovasta.sellers.shared.resources.ic_delivery_fees
import com.ovasta.sellers.shared.resources.ic_location
import com.ovasta.sellers.shared.resources.ic_total_price
import com.ovasta.sellers.shared.resources.last_orders
import com.ovasta.sellers.shared.resources.no_orders_yet
import com.ovasta.sellers.shared.resources.price_currency
import com.ovasta.sellers.shared.resources.status_assigned
import com.ovasta.sellers.shared.resources.status_cancelled
import com.ovasta.sellers.shared.resources.status_delivered
import com.ovasta.sellers.shared.resources.status_pending
import com.ovasta.sellers.shared.resources.status_picked
import com.ovasta.sellers.ui.base.BaseScreen
import com.ovasta.sellers.ui.components.CenteredTextAppBar
import com.ovasta.sellers.ui.model.OrderSteps
import com.ovasta.sellers.ui.platform.openDialer
import com.ovasta.sellers.ui.theme.Amber
import com.ovasta.sellers.ui.theme.Gray100
import com.ovasta.sellers.ui.theme.Gray200
import com.ovasta.sellers.ui.theme.Gray500
import com.ovasta.sellers.ui.theme.Green
import com.ovasta.sellers.ui.theme.Primary
import com.ovasta.sellers.ui.theme.mdRegular
import com.ovasta.sellers.ui.theme.mdSemiBold
import com.ovasta.sellers.ui.theme.smNormal
import com.ovasta.sellers.ui.theme.smSemiBold
import com.ovasta.sellers.ui.theme.xsMedium
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun OrderHistoryScreen(viewModel: OrderHistoryViewModel) {
    val viewState by viewModel.viewState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onScreenAction(OrderHistoryAction.LoadOrderHistory)
    }

    BaseScreen(viewModel = viewModel) {
        OrderHistoryContent(
            viewState = viewState,
            onLoadMore = { viewModel.onScreenAction(OrderHistoryAction.LoadMore) }
        )
    }
}

@Composable
private fun OrderHistoryContent(
    viewState: OrderHistoryViewState,
    onLoadMore: () -> Unit = {}
) {
    val listState = rememberLazyListState()

    // Trigger loading the next page once the user scrolls near the end of the list.
    val shouldLoadMore by remember(viewState.canLoadMore) {
        derivedStateOf {
            if (!viewState.canLoadMore) return@derivedStateOf false
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: return@derivedStateOf false
            lastVisible >= listState.layoutInfo.totalItemsCount - 3
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) onLoadMore()
    }

    Scaffold(
        topBar = {
            Surface(shadowElevation = 2.dp, color = Color.White) {
                CenteredTextAppBar(
                    stringResource(Res.string.last_orders),
                    showBackButton = false
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .consumeWindowInsets(paddingValues)
                .background(Gray100),
            contentPadding = PaddingValues(
                start = 12.dp,
                end = 12.dp,
                top = paddingValues.calculateTopPadding() + 12.dp,
                bottom = paddingValues.calculateBottomPadding() + 12.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val orders = viewState.orders
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
                        onCallCourier = { phone -> openDialer(phone) }
                    )
                }
            }

            if (viewState.isLoadingMore) {
                item(key = "loading_more") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(28.dp),
                            color = Primary,
                            strokeWidth = 2.dp
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun DeliveryOrderCard(
    order: DeliveryOrder,
    onCallCourier: (String) -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
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
            InfoRow(icon = Res.drawable.ic_location, value = order.toAddress)
            Spacer(modifier = Modifier.height(6.dp))
            InfoRow(icon = Res.drawable.ic_call, value = order.receiverMobile)

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Gray200)
            Spacer(modifier = Modifier.height(10.dp))

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
                        style = smSemiBold, maxLines = 1
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
                        style = smSemiBold, maxLines = 1
                    )
                }
            }

            if (!order.note.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = order.note ?: "",
                    style = xsMedium, color = Gray500,
                    maxLines = 2, overflow = TextOverflow.Ellipsis
                )
            }

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
                            style = smSemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis
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
            text = text, style = xsMedium, color = color,
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
        Text(text = value, style = smNormal, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}
